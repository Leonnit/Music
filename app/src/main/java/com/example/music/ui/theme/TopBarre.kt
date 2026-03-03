package com.example.music.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music.EtatLecture
import com.example.music.Lecteur
import com.example.music.MusiqueLocale
import com.example.music.getMusiqueLocale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarre(
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    onSearchChange: (String) -> Unit = {},
    onTabChange: (Int) -> Unit = {},
    selectedTab: Int = 0,
    musiques: List<MusiqueLocale>
) {
    val tabs = listOf("Chansons", "Artistes", "Albums", "Favoris")
    var searchQuery by remember { mutableStateOf("") }
   val context= LocalContext.current
    val musique = musiques.random()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(
                colors = listOf(
                    Color(0xDD0A0A0F),
                    Color.Transparent)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        scope.launch { drawerState.open() }
                    }
                ) {
                    Icon(
                        imageVector =  Icons.Default.DensityMedium,
                        contentDescription = "Menu",
                        modifier = Modifier
                            .size(28.dp),
                        tint = Color.Gray
                    )
                }
                Spacer(Modifier.width(9.dp))
                Box(
                    modifier = Modifier.weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF1C1C28))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
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
                                    Icon(Icons.Default.Search, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Rechercher des chansons", color = Color.Gray, fontSize = 14.sp)
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
                    Column(
                        modifier = Modifier
                            .clickable { onTabChange(index) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tabs[index],
                            color = if (isSelected) Color.White else Color.Gray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = if (isSelected) 20.sp else 16.sp
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier.height(2.dp)
                                    .width(tabs[index].length.dp * 6)
                                    .background(Color.White, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            EtatLecture.estAleatoire = !EtatLecture.estAleatoire
                            Lecteur.jouer(context = context, musique)
                        }
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    tint = Color.Gray,
                    contentDescription = "Lecture aléatoire",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Lecture aléatoire",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}