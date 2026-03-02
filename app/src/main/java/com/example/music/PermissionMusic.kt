package com.example.music

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Permission requise selon la version Android ──────────────────────────────
fun permissionRequise(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO  // Android 13+
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE  // Android 12 et moins
    }
}

// ─── Composable principal de gestion des permissions ─────────────────────────
@Composable
fun PermissionMusique(
    onPermissionAccordee: @Composable () -> Unit
) {
    var permissionAccordee by remember { mutableStateOf(false) }
    var permissionRefusee  by remember { mutableStateOf(false) }

    // Launcher pour demander la permission
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionAccordee = isGranted
        permissionRefusee  = !isGranted
    }

    // Demande automatique au lancement
    LaunchedEffect(Unit) {
        launcher.launch(permissionRequise())
    }

    when {
        // ── Permission accordée → affiche le contenu ──────────────────────────
        permissionAccordee -> {
            onPermissionAccordee()
        }

        // ── Permission refusée → affiche un message ───────────────────────────
        permissionRefusee -> {
            EcranPermissionRefusee(
                onReessayer = {
                    permissionRefusee = false
                    launcher.launch(permissionRequise())
                }
            )
        }

        // ── En attente → affiche un écran de chargement ───────────────────────
        else -> {
            EcranAttentePermission()
        }
    }
}

// ─── Écran d'attente ──────────────────────────────────────────────────────────
@Composable
fun EcranAttentePermission() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color(0xFFFFA500),
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Chargement...",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

// ─── Écran permission refusée ─────────────────────────────────────────────────
@Composable
fun EcranPermissionRefusee(onReessayer: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color(0xFFFFA500),
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Permission requise",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "L'accès à vos fichiers audio est nécessaire pour afficher vos musiques locales.",
                color = Color(0xFF8A8A9A),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onReessayer,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA500)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Autoriser l'accès",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}