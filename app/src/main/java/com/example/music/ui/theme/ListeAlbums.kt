package com.example.music.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music.CoverArt
import com.example.music.MusiqueLocale
import kotlin.collections.component1
import kotlin.collections.component2

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
                        Text("${chansons.size} chanson(s) · ${chansons.first().artiste}", color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFF2E2E40))
            }
        }
    }
}