package com.example.oomapp

import android.app.ActivityManager
import android.app.Instrumentation
import android.content.Context
import android.opengl.GLES20
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nativelib.NativeLib
import com.example.oomapp.ui.theme.OOMappTheme
import kotlinx.coroutines.delay
import java.lang.Thread.sleep
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    val listLoop = mutableListOf<ByteArray>()
    val list200 = mutableListOf<ByteArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OOMappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var text by remember { mutableStateOf("Hello, world!") }

                    LaunchedEffect(true) {
                        while (true) {
                            text = showInfo(this@MainActivity)
                            delay(1000)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        Row {
                            OOMColumn()
                            NativeColumn()
                        }

                        Spacer(Modifier.height(24.dp))
                        Text(text)
                    }
                }
            }
        }

        val gl = GLES20.glGetString(GLES20.GL_EXTENSIONS)
        if (gl?.contains("GL_NVX_gpu_memory_info") == true) {
            println("Intag: MainActivity:onCreate: contains")
        }
        val stringFromCpp = NativeLib().stringFromJNI()
        println("Intag: MainActivity:onCreate: $stringFromCpp")
    }

    @Composable
    private fun NativeColumn() {
        Column {
            Button(
                onClick = {
                    try {
                        NativeLib().nativeNPE()

                    } catch (e: Exception) {
                        println("Intag: MainActivity:NativeColumn: $e")
                    }
                }
            ) {
                Text("Throw cpp NPE")
            }
            Button(
                onClick = {
                    NativeLib().nativeOOM()
                }
            ) {
                Text("Throw cpp OOM")
            }
        }
    }

    @Composable
    private fun OOMColumn() {
        Column {
            Button(onClick = ::allocate1) { Text("Allocate +1mb") }
            Button(onClick = ::allocate10) { Text("Allocate +10mb") }
            Button(onClick = ::allocate100) { Text("Allocate +100mb") }
            Button(onClick = ::runOOMLoop) { Text("Run 10 mb loop") }
        }
    }

    private fun runOOMLoop() {
        Thread { loopAllocation(this@MainActivity) }.start()
    }

    fun allocate1() {
        list200.add(ByteArray(1_000_000))
    }

    fun allocate10() {
        list200.add(ByteArray(10_000_000))
    }

    fun allocate100() {
        list200.add(ByteArray(100_000_000))
    }

    fun loopAllocation(context: Context) {
        while (true) {
            listLoop.add(ByteArray(10_000_000))
            showInfo(context)
            sleep(1_000 * 5)
        }
    }

    fun showInfo(context: Context): String {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRam = memoryInfo.totalMem / (1024 * 1024)
        val availableRam = memoryInfo.availMem / (1024 * 1024)

        val maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024) // в МБ
        val totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024) // в МБ
        val freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024) // в МБ
        val glError = GLES20.glGetError()
//        println("Intag Всего оперативной памяти: ${totalRam}MB")
//        println("Intag Свободно оперативной памяти: ${availableRam}MB")
//        println("Intag Максимальная память приложения: $maxMemory MB")
//        println("Intag Всего памяти, выделенной приложению: $totalMemory MB")
//        println("Intag Свободная память приложения: $freeMemory MB")
//        println("Intag ____________________________________________")

        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formattedTime = currentTime.format(formatter)

        return "ALL device RAM: ${totalRam}MB \n" +
                "Free RAM: ${availableRam}MB\n" +
                "App ram max: $maxMemory MB\n" +
                "App ram total: $totalMemory MB\n" +
                "App ram free: $freeMemory MB\n" +
                "$formattedTime\n + $glError"
    }
}
