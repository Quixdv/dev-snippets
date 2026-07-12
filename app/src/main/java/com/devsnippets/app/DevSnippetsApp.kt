package com.devsnippets.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Annotated for Hilt so it can generate the
 * dependency graph (SingletonComponent) used across the whole app.
 */
@HiltAndroidApp
class DevSnippetsApp : Application()
