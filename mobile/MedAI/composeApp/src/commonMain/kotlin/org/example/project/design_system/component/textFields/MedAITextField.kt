package org.example.project.design_system.component.textFields
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.example.project.core.presentation.util.DateVisualTransformation
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.ui.tooling.preview.Preview

// -----------------------------------------------------------------
// Base Component: MedAiTextField (Built on BasicTextField)
// -----------------------------------------------------------------

@Composable
fun MedAiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    // Design Specs
    val shape = RoundedCornerShape(16.dp)
    val containerColor = MedAITheme.colors.primary.copy(alpha = 0.1f)
    val textColor = MedAITheme.colors.text.primary
    val placeholderColor = MedAITheme.colors.text.secondary.copy(alpha = 0.6f)
    val cursorColor = MedAITheme.colors.primary
    val padding = 16.dp

    val textStyle = MedAITheme.textStyle.body.medium.copy(color = textColor)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        cursorBrush = SolidColor(cursorColor),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .clip(shape)
                    .background(containerColor)
                    .border(
                        width = 1.dp,
                        color = Color.Transparent, // Or MedAiTheme.colors.neutral.copy(0.2f)
                        shape = shape
                    )
                    .padding(horizontal = padding, vertical = padding), // Content padding
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    // Placeholder Logic
                    if (value.isEmpty() && placeholder != null) {
                        BasicText(
                            text = placeholder,
                            style = textStyle.copy(color = placeholderColor)
                        )
                    }
                    // The actual input field
                    innerTextField()
                }

                // Trailing Icon (if exists)
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(contentAlignment = Alignment.Center) {
                        trailingIcon()
                    }
                }
            }
        }
    )
}

// -----------------------------------------------------------------
// Variant: Password Field
// -----------------------------------------------------------------

@Composable
fun MedAiPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Password"
) {
    var passwordVisible by remember { mutableStateOf(false) }

    MedAiTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"

            MedAiIconButton(
                onClick = { passwordVisible = !passwordVisible },
                icon = image,
                contentDescription = description,
                tint = MedAITheme.colors.primary
            )
        }
    )
}

// -----------------------------------------------------------------
// Variant: Date Field
// -----------------------------------------------------------------

@Composable
fun MedAiDateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "DD / MM / YYYY"
) {
    MedAiTextField(
        value = value,
        onValueChange = { newValue ->
            // 1. Filter non-digits (user can't type letters or symbols)
            // 2. Limit to 8 characters (DDMMYYYY)
            val filtered = newValue.filter { it.isDigit() }.take(8)
            onValueChange(filtered)
        },
        modifier = modifier,
        placeholder = placeholder,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = DateVisualTransformation()
    )
}

// -----------------------------------------------------------------
// Variant: Text Area (Multi-line)
// -----------------------------------------------------------------

@Composable
fun MedAiTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    minLines: Int = 4
) {
    MedAiTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.heightIn(min = 120.dp),
        placeholder = placeholder,
        singleLine = false,
        minLines = minLines
    )
}

// -----------------------------------------------------------------
// Helper: Custom Icon Button (No Material3 Dependency)
// -----------------------------------------------------------------

@Composable
private fun MedAiIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    tint: Color,
    size: Dp = 24.dp
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Disable ripple if you strictly don't want Material effects
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = rememberVectorPainter(icon),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(tint),
            modifier = Modifier.size(size)
        )
    }
}

@Preview
@Composable
private fun MedAiTextFieldPreview(){
    MedAITheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MedAITheme.colors.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // 1. Standard Input
            var email by remember { mutableStateOf("") }
            MedAiTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "example@example.com"
            )
            // 2. Password Input
            var password by remember { mutableStateOf("") }
            MedAiPasswordTextField(
                value = password,
                onValueChange = { password = it }
            )
            // 3. Date Input (Visual only for now)
            var date by remember { mutableStateOf("") }
            MedAiDateTextField(
                value = date,
                onValueChange = { date = it },
                placeholder = "DD / MM / YYYY"
            )
            // 4. Text Area
            var reason by remember { mutableStateOf("") }
            MedAiTextArea(
                value = reason,
                onValueChange = { reason = it },
                placeholder = "Enter Your Reason Here..."
            )
        }
    }
}
