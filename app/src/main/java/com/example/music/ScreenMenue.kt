package com.example.music
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Couleurs ──────────────────────────────────────────────────────────────────
private val MBgDark    = Color(0xFF0A0A0F)
private val MBgMenu    = Color(0xFF111118)
private val MOverlay   = Color(0xAA000000)
private val MGold      = Color(0xFFFFA500)
private val MTextWhite = Color(0xFFFFFFFF)
private val MTextGray  = Color(0xFF9A9AB0)
private val MDivider   = Color(0xFF1E1E2A)

// ─── Items du menu ────────────────────────────────────────────────────────────
data class MenuItem(
    val label: String,
    val icon: ImageVector
)

val menuItems = listOf(
    MenuItem("Parametre",                  Icons.Default.Settings),
    MenuItem("FAQ et Commentaire",         Icons.Default.HelpOutline),
    MenuItem("Accord Utilisateur",         Icons.Default.Description),
    MenuItem("Politique de confidentialite", Icons.Default.Lock),
    MenuItem("Metre a jour automatiquement", Icons.Default.SystemUpdate),
    MenuItem("Styles joueur",              Icons.Default.Palette),
    MenuItem("Widget de l'Ecrant d'accueil", Icons.Default.Widgets),
)

// ─── Écran Menu (Drawer) ──────────────────────────────────────────────────────
@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    onSeConnecter: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // ── Fond sombre semi-transparent (cliquable pour fermer) ───────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MOverlay)
                .clickable { onClose() }
        )

        // ── Panneau menu ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.82f)
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF13131E), Color(0xFF0A0A12))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {

                // ── Header Se connecter ────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeConnecter() }
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1E2A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = MTextGray,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Text(
                        text = "Se connecte",
                        color = MTextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                HorizontalDivider(color = MDivider, thickness = 0.5.dp)
                Spacer(Modifier.height(8.dp))

                // ── Liste des items ────────────────────────────────────────────
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(menuItems) { item ->
                        MenuItemRow(item = item)
                    }
                }

                Spacer(Modifier.weight(1f))

                // ── Version ───────────────────────────────────────────────────
                Text(
                    text = "Version 1.0.0",
                    color = Color(0xFF444455),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
        }
    }
}

// ─── Item Menu ────────────────────────────────────────────────────────────────
@Composable
fun MenuItemRow(item: MenuItem) {
    var pressed by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { pressed = !pressed }
            .background(if (pressed) Color(0xFF1A1A28) else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = MTextGray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = item.label,
            color = MTextWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

// ─── Écran avec Menu intégré ──────────────────────────────────────────────────
@Composable
fun AccueilAvecMenu() {
    var menuOuvert by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Page principale
        ScreenHome(
            onMenuClick = { menuOuvert = true }
        )

        // Menu par-dessus
        if (menuOuvert) {
            MenuScreen(
                onClose = { menuOuvert = false }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MenuScreenPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0F))) {
            MenuScreen()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AccueilAvecMenuPreview() {
    MaterialTheme { AccueilAvecMenu() }
}