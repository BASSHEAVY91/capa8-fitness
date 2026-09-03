package com.bassheavy91.fitnesspersonalapp.ui.components.video

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.bassheavy91.fitnesspersonalapp.data.model.VideoItem
import com.bassheavy91.fitnesspersonalapp.data.model.VideoSource

/**
 * Inline 16:9 video player — the user never leaves the app.
 *
 *  • [VideoSource.YOUTUBE]    → WebView with YouTube iFrame embed.
 *                               Uses loadDataWithBaseURL("https://www.youtube.com")
 *                               so the iframe origin check passes.
 *  • [VideoSource.DIRECT_MP4] → ExoPlayer (Media3) PlayerView.
 *  • [VideoSource.FEATURED]   → same ExoPlayer path.
 *
 * Both renderers are keyed on [VideoItem.videoUrl] so switching videos while
 * the bottom sheet is open creates fresh players automatically.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InlineVideoPlayer(
    video: VideoItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        when (video.source) {
            VideoSource.YOUTUBE ->
                YoutubeWebPlayer(embedUrl = video.videoUrl)

            VideoSource.DIRECT_MP4,
            VideoSource.FEATURED ->
                ExoVideoPlayer(videoUrl = video.videoUrl)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// YouTube iFrame inside a WebView
// ─────────────────────────────────────────────────────────────────────────────

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun YoutubeWebPlayer(embedUrl: String) {
    // key(embedUrl) disposes and recreates the entire WebView whenever the URL
    // changes, which is simpler and safer than trying to reload inside `update`.
    key(embedUrl) {
        var loading by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            @Suppress("SetJavaScriptEnabled")
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            // Required for video autoplay in WebView
                            mediaPlaybackRequiresUserGesture = false
                            loadWithOverviewMode = true
                            useWideViewPort = true
                        }
                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                loading = false
                            }
                        }
                        // loadDataWithBaseURL sets the iframe origin to youtube.com.
                        // Without this YouTube's embed API rejects the request
                        // with "Failed to execute 'postMessage' on 'DOMWindow'".
                        loadDataWithBaseURL(
                            "https://www.youtube.com",
                            buildYoutubeHtml(embedUrl),
                            "text/html",
                            "utf-8",
                            null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )

            if (loading) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

/** Full-viewport YouTube iFrame HTML. playsinline=1 keeps video inside the WebView. */
private fun buildYoutubeHtml(embedUrl: String): String = """
    <!DOCTYPE html>
    <html>
    <head>
        <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
        <style>
            * { margin:0; padding:0; box-sizing:border-box; background:#000; }
            html, body { width:100%; height:100%; overflow:hidden; }
            iframe { position:absolute; top:0; left:0; width:100%; height:100%; border:none; }
        </style>
    </head>
    <body>
        <iframe
            src="${embedUrl}&playsinline=1&enablejsapi=1"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media;
                   gyroscope; picture-in-picture; web-share"
            allowfullscreen>
        </iframe>
    </body>
    </html>
""".trimIndent()

// ─────────────────────────────────────────────────────────────────────────────
// ExoPlayer (Media3) composable
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(UnstableApi::class)
@Composable
private fun ExoVideoPlayer(videoUrl: String) {
    val context = LocalContext.current

    // Key on videoUrl → new player is built whenever the user picks a different video.
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().also { player ->
            player.setMediaItem(MediaItem.fromUri(videoUrl))
            player.prepare()
            player.playWhenReady = true
        }
    }

    // Release when videoUrl changes (the old player is no longer needed)
    // or when the composable leaves the composition entirely.
    DisposableEffect(videoUrl) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                useController = true
                keepScreenOn = true
            }
        },
        update = { playerView ->
            // Bind the current (possibly new) player instance.
            playerView.player = exoPlayer
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    )
}
