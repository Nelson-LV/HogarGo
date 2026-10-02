package com.hogargo.app.ui.household

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.household.AuthError
import com.hogargo.app.data.household.AuthResult
import com.hogargo.app.ui.theme.BrandBrownStrong
import com.hogargo.app.ui.theme.BrandOrange
import com.hogargo.app.ui.theme.BrandOrangeContainer
import com.hogargo.app.ui.theme.BrandOrangeDeep
import kotlinx.coroutines.launch

@Composable
fun JoinHouseholdScreen(
    onJoin: suspend (name: String, code: String) -> AuthResult,
    onJoined: () -> Unit,
    onCreateInstead: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<AuthError?>(null) }
    var loading by remember { mutableStateOf(false) }
    var requestSent by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    fun submit() {
        if (loading) return
        focusManager.clearFocus()
        loading = true
        scope.launch {
            when (val result = onJoin(name, code)) {
                is AuthResult.Success -> onJoined()
                is AuthResult.Pending -> {
                    requestSent = true
                    loading = false
                }
                is AuthResult.Failure -> {
                    error = result.error
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
            HeaderBadge(icon = Icons.Filled.HomeWork, badge = Icons.Filled.Key)
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(16.dp))
                Text(
                    stringResource(R.string.join_badge),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = stringResource(R.string.join_heading),
                style = MaterialTheme.typography.headlineMedium,
                color = BrandBrownStrong,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp),
            )
            Text(
                text = stringResource(R.string.join_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        if (requestSent) {
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(BrandOrangeContainer)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(32.dp))
                Text(
                    text = stringResource(R.string.join_pending_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = BrandBrownStrong,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    text = stringResource(R.string.join_pending_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Form
        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
            FieldLabel(stringResource(R.string.join_name_label), Icons.Filled.Person)
            HogarTextField(
                value = name,
                onValueChange = { name = it; error = null; requestSent = false },
                placeholder = stringResource(R.string.join_name_hint),
                errorText = error?.takeIf { !it.isCodeError() }?.let { stringResource(it.messageRes()) },
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            )
            Spacer(Modifier.height(8.dp))
            FieldLabel(stringResource(R.string.join_code_label), Icons.Filled.Pin)
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
        }

        Spacer(Modifier.height(20.dp))

        // What you'll be able to do
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.join_benefits_title).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = BrandBrownStrong,
            )
            BenefitRow(Icons.Filled.Checklist, stringResource(R.string.join_benefit_tasks_title), stringResource(R.string.join_benefit_tasks_desc), BrandOrangeContainer)
            BenefitRow(Icons.Filled.Payments, stringResource(R.string.join_benefit_finance_title), stringResource(R.string.join_benefit_finance_desc), BrandOrangeContainer)
            BenefitRow(Icons.Filled.Pets, stringResource(R.string.join_benefit_pet_title), stringResource(R.string.join_benefit_pet_desc), BrandOrange)

            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.img_zori_fox),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BrandOrangeContainer),
                )
                Text(
                    text = stringResource(R.string.join_zori_quote),
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        BigActionButton(
            text = stringResource(R.string.join_button),
            icon = Icons.Filled.FavoriteBorder,
            onClick = ::submit,
            containerColor = BrandOrangeDeep,
            loading = loading,
        )

        Spacer(Modifier.height(8.dp))
        BottomLinkText(
            prefix = stringResource(R.string.join_no_home),
            link = stringResource(R.string.join_create_link),
            onClick = onCreateInstead,
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun BenefitRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    iconBackground: androidx.compose.ui.graphics.Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
