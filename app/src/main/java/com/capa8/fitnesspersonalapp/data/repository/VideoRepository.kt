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

    // ── Sample direct-stream MP4s (Google CDN – public domain test videos) ─────
    private const val MP4_1 =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private const val MP4_2 =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private const val MP4_3 =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    private const val MP4_4 =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"
    private const val MP4_5 =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

    // ── YouTube embed base ────────────────────────────────────────────────────
    // autoplay=1 is blocked by YouTube policy inside Android WebViews even when
    // mediaPlaybackRequiresUserGesture is disabled. Omitting it lets the iFrame
    // show YouTube's native play button so the user can start playback manually.
    private fun yt(id: String) =
        "https://www.youtube.com/embed/$id?rel=0&modestbranding=1"

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

        // ──────────────── YOUTUBE ────────────────────────────────────────────
        VideoItem(
            id = "yt_1",
            title = "Yoga para Principiantes – 30 min",
            description = "Clase completa de yoga para principiantes. Mejora tu flexibilidad, " +
                    "reduce el estrés y conecta con tu respiración en 30 minutos.",
            thumbnailUrl = "https://picsum.photos/seed/yoga30/640/360",
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
            thumbnailUrl = "https://picsum.photos/seed/cardiohouse/640/360",
            videoUrl = yt("FVnwgxAdPBk"),
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
            thumbnailUrl = "https://picsum.photos/seed/chestback/640/360",
            videoUrl = yt("CBY_bM5NzAc"),
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
            thumbnailUrl = "https://picsum.photos/seed/warmup10/640/360",
            videoUrl = yt("HDcHMBqCHLU"),
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
            thumbnailUrl = "https://picsum.photos/seed/tabatahiit/640/360",
            videoUrl = yt("ml6cT4AZdqI"),
            source = VideoSource.YOUTUBE,
            category = VideoCategory.HIIT,
            duration = "22:30",
            instructor = "Sydney Cummings",
            views = "1.9 M vistas"
        ),
        VideoItem(
            id = "yt_6",
            title = "Cardio Baile – Zumba Básico",
            description = "Muévete al ritmo de la música mientras quemas calorías. " +
                    "Clase de Zumba introductoria, sin experiencia previa necesaria.",
            thumbnailUrl = "https://picsum.photos/seed/zumba1/640/360",
            videoUrl = yt("UItWltVZZmE"),
            source = VideoSource.YOUTUBE,
            category = VideoCategory.CARDIO,
            duration = "35:00",
            instructor = "Zumba Official",
            views = "7.2 M vistas"
        ),

        // ──────────────── VIMEO ──────────────────────────────────────────────
        VideoItem(
            id = "vimeo_1",
            title = "Abstract — The Art of Design",
            description = "Documental de diseño de Netflix. Vimeo public embed test.",
            thumbnailUrl = "https://picsum.photos/seed/vimeo1/640/360",
            videoUrl = "https://player.vimeo.com/video/208488746?autoplay=0&byline=0&title=0&portrait=0",
            source = VideoSource.WEBVIEW,
            category = VideoCategory.FUERZA,
            duration = "—",
            instructor = "Vimeo Test",
            views = "Público"
        ),

        // ──────────────── DIRECT MP4 ─────────────────────────────────────────
        VideoItem(
            id = "mp4_1",
            title = "Sentadillas: Técnica Perfecta",
            description = "Guía visual offline sobre la técnica correcta de la sentadilla libre. " +
                    "Ángulos de rodilla, posición de la barra y activación del core.",
            thumbnailUrl = "https://picsum.photos/seed/squat_tech/640/360",
            videoUrl = MP4_3,
            source = VideoSource.DIRECT_MP4,
            category = VideoCategory.FUERZA,
            duration = "08:15",
            instructor = "Fitness App – Técnica",
            views = "12 K vistas"
        ),
        VideoItem(
            id = "mp4_2",
            title = "Estiramientos Post-Entreno",
            description = "Secuencia de estiramientos estáticos para hacer al terminar el " +
                    "entrenamiento. Reduce el DOMS y mejora la movilidad progresivamente.",
            thumbnailUrl = "https://picsum.photos/seed/stretching/640/360",
            videoUrl = MP4_4,
            source = VideoSource.DIRECT_MP4,
            category = VideoCategory.CALENTAMIENTO,
            duration = "12:00",
            instructor = "Fitness App – Recuperación",
            views = "9 K vistas"
        ),
        VideoItem(
            id = "mp4_3",
            title = "Yoga Restaurativo – Noche",
            description = "Flujo de yoga suave para antes de dormir. Posturas de suelo que " +
                    "liberan la tensión acumulada durante el día.",
            thumbnailUrl = "https://picsum.photos/seed/yoga_night/640/360",
            videoUrl = MP4_5,
            source = VideoSource.DIRECT_MP4,
            category = VideoCategory.YOGA,
            duration = "28:40",
            instructor = "Fitness App – Bienestar",
            views = "6 K vistas"
        )
    )

    /** Returns all videos or filters by [category] when it is not [VideoCategory.ALL]. */
    fun getVideos(category: VideoCategory = VideoCategory.ALL): List<VideoItem> =
        if (category == VideoCategory.ALL) catalogue
        else catalogue.filter { it.category == category }

    /** Returns only the featured / highlighted videos for the hero section. */
    fun getFeatured(): List<VideoItem> =
        catalogue.filter { it.source == VideoSource.FEATURED }

    /** Returns videos grouped by source for a multi-section layout. */
    fun getGroupedBySource(): Map<VideoSource, List<VideoItem>> =
        catalogue.groupBy { it.source }
}
