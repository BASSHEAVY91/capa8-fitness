package com.bassheavy91.fitnesspersonalapp.data.model

/**
 * Represents a single video entry in the gallery.
 *
 * @param id           Unique identifier.
 * @param title        Display title.
 * @param description  Long description shown in the player sheet.
 * @param thumbnailUrl Remote URL for the thumbnail image (used via AsyncImage / WebView).
 * @param videoUrl     Playback URL.
 *                       • YOUTUBE  → YouTube embed URL  (https://www.youtube.com/embed/<ID>)
 *                       • DIRECT_MP4 → Remote MP4 / HLS stream URL
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
enum class VideoSource(val label: String, val colorHex: Long) {
    YOUTUBE("YouTube", 0xFFCC0000L),
    DIRECT_MP4("Directo", 0xFF1B7A34L),
    FEATURED("Destacado", 0xFF4A90D9L)
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
