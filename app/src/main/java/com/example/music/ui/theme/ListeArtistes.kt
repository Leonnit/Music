package com.example.music.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music.localpermission.MusiqueLocale
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun ListeArtistes(musiques: List<MusiqueLocale>) {
    val parArtiste = musiques.groupBy { it.artiste }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp).padding(bottom = 90.dp)) {
        parArtiste.forEach { (artiste, chansons) ->
            item {
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).clip(RoundedCornerShape(22.dp)).background(Color(0xFF2E2E40)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(artiste, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("${chansons.size} chanson(s)", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFF2E2E40))
            }
        }
    }
}