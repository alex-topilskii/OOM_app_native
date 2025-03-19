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

/***
 * Вызывает ошибку OutOfMemoryError без физического заполнения
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_nativeOOM(
        JNIEnv *env,
        jobject /* this */
) {

    const size_t large_size = 1024 * 1024 * 1023 + 1024; // 4 GB
    int *large_array = new int[large_size];

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

/***
 * Вызывает ошибку OutOfMemoryError с физическим заполнением
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_nativeOOMFill(
        JNIEnv *env,
        jobject /* this */
) {

    const size_t large_size = 1024 * 1024 * 1023 + 1024;  //  4 GB
    int *large_array = new int[large_size];

    // Заполняем память, чтобы ОС выделила реальные физические страницы RAM
    for (size_t i = 0; i < large_size; i++) {
        large_array[i] = i;
    }

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

/***
 * Добавляет 100mb в память без очистки, без реального заполнения
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_add100Mb(
        JNIEnv *env,
        jobject /* this */
) {

    const size_t large_size = 1024 * 1024 * 25;
    int *large_array = new int[large_size];

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

/***
 * Добавляет 100mb в память без очистки, с реальным заполнением
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_add100MbFill(
        JNIEnv *env,
        jobject /* this */
) {

    const size_t large_size = 1024 * 1024 * 25;
    int *large_array = new int[large_size];

    // Заполняем память, чтобы ОС выделила реальные физические страницы RAM
    for (size_t i = 0; i < large_size; i++) {
        large_array[i] = i;
    }

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