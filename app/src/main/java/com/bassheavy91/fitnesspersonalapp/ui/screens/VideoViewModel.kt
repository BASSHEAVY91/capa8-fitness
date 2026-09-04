package com.bassheavy91.fitnesspersonalapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.bassheavy91.fitnesspersonalapp.data.model.VideoCategory
import com.bassheavy91.fitnesspersonalapp.data.model.VideoItem
import com.bassheavy91.fitnesspersonalapp.data.model.VideoSource
import com.bassheavy91.fitnesspersonalapp.data.repository.VideoRepository
import java.util.UUID

/**
 * Holds the mutable video catalogue and the live search / category state.
 *
 * Source detection priority in [addVideo]:
 *  1. YouTube  (youtube.com, youtu.be)     → [VideoSource.YOUTUBE]   embed URL
 *  2. Vimeo    (vimeo.com)                 → [VideoSource.WEBVIEW]   player embed URL
 *  3. Direct stream (.mp4 / .m3u8 / etc.) → [VideoSource.DIRECT_MP4]
 *  4. Anything else                        → [VideoSource.WEBVIEW]   loaded as-is
 */
class VideoViewModel : ViewModel() {

    private val _videos = mutableStateListOf<VideoItem>().also {
        it.addAll(VideoRepository.getVideos())
    }

    var searchQuery by mutableStateOf("")
        private set

    fun onSearchChange(query: String) { searchQuery = query }
    fun clearSearch() { searchQuery = "" }

    fun filteredVideos(category: VideoCategory): List<VideoItem> {
        val byCategory = if (category == VideoCategory.ALL) _videos.toList()
                         else _videos.filter { it.category == category }
        return if (searchQuery.isBlank()) byCategory
        else byCategory.filter { video ->
            video.title.contains(searchQuery, ignoreCase = true) ||
            video.instructor.contains(searchQuery, ignoreCase = true) ||
            video.category.label.contains(searchQuery, ignoreCase = true) ||
            video.source.label.contains(searchQuery, ignoreCase = true)
        }
    }

    fun featuredVideos(): List<VideoItem> =
        _videos.filter { it.source == VideoSource.FEATURED }

    // ── Add ───────────────────────────────────────────────────────────────────

    fun addVideo(
        title: String,
        rawUrl: String,
        category: VideoCategory,
        instructor: String,
        duration: String
    ) {
        val url = rawUrl.trim()
        val source: VideoSource
        val finalUrl: String

        when {
            // ── YouTube ──────────────────────────────────────────────────────
            url.contains("youtube.com/watch") || url.contains("youtu.be/") -> {
                source = VideoSource.YOUTUBE
                val id = extractYouTubeId(url)
                finalUrl = if (id != null)
                    "https://www.youtube.com/embed/$id?rel=0&modestbranding=1"
                else url
            }
            url.contains("youtube.com/embed") -> {
                source = VideoSource.YOUTUBE
                finalUrl = url
            }

            // ── Vimeo ────────────────────────────────────────────────────────
            url.contains("vimeo.com") && !url.contains("player.vimeo.com") -> {
                source = VideoSource.WEBVIEW
                val id = extractVimeoId(url)
                finalUrl = if (id != null)
                    "https://player.vimeo.com/video/$id?autoplay=1&byline=0&title=0&portrait=0"
                else url
            }
            url.contains("player.vimeo.com") -> {
                source = VideoSource.WEBVIEW
                finalUrl = url
            }

            // ── Direct video stream (.mp4 / .m3u8 / .webm / .ogg) ───────────
            url.matches(Regex(".*\\.(mp4|m3u8|webm|ogg|mov|avi)(\\?.*)?$", RegexOption.IGNORE_CASE)) -> {
                source = VideoSource.DIRECT_MP4
                finalUrl = url
            }

            // ── Fallback: any other URL → WebView embed ───────────────────────
            else -> {
                source = VideoSource.WEBVIEW
                finalUrl = url
            }
        }

        _videos.add(
            0, VideoItem(
                id = "user_${UUID.randomUUID()}",
                title = title.trim(),
                description = "Video agregado por el usuario.",
                thumbnailUrl = "https://picsum.photos/seed/${title.hashCode()}/640/360",
                videoUrl = finalUrl,
                source = source,
                category = category,
                duration = duration.trim().ifBlank { "—" },
                instructor = instructor.trim().ifBlank { "Usuario" },
                views = "Nuevo"
            )
        )
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /**
     * Removes a video by [id].
     * Only user-added videos (id starts with "user_") can be deleted;
     * catalogue videos are silently ignored.
     */
    fun deleteVideo(id: String) {
        if (id.startsWith("user_")) {
            _videos.removeIf { it.id == id }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun extractYouTubeId(url: String): String? {
        val watch = Regex("[?&]v=([a-zA-Z0-9_-]{11})")
        val short = Regex("youtu\\.be/([a-zA-Z0-9_-]{11})")
        return watch.find(url)?.groupValues?.get(1)
            ?: short.find(url)?.groupValues?.get(1)
    }

    private fun extractVimeoId(url: String): String? {
        // Handles:
        //   https://vimeo.com/123456789
        //   https://vimeo.com/channels/staffpicks/123456789
        //   https://vimeo.com/groups/foo/videos/123456789
        val pattern = Regex("vimeo\\.com(?:/[^/]+)*/([0-9]+)")
        return pattern.find(url)?.groupValues?.get(1)
    }
}
