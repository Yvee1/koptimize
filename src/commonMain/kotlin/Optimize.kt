package koptimize

import kotlin.math.abs
import kotlin.math.sqrt

data class OptimizationResult(val x: Double, val fx: Double, val iterations: Int)

/**
 * Returns a local minimum of [f] within [[lower], [upper]].
 */
fun minimize(lower: Double, upper: Double, tolerance: Double, maxIter: Int, f: (Double) -> Double): OptimizationResult {
    // Implements Richard Brent's algorithm from his book "Algorithms for Minimization without Derivatives", p. 79.

    require(lower < upper) {
        "The lower bound exceeds the upper bound.\nlower: $lower\nb: $upper"
    }

    val goldenSection = (3 - sqrt(5.0)) / 2
    val sqrtEps = sqrt(2.2E-16)
    var a = lower
    var b = upper
    var x = a + goldenSection * (b - a)
    var u: Double
    var v = x
    var w = x
    var d = 0.0
    var e = 0.0
    var fx = f(x)
    var iters = 1
    var fu: Double
    var fv = fx
    var fw = fx

    var m = (a + b) / 2
    var tol1 = sqrtEps * abs(x) + tolerance
    var tol2 = 2 * tol1

    while(abs(x - m) > tol2 - (b - a) / 2) {
        var r: Double
        var p: Double
        var q: Double

        var parabolicSucceeded = false
        if (abs(e) > tol1) {
            // Fit parabola
            r = (x - w) * (fx - fv)
            q = (x - v) * (fx - fw)
            p = (x - v) * q - (x - w) * r
            q = 2 * (q - r)
            if (q > 0) {
                p = -p
            } else {
                q = -q
            }
            r = e
            e = d

            if (abs(p) < abs(0.5 * q * r) && p < q * (a - x) && p < q * (b - x)) {
                // Parabolic interpolation step
                d = p / q
                u = x + d

                // f must not be evaluated too close to a or b
                if (u - a < tol2 || b - u < tol2) {
                    d = if (x < m) tol1 else -tol1
                }

                parabolicSucceeded = true
            }
        }

        if (!parabolicSucceeded) {
            // Golden section step
            e = (if (x < m) b else a) - x
            d = goldenSection * e
        }

        // f must not be evaluated too close to a or b
        u = x + (if (abs(d) >= tol1) d else if (d > 0) tol1 else -tol1)
        fu = f(u)
        iters++

        // Update a, b, v, w, and x
        if (fu <= fx) {
            if (u < x) {
                b = x
            } else {
                a = x
            }
            v = w
            fv = fw
            w = x
            fw = fx
            x = u
            fx = fu
        } else {
            if (u < x) {
                a = u
            } else {
                b = u
            }
            if (fu <= fw || w == x) {
                v = w
                fv = fw
                w = u
                fw = fu
            } else if (fu <= fv || v == x || v == w) {
                v = u
                fv = fu
            }
        }

        m = (a + b) / 2
        tol1 = sqrtEps * abs(x) + tolerance
        tol2 = 2 * tol1

        if (iters >= maxIter) break
    }

    return OptimizationResult(x, fx, iters)
}

/**
 * Returns a local maximum of [f] within [[lower], [upper]].
 */
fun maximize(lower: Double, upper: Double, tol: Double, maxIter: Int, f: (Double) -> Double): OptimizationResult {
    val result = minimize(lower, upper, tol, maxIter) { x -> -f(x) }
    return result.copy(fx = -result.fx)
}