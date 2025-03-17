#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_nativeOOM(
        JNIEnv *env,
        jobject /* this */
) {

    const size_t large_size = 1024 * 1024 * 1023 + 1024; // 1 GB
    int *large_array = new int[large_size];

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_nativeNPE(
        JNIEnv *env,
        jobject /* this */
) {

    // Попытка записи в null-указатель вызовет ошибку сегментации
    int *ptr = nullptr;
    *ptr = 42;

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}