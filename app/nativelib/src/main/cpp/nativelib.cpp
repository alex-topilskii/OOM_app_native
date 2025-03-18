#include <jni.h>
#include <string>
#include <iostream>
#include <exception>
#include <cstdlib>

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

    std::string hello = "Hello from C++ nativeNPE";
    return env->NewStringUTF(hello.c_str());
}

//
// Глобальный обработчик для необработанных исключений
void terminate_handler() {
    std::cerr << "Необработанное исключение! Завершаем программу.\n";
    std::abort();  // Завершаем программу, если исключение не обработано
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_setNativeHandler(
        JNIEnv *env,
        jobject /* this */
) {
    // Устанавливаем глобальный обработчик
    std::set_terminate(terminate_handler);


    std::string hello = "Hello from C++ setNativeHandler";
    return env->NewStringUTF(hello.c_str());
}