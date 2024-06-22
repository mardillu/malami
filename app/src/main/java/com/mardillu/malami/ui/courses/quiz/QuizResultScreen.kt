package com.mardillu.malami.ui.courses.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mardillu.malami.R
import com.mardillu.malami.ui.navigation.AppNavigation

/**
 * Created on 30/05/2024 at 12:25 pm
 * @author mardillu
 */
@Composable
fun QuizResultScreen(
    navigation: AppNavigation,
    passed: Boolean,
    obtainableScore: String?,
    obtainedScore: String?,
    viewModel: TakeQuizViewModel
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val image: Painter
            val message: String

            if (passed) {
                image =
                    painterResource(id = R.drawable.ic_quiz_passed)
                message =
                    "Congratulations! You passed the quiz with a score of $obtainedScore/$obtainableScore"
            } else {
                image =
                    painterResource(id = R.drawable.failed)
                message =
                    "Sorry, you did not pass the quiz. Your score is $obtainedScore/$obtainableScore"
            }

            if (passed) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.ic_confetti_bg),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 16.dp)
                    )
                    Image(
                        painter = image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 16.dp)
                            .align(Alignment.BottomCenter)
                    )
                }
            } else {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp)
                )
            }
            Text(
                text = message,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = {
                    navigation.gotToCourseList()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Back to Home", fontSize = 16.sp)
            }
        }
    }
}