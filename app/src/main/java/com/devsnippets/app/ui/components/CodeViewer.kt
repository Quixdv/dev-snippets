package com.devsnippets.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.devsnippets.app.domain.model.SnippetLanguage
import com.devsnippets.app.ui.theme.CodeTextStyle
import com.devsnippets.app.util.SyntaxHighlighter

/**
 * Displays code with syntax highlighting and optional line numbers,
 * inside a rounded "editor-like" surface.
 */
@Composable
fun CodeViewer(
    code: String,
    language: SnippetLanguage,
    showLineNumbers: Boolean,
    modifier: Modifier = Modifier
) {
    val highlighted = SyntaxHighlighter.highlight(code, language)
    val lineCount = code.split("\n").size

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            if (showLineNumbers) {
                Column(modifier = Modifier.padding(end = 12.dp)) {
                    for (line in 1..lineCount) {
                        Text(
                            text = line.toString(),
                            style = CodeTextStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            Text(
                text = highlighted,
                style = CodeTextStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
