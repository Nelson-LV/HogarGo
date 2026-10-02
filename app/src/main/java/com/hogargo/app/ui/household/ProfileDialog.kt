package com.hogargo.app.ui.household

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.runtime.rememberCoroutineScope
import com.hogargo.app.data.household.AuthError
import kotlinx.coroutines.launch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.household.ActiveSession
import com.hogargo.app.data.household.HouseholdCode
import com.hogargo.app.data.local.MemberEntity
import com.hogargo.app.ui.components.MemberAvatar
import com.hogargo.app.ui.theme.BrandBrownStrong

/** Who is signed in, which household they are in (with its code) and who else lives there. */
@Composable
fun ProfileDialog(
    session: ActiveSession,
    members: List<MemberEntity>,
    pendingMembers: List<MemberEntity>,
    householdName: String,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit,
    onRenameHousehold: (String) -> Unit,
    onRemoveMember: (MemberEntity) -> Unit,
    onApproveMember: (MemberEntity) -> Unit,
    onRejectMember: (MemberEntity) -> Unit,
    onLeaveHousehold: () -> Unit,
    onSetRecoveryUser: suspend (String) -> AuthError?,
) {
    // The session snapshot can be stale (e.g. admin role handed over), so read the live row.
    val me = members.firstOrNull { it.id == session.member.id } ?: session.member
    val isAdmin = me.isAdmin
    var showRecoveryUser by remember { mutableStateOf(false) }
    var showLeave by remember { mutableStateOf(false) }
    var showRename by remember { mutableStateOf(false) }
    var memberToRemove by remember { mutableStateOf<MemberEntity?>(null) }

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val copiedMessage = stringResource(R.string.create_code_copied)
    val formattedCode = HouseholdCode.format(session.household.code)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MemberAvatar(name = session.member.name, size = 72.dp)
                Text(
                    text = session.member.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.profile_household) + ": " + householdName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (isAdmin) {
                        IconButton(onClick = { showRename = true }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.admin_edit_household),
                                tint = BrandBrownStrong,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable {
                            clipboard.setText(AnnotatedString(formattedCode))
                            Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.profile_code),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = formattedCode,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrandBrownStrong,
                        )
                    }
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = stringResource(R.string.profile_copy_code),
                        tint = BrandBrownStrong,
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (isAdmin && pendingMembers.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.admin_pending_title, pendingMembers.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = BrandBrownStrong,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    pendingMembers.forEach { pending ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            MemberAvatar(name = pending.name, size = 36.dp)
                            Text(
                                text = pending.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = { onRejectMember(pending) }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.admin_reject),
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                            IconButton(onClick = { onApproveMember(pending) }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = stringResource(R.string.admin_accept),
                                    tint = BrandBrownStrong,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    text = stringResource(R.string.profile_members, members.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                members.forEach { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        MemberAvatar(name = member.name, size = 36.dp)
                        Text(
                            text = member.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                        val tag = buildList {
                            if (member.isAdmin) add(stringResource(R.string.profile_admin))
                            if (member.id == session.member.id) add(stringResource(R.string.profile_you))
                        }.joinToString(" · ")
                        if (tag.isNotEmpty()) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (isAdmin && !member.isAdmin) {
                            IconButton(onClick = { memberToRemove = member }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.admin_remove_member_cd),
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Recovery user (lets this person look up their household codes if they forget them)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable { showRecoveryUser = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = BrandBrownStrong)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.recovery_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(
                                if (me.recoveryKey.isNotEmpty()) R.string.profile_recovery_set else R.string.profile_recovery_missing,
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = BrandBrownStrong, modifier = Modifier.size(18.dp))
                }

                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { showLeave = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = stringResource(R.string.profile_leave),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onSignOut) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = stringResource(R.string.profile_sign_out),
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        },
    )

    if (showLeave) {
        val messageRes = when {
            isAdmin && members.size <= 1 -> R.string.leave_message_admin_alone
            isAdmin -> R.string.leave_message_admin
            else -> R.string.leave_message_member
        }
        AlertDialog(
            onDismissRequest = { showLeave = false },
            title = { Text(stringResource(R.string.leave_title, householdName)) },
            text = { Text(stringResource(messageRes)) },
            confirmButton = {
                TextButton(onClick = {
                    showLeave = false
                    onLeaveHousehold()
                }) {
                    Text(stringResource(R.string.leave_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeave = false }) { Text(stringResource(R.string.admin_cancel)) }
            },
        )
    }

    if (showRecoveryUser) {
        RecoveryUserDialog(
            onDismiss = { showRecoveryUser = false },
            onSave = onSetRecoveryUser,
        )
    }

    if (showRename) {
        RenameHouseholdDialog(
            currentName = householdName,
            onDismiss = { showRename = false },
            onSave = {
                onRenameHousehold(it)
                showRename = false
            },
        )
    }

    memberToRemove?.let { target ->
        AlertDialog(
            onDismissRequest = { memberToRemove = null },
            title = { Text(stringResource(R.string.admin_remove_title, target.name)) },
            text = { Text(stringResource(R.string.admin_remove_message)) },
            confirmButton = {
                TextButton(onClick = {
                    onRemoveMember(target)
                    memberToRemove = null
                }) {
                    Text(stringResource(R.string.admin_remove_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { memberToRemove = null }) { Text(stringResource(R.string.admin_cancel)) }
            },
        )
    }
}


@Composable
private fun RenameHouseholdDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.admin_edit_household)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.admin_household_name_label)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(name) }, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.admin_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.admin_cancel)) }
        },
    )
}

@Composable
private fun RecoveryUserDialog(
    onDismiss: () -> Unit,
    onSave: suspend (String) -> AuthError?,
) {
    var user by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<AuthError?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val savedMessage = stringResource(R.string.recovery_saved)

    fun save() {
        if (loading) return
        loading = true
        scope.launch {
            val result = onSave(user)
            loading = false
            if (result == null) {
                Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
                onDismiss()
            } else {
                error = result
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.recovery_label)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.recovery_help),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                HogarTextField(
                    value = user,
                    onValueChange = { user = it; error = null },
                    placeholder = stringResource(R.string.recovery_hint),
                    errorText = error?.let { stringResource(it.messageRes()) },
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                    onImeAction = ::save,
                    capitalization = KeyboardCapitalization.None,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = ::save, enabled = !loading && user.isNotBlank()) {
                Text(stringResource(R.string.admin_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.admin_cancel)) }
        },
    )
}
