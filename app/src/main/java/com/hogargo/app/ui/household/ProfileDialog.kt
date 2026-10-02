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
<<<<<<< Updated upstream
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
=======
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
>>>>>>> Stashed changes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
<<<<<<< Updated upstream
=======
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
    onDismiss: () -> Unit,
    onSignOut: () -> Unit,
) {
=======
    pendingMembers: List<MemberEntity>,
    householdName: String,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit,
    onRenameHousehold: (String) -> Unit,
    onRemoveMember: (MemberEntity) -> Unit,
    onApproveMember: (MemberEntity) -> Unit,
    onRejectMember: (MemberEntity) -> Unit,
) {
    val isAdmin = session.member.isAdmin
    var showRename by remember { mutableStateOf(false) }
    var memberToRemove by remember { mutableStateOf<MemberEntity?>(null) }

>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
                Text(
                    text = stringResource(R.string.profile_household) + ": " + session.household.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
=======
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
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
=======
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

>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======
                        if (isAdmin && !member.isAdmin) {
                            IconButton(onClick = { memberToRemove = member }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.admin_remove_member_cd),
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
>>>>>>> Stashed changes
                    }
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
<<<<<<< Updated upstream
=======

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
>>>>>>> Stashed changes
}
