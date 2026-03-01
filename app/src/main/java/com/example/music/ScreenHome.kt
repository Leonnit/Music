package com.example.music

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ─── Couleurs ──────────────────────────────────────────────────────────────────
private val BgDark     = Color(0xFF0A0A0F)
private val BgCard     = Color(0xFF13131A)
private val Gold       = Color(0xFFFFA500)
private val GoldLight  = Color(0xFFFFD580)
private val TextWhite  = Color(0xFFFFFFFF)
val TextGray   = Color(0xFF8A8A9A)
private val DivColor   = Color(0xFF1E1E2A)
private val BottomBg   = Color(0xFF0F0F18)

// ─── Modèle ────────────────────────────────────────────────────────────────────
data class Chanson(
    val id: Int,
    val titre: String,
    val artiste: String,
    val album: String,
    val coverUrl: String = ""
)

val sampleChansons = List(9) { i ->
    Chanson(
        id = i,
        titre = "G_I_M_S_2025_Video_officiele......",
        artiste = "Artiste inconnu",
        album = "Album inconnu",
        coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200"
    )
}

// ─── Écran Accueil ────────────────────────────────────────────────────────────
@Composable
fun ScreenHome(
) {
    val tabs = listOf("Chasons", "Artistes", "Albums", "Favories")
    var selectedTab by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── TopBar ────────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDark)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Menu burger
                    IconButton(onClick ={/*TODO*/}) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = TextWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    // Barre de recherche
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF1C1C28))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Recherches des chasons", color = TextGray, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Onglets
                LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    items(tabs.size) { index ->
                        val isSelected = selectedTab == index
                        Column(
                            modifier = Modifier.clickable { selectedTab = index },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = tabs[index],
                                color = if (isSelected) Gold else TextGray,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 15.sp
                            )
                            if (isSelected) {
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .height(2.dp)
                                        .width(tabs[index].length.dp * 6)
                                        .background(Gold, RoundedCornerShape(1.dp))
                                )
                            }
                        }
                    }
                }
            }

            // ── Lecture aléatoire ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clickable {},
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1C1C28)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Lecture aleatoire", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }

            HorizontalDivider(color = DivColor, thickness = 0.5.dp)

            // ── Liste chansons ────────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 140.dp)
            ) {
                items(sampleChansons) { chanson ->
                    ChansonItem(chanson = chanson, onClick = {  /*TODO*/ })
                    HorizontalDivider(color = DivColor, thickness = 0.5.dp, modifier = Modifier.padding(start = 76.dp))
                }
            }
        }

        // ── Mini lecteur bas ──────────────────────────────────────────────────
        MiniLecteur(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─── Item Chanson ─────────────────────────────────────────────────────────────
@Composable
fun ChansonItem(chanson: Chanson, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Couverture
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Gold)
        ) {
            AsyncImage(
                model = chanson.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.width(12.dp))

        // Infos
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chanson.titre,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Row {
                Text(chanson.artiste, color = TextGray, fontSize = 12.sp)
                Text("  |  ", color = TextGray, fontSize = 12.sp)
                Text(chanson.album, color = TextGray, fontSize = 12.sp)
            }
        }

        // Menu 3 points
        Icon(
            Icons.Default.MoreVert,
            contentDescription = "Options",
            tint = TextGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─── Mini Lecteur ─────────────────────────────────────────────────────────────
@Composable
fun MiniLecteur(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgDark)
    ) {
        // Lecteur
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF1A1A28), Color(0xFF0F0F1A)))
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cover
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Gold)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "G_I_M_S_2025_Video_officiele......",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Artiste inconue", color = TextGray, fontSize = 11.sp)
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = TextWhite, modifier = Modifier.size(28.dp))
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.FastForward, contentDescription = "Next", tint = TextWhite, modifier = Modifier.size(26.dp))
            }
        }

        // BottomNav
        BottomNavBar()
    }
}

// ─── BottomNavBar ─────────────────────────────────────────────────────────────
@Composable
fun BottomNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BottomBg)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomNavItem(icon = Icons.Default.MusicNote, label = "Musique", selected = true)
        BottomNavItem(icon = Icons.Default.PlayCircle, label = "Regarder", selected = false)
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {}
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Gold else TextGray,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = if (selected) Gold else TextGray,
            fontSize = 11.sp
        )
    }
}