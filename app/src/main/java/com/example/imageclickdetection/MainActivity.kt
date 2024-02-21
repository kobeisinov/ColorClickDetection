package com.example.imageclickdetection

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.imageclickdetection.ui.theme.ImageClickDetectionTheme

val colorMap = mapOf(
    "red: 243, green: 145, blue: 121" to "Others",
    "red: 184, green: 156, blue: 173" to "Nitrogen",
    "red: 197, green: 208, blue: 116" to "Hydrogen",
    "red: 220, green: 214, blue: 201" to "Carbon",
    "red: 168, green: 201, blue: 197" to "Oxygen",
)

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageClickDetectionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold {
                        ImageTouchDetection()
                    }
                }
            }
        }
    }
}

@Composable
fun ImageTouchDetection() {
    val imageBitmap: ImageBitmap = ImageBitmap.imageResource(
        LocalContext.current.resources,
        R.drawable._01_elements_of_the_human_body_02_svg
    )

    val bitmapWidth = imageBitmap.width
    val bitmapHeight = imageBitmap.height

    var offsetX by remember {
        mutableStateOf(0f)
    }
    var offsetY by remember {
        mutableStateOf(0f)
    }
    var imageSize by remember {
        mutableStateOf(Size.Zero)
    }

    var text by remember {
        mutableStateOf("")
    }
    var colorAtTouchPosition by remember {
        mutableStateOf(Color.Unspecified)
    }
    val imageModifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        .aspectRatio(3f / 4)
        .pointerInput(Unit) {
            detectTapGestures { offset: Offset ->
                offsetX = offset.x
                offsetY = offset.y

                val scaledX = (bitmapWidth / imageSize.width) * offsetX
                val scaledY = (bitmapHeight / imageSize.height) * offsetY

                try {
                    val pixel: Int = imageBitmap
                        .asAndroidBitmap()
                        .getPixel(scaledX.toInt(), scaledY.toInt())
                    val red = android.graphics.Color.red(pixel)
                    val green = android.graphics.Color.green(pixel)
                    val blue = android.graphics.Color.blue(pixel)

                    text = "red: $red, green: $green, blue: $blue"

                    colorAtTouchPosition = Color(red, green, blue)
                } catch (e: Exception) {
                    Log.d("Exception", "ImageTouchDetection: ${e.message}")
                }
            }
        }
        .onSizeChanged {
            imageSize = it.toSize()
        }

    Column {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Bitmap Image",
            modifier = imageModifier
                .border(2.dp, Color.Red),
            contentScale = ContentScale.Crop
        )
        Text(text = "${colorMap[text]}")

        Box(
            modifier = Modifier
                .then(
                    if (colorAtTouchPosition.isUnspecified) {
                        Modifier
                    } else {
                        Modifier.background(colorAtTouchPosition)
                    }
                )
                .size(100.dp)
        )
    }
}