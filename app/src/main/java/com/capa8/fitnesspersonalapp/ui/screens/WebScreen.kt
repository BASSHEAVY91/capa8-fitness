package com.capa8.fitnesspersonalapp.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlue
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlueDark
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

private const val HOME_URL = "https://www.who.int/health-topics/nutrition"

private data class Bookmark(val label: String, val url: String, val emoji: String)

private val BOOKMARKS = listOf(
    Bookmark("OMS Nutricion", "https://www.who.int/health-topics/nutrition", "🥗"),
    Bookmark("Harvard", "https://www.hsph.harvard.edu/nutritionsource/", "🎓"),
    Bookmark("MyFitnessPal", "https://www.myfitnesspal.com/", "💪"),
    Bookmark("Healthline", "https://www.healthline.com/nutrition", "❤️"),
    Bookmark("NIH Fitness", "https://www.nih.gov/health-information/physical-activity", "🏃"),
)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebScreen(modifier: Modifier = Modifier) {
    var currentUrl by rememberSaveable { mutableStateOf(HOME_URL) }
    var urlInput by rememberSaveable { mutableStateOf(HOME_URL) }
    var pageTitle by remember { mutableStateOf("Nutricion - OMS") }
    var loadProgress by remember { mutableFloatStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var webViewRef: WebView? by remember { mutableStateOf(null) }
    val keyboard = LocalSoftwareKeyboardController.current

    BackHandler(enabled = canGoBack) { webViewRef?.goBack() }

    fun navigate(raw: String) {
        val url = when {
            raw.startsWith("http://") || raw.startsWith("https://") -> raw
            raw.contains(".") && !raw.contains(" ") -> "https://$raw"
            else -> "https://www.google.com/search?q=${raw.replace(" ", "+")}"
        }
        urlInput = url
        currentUrl = url
        webViewRef?.loadUrl(url)
        keyboard?.hide()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Toolbar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FitnessBlueDark)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // URL bar row
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.92f),
                        focusedBorderColor = Color(0xFF90CAF9),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color(0xFF1A237E),
                        unfocusedTextColor = Color(0xFF424242),
                        cursorColor = FitnessBlue,
                    ),
                    textStyle = TextStyle(fontSize = 13.sp),
                    placeholder = { Text("Buscar o ingresar URL...", fontSize = 13.sp, color = Color.Gray) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = FitnessBlue, modifier = Modifier.size(18.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onGo = { navigate(urlInput) })
                )
                Spacer(Modifier.width(6.dp))
                IconButton(
                    onClick = { if (isLoading) webViewRef?.stopLoading() else webViewRef?.reload() },
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Recargar", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            // Nav buttons row
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NavBtn(Icons.Filled.ArrowBack, "Atras", canGoBack) { webViewRef?.goBack() }
                NavBtn(Icons.Filled.ArrowForward, "Adelante", canGoForward) { webViewRef?.goForward() }
                NavBtn(Icons.Filled.Home, "Inicio", true) { navigate(HOME_URL) }
                Spacer(Modifier.width(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.BookmarkBorder, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(pageTitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF90CAF9), strokeWidth = 2.dp)
                }
            }
        }

        // Progress bar
        AnimatedVisibility(visible = isLoading && loadProgress in 0.01f..0.99f, enter = fadeIn(), exit = fadeOut()) {
            LinearProgressIndicator(
                progress = { loadProgress },
                modifier = Modifier.fillMaxWidth().height(3.dp),
                color = Color(0xFF64B5F6),
                trackColor = FitnessBlueDark
            )
        }

        // Bookmark chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEF2FF))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚡", fontSize = 14.sp, modifier = Modifier.padding(end = 2.dp))
            BOOKMARKS.forEach { bm ->
                val active = currentUrl.startsWith(bm.url.trimEnd('/'))
                SuggestionChip(
                    onClick = { navigate(bm.url) },
                    label = { Text("${bm.emoji} ${bm.label}", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (active) FitnessBlue.copy(alpha = 0.15f) else Color.White,
                        labelColor = if (active) FitnessBlueDark else Color(0xFF37474F)
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = if (active) FitnessBlue else Color(0xFFCFD8DC)
                    ),
                    modifier = Modifier.height(30.dp)
                )
            }
        }

        // Error banner
        AnimatedVisibility(visible = hasError, enter = fadeIn(), exit = fadeOut()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF3E0))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⚠️", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "No se pudo cargar la pagina. Verifica tu conexion.",
                    fontSize = 13.sp,
                    color = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // WebView
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        settings.setSupportZoom(true)
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                                isLoading = true
                                hasError = false
                                urlInput = url
                                currentUrl = url
                                canGoBack = view.canGoBack()
                                canGoForward = view.canGoForward()
                            }
                            override fun onPageFinished(view: WebView, url: String) {
                                isLoading = false
                                loadProgress = 1f
                                urlInput = url
                                currentUrl = url
                                canGoBack = view.canGoBack()
                                canGoForward = view.canGoForward()
                            }
                            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                                if (request.isForMainFrame) {
                                    isLoading = false
                                    hasError = true
                                }
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView, newProgress: Int) {
                                loadProgress = newProgress / 100f
                                isLoading = newProgress < 100
                            }
                            override fun onReceivedTitle(view: WebView, title: String) {
                                pageTitle = title
                            }
                        }
                        loadUrl(HOME_URL)
                        webViewRef = this
                    }
                },
                update = { wv ->
                    webViewRef = wv
                }
            )
        }
    }
}

@Composable
private fun NavBtn(
    icon: ImageVector,
    desc: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = if (enabled) 0.18f else 0.06f))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = if (enabled) Color.White else Color.White.copy(alpha = 0.35f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WebScreenPreview() {
    FitnessPersonalAppTheme {
        WebScreen()
    }
}
