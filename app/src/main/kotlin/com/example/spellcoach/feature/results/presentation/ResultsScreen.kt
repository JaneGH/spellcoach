package com.example.spellcoach.feature.results.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.R
import com.example.spellcoach.core.designsystem.components.DesignProgressBar
import com.example.spellcoach.core.designsystem.components.LearningCard
import com.example.spellcoach.core.designsystem.components.SpellCoachTopBar

@Composable
fun ResultsScreen(
    onBack: () -> Unit,
    onPracticeAgain: (Long) -> Unit,
    onGoToLists: () -> Unit,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val result = viewModel.result
    val toPractice = result?.let { (it.total - it.correct).coerceAtLeast(0) } ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        SpellCoachTopBar(
            showBack = true,
            onBack = onBack,
            brandTitle = "SpellCoach",
            brandAccent = null,
            screenTitle = null,
            subtitleBelowBrand = null
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_results),
                    contentDescription = null
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Good job!",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "You worked hard and learned\nsome tricky words today.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569)
            )

            Spacer(Modifier.height(14.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                val correct = result?.correct ?: 0
                val total = result?.total ?: 0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$correct",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF16A34A)
                    )
                    Text(
                        text = " / $total",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFD1FAE5))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "  Words Correct",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$toPractice",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF7C3AED)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFEDE9FE))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "  To Practice",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5B21B6)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Goal", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text(text = "80%", fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }
                Spacer(Modifier.height(10.dp))
                DesignProgressBar(progress = 0.8f, fullMastered = false)
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Just 2 more sessions to reach your\ndiamond badge!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569)
                )
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = {
                    val id = result?.listId
                    if (id != null) onPracticeAgain(id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B6B8C))
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = "  Practice Again",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = onGoToLists,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF0B6B8C)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0B6B8C))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                    tint = Color(0xFF0B6B8C)
                )
                Text(
                    text = "  Go to Lists",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

