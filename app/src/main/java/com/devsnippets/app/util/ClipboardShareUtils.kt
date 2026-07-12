package com.devsnippets.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.devsnippets.app.domain.model.Snippet

/** Copies the snippet's code to the system clipboard. */
fun copySnippetToClipboard(context: Context, snippet: Snippet) {
    val clipboardManager = ContextCompat.getSystemService(context, ClipboardManager::class.java)
    val clip = ClipData.newPlainText(snippet.title, snippet.code)
    clipboardManager?.setPrimaryClip(clip)
}

/** Opens the Android share sheet with the snippet formatted as plain text. */
fun shareSnippetAsText(context: Context, snippet: Snippet) {
    val shareText = buildString {
        appendLine(snippet.title)
        appendLine("Language: ${snippet.language.displayName}")
        if (snippet.tags.isNotEmpty()) {
            appendLine("Tags: ${snippet.tags.joinToString(", ")}")
        }
        appendLine()
        appendLine(snippet.code)
        if (snippet.notes.isNotBlank()) {
            appendLine()
            appendLine("Notes:")
            appendLine(snippet.notes)
        }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, snippet.title)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share snippet via").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}
