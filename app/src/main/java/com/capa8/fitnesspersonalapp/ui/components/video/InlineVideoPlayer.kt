package com.capa8.fitnesspersonalapp.ui.components.video

import android.annotation.SuppressLint
import android.app.Activity
import android.net.http.SslError
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
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
fun InlineVideoPlayer(video: VideoItem, modifier: Modifier = Modifier, constrainToAspectRatio: Boolean = true) {
    val m = if (constrainToAspectRatio) modifier.fillMaxWidth().aspectRatio(16f/9f) else modifier.fillMaxSize()
    Box(modifier = m.background(Color.Black), contentAlignment = Alignment.Center) {
        when (video.source) {
            VideoSource.YOUTUBE -> YoutubeWebPlayer(video.videoUrl, constrainToAspectRatio)
            VideoSource.WEBVIEW -> GenericWebPlayer(video.videoUrl, constrainToAspectRatio)
            VideoSource.DIRECT_MP4, VideoSource.FEATURED -> ExoVideoPlayer(video.videoUrl, constrainToAspectRatio)
        }
    }
}

// ── YouTube ───────────────────────────────────────────────────────────────────

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun YoutubeWebPlayer(embedUrl: String, constrainToAspectRatio: Boolean) {
    val context = LocalContext.current
    val videoId = remember(embedUrl) {
        Regex("embed/([a-zA-Z0-9_-]{11})").find(embedUrl)?.groupValues?.get(1) ?: ""
    }
    key(videoId) {
        var loading by remember { mutableStateOf(true) }
        val bm = if (constrainToAspectRatio) Modifier.fillMaxWidth().aspectRatio(16f/9f).background(Color.Black)
                 else Modifier.fillMaxSize().background(Color.Black)
        Box(modifier = bm, contentAlignment = Alignment.Center) {
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
                            loadWithOverviewMode = false
                            useWideViewPort = false
                            setSupportZoom(false)
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }
                        webChromeClient = buildChromeClient(context)

                        // ── FIX: force software rendering ──────────────────────────
                        // The AidlBufferPool logs confirm the video IS decoding, but
                        // the hardware video surface renders below the Compose
                        // ModalBottomSheet layer (same z-order issue as ExoPlayer's
                        // SurfaceView). Setting LAYER_TYPE_SOFTWARE forces the WebView
                        // and its internal video surface onto the same software render
                        // layer as Compose, making the video visible.
                        setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)

                        val handler = Handler(Looper.getMainLooper())
                        var elapsed = 0

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(wv: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                Log.d("YT_PLAYER", "▶ onPageStarted: $url")
                            }
                            override fun onPageFinished(wv: WebView?, url: String?) {
                                Log.d("YT_PLAYER", "✅ onPageFinished: $url | title=${wv?.title}")
                                loading = false
                                wv?.scrollTo(0, 0)
                                // Log UA being used
                                wv?.evaluateJavascript("navigator.userAgent", { ua ->
                                    Log.d("YT_PLAYER", "UA: $ua")
                                })
                                fixVideoElement(wv)
                                val w = wv ?: return
                                val r = object : Runnable {
                                    override fun run() {
                                        w.scrollTo(0, 0)
                                        // Log whether <video> element exists at this point
                                        w.evaluateJavascript(
                                            "JSON.stringify({hasVideo:!!document.querySelector('video'),scrollY:window.scrollY,bodyH:document.body.scrollHeight})",
                                            { result -> Log.d("YT_PLAYER", "DOM state: $result") }
                                        )
                                        fixVideoElement(w)
                                        val delay = if (elapsed < 4000L) 100L else 500L
                                        elapsed += delay.toInt()
                                        if (elapsed < 10000) handler.postDelayed(this, delay)
                                    }
                                }
                                handler.postDelayed(r, 100)
                            }
                            override fun onReceivedError(wv: WebView?, req: WebResourceRequest?, err: WebResourceError?) {
                                Log.e("YT_PLAYER", "❌ onReceivedError url=${req?.url} code=${err?.errorCode} desc=${err?.description}")
                            }
                            override fun onReceivedHttpError(wv: WebView?, req: WebResourceRequest?, resp: WebResourceResponse?) {
                                Log.e("YT_PLAYER", "❌ HTTP ${resp?.statusCode} for ${req?.url}")
                            }
                            override fun onReceivedSslError(wv: WebView?, handler: SslErrorHandler?, error: SslError?) {
                                Log.e("YT_PLAYER", "❌ SSL error: $error")
                                handler?.cancel()
                            }
                        }
                        val ytUrl = "https://m.youtube.com/watch?v=$videoId"
                        Log.d("YT_PLAYER", "🔗 Loading: $ytUrl")
                        loadUrl(ytUrl)
                    }
                },
                modifier = if (constrainToAspectRatio) Modifier.fillMaxWidth().aspectRatio(16f/9f) else Modifier.fillMaxSize()
            )
            if (loading) CircularProgressIndicator(color = Color.White)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun fixVideoElement(view: WebView?) {
    // Single clean IIFE — no try/catch wrapping that caused SyntaxError before.
    // Returns a string so evaluateJavascript callback receives a non-null value.
    view?.evaluateJavascript("""
(function(){
  document.documentElement.style.overflow='hidden';
  document.body.style.overflow='hidden';
  window.scrollTo(0,0);
  if(!document.getElementById('_bg')){
    var d=document.createElement('div');
    d.id='_bg';
    d.style.cssText='position:fixed;top:0;left:0;width:100%;height:100%;background:#000;z-index:2147483644;pointer-events:none;';
    document.body.appendChild(d);
  }
  var v=document.querySelector('video');
  if(v){
    v.style.setProperty('position','fixed','important');
    v.style.setProperty('top','0','important');
    v.style.setProperty('left','0','important');
    v.style.setProperty('width','100%','important');
    v.style.setProperty('height','100%','important');
    v.style.setProperty('z-index','2147483647','important');
    v.style.setProperty('background','#000','important');
    v.style.setProperty('object-fit','contain','important');
  }
  return 'ok:hasVideo='+(!!document.querySelector('video'));
})();""".trimIndent()) { r -> Log.d("YT_FIX", "fixResult=$r") }
}

