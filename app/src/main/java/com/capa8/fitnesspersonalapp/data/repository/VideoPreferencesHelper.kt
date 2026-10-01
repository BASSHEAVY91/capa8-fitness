package com.capa8.fitnesspersonalapp.data.repository

import android.content.Context
import com.capa8.fitnesspersonalapp.data.model.VideoCategory
import com.capa8.fitnesspersonalapp.data.model.VideoItem
import com.capa8.fitnesspersonalapp.data.model.VideoSource
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persists the user's video catalogue (adds + deletes) in SharedPreferences
 * as a JSON array, using Android's built-in org.json — no extra dependencies.
 *
 * On first launch (no saved data) the caller should fall back to
 * [VideoRepository.getVideos].
 */
class VideoPreferencesHelper(context: Context) {

    private val prefs =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Write ─────────────────────────────────────────────────────────────────

    fun saveVideos(videos: List<VideoItem>) {
        val array = JSONArray()
        for (v in videos) {
            val obj = JSONObject().apply {
                put(KEY_ID,           v.id)
                put(KEY_TITLE,        v.title)
                put(KEY_DESCRIPTION,  v.description)
                put(KEY_THUMBNAIL,    v.thumbnailUrl)
                put(KEY_URL,          v.videoUrl)
                put(KEY_SOURCE,       v.source.name)
                put(KEY_CATEGORY,     v.category.name)
                put(KEY_DURATION,     v.duration)
                put(KEY_INSTRUCTOR,   v.instructor)
                put(KEY_VIEWS,        v.views)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_LIST, array.toString()).apply()
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Returns the persisted list, or **null** if nothing has been saved yet
     * (i.e. first-ever launch).
     */
    fun loadVideos(): List<VideoItem>? {
        val json = prefs.getString(KEY_LIST, null) ?: return null
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                VideoItem(
                    id           = o.getString(KEY_ID),
                    title        = o.getString(KEY_TITLE),
                    description  = o.getString(KEY_DESCRIPTION),
                    thumbnailUrl = o.getString(KEY_THUMBNAIL),
                    videoUrl     = o.getString(KEY_URL),
                    source       = VideoSource.valueOf(o.getString(KEY_SOURCE)),
                    category     = VideoCategory.valueOf(o.getString(KEY_CATEGORY)),
                    duration     = o.getString(KEY_DURATION),
                    instructor   = o.getString(KEY_INSTRUCTOR),
                    views        = o.optString(KEY_VIEWS, "")
                )
            }
        } catch (e: Exception) {
            // Corrupted data → return null so the caller falls back to defaults.
            null
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private companion object {
        const val PREFS_NAME   = "video_catalogue_prefs"
        const val KEY_LIST     = "videos_json"
        const val KEY_ID       = "id"
        const val KEY_TITLE    = "title"
        const val KEY_DESCRIPTION = "description"
        const val KEY_THUMBNAIL = "thumbnailUrl"
        const val KEY_URL      = "videoUrl"
        const val KEY_SOURCE   = "source"
        const val KEY_CATEGORY = "category"
        const val KEY_DURATION = "duration"
        const val KEY_INSTRUCTOR = "instructor"
        const val KEY_VIEWS    = "views"
    }
}
