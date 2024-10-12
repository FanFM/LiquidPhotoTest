package com.krass.liquidtestphoto.main.composables

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.krass.liquidtestphoto.R
import com.krass.liquidtestphoto.ui.theme.MainTheme

lateinit var images: MutableList<Uri>

@Composable
fun App(onClick: (String) -> Unit, _images: MutableList<Uri>) {
    images = _images
    val navController = rememberNavController()
    val cameraIcon = ImageVector.vectorResource(R.drawable.photo_camera)
    val newFolderIcon = ImageVector.vectorResource(R.drawable.create_new_folder)
    val sendIcon = ImageVector.vectorResource(R.drawable.send)
    val cameraDesc = stringResource(R.string.camera)
    val newFolderDesc = stringResource(R.string.add_new_folder)
    val sendDesc = stringResource(R.string.send)

    val lifecycleOwner = LocalLifecycleOwner.current

    val buttons = remember {
        listOf(
            Triple(newFolderDesc, false, newFolderIcon),
            Triple(cameraDesc, true, cameraIcon),
            Triple(sendDesc, false, sendIcon)
        )
    }

    val defaultScreen = remember { buttons.first() }

    MainTheme {
        Scaffold(
            bottomBar = {
                BottomBar(
                    navController = navController,
                    buttons = buttons,
                    defaultScreen = defaultScreen,
                    onClick
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = defaultScreen.first,
                route = "Route"
            ) {
                for (button in buttons) {
                    composable(button.first) {
//                        when (button.first) {
//                            newFolderDesc -> NewFolderScreen()
//                            cameraDesc -> CameraScreen()
//                            galleryDesc -> GalleryScreen()
//                        }
                        ScreenContent(padding)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    navController: NavHostController,
    buttons: List<Triple<String, Boolean, ImageVector>>,
    defaultScreen: Triple<String, Boolean, ImageVector>,
    onClick: (String) -> Unit
) {
    val cameraDesc = stringResource(R.string.camera)
    val newFolderDesc = stringResource(R.string.add_new_folder)
    val sendDesc = stringResource(R.string.send)
    var selected by rememberSaveable {
        mutableStateOf(defaultScreen.first)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigate: (route: String) -> Unit = { route ->
        selected = route

        if (currentDestination == null || currentDestination.route != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }

                launchSingleTop = true

                restoreState = true
            }
        }
    }

    val background = MaterialTheme.colorScheme.surfaceContainer

    val navigationBarHeight = 140.dp
    val density = LocalDensity.current
    val bottomBarHeightPx = WindowInsets.navigationBars.getBottom(density)

    val bottomBarHeight = with(density) { bottomBarHeightPx.toDp() }

    val arenaButtonSize = 96.dp
    val arenaButtonRadius = arenaButtonSize / 2
    val arenaButtonBorderSize = 8.dp

    val canvasHeight = navigationBarHeight - arenaButtonRadius + arenaButtonBorderSize

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(navigationBarHeight)
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(canvasHeight)
            .align(Alignment.BottomCenter)
            .clickable(enabled = false, onClick = {
                Log.d("Canvas", "Canvas clicked")
            })
        ) {
            val rect = Rect(center = Offset(x = size.width / 2, y = 0f), arenaButtonRadius.toPx())
            val path = Path()
            path.addOval(rect)

            clipPath(path = path, clipOp = ClipOp.Difference) {
                drawRect(color = background)
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(canvasHeight)
            .padding(bottom = bottomBarHeight)
            .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f)) {
                for (button in buttons) {
                    if (button.second) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Spacer(modifier = Modifier.height(26.dp))
//                            Text( text = button.first)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {

                                    when(button.first){
                                        newFolderDesc -> onClick("newFolder")
                                        cameraDesc -> onClick("camera")
                                        sendDesc -> onClick("send")
                                    }


//                                    navigate(button.first)

                                    Log.d("Canvas", "Item ${button.first} clicked")
                                           },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                modifier = Modifier.size(26.dp),
                                imageVector = button.third,
                                contentDescription = button.first
                            )

                            Text(text = button.first)
                        }
                    }
                }
            }
        }

        val padding = (canvasHeight - arenaButtonRadius) + arenaButtonBorderSize
        val circleSize = arenaButtonSize - arenaButtonBorderSize * 2

        val centerButton = remember {
            buttons.first { it.second }
        }

        Box(modifier = Modifier
            .padding(bottom = padding)
            .size(circleSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .align(Alignment.BottomCenter)
            .clickable {
//                navigate(centerButton.first)
                Log.d("Canvas", "Circle clicked")
                onClick("camera")
            },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(arenaButtonRadius),
                imageVector = centerButton.third,
                contentDescription = centerButton.first
            )
        }
    }
}

@Composable
private fun ScreenContent(padding: PaddingValues) {
    LazyColumn(contentPadding = padding) {
        images.forEachIndexed { index, uri ->
            if (index % 2 == 0) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AsyncImage(
                            modifier = Modifier.weight(1f).padding(8.dp),
                            model = images[index],
                            contentDescription = images[index].path
                        )
                        if (index + 1 < images.size) {
                            AsyncImage(
                                modifier = Modifier.weight(1f).padding(8.dp),
                                model = images[index + 1],
                                contentDescription = images[index + 1].path
                            )
                        } else{
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}