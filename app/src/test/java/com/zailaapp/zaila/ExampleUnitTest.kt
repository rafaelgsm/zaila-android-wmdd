package com.zailaapp.zaila

import android.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Test
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun interpolator_test() {


        fun getInterpolationAccelerate(input: Float): Float {
            return Math.pow(input.toDouble(), 2.0).toFloat()
        }

        fun getInterpolationDecelerate(input: Float): Float {
            return (1.0f - (1.0f - input) * (1.0f - input))
        }

        fun getInterpolationAccelerateDecelerate(input: Float): Float {
            return (Math.cos((input + 1) * Math.PI) / 2.0f).toFloat() + 0.5f
        }

        val arrayAcc = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val arrayDec = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val arrayAccDedc = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        for (i in 1..99) {
            val resultAcc = getInterpolationAccelerate(i / 100f)
            val resultDec = getInterpolationDecelerate(i / 100f)
            val resultAccDec = getInterpolationAccelerateDecelerate(i / 100f)


            arrayAcc[(resultAcc * 10).toInt()]++
            arrayDec[(resultDec * 10).toInt()]++
            arrayAccDedc[(resultAccDec * 10).toInt()]++

        }

        println("${arrayAcc.toList()} arrayAcc")
        println("${arrayDec.toList()} arrayDec")
        println("${arrayAccDedc.toList()} arrayAccDedc")

        assertEquals(4, 2 + 2)
    }

}
