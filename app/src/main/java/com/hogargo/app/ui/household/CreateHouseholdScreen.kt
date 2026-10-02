package com.hogargo.app.ui.household

import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.material.icons.filled.Lock
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogargo.app.R
import com.hogargo.app.data.household.AuthError
import com.hogargo.app.data.household.AuthResult
import com.hogargo.app.data.household.HouseholdCode
import com.hogargo.app.ui.theme.BrandBrownStrong
import com.hogargo.app.ui.theme.BrandOrange
import com.hogargo.app.ui.theme.BrandOrangeContainer
import kotlinx.coroutines.launch

@Composable
fun CreateHouseholdScreen(
    generateCode: suspend () -> String,
    onCreate: suspend (userName: String, householdName: String, code: String, recoveryUser: String) -> AuthResult,
    onCreated: () -> Unit,
    onJoinInstead: () -> Unit,
) {
    var userName by rememberSaveable { mutableStateOf("") }
    var householdName by rememberSaveable { mutableStateOf("") }
    var recoveryUser by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<AuthError?>(null) }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val copiedMessage = stringResource(R.string.create_code_copied)

    // The code is created once when the screen opens (and kept across rotation).
    LaunchedEffect(Unit) {
        if (code.isEmpty()) code = generateCode()
    }

    fun submit() {
        if (loading || code.isEmpty()) return
        focusManager.clearFocus()
        loading = true
        scope.launch {
            when (val result = onCreate(userName, householdName, code, recoveryUser)) {
                is AuthResult.Success -> onCreated()
                is AuthResult.Pending -> loading = false
                is AuthResult.Failure -> {
                    error = result.error
                    if (result.error == AuthError.CODE_TAKEN) code = generateCode()
                    loading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        // Header card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HeaderBadge(icon = Icons.Filled.HomeWork, badge = Icons.Filled.Favorite)
            Text(
                text = stringResource(R.string.create_heading),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp),
            )
            Text(
                text = stringResource(R.string.create_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        // Form
        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
            FieldLabel(stringResource(R.string.create_name_label), Icons.Filled.Person)
            HogarTextField(
                value = userName,
                onValueChange = { userName = it; error = null },
                placeholder = stringResource(R.string.create_name_hint),
                errorText = error?.takeIf { it == AuthError.EMPTY_NAME }?.let { stringResource(it.messageRes()) },
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            )
            Spacer(Modifier.height(8.dp))
            FieldLabel(stringResource(R.string.create_household_label), Icons.Filled.Home)
            HogarTextField(
                value = householdName,
                onValueChange = { householdName = it; error = null },
                placeholder = stringResource(R.string.create_household_hint),
                errorText = error?.takeIf { it == AuthError.EMPTY_HOUSEHOLD_NAME }?.let { stringResource(it.messageRes()) },
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            )
            Spacer(Modifier.height(8.dp))
            FieldLabel(stringResource(R.string.recovery_label), Icons.Filled.Lock)
            HogarTextField(
                value = recoveryUser,
                onValueChange = { recoveryUser = it; error = null },
                placeholder = stringResource(R.string.recovery_hint),
                errorText = error?.takeIf { it.isRecoveryError() }?.let { stringResource(it.messageRes()) },
                imeAction = ImeAction.Done,
                onImeAction = { focusManager.clearFocus() },
                capitalization = KeyboardCapitalization.None,
            )
            Text(
                text = stringResource(R.string.recovery_help),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        // Unique code card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(BrandOrangeContainer, BrandOrange.copy(alpha = 0.75f)),
                    ),
                )
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.8f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(Icons.Filled.Key, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(16.dp))
                    Text(
                        stringResource(R.string.create_code_chip),
                        style = MaterialTheme.typography.labelMedium,
                        color = BrandBrownStrong,
                    )
                }
                IconButton(
                    onClick = { scope.launch { code = generateCode(); error = null } },
                    enabled = !loading,
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = stringResource(R.string.create_code_refresh_cd),
                        tint = BrandBrownStrong,
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = HouseholdCode.format(code).ifEmpty { "······" },
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    color = BrandBrownStrong,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandBrownStrong)
                        .clickable(enabled = code.isNotEmpty()) {
                            clipboard.setText(AnnotatedString(HouseholdCode.format(code)))
                            Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text(
                        stringResource(R.string.create_code_copy),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                    )
                }
            }

            error?.takeIf { it == AuthError.CODE_TAKEN || it == AuthError.INVALID_CODE }?.let {
                Text(
                    text = stringResource(it.messageRes()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Text(
                text = stringResource(R.string.create_code_help),
                style = MaterialTheme.typography.bodyMedium,
                color = BrandBrownStrong,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        // Companion
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.img_zori_fox),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BrandOrangeContainer),
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.Pets, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(16.dp))
                    Text(
                        stringResource(R.string.create_companion_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = BrandBrownStrong,
                    )
                }
                Text(
                    stringResource(R.string.create_companion_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        BigActionButton(
            text = stringResource(R.string.create_button),
            icon = Icons.Filled.AutoAwesome,
            onClick = ::submit,
            containerColor = BrandBrownStrong,
            loading = loading,
        )

        Spacer(Modifier.height(8.dp))
        BottomLinkText(
            prefix = stringResource(R.string.create_have_code),
            link = stringResource(R.string.create_join_link),
            onClick = onJoinInstead,
        )
        Spacer(Modifier.height(16.dp))
    }
}
