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
    val albumUri: Uri?,   // ← CORRIGÉ : URI pochette dédiée
    val duree: Long
)

// ─── Lecture des musiques ──────────────────────────────────────────────────────
fun getMusiqueLocale(context: Context): List<MusiqueLocale> {
    val liste = mutableListOf<MusiqueLocale>()

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,  // ← CORRIGÉ
        MediaStore.Audio.Media.DURATION
    )

    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        "${MediaStore.Audio.Media.IS_MUSIC} != 0",  // ← exclure sons système
        null,
        "${MediaStore.Audio.Media.TITLE} ASC"
    )

    cursor?.use {
        val idCol      = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titreCol   = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artisteCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumCol   = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val albumIdCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val dureeCol   = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        while (it.moveToNext()) {
            val id      = it.getLong(idCol)
            val albumId = it.getLong(albumIdCol)
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
            )
            // ← CORRIGÉ : pochette basée sur ALBUM_ID réel
            val albumUri = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"), albumId
            )
            liste.add(
                MusiqueLocale(
                    id       = id,
                    titre    = it.getString(titreCol) ?: "Titre inconnu",
                    artiste  = it.getString(artisteCol) ?: "Artiste inconnu",
                    album    = it.getString(albumCol) ?: "Album inconnu",
                    uri      = uri,
                    albumUri = albumUri,
                    duree    = it.getLong(dureeCol)
                )
            )
        }
    }
    return liste
}

// ─── Gestionnaire des Favoris (SharedPreferences) ─────────────────────────────
object GestionnaireFavoris {
    private const val PREFS_NAME = "favoris_prefs"
    private const val KEY_FAVORIS = "favoris_ids"

    fun obtenirIds(context: Context): Set<Long> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_FAVORIS, emptySet())
            ?.mapNotNull { it.toLongOrNull() }?.toSet() ?: emptySet()
    }

    fun basculer(context: Context, id: Long): Boolean {
        val prefs   = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val actuels = prefs.getStringSet(KEY_FAVORIS, emptySet())?.toMutableSet() ?: mutableSetOf()
        val etait   = actuels.contains(id.toString())
        if (etait) actuels.remove(id.toString()) else actuels.add(id.toString())
        prefs.edit().putStringSet(KEY_FAVORIS, actuels).apply()
        return !etait
    }

    fun estFavori(context: Context, id: Long): Boolean =
        obtenirIds(context).contains(id)
}

// ─── Lecteur MediaPlayer ───────────────────────────────────────────────────────
object Lecteur {
    private var mediaPlayer: MediaPlayer? = null
    var musiqueEnCours: MusiqueLocale? = null
    var listeMusiques: List<MusiqueLocale> = emptyList()

    // ← NOUVEAU : callback fin de piste → passage automatique au suivant
    var surFinDePiste: (() -> Unit)? = null

    fun jouer(context: Context, musique: MusiqueLocale) {
        arreter()
        mediaPlayer = MediaPlayer.create(context, musique.uri)?.apply {
            setOnCompletionListener { surFinDePiste?.invoke() }  // ← CORRIGÉ
            start()
        }
        musiqueEnCours = musique
    }

    fun pause()     { mediaPlayer?.pause() }
    fun reprendre() { mediaPlayer?.start() }

    fun arreter() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        musiqueEnCours = null
    }

    // ← NOUVEAU : appeler dans onDestroy() pour éviter fuites mémoire
    fun liberer() {
        arreter()
        surFinDePiste = null
    }

    fun estEnLecture(): Boolean = mediaPlayer?.isPlaying ?: false
    fun progressionActuelle(): Int = mediaPlayer?.currentPosition ?: 0
    fun dureeTotale(): Int = mediaPlayer?.duration ?: 0
    fun allerA(position: Int) { mediaPlayer?.seekTo(position) }
}