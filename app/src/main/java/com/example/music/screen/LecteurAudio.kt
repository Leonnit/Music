package com.example.music.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.music.R
import com.example.music.localpermission.GestionnaireFavoris
import com.example.music.localpermission.Lecteur
import kotlinx.coroutines.delay

private val BgDark      = Color(0xFF080C14)
private val AccentCyan  = Color(0xFF4DD9E8)
private val TextWhite   = Color(0xFFFFFFFF)
private val TextGrayL   = Color(0xFF8A9BB0)
private val SliderTrack = Color(0xFF2A3545)
private val IconColor   = Color(0xFFCDD5E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecteurScreen(
    modifier: Modifier = Modifier,
    onRetour: () -> Unit = {}
) {
    val context = LocalContext.current
    val musique     = EtatLecture.musiqueActive
    val titreChanson = musique?.titre   ?: "Titre inconnu"
    val nomArtiste   = musique?.artiste ?: "Artiste inconnu"
    val nomAlbum     = musique?.album   ?: "< inconnu >"

    var progress by remember { mutableFloatStateOf(0f) }
    var enDrag   by remember { mutableStateOf(false) }

    LaunchedEffect(musique, EtatLecture.estEnLecture) {
        while (true) {
            if (!enDrag && EtatLecture.estEnLecture) {
                val total = Lecteur.dureeTotale()
                if (total > 0) progress = Lecteur.progressionActuelle().toFloat() / total
            }
            delay(500)
        }
    }

    fun msVersTexte(ms: Int): String {
        val s = ms / 1000
        return "%02d:%02d".format(s / 60, s % 60)
    }
    val tempsActuel = msVersTexte((progress * Lecteur.dureeTotale()).toInt())
    val tempsTotale = msVersTexte(Lecteur.dureeTotale())

    // ajoute au favori
    val ctx = LocalContext.current
    var isFavori by remember {
        mutableStateOf(
            musique?.let { GestionnaireFavoris.obtenirIds(ctx).contains(it.id) } ?: false
        )
    }
    Box(modifier = modifier.fillMaxSize().background(BgDark)) {

        Image(
            painter = painterResource(id = R.drawable.fond),
            contentDescription = "Fond",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier.fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF080C14), Color(0x55080C14), Color(0xCC080C14), Color(
                            0xFF080C14
                        )
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Retour",
                    tint = Color.Gray,
                    modifier = Modifier.size(22.dp).clickable { onRetour() }
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = titreChanson,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(32.dp))
            // ── Pochette ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF111827)),
                contentAlignment = Alignment.Center
            ) {
                // Affiche la vraie pochette si disponible
                if (musique?.albumUri != null) {
                    AsyncImage(
                        model = musique.albumUri,
                        contentDescription = "Pochette",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.fond),
                        contentDescription = "Fond",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color(0x44FFFFFF),
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Artiste + Album ─────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = nomArtiste, color = Color.Gray, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(text = nomAlbum, color = Color.Gray, fontSize = 15.sp)
            }

            Spacer(Modifier.height(25.dp))

            // ── Boutons action ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    musique?.let {
                        GestionnaireFavoris.basculer(ctx, it.id)
                        isFavori = GestionnaireFavoris.obtenirIds(ctx).contains(it.id)
                    }
                }) {
                    Icon(
                        imageVector = if (isFavori) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        tint = if (isFavori) Color(0xFFFF4D6D) else Color.Gray,
                        contentDescription = "Favori",
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.AddBox, contentDescription = "Ajouter", tint = Color.Gray, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(Icons.Default.QueueMusic, contentDescription = "Playlist", tint = Color.Gray, modifier = Modifier.size(28.dp))
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Timer, contentDescription = "Timer", tint = Color.Gray, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Slider progression ──────────────────────────────────────────
            Slider(
                value = progress,
                onValueChange = { v ->
                    enDrag = true
                    progress = v
                },
                onValueChangeFinished = {
                    Lecteur.allerA((progress * Lecteur.dureeTotale()).toInt())
                    enDrag = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Gray,
                    activeTrackColor = Color.Gray,
                    inactiveTrackColor = SliderTrack
                ),
                // ✅ Thumb minuscule = barre semble plus fine
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(12.dp)          // ← taille du rond
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),        // ← épaisseur de la barre
                        colors = SliderDefaults.colors(
                            activeTrackColor   = Color.Gray,
                            inactiveTrackColor = SliderTrack
                        )
                    )
                }
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(tempsActuel, color = TextGrayL, fontSize = 12.sp)  // ✅ Temps réel
                Text(tempsTotale, color = TextGrayL, fontSize = 12.sp)  // ✅ Durée totale
            }

            Spacer(Modifier.height(20.dp))

            // ── Contrôles ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ Aléatoire — synchronisé avec EtatLecture
                IconButton(onClick = { EtatLecture.estAleatoire = !EtatLecture.estAleatoire }) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = "Aléatoire",
                        tint = if (EtatLecture.estAleatoire) AccentCyan else IconColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // ✅ Suivant — appelle la même fonction que le MiniPlayer
                IconButton(onClick = { jouerSuivant(context) }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Suivant", tint = Color.Gray, modifier = Modifier.size(34.dp))
                }

                // ✅ Play/Pause — synchronisé avec EtatLecture
                Box(
                    modifier = Modifier.size(68.dp).clip(CircleShape)
                        .background(Color.Gray)
                        .clickable {
                            if (EtatLecture.estEnLecture) {
                                Lecteur.pause()
                                EtatLecture.estEnLecture = false
                            } else {
                                Lecteur.reprendre()
                                EtatLecture.estEnLecture = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (EtatLecture.estEnLecture) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = BgDark,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // ✅ Précédent — appelle la même fonction que le MiniPlayer
                IconButton(onClick = { jouerPrecedent(context) }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Précédent", tint = Color.Gray, modifier = Modifier.size(34.dp))
                }

                // ✅ Répétition — cycle 0 → 1 → 2 comme dans le MiniPlayer
                IconButton(onClick = { EtatLecture.modeRepetition = (EtatLecture.modeRepetition + 1) % 3 }) {
                    Icon(
                        imageVector = when (EtatLecture.modeRepetition) {
                            1 -> Icons.Default.Shuffle    // Lecture aléatoire
                            2 -> Icons.Default.RepeatOne  // Répéter 1 seul
                            else -> Icons.Default.Repeat  // Répéter tout
                        },
                        contentDescription = "Répéter",
                        tint = if (EtatLecture.modeRepetition != 0) AccentCyan else IconColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}