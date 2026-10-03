package com.hogargo.app.ui.household

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.household.AuthError
import com.hogargo.app.data.household.AuthResult
import com.hogargo.app.data.household.HouseholdCode
import com.hogargo.app.data.household.RecoveredHousehold
import com.hogargo.app.ui.theme.BrandBrownStrong
import kotlinx.coroutines.launch

/**
 * "Forgot the code?": asks for the recovery user (not the name used in the house) and lists the
 * codes of the households that user still belongs to. A household you left or were removed from
 * is not listed.
 */
@Composable
fun RecoverCodesDialog(
    onDismiss: () -> Unit,
    onRecover: suspend (recoveryUser: String) -> List<RecoveredHousehold>,
    onSignIn: suspend (name: String, code: String) -> AuthResult,
    onSignedIn: () -> Unit,
) {
    var user by rememberSaveable { mutableStateOf("") }
    var results by remember { mutableStateOf<List<RecoveredHousehold>?>(null) }
    var loading by remember { mutableStateOf(false) }
    var enterError by remember { mutableStateOf<AuthError?>(null) }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val copiedMessage = stringResource(R.string.create_code_copied)

    fun search() {
        if (loading) return
        loading = true
        scope.launch {
            results = onRecover(user)
            loading = false
        }
    }

    fun enter(item: RecoveredHousehold) {
        if (loading) return
        loading = true
        enterError = null
        scope.launch {
            when (val result = onSignIn(item.memberName, item.code)) {
                is AuthResult.Success -> onSignedIn()
                is AuthResult.Failure -> {
                    enterError = result.error
                    loading = false
                }
                is AuthResult.Pending -> loading = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.recover_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = stringResource(R.string.recover_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                FieldLabel(stringResource(R.string.recovery_label), Icons.Filled.Lock)
                HogarTextField(
                    value = user,
                    onValueChange = { user = it; results = null; enterError = null },
                    placeholder = stringResource(R.string.recovery_hint),
                    imeAction = ImeAction.Done,
                    onImeAction = ::search,
                    capitalization = KeyboardCapitalization.None,
                )

                results?.let { found ->
                    Spacer(Modifier.height(12.dp))
                    if (found.isEmpty()) {
                        Text(
                            text = stringResource(R.string.recover_none),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    } else {
                        found.forEach { item ->
                            val formatted = HouseholdCode.format(item.code)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            ) {
                                Text(
                                    text = if (item.isAdmin) {
                                        item.householdName + " · " + stringResource(R.string.profile_admin)
                                    } else {
                                        item.householdName
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = stringResource(R.string.recover_registered_as, item.memberName),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = formatted,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandBrownStrong,
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                clipboard.setText(AnnotatedString(formatted))
                                                Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                                            },
                                        ) {
                                            Icon(
                                                Icons.Filled.ContentCopy,
                                                contentDescription = stringResource(R.string.profile_copy_code),
                                                tint = BrandBrownStrong,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                        TextButton(onClick = { enter(item) }, enabled = !loading) {
                                            Text(stringResource(R.string.recover_enter))
                                        }
                                    }
                                }
                            }
                        }
                        enterError?.let {
                            Text(
                                text = stringResource(it.messageRes()),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = ::search, enabled = !loading && user.isNotBlank()) {
                Text(stringResource(R.string.recover_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.recover_close)) }
        },
    )
}
