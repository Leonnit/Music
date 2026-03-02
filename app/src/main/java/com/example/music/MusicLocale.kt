package com.example.music

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore

// ─── Modèle ────────────────────────────────────────────────────────────────────
data class MusiqueLocale(
    val id: Long,
    val titre: String,
    val artiste: String,
    val album: String,
    val uri: Uri,
    val duree: Long  // en millisecondes
)

// ─── Lecture des musiques du téléphone ────────────────────────────────────────
fun getMusiqueLocale(context: Context): List<MusiqueLocale> {

    val liste = mutableListOf<MusiqueLocale>()

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION
    )

    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        "${MediaStore.Audio.Media.TITLE} ASC"
    )

    cursor?.use {
        val idCol      = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titreCol   = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artisteCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumCol   = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val dureeCol   = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        while (it.moveToNext()) {
            val id  = it.getLong(idCol)
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
            )
            liste.add(
                MusiqueLocale(
                    id      = id,
                    titre   = it.getString(titreCol) ?: "Titre inconnu",
                    artiste = it.getString(artisteCol) ?: "Artiste inconnu",
                    album   = it.getString(albumCol) ?: "Album inconnu",
                    uri     = uri,
                    duree   = it.getLong(dureeCol)
                )
            )
        }
    }

    return liste
}

// ─── Lecteur MediaPlayer ───────────────────────────────────────────────────────
// ─── Ajout à l'objet Lecteur dans MusiqueLocale.kt ──────────────────────────
// Ajoutez simplement cette ligne dans l'objet Lecteur :

object Lecteur {
    private var mediaPlayer: MediaPlayer? = null
    var musiqueEnCours: MusiqueLocale? = null

    // ← NOUVEAU : liste pour navigation suivant/précédent
    var listeMusiques: List<MusiqueLocale> = emptyList()

    fun jouer(context: Context, musique: MusiqueLocale) {
        arreter()
        mediaPlayer = MediaPlayer.create(context, musique.uri)
        mediaPlayer?.start()
        musiqueEnCours = musique
    }

    fun pause() { mediaPlayer?.pause() }
    fun reprendre() { mediaPlayer?.start() }

    fun arreter() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        musiqueEnCours = null
    }

    fun estEnLecture(): Boolean = mediaPlayer?.isPlaying ?: false
    fun progressionActuelle(): Int = mediaPlayer?.currentPosition ?: 0
    fun dureeTotale(): Int = mediaPlayer?.duration ?: 0
    fun allerA(position: Int) { mediaPlayer?.seekTo(position) }
}