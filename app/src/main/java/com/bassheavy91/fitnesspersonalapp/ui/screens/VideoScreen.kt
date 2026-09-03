package com.bassheavy91.fitnesspersonalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bassheavy91.fitnesspersonalapp.data.model.VideoCategory
import com.bassheavy91.fitnesspersonalapp.data.model.VideoItem
import com.bassheavy91.fitnesspersonalapp.data.model.VideoSource
import com.bassheavy91.fitnesspersonalapp.ui.components.video.FeaturedVideoCard
import com.bassheavy91.fitnesspersonalapp.ui.components.video.InlineVideoPlayer
import com.bassheavy91.fitnesspersonalapp.ui.components.video.VideoGalleryCard
import com.bassheavy91.fitnesspersonalapp.ui.theme.ContentBackground
import com.bassheavy91.fitnesspersonalapp.ui.theme.ContentTitleColor
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessBlue
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessBlueDark
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(
    modifier: Modifier = Modifier,
    viewModel: VideoViewModel = viewModel()
) {
    var selectedCategory by remember { mutableStateOf(VideoCategory.ALL) }
    var selectedVideo by remember { mutableStateOf<VideoItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var fabExpanded  by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val filteredVideos = viewModel.filteredVideos(selectedCategory)
    val featuredVideos = viewModel.featuredVideos()
    val youtubeVideos = filteredVideos.filter { it.source == VideoSource.YOUTUBE }
    val directVideos = filteredVideos.filter { it.source == VideoSource.DIRECT_MP4 }
    val isSearchActive = viewModel.searchQuery.isNotBlank()

    Scaffold(
        modifier = modifier,
        containerColor = ContentBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    fabExpanded = true
                    showAddDialog = true
                },
                containerColor = FitnessBlue,
                contentColor = Color.White,
                modifier = Modifier.animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        horizontal = if (fabExpanded) 16.dp else 0.dp
                    )
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar video")
                    AnimatedVisibility(visible = fabExpanded) {
                        Text(
                            text = "  Agregar video",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Header
                item { ScreenHeader() }

                // Search bar
                item {
                    SearchBar(
                        query = viewModel.searchQuery,
                        onQueryChange = viewModel::onSearchChange,
                        onClear = viewModel::clearSearch
                    )
                }

                // Category chips (hidden while searching)
                if (!isSearchActive) {
                    item {
                        CategoryFilterRow(
                            selected = selectedCategory,
                            onSelect = { selectedCategory = it }
                        )
                    }
                }

                // Search results badge
                if (isSearchActive) {
                    item {
                        Text(
                            text = "${filteredVideos.size} resultado(s) para \"${viewModel.searchQuery}\"",
                            color = FitnessBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                // ── Destacados (hidden during active search) ──────────────────
                if (!isSearchActive && selectedCategory == VideoCategory.ALL
                    && featuredVideos.isNotEmpty()
                ) {
                    item {
                        SectionHeader("⭐", "Destacados", "Rutinas exclusivas de la app")
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(featuredVideos, key = { it.id }) { video ->
                                FeaturedVideoCard(
                                    video = video,
                                    onClick = {
                                        selectedVideo = video
                                        scope.launch { sheetState.show() }
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // ── YouTube section ───────────────────────────────────────────
                if (youtubeVideos.isNotEmpty()) {
                    item {
                        SectionHeader(
                            "▶", "YouTube",
                            "${youtubeVideos.size} videos de entrenadores"
                        )
                    }
                    items(youtubeVideos, key = { it.id }) { video ->
                        VideoGalleryCard(
                            video = video,
                            onClick = {
                                selectedVideo = video
                                scope.launch { sheetState.show() }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                // ── Directo section ───────────────────────────────────────────
                if (directVideos.isNotEmpty()) {
                    item {
                        SectionHeader(
                            "⬇", "Directo",
                            "${directVideos.size} guías técnicas"
                        )
                    }
                    items(directVideos, key = { it.id }) { video ->
                        VideoGalleryCard(
                            video = video,
                            onClick = {
                                selectedVideo = video
                                scope.launch { sheetState.show() }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                // ── Empty state ───────────────────────────────────────────────
                val hasContent = youtubeVideos.isNotEmpty() || directVideos.isNotEmpty() ||
                        (!isSearchActive && selectedCategory == VideoCategory.ALL
                                && featuredVideos.isNotEmpty())
                if (!hasContent) {
                    item {
                        EmptyState(
                            isSearch = isSearchActive,
                            query = viewModel.searchQuery,
                            category = selectedCategory
                        )
                    }
                }
            }

            // ── Inline player bottom sheet ────────────────────────────────────
            if (selectedVideo != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectedVideo = null },
                    sheetState = sheetState,
                    containerColor = Color(0xFF0F0F1A),
                    dragHandle = {
                        Box(
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 6.dp)
                                .width(40.dp)
                                .height(4.dp)
                                .background(Color(0xFF555577), RoundedCornerShape(2.dp))
                        )
                    }
                ) {
                    VideoPlayerSheet(
                        video = selectedVideo!!,
                        allVideos = filteredVideos,
                        onClose = {
                            scope.launch { sheetState.hide() }
                                .invokeOnCompletion { selectedVideo = null }
                        },
                        onVideoSelect = { video -> selectedVideo = video }
                    )
                }
            }
        }
    }

    // ── Add video dialog ──────────────────────────────────────────────────────
    if (showAddDialog) {
        AddVideoDialog(
            onDismiss = { showAddDialog = false; fabExpanded = false },
            onConfirm = { title, url, category, instructor, duration ->
                viewModel.addVideo(title, url, category, instructor, duration)
                showAddDialog = false
                fabExpanded = false
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Add-video dialog
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddVideoDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, url: String, category: VideoCategory,
                instructor: String, duration: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(VideoCategory.CARDIO) }
    var categoryExpanded by remember { mutableStateOf(false) }
    val isValid = title.isNotBlank() && url.isNotBlank()

    // Detect source type for the helper label
    val detectedSource = when {
        url.contains("youtube.com") || url.contains("youtu.be") -> "YouTube detectado ✓"
        url.isBlank() -> "Ingresa una URL de YouTube o MP4 directo"
        else -> "MP4 / stream directo ✓"
    }
    val sourceColor = when {
        url.contains("youtube.com") || url.contains("youtu.be") -> Color(0xFFCC0000)
        url.isBlank() -> Color(0xFF888888)
        else -> Color(0xFF1B7A34)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                "Agregar video",
                fontWeight = FontWeight.Bold,
                color = FitnessBlueDark
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FitnessBlue,
                        focusedLabelColor = FitnessBlue,
                        unfocusedBorderColor = Color(0xFF888888),
                        focusedTextColor = Color(0xFF111111),
                        unfocusedTextColor = Color(0xFF111111),
                        unfocusedLabelColor = Color(0xFF555555),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                // URL
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL del video *") },
                    placeholder = { Text("https://youtube.com/watch?v=...", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text(detectedSource, color = sourceColor, fontSize = 11.sp)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FitnessBlue,
                        focusedLabelColor = FitnessBlue,
                        unfocusedBorderColor = Color(0xFF888888),
                        focusedTextColor = Color(0xFF111111),
                        unfocusedTextColor = Color(0xFF111111),
                        unfocusedLabelColor = Color(0xFF555555),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FitnessBlue,
                            focusedLabelColor = FitnessBlue,
                            unfocusedBorderColor = Color(0xFF888888),
                            focusedTextColor = Color(0xFF111111),
                            unfocusedTextColor = Color(0xFF111111),
                            unfocusedLabelColor = Color(0xFF555555),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        VideoCategory.entries
                            .filter { it != VideoCategory.ALL }
                            .forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.label) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                    }
                }

                // Instructor (optional)
                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    label = { Text("Instructor (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FitnessBlue,
                        focusedLabelColor = FitnessBlue,
                        unfocusedBorderColor = Color(0xFF888888),
                        focusedTextColor = Color(0xFF111111),
                        unfocusedTextColor = Color(0xFF111111),
                        unfocusedLabelColor = Color(0xFF555555),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                // Duration (optional)
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duración (opcional)") },
                    placeholder = { Text("ej. 12:30", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FitnessBlue,
                        focusedLabelColor = FitnessBlue,
                        unfocusedBorderColor = Color(0xFF888888),
                        focusedTextColor = Color(0xFF111111),
                        unfocusedTextColor = Color(0xFF111111),
                        unfocusedLabelColor = Color(0xFF555555),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) onConfirm(title, url, selectedCategory, instructor, duration)
                },
                enabled = isValid
            ) {
                Text(
                    "Agregar",
                    color = if (isValid) FitnessBlue else Color(0xFFAAAAAA),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF555555))
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Live search text field
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                "Buscar videos, instructores…",
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                tint = FitnessBlue,
                modifier = Modifier.size(18.dp)
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(
                    onClick = { onClear(); focusManager.clearFocus() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Limpiar",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),           // pill shape
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FitnessBlue,
            unfocusedBorderColor = Color(0xFFCCCCCC),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF8F8F8)
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom-sheet: player + info + related
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VideoPlayerSheet(
    video: VideoItem,
    allVideos: List<VideoItem>,
    onClose: () -> Unit,
    onVideoSelect: (VideoItem) -> Unit
) {
    val relatedVideos = remember(video.id) {
        allVideos.filter { it.id != video.id && it.category == video.category }.take(4)
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF0F0F1A)),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item { InlineVideoPlayer(video = video) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title, color = Color.White,
                        fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SourceChip(video.source)
                        CategoryChip(video.category)
                    }
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = Color(0xFFAAAAAA))
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(34.dp)
                        .background(FitnessBlue, RoundedCornerShape(17.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        video.instructor.take(1).uppercase(),
                        color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(video.instructor, color = Color(0xFFDDDDDD), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    if (video.views.isNotBlank()) {
                        Text("${video.views}  •  ${video.duration}", color = Color(0xFF888899), fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            HorizontalDivider(color = Color(0xFF2A2A3A), modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = FitnessBlue,
                    modifier = Modifier.size(15.dp).padding(top = 1.dp))
                Spacer(Modifier.width(8.dp))
                Text(video.description, color = Color(0xFFAAAAAA), fontSize = 13.sp, lineHeight = 20.sp)
            }
        }

        if (relatedVideos.isNotEmpty()) {
            item {
                HorizontalDivider(color = Color(0xFF2A2A3A), modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                Text("Videos relacionados", color = Color.White, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
            items(relatedVideos, key = { "rel_${it.id}" }) { related ->
                RelatedVideoRow(video = related, onClick = { onVideoSelect(related) })
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Related video compact row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RelatedVideoRow(video: VideoItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(Color(0xFF1A1A2E), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF2A2A3A), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = FitnessBlue, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(video.title, color = Color(0xFFDDDDDD), fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold, maxLines = 2,
                overflow = TextOverflow.Ellipsis, lineHeight = 17.sp)
            Spacer(Modifier.height(2.dp))
            Text("${video.instructor}  •  ${video.duration}", color = Color(0xFF888899), fontSize = 11.sp)
        }
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.background(FitnessBlue.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text("Ver", color = FitnessBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ScreenHeader() {
    Column(
        modifier = Modifier.fillMaxWidth().background(FitnessBlueDark)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text("Videos de Entrenamiento", color = Color.White,
            fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
        Spacer(Modifier.height(2.dp))
        Text("Reproduce cualquier video sin salir de la app",
            color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Category filter chips
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CategoryFilterRow(selected: VideoCategory, onSelect: (VideoCategory) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(VideoCategory.entries.toTypedArray()) { category ->
            FilterChip(
                selected = category == selected,
                onClick = { onSelect(category) },
                label = {
                    Text(category.label, fontSize = 13.sp,
                        fontWeight = if (category == selected) FontWeight.Bold else FontWeight.Normal)
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = FitnessBlue, selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF0F4FA), labelColor = FitnessBlueDark
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(emoji: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.width(8.dp))
        Column {
            Text(title, color = ContentTitleColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFF888899), fontSize = 11.sp)
        }
    }
    HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(horizontal = 16.dp))
    Spacer(Modifier.height(4.dp))
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(isSearch: Boolean, query: String, category: VideoCategory) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (isSearch) "🔍" else "🎬", fontSize = 48.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = if (isSearch) "Sin resultados para \"$query\""
                   else "Sin videos en ${category.label}",
            color = ContentTitleColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (isSearch) "Intenta con otro término o agrega un video."
                   else "Toca + para agregar un video.",
            color = Color(0xFF888899), fontSize = 13.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Source / Category chips (inside the player sheet)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SourceChip(source: VideoSource) {
    val (label, color) = when (source) {
        VideoSource.YOUTUBE -> "▶ YouTube" to Color(0xFFCC0000)
        VideoSource.DIRECT_MP4 -> "⬇ Directo" to Color(0xFF1B7A34)
        VideoSource.FEATURED -> "⭐ Destacado" to FitnessBlue
    }
    SuggestionChip(
        onClick = {},
        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = color.copy(alpha = 0.15f),
            labelColor = color
        ),
        modifier = Modifier.height(24.dp)
    )
}

@Composable
private fun CategoryChip(category: VideoCategory) {
    SuggestionChip(
        onClick = {},
        label = { Text(category.label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = FitnessBlue.copy(alpha = 0.1f),
            labelColor = FitnessBlue
        ),
        modifier = Modifier.height(24.dp)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun VideoScreenPreview() {
    FitnessPersonalAppTheme {
        VideoScreen()
    }
}
