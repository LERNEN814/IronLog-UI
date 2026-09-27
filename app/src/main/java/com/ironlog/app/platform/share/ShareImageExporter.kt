package com.ironlog.app.platform.share

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * M5-T5.3. Saving to Pictures uses MediaStore, which needs no permission on API 29+.
 * API 26–28 would need WRITE_EXTERNAL_STORAGE (maxSdkVersion=28) — NOT requested here;
 * see docs/handoff/M5.md "待审批". Sharing works on all API levels via FileProvider + cacheDir.
 */
@Singleton
class ShareImageExporter @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /** @return the MediaStore uri on API 29+, null on older releases (permission not requested). */
    fun saveToPictures(bitmap: Bitmap, fileName: String): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null

        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/IronLog")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null
        resolver.openOutputStream(uri)?.use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        return uri
    }

    /** ACTION_SEND intent backed by a cache file via FileProvider (no storage permission needed). */
    fun shareIntent(bitmap: Bitmap, fileName: String): Intent {
        val directory = File(context.cacheDir, SHARE_DIR).apply { mkdirs() }
        val file = File(directory, fileName)
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private companion object {
        const val SHARE_DIR = "share"
    }
}
