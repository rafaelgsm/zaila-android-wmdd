package com.zailaapp.zaila.extensions.projectOnly

import com.zailaapp.zaila.BuildConfig
import com.zailaapp.zaila.ZailaApplication

/**
 * nfs://com.zailaapp.zaila/n123
 * http://com.zailaapp.zaila/n123
 */
fun String.getSensorFromUri(): String? {

    val packageName =
        ZailaApplication.instance.packageName?.replace(
            ".${BuildConfig.FLAVOR}",
            ""
        )

    if (packageName != null && this.contains(packageName)) {

        val value = this
            .replace("http://", "")
            .replace("https://", "")
            .replace("nfs://", "")
            .replace("$packageName/", "")


        //todo - validate sensor ID format!!!

        return value

    } //end if package name match

    return null
}