package com.example.ntiteamtest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.ntiteamtest.ui.navigation.NavRoutes
import com.example.ntiteamtest.ui.navigation.Navigator
import com.example.ntiteamtest.ui.theme.NtiTeamTestTheme
import dagger.hilt.android.components.ActivityComponent

@SuppressLint("CustomSplashScreen")
class SplashScreen: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NtiTeamTestTheme {
                Box(modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash))
                    val progress = animateLottieCompositionAsState(composition)

                    LottieAnimation(
                        composition = composition,
                        progress = { progress.progress },
                        alignment = Alignment.Center,
                    )

                    if(progress.isAtEnd && progress.isPlaying) {
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    }
                }
            }
        }
    }
}