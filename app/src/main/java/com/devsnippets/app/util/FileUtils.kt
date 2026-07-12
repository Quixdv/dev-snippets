package com.devsnippets.app.util

import android.content.Context
import android.net.Uri

/** Small helpers for writing/reading text through content:// Uris (Storage Access Framework). */
object FileUtils {

    fun writeTextToUri(context: Context, uri: Uri, text: String) {
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(text.toByteArray(Charsets.UTF_8))
        }
    }

    fun readTextFromUri(context: Context, uri: Uri): String {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            return stream.readBytes().toString(Charsets.UTF_8)
        }
        return ""
    }
}
