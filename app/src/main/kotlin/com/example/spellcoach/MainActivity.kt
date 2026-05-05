package com.example.spellcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.spellcoach.di.SpellCoachEntryPoint
import com.example.spellcoach.presentation.navigation.SpellCoachNavHost
import com.example.spellcoach.presentation.theme.SpellCoachTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val practiceListHolder = EntryPointAccessors.fromApplication(
            applicationContext,
            SpellCoachEntryPoint::class.java
        ).practiceListHolder()
        setContent {
            SpellCoachTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SpellCoachNavHost(practiceListHolder = practiceListHolder)
                }
            }
        }
    }
}
