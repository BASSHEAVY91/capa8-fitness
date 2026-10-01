package com.capa8.fitnesspersonalapp.data.repository

import com.capa8.fitnesspersonalapp.data.model.VideoCategory
import com.capa8.fitnesspersonalapp.data.model.VideoItem
import com.capa8.fitnesspersonalapp.data.model.VideoSource

/**
 * Static multi-source video catalogue.
 *
 * Three source types are represented:
 *  • YOUTUBE    – embedded via the YouTube iFrame API inside a WebView
 *  • DIRECT_MP4 – streamed directly with ExoPlayer (Media3)
 *  • FEATURED   – also streamed with ExoPlayer but highlighted in the UI
 *
 * Thumbnails use the Picsum Photos seed service so they always resolve
 * without requiring an API key.
 */
object VideoRepository {

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Builds the standard YouTube embed URL from a video ID. */
    private fun yt(id: String) =
        "https://www.youtube.com/embed/$id?autoplay=1&rel=0&modestbranding=1&playsinline=1"

    /** Thumbnail via YouTube's image CDN (no API key needed). */
    private fun ytThumb(id: String) = "https://img.youtube.com/vi/$id/hqdefault.jpg"

    // ── Sample direct-stream MP4s (Google CDN – public domain test videos) ─────
    private const val MP4_1 =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private const val MP4_2 =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"

    private val catalogue: List<VideoItem> = listOf(

        // ──────────────── DESTACADOS ─────────────────────────────────────────
        VideoItem(
            id = "feat_1",
            title = "Rutina Full Body – Principiantes",
            description = "Una rutina completa de cuerpo entero diseñada para quienes se inician en el fitness. " +
                    "Ejercicios básicos de empuje, jalón y pierna con descansos adecuados para maximizar la adaptación.",
            thumbnailUrl = "https://picsum.photos/seed/fullbody1/640/360",
            videoUrl = MP4_1,
            source = VideoSource.FEATURED,
            category = VideoCategory.FUERZA,
            duration = "15:30",
            instructor = "Coach Fitness App",
            views = "45 K vistas"
        ),
        VideoItem(
            id = "feat_2",
            title = "HIIT Explosivo – 20 Minutos",
            description = "Quema calorías al máximo con este circuito de alta intensidad. " +
                    "Intervalos de trabajo/descanso 40/20 seg. No se necesita equipo.",
            thumbnailUrl = "https://picsum.photos/seed/hiit20/640/360",
            videoUrl = MP4_2,
            source = VideoSource.FEATURED,
            category = VideoCategory.HIIT,
            duration = "20:00",
            instructor = "Coach Fitness App",
            views = "82 K vistas"
        ),

        // ──────────────── YOUTUBE ─────────────────────────────────────────────
        VideoItem(
            id = "yt_1",
            title = "Yoga para Principiantes – 30 min",
            description = "Clase completa de yoga para principiantes. Mejora tu flexibilidad, " +
                    "reduce el estrés y conecta con tu respiración en 30 minutos.",
            thumbnailUrl = ytThumb("v7AYKMP6rOE"),
            videoUrl = yt("v7AYKMP6rOE"),
            source = VideoSource.YOUTUBE,
            category = VideoCategory.YOGA,
            duration = "30:14",
            instructor = "Yoga With Adriene",
            views = "10.3 M vistas"
        ),
        VideoItem(
            id = "yt_2",
            title = "Cardio en Casa sin Saltar",
            description = "Rutina cardiovascular de bajo impacto ideal para espacios pequeños o " +
                    "cuando no puedes hacer ruido. Perfecta para todos los niveles.",
            thumbnailUrl = ytThumb("ml6cT4AZdqI"),
            videoUrl = yt("ml6cT4AZdqI"),
            source = VideoSource.YOUTUBE,
            category = VideoCategory.CARDIO,
            duration = "25:08",
            instructor = "MadFit",
            views = "4.7 M vistas"
        ),
        VideoItem(
            id = "yt_3",
            title = "Entrenamiento Pecho y Espalda",
            description = "Sesión de hipertrofia enfocada en pecho y espalda. Incluye variantes " +
                    "de press, jalones y remos con consejos de técnica detallados.",
            thumbnailUrl = ytThumb("gB9FpnOBKzY"),
            videoUrl = yt("gB9FpnOBKzY"),
            source = VideoSource.YOUTUBE,
            category = VideoCategory.FUERZA,
            duration = "18:45",
            instructor = "Jeff Nippard",
            views = "2.1 M vistas"
        ),
        VideoItem(
            id = "yt_4",
            title = "Calentamiento Dinámico – 10 min",
            description = "Prepara tus articulaciones y músculos antes de cualquier entrenamiento " +
                    "con este calentamiento dinámico que activa todo el cuerpo.",
            thumbnailUrl = ytThumb("HDhQMr0G_lQ"),
            videoUrl = yt("HDhQMr0G_lQ"),
            source = VideoSource.YOUTUBE,
            category = VideoCategory.CALENTAMIENTO,
            duration = "10:02",
            instructor = "Athlean-X",
            views = "3.5 M vistas"
        ),
        VideoItem(
            id = "yt_5",
            title = "HIIT Tabata – Quema Grasa Total",
            description = "Protocolo Tabata 20/10 seg de los más intensos. Ideal para quemar " +
                    "grasa en poco tiempo. Nivel intermedio-avanzado.",
            thumbnailUrl = ytThumb("TkaYafQ-XC4"),
            videoUrl = yt("TkaYafQ-XC4"),
            source = VideoSource.YOUTUBE,
            category = VideoCategory.HIIT,
            duration = "22:30",
            instructor = "Sydney Cummings",
            views = "1.9 M vistas"
        )
    )

    /** Returns all videos or filters by [category] when it is not [VideoCategory.ALL]. */
    fun getVideos(category: VideoCategory = VideoCategory.ALL): List<VideoItem> =
        if (category == VideoCategory.ALL) catalogue
        else catalogue.filter { it.category == category }

}
