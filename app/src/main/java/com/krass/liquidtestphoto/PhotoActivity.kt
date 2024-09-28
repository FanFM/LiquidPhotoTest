package com.krass.liquidtestphoto

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.krass.liquidtestphoto.composables.Dropdown
import com.krass.liquidtestphoto.composables.ui.theme.LiquidTestPhotoTheme
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class PhotoActivity : ComponentActivity() {

    private val context = this
    private var uri: Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiquidTestPhotoTheme {
                uri = Uri.parse(intent.extras?.getString("uri"))
                cropImage(uri, context)
            }
        }
        onBackPressedDispatcher.addCallback(this /* lifecycle owner */) {
            deleteFile(uri, context)
            finish()
        }
    }
}

    @Composable
    fun cropImage(uri: Uri, context: Context) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)
        val width: Int = bitmap.width
        val height: Int = bitmap.height

        val scale = context.resources.displayMetrics.density
        val dpAsPixels = (16.0f * scale + 0.5f).toInt()

        val crop = ((height - width) / 2) + dpAsPixels


        val cropImg: Bitmap = Bitmap.createBitmap(bitmap, dpAsPixels, crop, width - dpAsPixels*2, width - dpAsPixels*2)
        PhotoScreen(cropImg, uri, context)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoScreen(bitmap: Bitmap, uri: Uri, context: Context) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var selectedItem by rememberSaveable { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Top app bar")
                }
            )
        }){
            innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(Modifier.fillMaxSize()) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxWidth().padding(16.dp))
                    val buttonModifier = Modifier.fillMaxWidth()
                    Dropdown(false, onItemClick = {selectedItem = it})
                    }
                    Button(onClick = {
                        if(selectedItem.isEmpty()) {
                            selectedItem = fileNames.get(0)
                        }
                    saveMediaToStorage(selectedItem, bitmap, context)
                        onBackPressedDispatcher?.onBackPressed()
                    }, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)){
                    Text(text = "Save")
                }
            }
        }
    }
}

fun deleteFile(uri: Uri, context: Context){
    val fdelete = File(getFilePath(uri, context)!!)
    if (fdelete.exists()) {
        if (fdelete.delete()) {
            println("file Deleted :")
        } else {
            println("file not Deleted :")
        }
    }
}

fun saveMediaToStorage(fileName: String, bitmap: Bitmap, context: Context) {
    //Generating a file name
    val filename = "${fileName}.jpg"

    //Output stream
    var fos: OutputStream? = null

    //For devices running android >= Q
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        //getting the contentResolver
        context.contentResolver?.also { resolver ->

            //Content resolver will process the contentvalues
            val contentValues = ContentValues().apply {

                //putting file information in content values
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            //Inserting the contentValues to contentResolver and getting the Uri
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            //Opening an outputstream with the Uri that we got
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }
    } else {
        //These for devices running on android < Q
        //So I don't think an explanation is needed here
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
    }

    fos?.use {
        //Finally writing the bitmap to the output stream that we opened
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, it)
    }
}

private fun getFilePath(uri: Uri, context: Context): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)

    val cursor: Cursor? = context.getContentResolver().query(uri, projection, null, null, null)
    if (cursor != null) {
        cursor.moveToFirst()

        val columnIndex = cursor.getColumnIndex(projection[0])
        val picturePath = cursor.getString(columnIndex) // returns null
        cursor.close()
        return picturePath
    }
    return null
}