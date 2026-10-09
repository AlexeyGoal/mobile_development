
abstract class Figure(val id: Int) {
    // мы не знаем площади абстрактной фигуры, но это не мешает нам определить метод
    abstract fun area(): Float
}