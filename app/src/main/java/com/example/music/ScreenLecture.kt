package com.example.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ─── Couleurs ──────────────────────────────────────────────────────────────────
private val LBgDark    = Color(0xFF080810)
private val LBgMid     = Color(0xFF0D0D1A)
private val LGold      = Color(0xFFFFA500)
private val LTextWhite = Color(0xFFFFFFFF)
private val LTextGray  = Color(0xFF8A8A9A)
private val LSlider    = Color(0xFF2A2A3A)

// ─── Écran Lecteur ────────────────────────────────────────────────────────────
@Composable
fun LecteurScreen(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    chanson: Chanson = sampleChansons.first()
) {
    var progress by remember { mutableStateOf(0.01f) }
    var isPlaying by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LBgDark)
    ) {

        // ── Image de fond (pochette pleine) ────────────────────────────────────
        AsyncImage(
            model = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ── Overlay dégradé sombre ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC080810),
                            Color(0x44080810),
                            Color(0x00080810),
                            Color(0xCC080810),
                            Color(0xFF080810)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            // ── TopBar ────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = LTextWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Abraham_Schreiber@nowhere.com.........",
                    color = LTextWhite,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Espace pour l'image ────────────────────────────────────────────
            Spacer(Modifier.weight(1f))

            // ── Contrôles bas ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Barre de progression
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = LGold,
                            activeTrackColor = LGold,
                            inactiveTrackColor = LSlider
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("00:03", color = LTextGray, fontSize = 12.sp)
                        Text("04:25", color = LTextGray, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Boutons de contrôle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Précédent
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.FastRewind,
                            contentDescription = "Précédent",
                            tint = LTextWhite,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // Play / Pause
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(LTextWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { isPlaying = !isPlaying }) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = LBgDark,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Suivant
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.FastForward,
                            contentDescription = "Suivant",
                            tint = LTextWhite,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}