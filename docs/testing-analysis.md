# SpellCoach testing analysis

## Stack

| Area | Choice |
|------|--------|
| DI | Hilt |
| UI | Jetpack Compose |
| Unit tests | JUnit 4, coroutines-test, Turbine, Truth |
| JVM Compose UI | Robolectric + `ui-test-junit4` |
| Device UI | Compose UI Test in `androidTest` |
| Fakes | `app/src/sharedTest/kotlin` (shared by `test` and `androidTest`) |
| Mocking | None (fakes only) |

## Test layout

| Source set | Contents |
|------------|----------|
| `test` | ViewModel unit tests, Robolectric Compose tests for `WordListsScreen` |
| `androidTest` | Instrumented Compose tests for `WordListsScreen` |
| `sharedTest` | `FakeWordRepository`, `FakeSettingsRepository`, `FakeSpellCoachTextToSpeech`, `WordListFixtures` |

## Commands

```bash
./gradlew test                  # JVM unit + Robolectric UI tests
./gradlew connectedAndroidTest  # Requires emulator or device
```

## Notes

- Main screen = **Word lists** (`WordListsScreen`).
- `PracticeViewModel` / `AddWordsViewModel` use ML Kit and Android APIs; cover with integration tests later if needed.
