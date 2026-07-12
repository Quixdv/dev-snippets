# Add project specific ProGuard rules here.

# Room
-keep class androidx.room.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.devsnippets.app.**$$serializer { *; }
-keepclassmembers class com.devsnippets.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.devsnippets.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
