package com.example.oomapp

import android.app.ActivityManager
import android.app.Instrumentation
import android.content.Context
import android.opengl.GLES20
import android.os.Bundle
import android.os.Debug
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
    val nativeLib = NativeLib()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        nativeLib.setNativeHandler()

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
    }

    @Composable
    private fun NativeColumn() {
        Column {
            Button(
                onClick = {
                    nativeLib.nativeNPE()
                }
            ) {
                Text("Throw cpp NPE")
            }
            Button(
                onClick = { nativeLib.nativeOOM() }
            ) {
                Text("Throw cpp OOM (4gb)")
            }
            Button(
                onClick = { nativeLib.nativeOOMFill() }
            ) {
                Text("Throw cpp OOM (4gb) fill")
            }
            Button(
                onClick = { nativeLib.add100Mb() }
            ) {
                Text("Add 100mb cpp")
            }
            Button(
                onClick = { nativeLib.add100MbFill() }
            ) {
                Text("Add 100mb cpp fill")
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
        val threshold = memoryInfo.threshold / (1024 * 1024)

        val maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024) // в МБ
        val totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024) // в МБ
        val freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024) // в МБ

        val nativeHeapSize = Debug.getNativeHeapSize() / (1024 * 1024) // в MB
        val nativeHeapAllocated = Debug.getNativeHeapAllocatedSize() / (1024 * 1024) // в MB
        val nativeHeapFree = Debug.getNativeHeapFreeSize() / (1024 * 1024) // в MB


        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formattedTime = currentTime.format(formatter)
        val glError = GLES20.glGetError()

        println("Intag _________________${formattedTime}__${glError}____________________")
        println("Intag O Всего оперативной памяти: ${totalRam} MB")
        println("Intag O Свободно оперативной памяти: ${availableRam} MB")
        println("Intag M Максимальная память приложения: $maxMemory MB")
        println("Intag M Всего памяти, выделенной приложению: >$totalMemory< MB")
        println("Intag M Освобожденная память приложения: $freeMemory MB")
        println("Intag N Размер нативного heap: ${nativeHeapSize} MB")
        println("Intag N Выделено в нативном heap: >${nativeHeapAllocated}< MB")
        println("Intag N Освобожденная в нативном heap: ${nativeHeapFree} MB")
        println("Intag Порог low memory: ${threshold} MB")

        return "ALL device RAM: ${totalRam}MB \n" +
                "Free RAM: ${availableRam}MB\n" +
                "App ram max: $maxMemory MB\n" +
                "App ram total: $totalMemory MB\n" +
                "App ram free: $freeMemory MB\n" +
                "Native heap size: $nativeHeapSize MB\n" +
                "Native heap allocated: $nativeHeapAllocated MB\n" +
                "Native heap free: $nativeHeapFree MB\n" +
                "Low memory threshold: $threshold MB\n" +
                "$formattedTime\n + $glError"
    }
}
