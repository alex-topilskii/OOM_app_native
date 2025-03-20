#!/bin/bash

# Указываем имя файла для вывода
output_file="log_$(date +'%Y%m%d_%H%M%S')_data_all_mem_pixel7.txt"

# Включаем цикл, который будет выполняться бесконечно, пока не будет остановлен вручную
while true
do
     echo "-----новый шаг замера--$(date +'%Y%m%d_%H%M%S')---"
    # Добавляем временную метку (дата и время) в файл, чтобы отличать результаты друг от друга
     echo "--------------------" >> "$output_file"
     date >> "$output_file"
     echo "--------------------" >> "$output_file"

    # Выполняем команду adb и сохраняем вывод в файл
    adb shell dumpsys meminfo | tail -n 10 >> "$output_file"

    # Добавляем временную метку (дата и время) в файл, чтобы отличать результаты друг от друга
     echo "--------------------" >> "$output_file"
     date >> "$output_file"
     echo "--------------------" >> "$output_file"

    # Выполняем команду adb и сохраняем вывод в файл
    adb shell dumpsys meminfo com.example.oomapp | head -n 51 >> "$output_file"
done
