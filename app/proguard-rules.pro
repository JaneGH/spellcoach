# SpellCoach — release shrinking (enable `isMinifyEnabled` when you are ready to validate R8 on device).
# Hilt / Room ship consumer rules; keep ML Kit and model classes if you turn minification on.

-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
