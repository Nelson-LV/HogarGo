package com.hogargo.app.ui.household

import com.hogargo.app.data.household.RecoveredHousehold
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.household.AuthError
import com.hogargo.app.data.household.AuthResult
import com.hogargo.app.ui.theme.BrandBrownStrong
import com.hogargo.app.ui.theme.BrandOrange
import com.hogargo.app.ui.theme.BrandOrangeContainer
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onCreateHousehold: () -> Unit,
    onJoinHousehold: () -> Unit,
    onSignIn: suspend (name: String, code: String) -> AuthResult,
    onSignedIn: () -> Unit,
    onRecoverCodes: suspend (recoveryUser: String) -> List<RecoveredHousehold>,
) {
    var showSignIn by rememberSaveable { mutableStateOf(false) }
    var showRecover by rememberSaveable { mutableStateOf(false) }

    if (showRecover) {
        RecoverCodesDialog(
            onDismiss = { showRecover = false },
            onRecover = onRecoverCodes,
            onSignIn = onSignIn,
            onSignedIn = {
                showRecover = false
                onSignedIn()
            },
        )
    }

    if (showSignIn) {
        SignInDialog(
            onDismiss = { showSignIn = false },
            onSignIn = onSignIn,
            onForgotCode = {
                showSignIn = false
                showRecover = true
            },
            onSignedIn = {
                showSignIn = false
                onSignedIn()
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Brand pill + tagline
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Filled.Home, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(24.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = BrandBrownStrong,
            )
        }
        Text(
            text = stringResource(R.string.welcome_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )

        Spacer(Modifier.height(20.dp))

        // Speech bubble
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Filled.Favorite, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(22.dp))
            Text(
                text = stringResource(R.string.welcome_bubble),
                style = MaterialTheme.typography.labelLarge,
                color = BrandBrownStrong,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(14.dp))

        // Zori
        Box(modifier = Modifier.size(190.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(BrandOrangeContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.img_zori_fox),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 6.dp, bottom = 6.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrandBrownStrong),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )

        Spacer(Modifier.height(24.dp))

        ChoiceCard(
            title = stringResource(R.string.welcome_create_title),
            description = stringResource(R.string.welcome_create_desc),
            icon = Icons.Filled.HomeWork,
            primary = true,
            onClick = onCreateHousehold,
        )
        Spacer(Modifier.height(14.dp))
        ChoiceCard(
            title = stringResource(R.string.welcome_join_title),
            description = stringResource(R.string.welcome_join_desc),
            icon = Icons.Filled.Key,
            primary = false,
            onClick = onJoinHousehold,
        )

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            FeatureChip(Icons.Filled.Checklist, stringResource(R.string.welcome_chip_tasks))
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(BrandOrange),
            )
            FeatureChip(Icons.Filled.Savings, stringResource(R.string.welcome_chip_finance))
        }

        Spacer(Modifier.height(12.dp))

        BottomLinkText(
            prefix = stringResource(R.string.welcome_already_member),
            link = stringResource(R.string.welcome_sign_in),
            onClick = { showSignIn = true },
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ChoiceCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primary: Boolean,
    onClick: () -> Unit,
) {
    val container = if (primary) BrandBrownStrong else MaterialTheme.colorScheme.surfaceContainerHigh
    val titleColor = if (primary) Color.White else MaterialTheme.colorScheme.onSurface
    val bodyColor = if (primary) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
    val iconBox = if (primary) Color.White.copy(alpha = 0.18f) else BrandOrangeContainer
    val iconTint = if (primary) Color.White else BrandBrownStrong

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(container)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBox),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = titleColor)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = titleColor, modifier = Modifier.size(16.dp))
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = bodyColor,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun FeatureChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(16.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SignInDialog(
    onDismiss: () -> Unit,
    onSignIn: suspend (name: String, code: String) -> AuthResult,
    onForgotCode: () -> Unit,
    onSignedIn: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<AuthError?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        if (loading) return
        loading = true
        scope.launch {
            when (val result = onSignIn(name, code)) {
                is AuthResult.Success -> onSignedIn()
                is AuthResult.Pending -> loading = false
                is AuthResult.Failure -> {
                    error = result.error
                    loading = false
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text(stringResource(R.string.login_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.login_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                FieldLabel(stringResource(R.string.login_name_label), Icons.Filled.Person)
                HogarTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
                    placeholder = stringResource(R.string.login_name_hint),
                    errorText = error?.takeIf { !it.isCodeError() }?.let { stringResource(it.messageRes()) },
                )
                Spacer(Modifier.height(8.dp))
                FieldLabel(stringResource(R.string.join_code_label), Icons.Filled.Key)
                CodeInput(
                    value = code,
                    onValueChange = { code = it; error = null },
                    isError = error?.isCodeError() == true,
                    onDone = ::submit,
                )
                error?.takeIf { it.isCodeError() }?.let {
                    Text(
                        text = stringResource(it.messageRes()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                TextButton(onClick = onForgotCode, enabled = !loading) {
                    Text(stringResource(R.string.login_forgot_code))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = ::submit, enabled = !loading) {
                Text(stringResource(R.string.login_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !loading) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}
