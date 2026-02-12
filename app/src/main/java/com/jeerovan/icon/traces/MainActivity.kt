package com.jeerovan.icon.traces

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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

    // 300ms is a standard "natural" duration for screen slides
    val transitionDuration = 300

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        // Slide in from Right (New Screen)
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(transitionDuration)
            )
        },
        // Slide out to Left (Old Screen)
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(transitionDuration)
            )
        },
        // Slide in from Left (When going back)
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(transitionDuration)
            )
        },
        // Slide out to Right (Current screen removing)
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(transitionDuration)
            )
        }
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(Routes.ICONS) {
            IconsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(Routes.HOWTO) {
            HowToScreen(
                navController = navController
            )
        }
        composable(Routes.OPENSOURCE) {
            OpenSourceScreen(
                navController = navController
            )
        }
        composable(Routes.REQUEST) {
            RequestScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val versionInfo by viewModel.versionInfo.collectAsStateWithLifecycle()
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
                            viewModel.shareFeedback(context)
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
                            viewModel.shareApp(context)
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
                    headlineContent = { Text("Version") },
                    supportingContent = { Text(versionInfo)}
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Intent to open Comfer Launcher in Play Store
    val comferPackageName = "com.jeerovan.comfer"
    val openPlayStore = remember {
        {
            try {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$comferPackageName"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                // Fallback for devices without Play Store
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$comferPackageName"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How to use") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState) // Prevent layout issues on small screens
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Section 1: Recommended Launcher (Highlighted) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Best Viewed With",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape) // Adaptive shape
                            .background(Color.White) // User-selected Background
                            .size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_comfer_launcher),
                            contentDescription = "Comfer Launcher Icon",
                            tint = Color.Red,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp) // Adjust padding to match adaptive icon safe-zone

                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // APP NAME
                    Text(
                        text = "Comfer Launcher",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Experience the icon pack as intended with a clean, distraction-free home screen.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // VIEW BUTTON
                    Button(
                        onClick = openPlayStore,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("View on Play Store")
                    }
                }
            }

            // --- Section 2: General Instructions ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Other Launchers",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "This icon pack is built on standard Android protocols and works with most third-party launchers that support custom icon packs.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InstructionStep(1, "Open your launcher settings.")
                        InstructionStep(2, "Find the 'Look & Feel' or 'Icon Style' section.")
                        InstructionStep(3, "Select 'Icon Pack' and choose this app from the list.")
                    }
                }
            }
        }
    }
}
@Composable
fun InstructionStep(number: Int, text: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenSourceScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Intent to open GitHub
    val repoUrl = "https://github.com/jeerovan/IconTraces"
    val openGitHub = remember {
        {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl))
            context.startActivity(intent)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contribute") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Section 1: Repository Hero Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape) // Adaptive shape
                            .background(Color.White) // User-selected Background
                            .size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.mipmap.ic_launcher_foreground),
                            contentDescription = "Source Code",
                            tint = Color.Black,
                            modifier = Modifier
                                .fillMaxSize()

                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "IconTraces",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This project is fully open source. Access the raw SVGs, report issues, or contribute new icons.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = openGitHub,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View on GitHub")
                    }
                }
            }

            // --- Section 2: How to Contribute ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "How to Improve Icons",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ContributionStep(
                            icon = Icons.Filled.Share,
                            title = "Clone Repository",
                            description = "Download the source code to access the raw SVG files."
                        )
                        ContributionStep(
                            icon = Icons.Filled.Create,
                            title = "Modify SVGs",
                            description = "Edit existing icons or add new ones using any vector editor."
                        )
                        ContributionStep(
                            icon = Icons.Filled.Check,
                            title = "Submit PR",
                            description = "Push your changes and create a Pull Request on GitHub."
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContributionStep(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Icon Circle
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier.size(40.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text Content
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val apps by viewModel.missingApps.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Icons") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            // Removed global padding(16.dp) to allow list to touch edges,
            // applied padding to children instead for better UI
        ) {
            // Toolbar with Send Button (Fixed at top)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Padding for the header only
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically // Align text and button vertically
            ) {
                Text(
                    text = "Select Missing Icons",
                    style = MaterialTheme.typography.headlineSmall // Slightly smaller for better fit
                )

                val selectedCount = apps.count { it.isSelected }
                // Use AnimatedVisibility for a smoother UI when button appears/disappears
                androidx.compose.animation.AnimatedVisibility(visible = selectedCount > 0) {
                    Button(onClick = {
                        viewModel.sendRequest(
                            context,
                            apps.filter { it.isSelected })
                    }) {
                        Text("Send ($selectedCount)")
                    }
                }
            }

            // App List (Scrollable Area)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // CRITICAL: Takes up all remaining space
                contentPadding = PaddingValues(bottom = 16.dp) // Add padding at bottom of list
            ) {
                items(apps, key = { it.packageName }) { app -> // Add key for performance
                    RequestItem(
                        app = app,
                        onToggle = { isChecked ->
                            viewModel.toggleSelection(app.packageName, isChecked)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun RequestItem(app: RequestApp, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!app.isSelected) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = app.isSelected, onCheckedChange = onToggle)
        Spacer(modifier = Modifier.width(16.dp))
        Image(
            bitmap = app.icon.toBitmap().asImageBitmap(), // Or use Coil for performance
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(app.label, style = MaterialTheme.typography.labelLarge)
            Text(app.packageName, style = MaterialTheme.typography.bodySmall)
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