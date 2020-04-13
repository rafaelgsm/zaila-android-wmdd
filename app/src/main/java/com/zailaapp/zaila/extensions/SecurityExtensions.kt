package com.zailaapp.zaila.extensions

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

////////////////////////////////////////////////////////////
// SHA-256
////////////////////////////////////////////////////////////

/**
 * Returns a string, hashed with SHA-256.
 *
 * 1. Turns String into byteArray
 * 2. Hashes byteArray into sha256
 * 3. Encode the result into a String, using HEX
 * 4. Returns the String (Encoding and return is happening on the ".let")
 */
fun String.toSha256HexDigest(): String =
    sha256(toByteArray()).let {
        var digestedResultInHexFormat = ""
        for (b in it) {
            val st = String.format("%02X", b)
            digestedResultInHexFormat += st
        }

        digestedResultInHexFormat
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
