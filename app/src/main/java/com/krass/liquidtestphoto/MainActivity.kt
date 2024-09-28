package com.krass.liquidtestphoto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krass.liquidtestphoto.composables.CameraPreviewScreen
import com.krass.liquidtestphoto.ui.theme.LiquidTestPhotoTheme

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(painterResource(R.drawable.logo_flex), contentDescription = "Logo", modifier = Modifier.fillMaxWidth().align(Alignment.Center))
                Button(
                    onClick = {
                        // Calls handleCameraPermission() from BaseActivity when the button is clicked
                        // This function checks for camera permission and requests it if not already granted
                        if(handleCameraPermission()){
                            setCameraPreview()
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        Color.Blue
                    )
                ) {
                    Text(text = "GO")
                }
                Button(
                    onClick = {
                        // Calls handleCameraPermission() from BaseActivity when the button is clicked
                        // This function checks for camera permission and requests it if not already granted
                        addNewFolder()
                    },
                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        Color.DarkGray
                    )
                ) {
                    Text(text = "New Folder")
                }
            }
        }
    }

    private fun addNewFolder(){
        val sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        var folder = sharedPref.getInt("folder", 0)
        with (sharedPref.edit()) {
            putInt("folder", ++folder)
            apply()
        }
    }

    private fun setCameraPreview() {
        setContent {
            LiquidTestPhotoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraPreviewScreen()
                }
            }
        }
    }
}