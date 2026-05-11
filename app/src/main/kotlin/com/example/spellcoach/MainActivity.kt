package com.example.spellcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spellcoach.core.designsystem.theme.SpellCoachTheme
import com.example.spellcoach.core.navigation.SpellCoachNavHost
import com.example.spellcoach.di.SpellCoachEntryPoint
import com.example.spellcoach.domain.model.AppSettings
import com.example.spellcoach.domain.model.ThemePreference
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            SpellCoachEntryPoint::class.java
        )
        val practiceListHolder = entryPoint.practiceListHolder()
        val observeSettings = entryPoint.observeSettingsUseCase()
        setContent {
            val settings by observeSettings()
                .collectAsStateWithLifecycle(
                    initialValue = AppSettings(),
                    lifecycleOwner = LocalLifecycleOwner.current
                )
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (settings.themePreference) {
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
                ThemePreference.SYSTEM -> systemDark
            }
            SpellCoachTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SpellCoachNavHost(practiceListHolder = practiceListHolder)
                }
            }
        }
    }
}
