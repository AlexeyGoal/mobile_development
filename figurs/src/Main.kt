package org.example

import Circle
import Rect
import Square

fun main() {

    println("===== Circle =====")
    val circle = Circle(x = 10, y = 20, radius = 5)
    println("Исходная: $circle")
    circle.resize(3)
    println("resize(3): $circle")
    circle.rotate(RotateDirection.Clockwise, 0, 0)
    println("rotate(Clockwise, 0, 0): $circle")

    println("\n===== Rect =====")
    val rect = Rect(x = 0, y = 0, width = 10, height = 4)
    rect.name = "Прямоугольник"
    println("Исходный: $rect")
    rect.move(5, 3)
    println("move(5, 3): $rect")
    rect.resize(2)
    println("resize(2): $rect")
    rect.rotate(RotateDirection.Clockwise, 0, 0)
    println("rotate(Clockwise, 0, 0): $rect")

    println("\n===== Square =====")
    val square = Square(x = 2, y = 3, side = 4)
    square.name = "Квадрат №1"
    println("Исходный: $square")

    // Проверка move
    square.move(3, 5)
    println("После move(3, 5): $square")

    // Проверка resize
    square.resize(3)
    println("После resize(3): $square")

    // Проверка rotate по часовой стрелке вокруг (0, 0)
    square.rotate(RotateDirection.Clockwise, centerX = 0, centerY = 0)
    println("После rotate(Clockwise, 0, 0): $square")

}