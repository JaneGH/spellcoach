# SpellCoach — app-specific R8 rules.
#
# Consumer rules are already merged from dependencies:
#   - Hilt / Dagger
#   - Room
#   - Navigation Compose
#   - ML Kit (text-recognition, digital-ink-recognition) — protobuf fields, JNI, Gson manifest models
#   - Google Play services / Data Transport (transitive via ML Kit)
#   - OkHttp (transitive via ML Kit digital-ink)
#   - Kotlin Coroutines
#
# Add rules here only for app code patterns that R8 cannot infer from static analysis.

# SettingsDataStore persists enum constant names via Enum.name / Enum.valueOf(String).
-keepclassmembers enum com.itclimb.spellcoach.domain.model.MistakeBehavior { *; }
-keepclassmembers enum com.itclimb.spellcoach.domain.model.ThemePreference { *; }
-keepclassmembers enum com.itclimb.spellcoach.domain.model.Badge { *; }
