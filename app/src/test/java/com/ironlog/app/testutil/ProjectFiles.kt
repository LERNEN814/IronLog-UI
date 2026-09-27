package com.ironlog.app.testutil

import java.io.File

/** Locates repository files from the Gradle test working directory (module or repo root). */
object ProjectFiles {
    val root: File = generateSequence(File("").absoluteFile) { it.parentFile }
        .first { File(it, "settings.gradle.kts").isFile }

    fun file(relative: String): File = File(root, relative)
}
