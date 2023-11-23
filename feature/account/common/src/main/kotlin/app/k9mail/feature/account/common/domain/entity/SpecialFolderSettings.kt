package app.k9mail.feature.account.common.domain.entity

data class SpecialFolderSettings(
    val archiveFolder: Folder,
    val draftsFolder: Folder,
    val sentFolder: Folder,
    val spamFolder: Folder,
    val trashFolder: Folder,
)
