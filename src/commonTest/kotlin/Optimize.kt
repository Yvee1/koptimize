import koptimize.maximize
import koptimize.minimize
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertTrue

class Optimize {
    enum class Objective {
        MINIMIZE, MAXIMIZE
    }

    private fun test(a: Double, b: Double, expectedX: Double,
                     expectedF: Double, objective: Objective, f: (Double) -> Double) {
        val minMax = if (objective == Objective.MAXIMIZE) ::maximize else ::minimize
        val tol = 1E-5
        val maxIters = 100
        val result = minMax(a, b, tol, maxIters, f)
        val sqrtEps = sqrt(2.2E-16)
        val t = sqrtEps * result.x + tol

        assertTrue("Incorrect x.\nExpected $expectedX but is ${result.x}") {
            abs(result.x - expectedX) < 3 * t
        }
        assertTrue("Incorrect function value") {
            abs(result.fx - expectedF) < 3 * t
        }
        assertTrue("Iterations exceeded\nIterations: ${result.iterations} > $maxIters") {
            result.iterations <= maxIters
        }
    }

    @Test
    fun simpleQuadraticMinimize() = test(-1.0, 1.0, 0.0, 0.0, Objective.MINIMIZE)
        { x -> x * x }

    @Test
    fun simpleQuadraticMaximize() = test(-1.0, 1.0, 0.0, 0.0, Objective.MAXIMIZE)
        { x -> -x * x }

    @Test
    fun quadraticMinimize() = test(-100.0, 100.0, 42.0, 21.0, Objective.MINIMIZE)
        { x -> (x - 42.0).pow(2) / 1E6 + 21 }

    @Test
    fun quadraticMaximize() = test(-100.0, 100.0, 42.0, -21.0, Objective.MAXIMIZE)
        { x -> -(x - 42.0).pow(2) / 1E6 - 21 }
}