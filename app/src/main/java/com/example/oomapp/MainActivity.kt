package com.example.oomapp

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLES20
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getSystemService
import com.example.nativelib.NativeLib
import com.example.oomapp.ui.theme.OOMappTheme
import java.lang.Thread.sleep

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OOMappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Button(modifier = Modifier.padding(innerPadding),
                        onClick = {
                            showInfo(this)
                        }) {
                        Text("Click me")
                    }
                }
            }
        }
        val gl = GLES20.glGetString(GLES20.GL_EXTENSIONS)
        if (gl?.contains("GL_NVX_gpu_memory_info") == true) {
            println("Intag: MainActivity:onCreate: contains")
        }
        Thread { oom(this) }.start()

        val v2 = NativeLib().stringFromJNI()
        println("Intag: MainActivity:onCreate: $v2")

    }
}

fun oom(context: Context) {
    val list = mutableListOf<ByteArray>()

    while (true) {
        list.add(ByteArray(10_000_000))
        showInfo(context)
        sleep(1_000 * 5)
    }
}

fun showInfo(context: Context) {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val totalRam = memoryInfo.totalMem / (1024 * 1024)
    val availableRam = memoryInfo.availMem / (1024 * 1024)

    val maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024) // в МБ
    val totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024) // в МБ
    val freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024) // в МБ

    println("Intag Всего оперативной памяти: ${totalRam}MB")
    println("Intag Свободно оперативной памяти: ${availableRam}MB")
    println("Intag Максимальная память приложения: $maxMemory MB")
    println("Intag Всего памяти, выделенной приложению: $totalMemory MB")
    println("Intag Свободная память приложения: $freeMemory MB")
    println("Intag ____________________________________________")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OOMappTheme {
        Greeting("Android")
    }
}