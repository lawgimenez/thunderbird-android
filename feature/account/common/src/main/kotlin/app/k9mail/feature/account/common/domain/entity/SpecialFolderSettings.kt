package app.k9mail.feature.account.common.domain.entity

import com.fsck.k9.mail.folders.RemoteFolder

data class SpecialFolderSettings(
    val archiveFolder: RemoteFolder,
    val draftsFolder: RemoteFolder,
    val sentFolder: RemoteFolder,
    val spamFolder: RemoteFolder,
    val trashFolder: RemoteFolder,
)
