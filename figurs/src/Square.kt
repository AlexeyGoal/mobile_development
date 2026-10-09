class Square(
    var x: Int,
    var y: Int,
    var side: Int
) : Movable, Figure(0), Transforming {

    var color: Int = -1
    lateinit var name: String

    constructor(square: Square) : this(square.x, square.y, square.side)

    override fun move(dx: Int, dy: Int) {
        x += dx; y += dy
    }

    override fun area(): Float = (side * side).toFloat()


    override fun resize(zoom: Int) {
        require(zoom > 0) { "Zoom must be positive" }
        side *= zoom
    }

    override fun rotate(direction: RotateDirection, centerX: Int, centerY: Int) {
        // Центр квадрата
        val cx = x + side / 2.0
        val cy = y + side / 2.0

        val dx = cx - centerX
        val dy = cy - centerY

        val (newDx, newDy) = when (direction) {
            RotateDirection.Clockwise -> -dy to dx
            RotateDirection.CounterClockwise -> dy to -dx
        }

        val newCx = centerX + newDx
        val newCy = centerY + newDy

        x = (newCx - side / 2.0).toInt()
        y = (newCy - side / 2.0).toInt()
    }

    override fun toString(): String =
        "Square(id=$id, pos=($x, $y), side=$side, area=${area()}, " +
                "name='${if (::name.isInitialized) name else "<не задано>"}')"
}
