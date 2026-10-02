package com.hogargo.app.ui.household

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.household.AuthError
import com.hogargo.app.data.household.HouseholdCode
import com.hogargo.app.ui.theme.BrandBrownStrong
import com.hogargo.app.ui.theme.BrandOrangeContainer

@StringRes
fun AuthError.messageRes(): Int = when (this) {
    AuthError.EMPTY_NAME -> R.string.auth_error_name_empty
    AuthError.EMPTY_HOUSEHOLD_NAME -> R.string.auth_error_household_empty
    AuthError.INVALID_CODE -> R.string.auth_error_code_invalid
    AuthError.CODE_NOT_FOUND -> R.string.auth_error_code_not_found
    AuthError.NAME_TAKEN -> R.string.auth_error_name_taken
    AuthError.MEMBER_NOT_FOUND -> R.string.auth_error_member_not_found
    AuthError.CODE_TAKEN -> R.string.auth_error_code_taken
<<<<<<< Updated upstream
=======
    AuthError.PENDING_APPROVAL -> R.string.auth_error_pending_approval
>>>>>>> Stashed changes
}

/** Which field an [AuthError] should be shown under. */
fun AuthError.isCodeError(): Boolean =
    this == AuthError.INVALID_CODE || this == AuthError.CODE_NOT_FOUND || this == AuthError.CODE_TAKEN

/** Peach circle with a house icon and a small badge, used at the top of the Join / Create cards. */
@Composable
fun HeaderBadge(icon: ImageVector, badge: ImageVector, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(88.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(80.dp)
                .clip(CircleShape)
                .background(BrandOrangeContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(40.dp))
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(badge, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun FieldLabel(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun HogarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = true,
        isError = errorText != null,
        supportingText = if (errorText != null) {
            { Text(errorText) }
        } else {
            null
        },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = imeAction),
        keyboardActions = KeyboardActions(onNext = { onImeAction() }, onDone = { onImeAction() }),
    )
}

/**
 * Invitation code entry: six boxes (3 + 3) backed by one invisible text field, so the keyboard,
 * paste and backspace all behave naturally. Accepts `HGR789` and `HGR-789` alike.
 */
@Composable
fun CodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onDone: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = { onValueChange(HouseholdCode.normalize(it)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        cursorBrush = SolidColor(Color.Transparent),
        textStyle = TextStyle(color = Color.Transparent),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(HouseholdCode.LENGTH) { index ->
                    if (index == 3) {
                        Box(
                            Modifier
                                .width(8.dp)
                                .height(2.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant),
                        )
                    }
                    CodeBox(
                        char = value.getOrNull(index),
                        active = focused && index == value.length.coerceAtMost(HouseholdCode.LENGTH - 1),
                        isError = isError,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
    )
}

@Composable
private fun CodeBox(char: Char?, active: Boolean, isError: Boolean, modifier: Modifier = Modifier) {
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        active -> BrandBrownStrong
        else -> MaterialTheme.colorScheme.outline
    }
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .aspectRatio(0.8f)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(if (active) 2.dp else 1.dp, borderColor, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = char?.toString() ?: "•",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (char != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun BigActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (loading) containerColor.copy(alpha = 0.7f) else containerColor)
            .clickable(enabled = !loading, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f, fill = false),
            )
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.padding(start = 10.dp).size(22.dp))
        }
    }
}

@Composable
fun BottomLinkText(prefix: String, link: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = prefix,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = " $link",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = BrandBrownStrong,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp, horizontal = 2.dp),
        )
    }
}
