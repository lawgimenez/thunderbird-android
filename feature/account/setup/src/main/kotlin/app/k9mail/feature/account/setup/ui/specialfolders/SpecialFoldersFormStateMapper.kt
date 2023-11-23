package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.feature.account.common.domain.entity.Folder
import app.k9mail.feature.account.common.domain.entity.Folders
import app.k9mail.feature.account.common.domain.entity.SpecialFolder
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState

fun Folders.toFormState(): FormState {
    return FormState(
        archiveFolders = archiveFolders,
        draftsFolders = draftsFolders,
        sentFolders = sentFolders,
        spamFolders = spamFolders,
        trashFolders = trashFolders,

        selectedArchiveFolder = archiveFolders.mapToDefaultFolder(),
        selectedDraftsFolder = draftsFolders.mapToDefaultFolder(),
        selectedSentFolder = sentFolders.mapToDefaultFolder(),
        selectedSpamFolder = spamFolders.mapToDefaultFolder(),
        selectedTrashFolder = trashFolders.mapToDefaultFolder(),
    )
}

private fun List<Folder>.mapToDefaultFolder(): Folder? {
    return firstOrNull {
        (it is SpecialFolder && it.isAutomatic)
    }
}
