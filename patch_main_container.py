import re

file_path = 'mobile/MedAI/composeApp/src/commonMain/kotlin/org/example/project/presentation/MainContainerScreen.kt'

with open(file_path, 'r') as f:
    content = f.read()

# Add Mic icon import
content = content.replace(
    'import androidx.compose.material.icons.filled.Person',
    'import androidx.compose.material.icons.filled.Person\nimport androidx.compose.material.icons.filled.Mic'
)

# Add AI Analysis Screen import
content = content.replace(
    'import org.example.project.presentation.profileScreen.ProfileScreen',
    'import org.example.project.presentation.profileScreen.ProfileScreen\nimport org.example.project.presentation.analysis.AnalysisScreen\nimport org.example.project.presentation.analysis.AnalysisViewModel'
)

# Add Analysis tab to navItems logic
content = content.replace(
    'navItems.add(BottomNavItem("Messages", "messages", Icons.Default.ChatBubbleOutline))',
    'navItems.add(BottomNavItem("Messages", "messages", Icons.Default.ChatBubbleOutline))\n\n            if (role == UserRole.PATIENT) {\n                 navItems.add(BottomNavItem("AI Diagnoses", "analysis", Icons.Default.Mic))\n            }'
)

# Handle the click route mapping
content = content.replace(
    '"schedule" -> if(role == UserRole.DOCTOR || role == UserRole.SECRETARY) tabNavigator.current = ScheduleTab',
    '"schedule" -> if(role == UserRole.DOCTOR || role == UserRole.SECRETARY) tabNavigator.current = ScheduleTab\n                                "analysis" -> if(role == UserRole.PATIENT) tabNavigator.current = AnalysisTab'
)

# Handle getRouteFromTab
content = content.replace(
    'ProfileTab -> "profile"',
    'ProfileTab -> "profile"\n            AnalysisTab -> "analysis"'
)

# Add the Tab Object for Analysis
analysis_tab = """

object AnalysisTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Mic)
            return remember { TabOptions(index = 4u, title = "AI Diagnoses", icon = icon) }
        }

    @Composable
    override fun Content() {
        val viewModel = koinInject<AnalysisViewModel>()
        AnalysisScreen(viewModel = viewModel)
    }
}
"""
content = content + analysis_tab

with open(file_path, 'w') as f:
    f.write(content)

print("Patch applied")
