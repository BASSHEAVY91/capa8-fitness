package com.capa8.fitnesspersonalapp.ui.components.video

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.capa8.fitnesspersonalapp.R
import com.capa8.fitnesspersonalapp.data.model.VideoItem
import com.capa8.fitnesspersonalapp.data.model.VideoSource

private const val CHROME_MOBILE_UA =
    "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 " +
    "(KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"

// ─────────────────────────────────────────────────────────────────────────────

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InlineVideoPlayer(
    video: VideoItem,
    modifier: Modifier = Modifier,
    constrainToAspectRatio: Boolean = true
) {
    val m = if (constrainToAspectRatio) modifier.fillMaxWidth().aspectRatio(16f / 9f)
            else modifier.fillMaxSize()
    Box(modifier = m.background(Color.Black), contentAlignment = Alignment.Center) {
        when (video.source) {
            VideoSource.YOUTUBE  -> YoutubeWebPlayer(video.videoUrl, constrainToAspectRatio)
            VideoSource.WEBVIEW  -> GenericWebPlayer(video.videoUrl, constrainToAspectRatio)
            VideoSource.DIRECT_MP4,
            VideoSource.FEATURED,
            VideoSource.LOCAL    -> ExoVideoPlayer(video.videoUrl, constrainToAspectRatio)
        }
    }
}

// ── YouTube ───────────────────────────────────────────────────────────────────

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun YoutubeWebPlayer(embedUrl: String, constrainToAspectRatio: Boolean) {
    val videoId = remember(embedUrl) {
        Regex("embed/([a-zA-Z0-9_-]{11})").find(embedUrl)?.groupValues?.get(1) ?: ""
    }
    key(videoId) {
        var loading by remember { mutableStateOf(true) }
        val context = LocalContext.current
        val boxMod = if (constrainToAspectRatio)
            Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black)
        else Modifier.fillMaxSize().background(Color.Black)

        Box(modifier = boxMod, contentAlignment = Alignment.Center) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            @Suppress("SetJavaScriptEnabled")
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            userAgentString = CHROME_MOBILE_UA
                            mediaPlaybackRequiresUserGesture = false
                            @Suppress("DEPRECATION")
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(false)
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }
                        webChromeClient = buildChromeClient(context)
                        setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(wv: WebView?, url: String?) {
                                loading = false
                                Log.d("YT_PLAYER", "✅ loaded id=$videoId")
                            }
                        }
                        Log.d("YT_PLAYER", "🔗 loadDataWithBaseURL embed id=$videoId")
                        loadDataWithBaseURL(
                            "https://www.youtube.com",
                            youtubeHtml(videoId),
                            "text/html",
                            "utf-8",
                            null
                        )
                    }
                },
                modifier = if (constrainToAspectRatio)
                    Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                else Modifier.fillMaxSize()
            )
            if (loading) CircularProgressIndicator(color = Color.White)
        }
    }
}

private fun youtubeHtml(id: String) = """<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
<style>
  * { margin:0; padding:0; background:#000; }
  html, body, iframe { width:100%; height:100%; overflow:hidden; border:none; }
</style>
</head>
<body>
<iframe
  src="https://www.youtube.com/embed/$id?autoplay=1&rel=0&modestbranding=1&playsinline=1"
  allow="autoplay; fullscreen; encrypted-media"
  allowfullscreen
  frameborder="0"
  style="width:100%;height:100%;border:none;">
</iframe>
</body>
</html>"""

// ── Vimeo / Generic WebView ───────────────────────────────────────────────────

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun GenericWebPlayer(url: String, constrainToAspectRatio: Boolean) {
    val context = LocalContext.current
    key(url) {
        var loading by remember { mutableStateOf(true) }
        val boxMod = if (constrainToAspectRatio)
            Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black)
        else Modifier.fillMaxSize().background(Color.Black)

        Box(modifier = boxMod, contentAlignment = Alignment.Center) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            @Suppress("SetJavaScriptEnabled")
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            userAgentString = CHROME_MOBILE_UA
                            mediaPlaybackRequiresUserGesture = false
                            @Suppress("DEPRECATION")
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(false)
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }
                        webChromeClient = buildChromeClient(context)
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(wv: WebView?, u: String?) {
                                loading = false
                            }
                        }
                        val vimeoId = Regex("video/(\\d+)").find(url)?.groupValues?.get(1)
                        if (url.contains("vimeo.com") && vimeoId != null) {
                            loadDataWithBaseURL(
                                "https://player.vimeo.com",
                                vimeoHtml(vimeoId),
                                "text/html",
                                "utf-8",
                                null
                            )
                        } else {
                            loadUrl(url)
                        }
                    }
                },
                modifier = if (constrainToAspectRatio)
                    Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                else Modifier.fillMaxSize()
            )
            if (loading) CircularProgressIndicator(color = Color.White)
        }
    }
}

private fun vimeoHtml(id: String) = """<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width,initial-scale=1">
<style>
  * { margin:0; padding:0; background:#000; }
  html, body, #v { width:100%; height:100%; overflow:hidden; }
</style>
</head>
<body>
<div id="v"></div>
<script src="https://player.vimeo.com/api/player.js"></script>
<script>
new Vimeo.Player('v', {
  id: $id,
  width: '100%',
  height: '100%',
  autoplay: true,
  playsinline: true,
  byline: false,
  title: false,
  portrait: false
});
</script>
</body>
</html>"""

// ── Shared WebChromeClient (fullscreen support) ───────────────────────────────

private fun buildChromeClient(context: android.content.Context) = object : WebChromeClient() {
    private var fsView: View? = null
    private var fsCb: CustomViewCallback? = null

    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        fsView?.let { onHideCustomView() }
        fsView = view; fsCb = callback
        val activity = context as? Activity ?: return
        val decor = activity.window.decorView as FrameLayout
        decor.addView(
            view,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        view.bringToFront()
    }

    override fun onHideCustomView() {
        val decor = (context as? Activity)?.window?.decorView as? FrameLayout
        fsView?.let { decor?.removeView(it) }
        fsView = null; fsCb?.onCustomViewHidden(); fsCb = null
    }
}

// ── ExoPlayer (MP4 / local) ───────────────────────────────────────────────────

@OptIn(UnstableApi::class)
@Composable
private fun ExoVideoPlayer(videoUrl: String, constrainToAspectRatio: Boolean) {
    val context = LocalContext.current
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().also {
            it.setMediaItem(MediaItem.fromUri(videoUrl))
            it.prepare()
            it.playWhenReady = true
        }
    }
    DisposableEffect(videoUrl) { onDispose { exoPlayer.release() } }
    AndroidView(
        factory = { ctx ->
            val parent = android.widget.FrameLayout(ctx)
            LayoutInflater.from(ctx).inflate(R.layout.exo_player_texture, parent, false) as PlayerView
        },
        update = { it.player = exoPlayer },
        modifier = if (constrainToAspectRatio)
            Modifier.fillMaxWidth().aspectRatio(16f / 9f)
        else Modifier.fillMaxSize()
    )
}
