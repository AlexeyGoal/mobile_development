class Rect(
    var x: Int,
    var y: Int,
    val width: Int,
    val height: Int
) : Movable, Figure(0), Transforming {

    var color: Int = -1

    lateinit var name: String


    constructor(rect: Rect) : this(rect.x, rect.y, rect.width, rect.height)


    override fun move(dx: Int, dy: Int) {
        x += dx; y += dy
    }
    
    override fun area(): Float {
        return (width * height).toFloat()
    }


    override fun resize(zoom: Int) {
        require(zoom > 0) { "Zoom must be positive" }

        scaledWidth *= zoom
        scaledHeight *= zoom
    }


    private var scaledWidth: Int = width
    private var scaledHeight: Int = height


    val currentWidth: Int get() = scaledWidth
    val currentHeight: Int get() = scaledHeight

    fun currentArea(): Float = (scaledWidth * scaledHeight).toFloat()


    override fun rotate(direction: RotateDirection, centerX: Int, centerY: Int) {

        val cx = x + scaledWidth / 2.0
        val cy = y + scaledHeight / 2.0

        val dx = cx - centerX
        val dy = cy - centerY

        val (newDx, newDy) = when (direction) {
            RotateDirection.Clockwise -> -dy to dx
            RotateDirection.CounterClockwise -> dy to -dx
        }

        val newCx = centerX + newDx
        val newCy = centerY + newDy

        val tmp = scaledWidth
        scaledWidth = scaledHeight
        scaledHeight = tmp


        x = (newCx - scaledWidth / 2.0).toInt()
        y = (newCy - scaledHeight / 2.0).toInt()
    }

    override fun toString(): String =
        "Rect(id=$id, pos=($x, $y), size=${scaledWidth}x${scaledHeight}, " +
                "area=${currentArea()}, name='${if (::name.isInitialized) name else "<не задано>"}')"
}