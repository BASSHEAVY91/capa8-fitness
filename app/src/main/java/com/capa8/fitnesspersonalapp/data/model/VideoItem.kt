package com.capa8.fitnesspersonalapp.data.model

/**
 * Represents a single video entry in the gallery.
 *
 * @param id           Unique identifier. User-added videos have IDs prefixed with "user_".
 * @param title        Display title.
 * @param description  Long description shown in the player sheet.
 * @param thumbnailUrl Remote URL for the thumbnail image.
 * @param videoUrl     Playback URL.
 *                       • YOUTUBE    → YouTube embed URL  (https://www.youtube.com/embed/<ID>)
 *                       • DIRECT_MP4 → Remote MP4 / HLS stream URL
 *                       • WEBVIEW    → Any web embed URL (Vimeo player, etc.)
 * @param source       Identifies the origin so the player knows how to render it.
 * @param category     Used for the filter tabs on the gallery screen.
 * @param duration     Human-readable length string, e.g. "12:45".
 * @param instructor   Name of the coach / channel.
 * @param views        Human-readable view count, e.g. "1.4M vistas".
 */
data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val source: VideoSource,
    val category: VideoCategory,
    val duration: String,
    val instructor: String,
    val views: String = ""
)

/** Origin of the video — drives which renderer is used in the inline player. */
enum class VideoSource(val label: String) {
    YOUTUBE("YouTube"),
    DIRECT_MP4("Directo"),
    FEATURED("Destacado"),
    /** Vimeo, Instagram embeds, or any other web-based embed URL loaded in a WebView. */
    WEBVIEW("Web Embed"),
    /** Video stored on the device (content:// or file:// URI). Played with ExoPlayer. */
    LOCAL("Local")
}

/** Categories for the horizontal filter tab row. */
enum class VideoCategory(val label: String) {
    ALL("Todos"),
    CARDIO("Cardio"),
    FUERZA("Fuerza"),
    YOGA("Yoga"),
    HIIT("HIIT"),
    CALENTAMIENTO("Calentamiento")
}
