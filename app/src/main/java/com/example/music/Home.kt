package com.example.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fond),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Topbarre()
    }
}

@Composable
fun Topbarre() {
    val tabs = listOf("Chasons", "Artistes", "Albums", "Favories")
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).statusBarsPadding()) {
        Row(modifier = Modifier.fillMaxWidth(),  verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = {/*TODO*/}) {
                Icon(
                    imageVector = Icons.Default.Menu, contentDescription = "Menu",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFF8A8A9A),

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
                    value = searchQuery,
                    onValueChange = { searchQuery = it},
                    singleLine = true,
                    textStyle = TextStyle(color = Color(0xFF8A8A9A), fontSize = 16.sp),
                    decorationBox = {innerTextField ->
                        if (searchQuery.isEmpty()){
                            Row(verticalAlignment = Alignment.CenterVertically)  {
                                Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Recherches des chansons", color = Color(0xFF8A8A9A), fontSize = 16.sp)
                            }
                        }
                        innerTextField()
                    },
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            items(tabs.size) { index ->
                val isSelected = selectedTab == index
                Column(
                    modifier = Modifier.clickable { selectedTab = index },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tabs[index],
                        color = Color(0xFF8A8A9A),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = if (isSelected) 20.sp else 16.sp
                    )
                    if (isSelected) {
                        Spacer(Modifier.height(4.dp))
                        Box( modifier = Modifier.height(2.dp).width(tabs[index].length.dp * 6).background(Color(0xFF8A8A9A), RoundedCornerShape(1.dp))
                        )
                    }
                }
            }
        }
    }
}