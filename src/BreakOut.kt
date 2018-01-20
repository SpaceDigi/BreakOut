import acm.graphics.GLabel
import acm.graphics.GOval
import acm.graphics.GPoint
import acm.graphics.GRect
import acm.program.GraphicsProgram
import acm.util.RandomGenerator
import java.awt.Color
import java.awt.Dimension
import java.awt.Event
import java.awt.event.MouseEvent

/**
 * Created by SpaceDigi on 06.01.2018.
 */

class BreakOut : GraphicsProgram() {
    private var tries = 3
    private lateinit var label: GLabel
    var HEIGHT: Int = 600
    var WIDTH: Int = 400
    var BALL_RADIUS: Int = 20
    val ROCKET_HEIGHT: Int = 15
    val ROCKET_WIDTH: Int = BALL_RADIUS * 3
    val BRICK_HEIGHT: Int = 10
    val BRICK_WIDTH: Int = 35
    val BRICK_SPACE: Int = 5
    val RAWS_BRICK = 10
    var COLUMNS_BRICK = 10
    private lateinit var ball_move: Thread
    lateinit var ball: GOval
    lateinit var rocket: GRect
    override fun run() {
        this.size = Dimension(WIDTH, HEIGHT)
        addRocket()
        addBall()
        addBricks()
        addMouseListeners()
    }

    private fun addBricks() {
        var brick: GRect
        for (k in 0..RAWS_BRICK - 1) {
            for (i in 0..COLUMNS_BRICK - 1) {
                brick = GRect((i * BRICK_SPACE + i * BRICK_WIDTH).toDouble(), (k * BRICK_SPACE + k * BRICK_HEIGHT).toDouble(), BRICK_WIDTH.toDouble(), BRICK_HEIGHT.toDouble())
                when (k) {
                    0,1 -> brick.fillColor = Color.BLUE
                    2,3 -> brick.fillColor = Color.ORANGE
                    4,5 -> brick.fillColor = Color.RED
                    6,7 -> brick.fillColor = Color.GREEN
                    8,9 -> brick.fillColor = Color.YELLOW
                }
                brick.isFilled = true
                add(brick)
            }
        }
    }

    private fun addRocket() {
        rocket = GRect((WIDTH / 2 - ROCKET_WIDTH / 2).toDouble(), (HEIGHT - BALL_RADIUS).toDouble(), ROCKET_WIDTH.toDouble(), ROCKET_HEIGHT.toDouble())
        rocket.fillColor = Color.BLACK
        rocket.isFilled = true
        add(rocket)

    }

    private fun addBall() {
        ball = GOval((WIDTH / 2 + BALL_RADIUS / 2).toDouble(), (HEIGHT / 2 + BALL_RADIUS).toDouble(), BALL_RADIUS.toDouble(), BALL_RADIUS.toDouble())
        ball.fillColor = Color.BLACK
        ball.isFilled = true
        add(ball)
        ball_move = Thread(RunBall(this))
        ball_move.priority = 1
        ball_move.start()
    }

    override fun mouseMoved(evt: MouseEvent?) {
        if (evt!!.x > 0 && evt.x < WIDTH)
            rocket.location = GPoint((evt!!.x).toDouble(), (HEIGHT - BALL_RADIUS).toDouble())
    }

    override fun mouseClicked(e: MouseEvent?) {
        if (tries > 0) {
            addBall()
            remove(label)
        }
    }

    fun resetGame() {
        remove(ball)
        if (--tries > 0) {
            label = GLabel("Tries: $tries", (WIDTH / 2 - 50).toDouble(), (HEIGHT / 2 - 20).toDouble())
        } else
            label = GLabel("Sorry, game over :(", (WIDTH / 2 - 50).toDouble(), (HEIGHT / 2 - 20).toDouble())
        add(label)
    }
}

class RunBall(var game: BreakOut) : Runnable {
    val MIN = 0.0002
    val MAX = 0.0005
    var vx: Double = RandomGenerator.getInstance().nextDouble(MIN, MAX)
    var vy: Double = RandomGenerator.getInstance().nextDouble(MIN, MAX)
    override fun run() {
        while (true) {
            if (game.ball.y + game.BALL_RADIUS >= game.HEIGHT) {
                game.resetGame()
                return
            }
            if (game.ball.x + game.BALL_RADIUS >= game.WIDTH || game.ball.x <= 0) {
                if (vx > 0)
                    vx = RandomGenerator.getInstance().nextDouble(MIN, MAX)
                else
                    vx = -RandomGenerator.getInstance().nextDouble(MIN, MAX)
                vx = -vx
            }
            if (game.ball.y <= 0) {
                if (vy > 0)
                    vy = RandomGenerator.getInstance().nextDouble(MIN, MAX)
                else
                    vy = -RandomGenerator.getInstance().nextDouble(MIN, MAX)
                vy = -vy
            }
            if (game.ball.x >= game.rocket.x && game.ball.x <= game.rocket.x + game.ROCKET_WIDTH
                    && game.ball.y + game.BALL_RADIUS >= game.rocket.y
                    && game.ball.y + game.BALL_RADIUS <= game.rocket.y + game.ROCKET_HEIGHT) {
                if (vy > 0)
                    vy = RandomGenerator.getInstance().nextDouble(MIN, MAX)
                else
                    vy = -RandomGenerator.getInstance().nextDouble(MIN, MAX)
                vy = -vy
            }
            var component = game.getElementAt(game.ball.x + game.BALL_RADIUS / 2, (game.ball.y - 1))
            if (component != null && component.y <= 200) {
                game.remove(game.getElementAt(game.ball.x + game.BALL_RADIUS / 2, (game.ball.y - 1)))
                if (vy > 0)
                    vy = RandomGenerator.getInstance().nextDouble(MIN, MAX)
                else
                    vy = -RandomGenerator.getInstance().nextDouble(MIN, MAX)
                vy = -vy
            }
            game.ball.move(vx, vy)
        }
    }
}