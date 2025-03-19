package com.example.nativelib

class NativeLib {

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    external fun nativeOOM(): String
    external fun nativeOOMFill(): String
    external fun add100Mb(): String
    external fun add100MbFill(): String

    external fun nativeNPE(): String
    external fun setNativeHandler(): String

    companion object {
        // Used to load the 'nativelib' library on application startup.
        init {
            System.loadLibrary("nativelib")
        }
    }
}