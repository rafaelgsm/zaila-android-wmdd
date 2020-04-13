package com.zailaapp.zaila

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

//    @Test
//    fun useAppContext() {
//        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.zailaapp.zaila", appContext.packageName)
//    }

    @Test
    fun sha256() {

        val pass = sha256("1234".toByteArray()).let {

            var x = ""
            for (b in it) {
                val st = String.format("%02X", b)
                x += st
            }

            x
        }

//        println("TESTE ${pass}")

        assertEquals("03AC674216F3E15C761EE1A5E255F067953623C8B388B4459E13F978D7C846F4", pass)
    }

    /**
     * Encrypts a ByteArray, with SHA-256.
     *
     * 1. Gets a MessageDigest object.
     * 2. Adds the byteArray to the digest object ("update" method)
     * 3. Returns the encrypted byteArray ("digest" method)
     */
    private fun sha256(byteArray: ByteArray): ByteArray {
        val digest = try {
            MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            MessageDigest.getInstance("SHA")
        }
        return with(digest) {
            update(byteArray)
            digest()
        }
    }

    @Test
    fun testMuseumMock() {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // res/raw/test.json
//        val rawString =
//            appContext.resources.openRawResource(R.raw.museum_mock).bufferedReader()
//                .use { it.readText() }

        val mock = ZailaApplication.instance.resources.openRawResource(R.raw.museum_mock)
            .bufferedReader()
            .use { it.readText() }

        assertEquals("raw", mock)
    }
}