// ── Vimeo / Generic ───────────────────────────────────────────────────────────

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun GenericWebPlayer(url: String, constrainToAspectRatio: Boolean) {
    val context = LocalContext.current
    key(url) {
        var loading by remember { mutableStateOf(true) }
        val bm = if (constrainToAspectRatio) Modifier.fillMaxWidth().aspectRatio(16f/9f).background(Color.Black)
                 else Modifier.fillMaxSize().background(Color.Black)
        Box(modifier = bm, contentAlignment = Alignment.Center) {
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
                            override fun onPageFinished(wv: WebView?, u: String?) { loading = false }
                        }
                        val vimeoId = Regex("video/(\\d+)").find(url)?.groupValues?.get(1)
                        if (url.contains("vimeo.com") && vimeoId != null) {
                            loadDataWithBaseURL("https://player.vimeo.com",
                                vimeoHtml(vimeoId), "text/html", "utf-8", null)
                        } else {
                            loadUrl(url)
                        }
                    }
                },
                modifier = if (constrainToAspectRatio) Modifier.fillMaxWidth().aspectRatio(16f/9f) else Modifier.fillMaxSize()
            )
            if (loading) CircularProgressIndicator(color = Color.White)
        }
    }
}

private fun vimeoHtml(id: String) = """<!DOCTYPE html><html>
<head><meta name="viewport" content="width=device-width,initial-scale=1">
<style>*{margin:0;padding:0;background:#000}html,body,#v{width:100%;height:100%;overflow:hidden}</style></head>
<body><div id="v"></div>
<script src="https://player.vimeo.com/api/player.js"></script>
<script>new Vimeo.Player('v',{id:$id,width:'100%',height:'100%',autoplay:true,playsinline:true,byline:false,title:false,portrait:false});</script>
</body></html>"""

// ── Shared Chrome client ──────────────────────────────────────────────────────

private fun buildChromeClient(context: android.content.Context) = object : WebChromeClient() {
    private var fsView: View? = null
    private var fsCb: CustomViewCallback? = null
    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        fsView?.let { onHideCustomView() }
        fsView = view; fsCb = callback
        val activity = context as? Activity ?: return
        val decor = activity.window.decorView as FrameLayout
        decor.addView(view, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        view.bringToFront()
    }
    override fun onHideCustomView() {
        val decor = (context as? Activity)?.window?.decorView as? FrameLayout
        fsView?.let { decor?.removeView(it) }
        fsView = null; fsCb?.onCustomViewHidden(); fsCb = null
    }
}

// ── ExoPlayer ─────────────────────────────────────────────────────────────────

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
        factory = { ctx -> LayoutInflater.from(ctx).inflate(R.layout.exo_player_texture, null, false) as PlayerView },
        update = { it.player = exoPlayer },
        modifier = if (constrainToAspectRatio) Modifier.fillMaxWidth().aspectRatio(16f/9f) else Modifier.fillMaxSize()
    )
}
