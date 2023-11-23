package app.k9mail.feature.account.common.domain.entity

data class Folders(
    val archiveFolders: List<Folder>,
    val draftsFolders: List<Folder>,
    val sentFolders: List<Folder>,
    val spamFolders: List<Folder>,
    val trashFolders: List<Folder>,
)
