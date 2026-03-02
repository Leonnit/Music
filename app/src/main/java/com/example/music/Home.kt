package com.example.music

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

object EtatLecture {
    var musiqueActive  by mutableStateOf<MusiqueLocale?>(null)
    var estEnLecture   by mutableStateOf(false)
    var estAleatoire   by mutableStateOf(false)
    var modeRepetition by mutableIntStateOf(0) // 0=off 1=tous 2=une
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    val context = LocalContext.current
    var permissionOk by remember { mutableStateOf(false) }

    // ← CORRIGÉ : demande de permission au lancement
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_AUDIO
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> permissionOk = granted }

    LaunchedEffect(Unit) { launcher.launch(permission) }

    // ← CORRIGÉ : enregistrer le callback de fin de piste
    DisposableEffect(Unit) {
        Lecteur.surFinDePiste = { jouerSuivant(context) }
        onDispose { Lecteur.liberer() }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor      = Color.Transparent,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter          = painterResource(id = R.drawable.fond),
                contentDescription = "Fond",
                contentScale     = ContentScale.Crop,
                modifier         = Modifier.fillMaxSize()
            )
            Column(modifier = Modifier.padding(innerPadding)) {
                if (permissionOk) Contenue()
                else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Permission requise pour lire la musique", color = TextGray)
                    }
                }
            }
            AnimatedVisibility(
                visible  = EtatLecture.musiqueActive != null,
                enter    = slideInVertically { it },
                exit     = slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                EtatLecture.musiqueActive?.let { MiniPlayer(musique = it) }
            }
        }
    }
}

