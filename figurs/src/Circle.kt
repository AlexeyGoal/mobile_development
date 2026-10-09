import kotlin.math.PI

class Circle(
    var x: Int,
    var y: Int,
    var radius: Int
) : Figure(0), Transforming {

    override fun area(): Float {
        return (PI * radius * radius).toFloat()
    }

    override fun resize(zoom: Int) {
        require(zoom > 0) { "Zoom must be positive" }
        radius *= zoom
    }


    override fun rotate(direction: RotateDirection, centerX: Int, centerY: Int) {

        val dx = x - centerX
        val dy = y - centerY

        // Поворот на 90 градусов
        val (newDx, newDy) = when (direction) {
            RotateDirection.Clockwise -> -dy to dx
            RotateDirection.CounterClockwise -> dy to -dx
        }


        x = centerX + newDx
        y = centerY + newDy
    }

    override fun toString(): String {
        return "Circle(id=$id, center=($x, $y), radius=$radius, area=${area()})"
    }
}