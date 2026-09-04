package com.bassheavy91.fitnesspersonalapp.ui.components.video

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import com.bassheavy91.fitnesspersonalapp.data.model.VideoItem
import com.bassheavy91.fitnesspersonalapp.data.model.VideoSource
import com.bassheavy91.fitnesspersonalapp.ui.theme.ContentTitleColor
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessBlue

/**
 * Card shown in the video gallery grid/list.
 *
 * Displays a thumbnail (loaded via WebView img tag to avoid adding
 * a Coil/Glide dependency), an overlay play button, a source badge,
 * the video title, instructor name and duration.
 *
 * Tapping the card invokes [onClick] — the parent composable is
 * responsible for showing the inline player bottom sheet.
 */
@Composable
fun VideoGalleryCard(
    video: VideoItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // ── Thumbnail ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color(0xFF1A1A2E))
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Load thumbnail image via a tiny WebView so no extra library is needed
                ThumbnailWebView(
                    url = video.thumbnailUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )

                // Dark gradient overlay at the bottom
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to Color.Transparent,
                                    0.6f to Color.Transparent,
                                    1f to Color(0xCC000000)
                                )
                            )
                        )
                )

                // Play button circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xCCFFFFFF), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Reproducir ${video.title}",
                        tint = Color(0xFF1A1A2E),
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Duration badge – bottom-right
                Text(
                    text = video.duration,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(Color(0xCC000000), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                )

                // Source badge – bottom-left
                SourceBadge(
                    source = video.source,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                )
            }

            // ── Info ──────────────────────────────────────────────────────────
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = video.title,
                    color = ContentTitleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = video.instructor,
                        color = Color(0xFF666666),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (video.views.isNotBlank()) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = video.views,
                            color = Color(0xFF999999),
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Featured / hero card – wider, with a slightly different style
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun FeaturedVideoCard(
    video: VideoItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color(0xFF1A1A2E)),
                contentAlignment = Alignment.Center
            ) {
                ThumbnailWebView(
                    url = video.thumbnailUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0f to Color.Transparent,
                                    0.5f to Color.Transparent,
                                    1f to Color(0xDD000000)
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(FitnessBlue.copy(alpha = 0.9f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Reproducir ${video.title}",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = video.duration,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color(0xCC000000), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = "⭐ Destacado",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = FitnessBlue.copy(alpha = 0.12f),
                        labelColor = FitnessBlue
                    ),
                    modifier = Modifier.height(24.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = video.title,
                    color = ContentTitleColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = video.instructor,
                    color = Color(0xFF666666),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Source coloured badge
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SourceBadge(source: VideoSource, modifier: Modifier = Modifier) {
    val (label, bgColor) = when (source) {
        VideoSource.YOUTUBE    -> "▶ YouTube"   to Color(0xFFCC0000)
        VideoSource.DIRECT_MP4 -> "⬇ Directo"  to Color(0xFF1B7A34)
        VideoSource.FEATURED   -> "⭐ Destacado" to Color(0xFF4A90D9)
        VideoSource.WEBVIEW    -> "🌐 Web"       to Color(0xFF7B2FBE)
    }
    Text(
        text = label,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Lightweight thumbnail loader via WebView (avoids Coil/Glide dependency)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ThumbnailWebView(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled = false
                    loadWithOverviewMode = true
                    useWideViewPort = true
                }
                isClickable = false
                isFocusable = false
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                loadData(
                    buildThumbnailHtml(url),
                    "text/html",
                    "utf-8"
                )
            }
        },
        modifier = modifier
    )
}

private fun buildThumbnailHtml(imgUrl: String): String = """
    <!DOCTYPE html>
    <html>
    <head>
        <meta name="viewport" content="width=device-width,initial-scale=1">
        <style>
            * { margin:0; padding:0; }
            body { background:#1A1A2E; width:100%; height:100%; overflow:hidden; }
            img { width:100%; height:100%; object-fit:cover; display:block; }
        </style>
    </head>
    <body>
        <img src="$imgUrl" alt="" onerror="this.style.display='none'"/>
    </body>
    </html>
""".trimIndent()