// ─── MiniPlayer ───────────────────────────────────────────────────────────────
@Composable
fun MiniPlayer(musique: MusiqueLocale) {
    val context     = LocalContext.current
    var progression by remember { mutableFloatStateOf(0f) }
    var enDrag      by remember { mutableStateOf(false) }

    LaunchedEffect(musique, EtatLecture.estEnLecture) {
        while (true) {
            if (!enDrag && EtatLecture.estEnLecture) {
                val total = Lecteur.dureeTotale()
                if (total > 0) progression = Lecteur.progressionActuelle().toFloat() / total
            }
            delay(500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xEE1C1C2E))
    ) {
        // ← CORRIGÉ : barre de progression interactive (glissable)
        Slider(
            value         = progression,
            onValueChange = { v ->
                enDrag = true
                progression = v
            },
            onValueChangeFinished = {
                Lecteur.allerA((progression * Lecteur.dureeTotale()).toInt())
                enDrag = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .padding(horizontal = 4.dp),
            colors = SliderDefaults.colors(
                thumbColor         = Color(0xFF7B61FF),
                activeTrackColor   = Color(0xFF7B61FF),
                inactiveTrackColor = Color(0xFF2E2E40)
            )
        )

        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                CoverArt(albumUri = musique.albumUri, size = 48)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(musique.titre,   color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(musique.artiste, color = TextGray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                // ← NOUVEAU : bouton répétition
                IconButton(onClick = { EtatLecture.modeRepetition = (EtatLecture.modeRepetition + 1) % 3 }) {
                    Icon(
                        imageVector = when (EtatLecture.modeRepetition) {
                            1 -> Icons.Default.Repeat
                            2 -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        },
                        tint = if (EtatLecture.modeRepetition > 0) Color(0xFF7B61FF) else TextGray,
                        contentDescription = "Répétition",
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = { jouerPrecedent(context) }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Précédent", tint = Color.White, modifier = Modifier.size(26.dp))
                }
                IconButton(onClick = {
                    if (EtatLecture.estEnLecture) { Lecteur.pause(); EtatLecture.estEnLecture = false }
                    else { Lecteur.reprendre(); EtatLecture.estEnLecture = true }
                }) {
                    Icon(
                        imageVector = if (EtatLecture.estEnLecture) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause", tint = Color.White, modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = { jouerSuivant(context) }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Suivant", tint = Color.White, modifier = Modifier.size(26.dp))
                }
            }
        }
    }
}

// ─── Navigation ───────────────────────────────────────────────────────────────
fun jouerSuivant(context: Context) {
    val liste  = Lecteur.listeMusiques
    val actuel = EtatLecture.musiqueActive ?: return

    // ← CORRIGÉ : gérer répétition et aléatoire
    val suivant = when (EtatLecture.modeRepetition) {
        2 -> actuel  // répéter la même
        else -> {
            val idx = liste.indexOfFirst { it.id == actuel.id }
            if (EtatLecture.estAleatoire) {
                liste.filter { it.id != actuel.id }.randomOrNull() ?: liste.firstOrNull()
            } else {
                if (EtatLecture.modeRepetition == 1) {
                    liste.getOrNull(idx + 1) ?: liste.firstOrNull()
                } else {
                    liste.getOrNull(idx + 1)
                }
            }
        }
    } ?: return

    Lecteur.jouer(context, suivant)
    EtatLecture.musiqueActive = suivant
    EtatLecture.estEnLecture  = true
}

fun jouerPrecedent(context: Context) {
    // Si > 3 secondes de lecture, revenir au début de la piste
    if (Lecteur.progressionActuelle() > 3000) {
        Lecteur.allerA(0)
        return
    }
    val liste     = Lecteur.listeMusiques
    val actuel    = EtatLecture.musiqueActive ?: return
    val idx       = liste.indexOfFirst { it.id == actuel.id }
    val precedent = liste.getOrNull(idx - 1) ?: liste.lastOrNull() ?: return
    Lecteur.jouer(context, precedent)
    EtatLecture.musiqueActive = precedent
    EtatLecture.estEnLecture  = true
}

// ─── CoverArt — CORRIGÉ ───────────────────────────────────────────────────────
@Composable
fun CoverArt(albumUri: Uri?, size: Int) {
    Box(
        modifier         = Modifier.size(size.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF2E2E40)),
        contentAlignment = Alignment.Center
    ) {
        if (albumUri != null) {
            AsyncImage(
                model              = albumUri,
                contentDescription = "Cover",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
                error              = painterResource(id = android.R.drawable.ic_media_play)
            )
        } else {
            Icon(Icons.Default.MusicNote, contentDescription = null, tint = TextGray, modifier = Modifier.size((size * 0.55f).dp))
        }
    }
}

// ─── Topbarre ─────────────────────────────────────────────────────────────────
@Composable
fun Topbarre(
    onSearchChange: (String) -> Unit = {},
    onTabChange: (Int) -> Unit = {},
    selectedTab: Int = 0
) {
    val tabs = listOf("Chansons", "Artistes", "Albums", "Favoris")
    var searchQuery by remember { mutableStateOf("") }
    LocalContext.current

    Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(colors = listOf(Color(0xDD0A0A0F), Color.Transparent)))) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).statusBarsPadding()) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.DensityMedium, contentDescription = "Menu", modifier = Modifier.size(28.dp), tint = TextGray)
                }
                Spacer(Modifier.width(9.dp))
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(50)).background(Color(0xFF1C1C28)).padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value         = searchQuery,
                        onValueChange = { searchQuery = it; onSearchChange(it) },
                        singleLine    = true,
                        textStyle     = TextStyle(color = Color(0xFF8A8A9A), fontSize = 16.sp),
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Search, null, tint = TextGray, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Rechercher des chansons", color = TextGray, fontSize = 14.sp)
                                }
                            }
                            inner()
                        }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                items(tabs.size) { index ->
                    val isSelected = selectedTab == index
                    Column(modifier = Modifier.clickable { onTabChange(index) }, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(tabs[index], color = if (isSelected) Color.White else TextGray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = if (isSelected) 20.sp else 16.sp)
                        if (isSelected) {
                            Spacer(Modifier.height(4.dp))
                            Box(Modifier.height(2.dp).width(tabs[index].length.dp * 6).background(Color.White, RoundedCornerShape(1.dp)))
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                // ← CORRIGÉ : lecture aléatoire fonctionnelle
                IconButton(onClick = { EtatLecture.estAleatoire = !EtatLecture.estAleatoire }) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        tint        = if (EtatLecture.estAleatoire) Color(0xFF7B61FF) else Color.White,
                        contentDescription = "Lecture aléatoire",
                        modifier    = Modifier.size(36.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text("Lecture aléatoire", style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp, color = if (EtatLecture.estAleatoire) Color(0xFF7B61FF) else Color.White))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─── Contenue ─────────────────────────────────────────────────────────────────
@Composable
fun Contenue() {
    val context   = LocalContext.current
    val musiques  = remember { getMusiqueLocale(context) }
    var recherche by remember { mutableStateOf("") }
    var onglet    by remember { mutableIntStateOf(0) }
    // ← CORRIGÉ : favoris réactifs
    var favorisIds by remember { mutableStateOf(GestionnaireFavoris.obtenirIds(context)) }

    DisposableEffect(musiques) {
        Lecteur.listeMusiques = musiques
        onDispose { }
    }

    // ← CORRIGÉ : filtrage selon onglet + recherche
    val listeFiltree = remember(recherche, onglet, favorisIds) {
        var liste = when (onglet) {
            3 -> musiques.filter { favorisIds.contains(it.id) }
            else -> musiques
        }
        if (recherche.isNotBlank()) {
            liste = liste.filter {
                it.titre.contains(recherche, ignoreCase = true) ||
                        it.artiste.contains(recherche, ignoreCase = true) ||
                        it.album.contains(recherche, ignoreCase = true)
            }
        }
        liste
    }

    Column {
        Topbarre(
            onSearchChange = { recherche = it },
            onTabChange    = { onglet = it },
            selectedTab    = onglet
        )

        if (listeFiltree.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text  = if (onglet == 3) "Aucun favori" else "Aucune musique trouvée",
                    color = TextGray, fontSize = 16.sp
                )
            }
        } else {
            when (onglet) {
                1 -> ListeArtistes(musiques = listeFiltree)
                2 -> ListeAlbums(musiques = listeFiltree)
                else -> LazyColumn(Modifier.fillMaxSize().padding(bottom = 90.dp)) {
                    items(listeFiltree) { musique ->
                        Chason(
                            musique   = musique,
                            estFavori = favorisIds.contains(musique.id),
                            onFavoriChange = {
                                GestionnaireFavoris.basculer(context, musique.id)
                                favorisIds = GestionnaireFavoris.obtenirIds(context)
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// ─── Vue Artistes ─────────────────────────────────────────────────────────────
@Composable
fun ListeArtistes(musiques: List<MusiqueLocale>) {
    val parArtiste = musiques.groupBy { it.artiste }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp).padding(bottom = 90.dp)) {
        parArtiste.forEach { (artiste, chansons) ->
            item {
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).clip(RoundedCornerShape(22.dp)).background(Color(0xFF2E2E40)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = TextGray, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(artiste, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("${chansons.size} chanson(s)", color = TextGray, fontSize = 12.sp)
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFF2E2E40))
            }
        }
    }
}

// ─── Vue Albums ───────────────────────────────────────────────────────────────
@Composable
fun ListeAlbums(musiques: List<MusiqueLocale>) {
    val parAlbum = musiques.groupBy { it.album }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp).padding(bottom = 90.dp)) {
        parAlbum.forEach { (album, chansons) ->
            item {
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    CoverArt(albumUri = chansons.first().albumUri, size = 50)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(album, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${chansons.size} chanson(s) · ${chansons.first().artiste}", color = TextGray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFF2E2E40))
            }
        }
    }
}

// ─── Chason (item de liste) ───────────────────────────────────────────────────
@Composable
fun Chason(
    musique: MusiqueLocale,
    estFavori: Boolean = false,
    onFavoriChange: () -> Unit = {}
) {
    val context = LocalContext.current
    val estCetteMusiqueActive = EtatLecture.musiqueActive?.id == musique.id
    val estEnLecture          = estCetteMusiqueActive && EtatLecture.estEnLecture

    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier              = Modifier
            .fillMaxWidth()
            .background(if (estCetteMusiqueActive) Color(0x221E90FF) else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable {
                Lecteur.jouer(context, musique)
                EtatLecture.musiqueActive = musique
                EtatLecture.estEnLecture  = true
            }
            .padding(start = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            // ← CORRIGÉ : utiliser albumUri
            CoverArt(albumUri = musique.albumUri, size = 50)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(musique.titre, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = if (estCetteMusiqueActive) Color(0xFF7B8FFF) else Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(musique.artiste, fontSize = 12.sp, color = TextGray, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    Text("  |  ", fontSize = 12.sp, color = TextGray)
                    Text(musique.album, fontSize = 12.sp, color = TextGray, maxLines = 1)
                }
            }
        }
        Row {
            // ← NOUVEAU : bouton favori
            IconButton(onClick = onFavoriChange) {
                Icon(
                    imageVector = if (estFavori) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    tint        = if (estFavori) Color(0xFFFF4081) else TextGray,
                    contentDescription = "Favori",
                    modifier    = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = {
                if (estEnLecture) { Lecteur.pause(); EtatLecture.estEnLecture = false }
                else {
                    if (estCetteMusiqueActive) Lecteur.reprendre()
                    else { Lecteur.jouer(context, musique); EtatLecture.musiqueActive = musique }
                    EtatLecture.estEnLecture = true
                }
            }) {
                Icon(
                    imageVector = if (estEnLecture) Icons.Default.Pause else Icons.Default.PlayArrow,
                    tint        = if (estCetteMusiqueActive) Color(0xFF7B8FFF) else Color.White,
                    contentDescription = "Play/Pause",
                    modifier    = Modifier.size(30.dp)
                )
            }
        }
    }
}