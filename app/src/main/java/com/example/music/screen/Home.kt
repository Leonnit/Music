package com.example.music.screen

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.music.R
import com.example.music.localpermission.GestionnaireFavoris
import com.example.music.localpermission.Lecteur
import com.example.music.localpermission.MusiqueLocale
import com.example.music.localpermission.getMusiqueLocale
import com.example.music.ui.theme.DrowerContent
import com.example.music.ui.theme.ListeAlbums
import com.example.music.ui.theme.ListeArtistes
import com.example.music.ui.theme.TopBarre
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

object  EtatLecture {
    var musiqueActive by mutableStateOf<MusiqueLocale?>(null)
    var estEnLecture by mutableStateOf(false)
    var estAleatoire   by mutableStateOf(false)
    var modeRepetition by mutableIntStateOf(0)
}

@Composable
fun Home(navController: NavController) {

    val context = LocalContext.current
    var permissionOk by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //détecte automatiquement la version Android du téléphone pour demander la bonne permission.
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_AUDIO
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {granted -> permissionOk = granted}

    LaunchedEffect(Unit) { launcher.launch(permission)}

    DisposableEffect(Unit) {
        Lecteur.surFinDePiste = { jouerSuivant(context) }
        onDispose {  }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {DrowerContent()},
        modifier = Modifier.background(Color(0xFF05070C)),
    ) {
        Box(modifier = Modifier.fillMaxSize()){
            Image(
                painter = painterResource(id = R.drawable.fond),
                contentDescription = "Fond",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(modifier = Modifier.padding(horizontal = 0.dp)) {
                if (permissionOk) Contenue(drawerState, scope, navController)
                else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Permission requise pour lire la musique", color = Color.Gray)
                    }
                }
            }
            AnimatedVisibility(
                visible  = EtatLecture.musiqueActive != null,
                enter    = slideInVertically { it },
                exit     = slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                EtatLecture.musiqueActive?.let {
                    MiniPlayer(
                        musique = it,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun MiniPlayer(musique: MusiqueLocale, navController: NavController) {
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
        modifier = Modifier.fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xEE1C1C2E))
            .clickable(
                onClick = {navController.navigate("LecteurAudio")}
            )

    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Slider( value = progression,
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
                .height(10.dp)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            colors = SliderDefaults
                .colors(
                    thumbColor = Color(0xFF7B61FF),
                    activeTrackColor= Color(0xFF7B61FF),
                    inactiveTrackColor = Color(0xFF2E2E40)
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                CoverArt(albumUri = musique.albumUri, size = 48)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = musique.titre,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(musique.artiste, color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        EtatLecture.modeRepetition = (EtatLecture.modeRepetition + 1) % 3
                    }
                ) {
                    Icon(
                        imageVector = when (EtatLecture.modeRepetition) {
                            1 -> Icons.Default.Shuffle
                            2 -> Icons.Default.RepeatOne

                            else -> Icons.Default.Repeat
                        },
                        tint = Color(0xFF7B61FF),
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

fun jouerSuivant(context: Context) {
    val liste  = Lecteur.listeMusiques
    val actuel = EtatLecture.musiqueActive ?: return

    val suivant = when (EtatLecture.modeRepetition) {
        2 -> actuel
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
    val liste = Lecteur.listeMusiques
    val actuel = EtatLecture.musiqueActive ?: return
    val idx = liste.indexOfFirst { it.id == actuel.id }
    val precedent = liste.getOrNull(idx - 1) ?: liste.lastOrNull() ?: return
    Lecteur.jouer(context, precedent)
    EtatLecture.musiqueActive = precedent
    EtatLecture.estEnLecture  = true
}
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
            Icon(
                imageVector =  Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.Gray, modifier = Modifier.size((size * 0.55f).dp))
        }
    }
}
@Composable
fun Contenue( drawerState: DrawerState, scope: CoroutineScope, navController: NavController) {
    val context   = LocalContext.current
    val musiques  = remember { getMusiqueLocale(context) }
    var recherche by remember { mutableStateOf("") }
    var onglet    by remember { mutableIntStateOf(0) }

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
        TopBarre(
            drawerState,
            scope,
            onSearchChange = { recherche = it },
            onTabChange    = { onglet = it },
            selectedTab    = onglet,
            musiques,
        )
        if (listeFiltree.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text  = if (onglet == 3) "Aucun favori" else "Aucune musique trouvée",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
        else {
            when (onglet) {
                1 -> ListeArtistes(musiques = listeFiltree)
                2 -> ListeAlbums(musiques = listeFiltree)
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 90.dp)
                ) {
                    items(listeFiltree) { musique ->
                        Chason(
                            musique   = musique,
                            estFavori = favorisIds.contains(musique.id),
                            onFavoriChange = {
                                GestionnaireFavoris.basculer(context, musique.id)
                                favorisIds = GestionnaireFavoris.obtenirIds(context)
                            },
                            navController = navController
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun Chason(
    musique: MusiqueLocale,
    estFavori: Boolean = false,
    onFavoriChange: () -> Unit = {},
    navController: NavController
) {
    val context = LocalContext.current
    val estCetteMusiqueActive = EtatLecture.musiqueActive?.id == musique.id
    val estEnLecture          = estCetteMusiqueActive && EtatLecture.estEnLecture

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(if (estCetteMusiqueActive) Color(0x221E90FF) else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable {
                Lecteur.jouer(context, musique)
                EtatLecture.musiqueActive = musique
                EtatLecture.estEnLecture  = true
                navController.navigate("LecteurAudio")
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
                    Text(musique.artiste, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    Text("  |  ", fontSize = 12.sp, color = Color.Gray)
                    Text(musique.album, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                }
            }
        }
        Row {
            // ← NOUVEAU : bouton favori
            IconButton(onClick = onFavoriChange) {
                Icon(
                    imageVector = if (estFavori) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    tint        = if (estFavori) Color(0xFFFF4081) else Color.Gray,
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