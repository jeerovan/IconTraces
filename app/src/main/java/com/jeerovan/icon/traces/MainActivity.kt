package com.jeerovan.icon.traces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jeerovan.icon.traces.ui.theme.TracesTheme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import kotlin.getValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TracesTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

object Routes {
    const val HOME = "home"
    const val ICONS = "icons"
    const val HOWTO = "howto"
    const val OPENSOURCE = "opensource"
    const val REQUEST = "request"
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController
            )
        }
        composable(Routes.ICONS) {
            IconsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Traces - Icon Pack") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        // Use LazyColumn for lists to avoid ANRs with many items
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_icons_symbol),
                            contentDescription = "View Icons"
                        )
                    },
                    headlineContent = { Text("View Icons") },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "View Icons"
                        )
                    },
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Routes.ICONS)
                        }
                )
            }
            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "How to guide"
                        )
                    },
                    headlineContent = { Text("How to use") },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "How to guide"
                        )
                    },
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Routes.HOWTO)
                        }
                )
            }
            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_open_source),
                            contentDescription = "Github"
                        )
                    },
                    headlineContent = { Text("Open-Source") },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "View Repo details"
                        )
                    },
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Routes.OPENSOURCE)
                        }
                )
            }
            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_icon_request),
                            contentDescription = "Request icon"
                        )
                    },
                    headlineContent = { Text("Request Icon") },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Request icon details"
                        )
                    },
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Routes.REQUEST)
                        }
                )
            }
            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Feedback Icon"
                        )
                    },
                    headlineContent = { Text("Feedback") },
                    modifier = Modifier
                        .clickable {
                            // open feedback play store page
                        }
                )
            }
            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Icon"
                        )
                    },
                    headlineContent = { Text("Share") },
                    modifier = Modifier
                        .clickable {
                            // Share app details
                        }
                )
            }
            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info Icon"
                        )
                    },
                    headlineContent = { Text("Info") },
                    modifier = Modifier
                        .clickable {
                            // Info details
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconsScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val icons by viewModel.visibleIcons.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var foregroundColor by remember { mutableStateOf(Color.Black) }
    var backgroundColor by remember { mutableStateOf(Color.White) }
    var showColorPicker by remember { mutableStateOf(false) }

    // Color Picker Dialog
    if (showColorPicker) {
        ColorPickerDialog(
            initialFg = foregroundColor,
            initialBg = backgroundColor,
            onDismiss = { showColorPicker = false },
            onApply = { newFg, newBg ->
                foregroundColor = newFg
                backgroundColor = newBg
                showColorPicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Icons (${icons.size})") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                // 2. Action Button for Color Picker
                actions = {
                    IconButton(onClick = { showColorPicker = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit, // Or a Palette icon if available
                            contentDescription = "Customize Colors"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 72.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = icons,
                key = { it.resId }
            ) { icon ->
                // 3. Pass colors to the item
                IconGridItem(
                    icon = icon,
                    foregroundColor = foregroundColor,
                    backgroundColor = backgroundColor
                )
            }
            item {
                LaunchedEffect(Unit) {
                    viewModel.loadNextPage()
                }
            }
        }
    }
}

@Composable
fun IconGridItem(
    icon: IconItem,
    foregroundColor: Color,
    backgroundColor: Color
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 4. Adaptive Icon Container
        Box(
            modifier = Modifier
                .clip(CircleShape) // Adaptive shape
                .background(backgroundColor) // User-selected Background
                .size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon.foregroundResId),
                contentDescription = icon.name,
                tint = foregroundColor, // 3. Apply User Foreground Tint
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp) // Adjust padding to match adaptive icon safe-zone
            )
        }
    }
}

@Composable
fun ColorPickerDialog(
    initialFg: Color,
    initialBg: Color,
    onDismiss: () -> Unit,
    onApply: (Color, Color) -> Unit
) {
    // Local state for the dialog before applying
    var tempFg by remember { mutableStateOf(initialFg) }
    var tempBg by remember { mutableStateOf(initialBg) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Customize Icons") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ColorSection(title = "Foreground", currentColor = tempFg) { tempFg = it }
                HorizontalDivider()
                ColorSection(title = "Background", currentColor = tempBg) { tempBg = it }
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(tempFg, tempBg) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ColorSection(
    title: String,
    currentColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Column {
        Text(text = title, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Preset Colors for Demo
            val presets = listOf(
                Color.White,       // Best for foreground on dark backgrounds
                Color.Black,       // Best for foreground on light backgrounds
                Color(0xFFF44336), // Material Red 500
                Color(0xFF2196F3), // Material Blue 500
                Color(0xFF4CAF50), // Material Green 500
                Color(0xFFFFEB3B)  // Material Yellow 500 (Needs dark content on top)
            )

            presets.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (color == currentColor) 2.dp else 1.dp,
                            color = if (color == currentColor) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}