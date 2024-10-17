package com.krass.liquidtestphoto.main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.krass.liquidtestphoto.BaseActivity
import com.krass.liquidtestphoto.SamplesNames
import com.krass.liquidtestphoto.main.composables.CameraPreviewScreen
import com.krass.liquidtestphoto.main.composables.App
import com.krass.liquidtestphoto.main.composables.MachinesList
import com.krass.liquidtestphoto.ui.theme.MainTheme
import java.io.File


class MainActivity : BaseActivity() {

    val images = mutableStateListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MainPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        getImages()
    }

    @Composable
    fun MainPreview(){
        val machines = remember { SamplesNames.getInstance().getMachines(this) }
        App(onClick = { button ->
            when(button){
                "camera" -> {
                    if (handleCameraPermission()) {
                        setCameraPreview()
                    }
                }
                "newFolder" -> {
                    addNewFolder()
                    getImages()
                }
                "machines" -> {
                    setContent{
                        MainTheme {
                            MachinesList(machines, LocalLifecycleOwner.current, onStart = {
                            }, onStop = {
                                setContent {
                                    SamplesNames.storeMachines(this, machines)
                                    MainPreview()
                                }
                            })
                        }
                    }
                }
            }
        }, images)
    }

    private fun addNewFolder(){
        val sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        var folder = sharedPref.getInt("folder", 0)
        with (sharedPref.edit()) {
            putInt("folder", ++folder)
            apply()
        }
        Toast.makeText(this, "Folder Sample $folder created", Toast.LENGTH_SHORT).show()
    }

    private fun setCameraPreview() {
        setContent {
            MainTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraPreviewScreen(LocalLifecycleOwner.current, onStart = {
//                        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show()
                    }, onStop = {
//                        Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show()
                        setContent {
                            MainPreview()
                        }
                    })
                }
            }
        }
    }

    private fun getImages(){
        images.clear()
        val sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val folder = sharedPref.getInt("folder", 0)
        //Generating a file name
        val directory = File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Samples $folder")

        if (directory.exists()) {
            val allFiles: Array<out File>? = directory.listFiles()
            if (allFiles != null) {
                for (file in allFiles) {
                    images.add(Uri.fromFile(file))
                    Log.d("file", file.name)
                }
            }
        }
    }
}