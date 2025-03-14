package com.example.oomapp

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getSystemService
import com.example.oomapp.ui.theme.OOMappTheme
import java.lang.Thread.sleep

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

       oom()

        setContent {
            OOMappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun oom() {
    val list = mutableListOf<ByteArray>()

    while (true) {
        list.add(ByteArray(10_000_000))
        showInfo()
        sleep(1000)
    }
}

fun showInfo() {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
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