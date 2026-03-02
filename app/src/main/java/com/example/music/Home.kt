package com.example.music

import android.content.Context
import android.net.Uri
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.net.toUri

// ─── Couleurs ──────────────────────────────────────────────────────────────────

// ─── État global de lecture partagé ──────────────────────────────────────────
object EtatLecture {
    var musiqueActive by mutableStateOf<MusiqueLocale?>(null)
    var estEnLecture  by mutableStateOf(false)
}

// ─── Home ─────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = Color.Transparent,
        topBar = { Topbarre() }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = R.drawable.fond),
                contentDescription = "Fond d'application",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(modifier = Modifier.padding(innerPadding)) {
                Contenue()
            }

            // MiniPlayer flottant en bas
            AnimatedVisibility(
                visible  = EtatLecture.musiqueActive != null,
                enter    = slideInVertically { it },
                exit     = slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                EtatLecture.musiqueActive?.let { musique ->
                    MiniPlayer(musique = musique)
                }
            }
        }
    }
}

// ─── MiniPlayer ───────────────────────────────────────────────────────────────
@Composable
fun MiniPlayer(musique: MusiqueLocale) {
    val context    = LocalContext.current
    var progression by remember { mutableStateOf(0f) }

    // Mise à jour de la progression toutes les 500ms
    LaunchedEffect(musique, EtatLecture.estEnLecture) {
        while (true) {
            if (EtatLecture.estEnLecture) {
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
        // Barre de progression
        LinearProgressIndicator(
            progress  = { progression },
            modifier  = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            color      = Color(0xFF7B61FF),
            trackColor = Color(0xFF2E2E40),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Cover + Infos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.weight(1f)
            ) {
                CoverArt(uri = musique.uri, size = 48)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = musique.titre,
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(
                        text     = musique.artiste,
                        color    = TextGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Contrôles
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { jouerPrecedent(context) }) {
                    Icon(
                        imageVector        = Icons.Default.SkipPrevious,
                        contentDescription = "Précédent",
                        tint               = Color.White,
                        modifier           = Modifier.size(26.dp)
                    )
                }
                IconButton(onClick = {
                    if (EtatLecture.estEnLecture) {
                        Lecteur.pause()
                        EtatLecture.estEnLecture = false
                    } else {
                        Lecteur.reprendre()
                        EtatLecture.estEnLecture = true
                    }
                }) {
                    Icon(
                        imageVector        = if (EtatLecture.estEnLecture) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint               = Color.White,
                        modifier           = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = { jouerSuivant(context) }) {
                    Icon(
                        imageVector        = Icons.Default.SkipNext,
                        contentDescription = "Suivant",
                        tint               = Color.White,
                        modifier           = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

// ─── Navigation Précédent / Suivant ──────────────────────────────────────────
fun jouerSuivant(context: Context) {
    val liste   = Lecteur.listeMusiques
    val actuel  = EtatLecture.musiqueActive ?: return
    val idx     = liste.indexOfFirst { it.id == actuel.id }
    val suivant = liste.getOrNull(idx + 1) ?: liste.firstOrNull() ?: return
    Lecteur.jouer(context, suivant)
    EtatLecture.musiqueActive = suivant
    EtatLecture.estEnLecture  = true
}

fun jouerPrecedent(context: Context) {
    val liste     = Lecteur.listeMusiques
    val actuel    = EtatLecture.musiqueActive ?: return
    val idx       = liste.indexOfFirst { it.id == actuel.id }
    val precedent = liste.getOrNull(idx - 1) ?: liste.lastOrNull() ?: return
    Lecteur.jouer(context, precedent)
    EtatLecture.musiqueActive = precedent
    EtatLecture.estEnLecture  = true
}

// ─── CoverArt — artwork de l'album ou icône par défaut ───────────────────────
@Composable
fun CoverArt(uri: Uri, size: Int) {
    val coverUri = remember(uri) {
        try {
            // L'albumart est accessible via content://media/external/audio/albumart/<id>
            val segments = uri.pathSegments
            val id = segments.lastOrNull()
            if (id != null) "content://media/external/audio/albumart/$id".toUri() else null
        } catch (e: Exception) { null }
    }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF2E2E40)),
        contentAlignment  = Alignment.Center
    ) {
        if (coverUri != null) {
            AsyncImage(
                model              = coverUri,
                contentDescription = "Cover",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
                error              = painterResource(id = android.R.drawable.ic_media_play)
            )
        } else {
            Icon(
                imageVector        = Icons.Default.MusicNote,
                contentDescription = null,
                tint               = TextGray,
                modifier           = Modifier.size((size * 0.55f).dp)
            )
        }
    }
}

// ─── Topbarre ─────────────────────────────────────────────────────────────────
@Composable
fun Topbarre() {
    val tabs         = listOf("Chansons", "Artistes", "Albums", "Favoris")
    var searchQuery  by remember { mutableStateOf("") }
    var selectedTab  by remember { mutableIntStateOf(0) }

    Box( modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(colors = listOf(Color(0xDD0A0A0F), Color.Transparent)))) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .statusBarsPadding()
                .background(Color.Transparent)
        ) {
            // Ligne Search
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector        = Icons.Default.DensityMedium,
                        contentDescription = "Menu",
                        modifier           = Modifier.size(28.dp),
                        tint               = TextGray
                    )
                }
                Spacer(modifier = Modifier.width(9.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF1C1C28))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value         = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine    = true,
                        textStyle     = TextStyle(color = Color(0xFF8A8A9A), fontSize = 16.sp),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint     = TextGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Rechercher des chansons", color = TextGray, fontSize = 14.sp)
                                }
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Onglets
            LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                items(tabs.size) { index ->
                    val isSelected = selectedTab == index
                    Column(
                        modifier             = Modifier.clickable { selectedTab = index },
                        horizontalAlignment  = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text       = tabs[index],
                            color      = if (isSelected) Color.White else TextGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize   = if (isSelected) 20.sp else 16.sp
                        )
                        if (isSelected) {
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(tabs[index].length.dp * 6)
                                    .background(Color.White, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lecture aléatoire
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO : lecture aléatoire */ }) {
                    Icon(
                        imageVector        = Icons.Default.PlayCircle,
                        tint               = Color.White,
                        contentDescription = "Lecture aléatoire",
                        modifier           = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text  = "Lecture aléatoire",
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp, color = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ─── Contenue ─────────────────────────────────────────────────────────────────
@Composable
fun Contenue() {
    val context  = LocalContext.current
    val musiques = remember { getMusiqueLocale(context) }

    // Stocker la liste dans le Lecteur pour navigation suivant/précédent
    DisposableEffect(musiques) {
        Lecteur.listeMusiques = musiques
        onDispose { }
    }

    if (musiques.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Aucune musique trouvée", color = TextGray, fontSize = 16.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp) // espace pour le MiniPlayer
        ) {
            items(musiques) { musique ->
                Chason(musique = musique)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ─── Chason (item de liste) ───────────────────────────────────────────────────
@Composable
fun Chason(musique: MusiqueLocale) {
    val context = LocalContext.current

    // État dérivé de l'état global — pas de state local bugué
    val estCetteMusiqueActive = EtatLecture.musiqueActive?.id == musique.id
    val estEnLecture          = estCetteMusiqueActive && EtatLecture.estEnLecture

    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier              = Modifier
            .fillMaxWidth()
            .background(
                if (estCetteMusiqueActive) Color(0x221E90FF) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable {
                Lecteur.jouer(context, musique)
                EtatLecture.musiqueActive = musique
                EtatLecture.estEnLecture  = true
            }
            .padding(start = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {

            // Cover de l'album
            CoverArt(uri = musique.uri, size = 50)

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = musique.titre,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 15.sp,
                    color      = if (estCetteMusiqueActive) Color(0xFF7B8FFF) else Color.White,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text     = musique.artiste,
                        fontSize = 12.sp,
                        color    = TextGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(text = "  |  ", fontSize = 12.sp, color = TextGray)
                    Text(
                        text     = musique.album,
                        fontSize = 12.sp,
                        color    = TextGray,
                        maxLines = 1
                    )
                }
            }
        }

        Row {
            IconButton(onClick = {
                if (estEnLecture) {
                    Lecteur.pause()
                    EtatLecture.estEnLecture = false
                } else {
                    if (estCetteMusiqueActive) {
                        Lecteur.reprendre()
                    } else {
                        Lecteur.jouer(context, musique)
                        EtatLecture.musiqueActive = musique
                    }
                    EtatLecture.estEnLecture = true
                }
            }) {
                Icon(
                    imageVector        = if (estEnLecture) Icons.Default.Pause else Icons.Default.PlayArrow,
                    tint               = if (estCetteMusiqueActive) Color(0xFF7B8FFF) else Color.White,
                    contentDescription = "Play/Pause",
                    modifier           = Modifier.size(30.dp)
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector        = Icons.Default.MoreVert,
                    tint               = Color.White,
                    contentDescription = "Options",
                    modifier           = Modifier.size(30.dp)
                )
            }
        }
    }
}