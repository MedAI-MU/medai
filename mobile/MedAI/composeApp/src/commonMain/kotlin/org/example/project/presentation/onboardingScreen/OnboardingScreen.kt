package org.example.project.presentation.onboardingScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import medai.composeapp.generated.resources.CheckYourMedicalHistory
import medai.composeapp.generated.resources.ChooseYourDoctor
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.ScheduleYourAppointments
import medai.composeapp.generated.resources.get_started
import medai.composeapp.generated.resources.next
import medai.composeapp.generated.resources.onboarding_desc_1
import medai.composeapp.generated.resources.onboarding_desc_2
import medai.composeapp.generated.resources.onboarding_desc_3
import medai.composeapp.generated.resources.onboarding_title_1
import medai.composeapp.generated.resources.onboarding_title_2
import medai.composeapp.generated.resources.onboarding_title_3
import medai.composeapp.generated.resources.skip
import org.example.project.core.data.OnboardingStorage
import org.example.project.core.presentation.util.UiText
import org.example.project.core.presentation.util.asString
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.welcomeScreen.WelcomeScreen
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
class OnboardingScreen : Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val storage = remember { OnboardingStorage() }

        // 2. Define Navigation Logic
        fun onComplete() {
            storage.setOnboardingCompleted(true)
            navigator.replace(WelcomeScreen())
        }

        val pages = remember {
            listOf(
                OnboardingPage(
                    title = UiText.StringRes(Res.string.onboarding_title_1),
                    description = UiText.StringRes(Res.string.onboarding_desc_1),
                    image = Res.drawable.ChooseYourDoctor
                ),
                OnboardingPage(
                    title = UiText.StringRes(Res.string.onboarding_title_2),
                    description = UiText.StringRes(Res.string.onboarding_desc_2),
                    image = Res.drawable.ScheduleYourAppointments
                ),
                OnboardingPage(
                    title = UiText.StringRes(Res.string.onboarding_title_3),
                    description = UiText.StringRes(Res.string.onboarding_desc_3),
                    image = Res.drawable.CheckYourMedicalHistory
                )
            )
        }

        val pagerState = rememberPagerState(pageCount = { pages.size })
        val scope = rememberCoroutineScope()

//        val brandGradient = Brush.verticalGradient(
//            colors = listOf(
//                Color(0xFF00E5FF),
//                MedAITheme.colors.background
//            )
//        )

        MedAIScaffold{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(vertical = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    MedAIText(
                        text = UiText.StringRes(Res.string.skip).asString() + " >",
                        style = MedAITheme.textStyle.body.medium,
                        color = MedAITheme.colors.text.secondary,
                        modifier = Modifier.clickable { onComplete() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))


                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Take up available space
                ) { index ->
                    OnBoardingPageContent(page = pages[index])
                }

                // --- Bottom Section: Indicators & Button ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Page Indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pages.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration)
                                MedAITheme.colors.primary
                            else
                                MedAITheme.colors.primary.copy(alpha = 0.2f)

                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(shape = CircleShape)
                                    .background(color)
                                    .size(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Main Action Button
                    val isLastPage = pagerState.currentPage == pages.size - 1
                    val buttonTextRes = if (isLastPage) Res.string.get_started else Res.string.next

                    MedAIButton(
                        text = UiText.StringRes(buttonTextRes).asString(),
                        onClick = {
                            if (isLastPage) {
                                onComplete()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    @Composable
    fun OnBoardingPageContent(page: OnboardingPage) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Image Container
            Image(
                painter = painterResource(page.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MedAIText(
                    text = page.title.asString(),
                    style = MedAITheme.textStyle.headline.large.copy(
                        color = MedAITheme.colors.primary
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                MedAIText(
                    text = page.description.asString(),
                    style = MedAITheme.textStyle.body.large.copy(
                        color = MedAITheme.colors.text.secondary
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
