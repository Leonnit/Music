package com.example.music.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ItemMenu(
    val icone: ImageVector,
    val labele : String
)

val listeMenu = listOf(
    ItemMenu(Icons.Default.Settings,     "Parametre"),
    ItemMenu(Icons.Default.PriorityHigh, "FAQ et Commentaire"),
    ItemMenu(Icons.Default.Description,  "Accord Utilisateur"),
    ItemMenu(Icons.Default.Lock,         "Politique de confidentialite"),
    ItemMenu(Icons.Default.SystemUpdate, "Metre a jour automatiquement"),
    ItemMenu(Icons.Default.Palette,      "Styles joueur"),
    ItemMenu(Icons.Default.Widgets,      "Widget de l'Ecrant d'accueil"),
)

@Composable
fun DrowerContent() {
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF0A0A0F), // ← fond du drawer
        drawerContentColor = Color.White,          // ← couleur du contenu
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .width(280.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 20.dp, bottom = 30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonPin,
                    contentDescription = "Photo profile",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(50.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Se connecter",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            listeMenu.forEach { item ->
                Menu(item = item)
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun Menu(item: ItemMenu) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        Icon(
            imageVector = item.icone,
            tint = Color.Gray,
            contentDescription = "Icone ${item.labele}",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.labele,
            color = Color.Gray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
