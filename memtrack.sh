#!/bin/bash

# Указываем имя файла для вывода
output_file_all="data_all_mem.txt"
output_file_indrive="data_app_mem.txt"

# Включаем цикл, который будет выполняться бесконечно, пока не будет остановлен вручную
while true
do
     echo "-----новый шаг замера--$date---"
    # Добавляем временную метку (дата и время) в файл, чтобы отличать результаты друг от друга
     echo "--------------------" >> "$output_file_all"
     date >> "$output_file_all"
     echo "--------------------" >> "$output_file_all"

    # Выполняем команду adb и сохраняем вывод в файл
    adb shell dumpsys meminfo | tail -n 6 >> "$output_file_all"

    # Добавляем временную метку (дата и время) в файл, чтобы отличать результаты друг от друга
     echo "--------------------" >> "$output_file_indrive"
     date >> "$output_file_indrive"
     echo "--------------------" >> "$output_file_indrive"

    # Выполняем команду adb и сохраняем вывод в файл
    adb shell dumpsys meminfo com.example.oomapp >> "$output_file_indrive"
done
