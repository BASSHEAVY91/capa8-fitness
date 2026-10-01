package com.capa8.fitnesspersonalapp.ui.screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.capa8.fitnesspersonalapp.data.model.VideoCategory
import com.capa8.fitnesspersonalapp.data.model.VideoItem
import com.capa8.fitnesspersonalapp.data.model.VideoSource
import com.capa8.fitnesspersonalapp.data.repository.VideoPreferencesHelper
import com.capa8.fitnesspersonalapp.data.repository.VideoRepository
import java.util.UUID

/**
 * Holds the mutable video catalogue and the live search / category state.
 *
 * Persistence strategy:
 *  – On init, the catalogue is loaded from [VideoPreferencesHelper] (SharedPreferences).
 *    If no saved data exists (first launch), it falls back to the static [VideoRepository].
 *  – Every [addVideo] and [deleteVideo] call immediately persists the updated list,
 *    so changes survive process death and app restarts.
 *
 * Source detection priority in [addVideo]:
 *  1. YouTube  (youtube.com, youtu.be)     → [VideoSource.YOUTUBE]   embed URL
 *  2. Vimeo    (vimeo.com)                 → [VideoSource.WEBVIEW]   player embed URL
 *  3. Direct stream (.mp4 / .m3u8 / etc.) → [VideoSource.DIRECT_MP4]
 *  4. Anything else                        → [VideoSource.WEBVIEW]   loaded as-is
 */
class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = VideoPreferencesHelper(application)

    private val _videos = mutableStateListOf<VideoItem>().also { list ->
        // Load persisted catalogue; fall back to built-in catalogue on first launch.
        val saved = prefs.loadVideos()
        if (saved != null) {
            list.addAll(saved)
        } else {
            list.addAll(VideoRepository.getVideos())
            // Persist the initial catalogue so future launches have a saved baseline.
            prefs.saveVideos(list.toList())
        }
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
        var source = VideoSource.WEBVIEW
        var finalUrl = url
        var thumbnailUrl = "https://picsum.photos/seed/${title.hashCode()}/640/360"

        when {
            // ── YouTube ──────────────────────────────────────────────────────
            url.contains("youtube.com/watch") || url.contains("youtu.be/") -> {
                source = VideoSource.YOUTUBE
                val id = extractYouTubeId(url)
                finalUrl = if (id != null)
                    "https://www.youtube.com/embed/$id?rel=0&modestbranding=1"
                else url
                if (id != null)
                    thumbnailUrl = "https://img.youtube.com/vi/$id/hqdefault.jpg"
            }
            url.contains("youtube.com/embed") -> {
                source = VideoSource.YOUTUBE
                finalUrl = url
                val id = Regex("embed/([a-zA-Z0-9_-]{11})").find(url)?.groupValues?.get(1)
                if (id != null)
                    thumbnailUrl = "https://img.youtube.com/vi/$id/hqdefault.jpg"
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

            // ── Local device video (content:// or file:// URI) ────────────────
            url.startsWith("content://") || url.startsWith("file://") -> {
                source = VideoSource.LOCAL
                finalUrl = url
                thumbnailUrl = "" // will be extracted from the video frame at display time
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
                thumbnailUrl = thumbnailUrl,
                videoUrl = finalUrl,
                source = source,
                category = category,
                duration = duration.trim().ifBlank { "—" },
                instructor = instructor.trim().ifBlank { "Usuario" },
                views = "Nuevo"
            )
        )

        // Persist immediately so the new video survives app restarts.
        prefs.saveVideos(_videos.toList())
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /**
     * Removes a video by [id]. Works for both catalogue and user-added videos.
     * The deletion is immediately persisted so it survives app restarts.
     */
    fun deleteVideo(id: String) {
        _videos.removeIf { it.id == id }
        // Persist immediately so the deleted video does not reappear on next launch.
        prefs.saveVideos(_videos.toList())
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
