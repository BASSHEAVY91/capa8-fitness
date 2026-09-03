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
 * Adding a video at runtime appends it to the in-memory list so the gallery
 * updates immediately without a repository write (no persistence layer yet).
 */
class VideoViewModel : ViewModel() {

    // Mutable snapshot list — Compose observes additions automatically.
    private val _videos = mutableStateListOf<VideoItem>().also {
        it.addAll(VideoRepository.getVideos())
    }

    /** Live search query typed by the user. */
    var searchQuery by mutableStateOf("")
        private set

    fun onSearchChange(query: String) {
        searchQuery = query
    }

    fun clearSearch() {
        searchQuery = ""
    }

    /**
     * Returns the filtered + searched list.
     *
     * • Category filter is applied first.
     * • Then the search query is matched against title, instructor and category label
     *   (case-insensitive, partial match).
     */
    fun filteredVideos(category: VideoCategory): List<VideoItem> {
        val byCategory =
            if (category == VideoCategory.ALL) _videos.toList()
            else _videos.filter { it.category == category }

        return if (searchQuery.isBlank()) byCategory
        else byCategory.filter { video ->
            video.title.contains(searchQuery, ignoreCase = true) ||
                    video.instructor.contains(searchQuery, ignoreCase = true) ||
                    video.category.label.contains(searchQuery, ignoreCase = true) ||
                    video.source.label.contains(searchQuery, ignoreCase = true)
        }
    }

    /** Featured videos (source == FEATURED) are always shown regardless of search. */
    fun featuredVideos(): List<VideoItem> =
        _videos.filter { it.source == VideoSource.FEATURED }

    /**
     * Adds a user-supplied video to the top of the list.
     *
     * The source type is inferred from the URL:
     *  • youtube.com / youtu.be  → [VideoSource.YOUTUBE]  (auto-wraps bare watch URLs)
     *  • anything else           → [VideoSource.DIRECT_MP4]
     */
    fun addVideo(
        title: String,
        rawUrl: String,
        category: VideoCategory,
        instructor: String,
        duration: String
    ) {
        val trimmedUrl = rawUrl.trim()
        val source: VideoSource
        val finalUrl: String

        when {
            // Accept both watch URLs and embed URLs for YouTube
            trimmedUrl.contains("youtube.com/watch") || trimmedUrl.contains("youtu.be/") -> {
                source = VideoSource.YOUTUBE
                // Convert watch URL → embed URL
                val videoId = extractYouTubeId(trimmedUrl)
                finalUrl = if (videoId != null)
                    "https://www.youtube.com/embed/$videoId?rel=0&modestbranding=1"
                else trimmedUrl
            }
            trimmedUrl.contains("youtube.com/embed") -> {
                source = VideoSource.YOUTUBE
                finalUrl = trimmedUrl
            }
            else -> {
                source = VideoSource.DIRECT_MP4
                finalUrl = trimmedUrl
            }
        }

        val newVideo = VideoItem(
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

        // Insert at the top of the list so it's immediately visible.
        _videos.add(0, newVideo)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun extractYouTubeId(url: String): String? {
        // Handles: https://www.youtube.com/watch?v=VIDEO_ID
        //          https://youtu.be/VIDEO_ID
        val watchPattern = Regex("[?&]v=([a-zA-Z0-9_-]{11})")
        val shortPattern = Regex("youtu\\.be/([a-zA-Z0-9_-]{11})")
        return watchPattern.find(url)?.groupValues?.get(1)
            ?: shortPattern.find(url)?.groupValues?.get(1)
    }
}
