package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormUiModel

class SpecialFoldersFormUiModel : FormUiModel {

    override fun event(event: FormEvent, formState: FormState): FormState {
        return when (event) {
            is FormEvent.ArchiveFolderChanged -> onArchiveFolderChanged(formState, event.folderName)
            is FormEvent.DraftsFolderChanged -> onDraftsFolderChanged(formState, event.folderName)
            is FormEvent.SentFolderChanged -> onSentFolderChanged(formState, event.folderName)
            is FormEvent.SpamFolderChanged -> onSpamFolderChanged(formState, event.folderName)
            is FormEvent.TrashFolderChanged -> onTrashFolderChanged(formState, event.folderName)
        }
    }

    private fun onArchiveFolderChanged(formState: FormState, folderName: String): FormState {
        return formState.copy(
            selectedArchiveFolder = formState.archiveFolders[folderName],
        )
    }

    private fun onDraftsFolderChanged(formState: FormState, folderName: String): FormState {
        return formState.copy(
            selectedDraftsFolder = formState.draftsFolders[folderName],
        )
    }

    private fun onSentFolderChanged(formState: FormState, folderName: String): FormState {
        return formState.copy(
            selectedSentFolder = formState.sentFolders[folderName],
        )
    }

    private fun onSpamFolderChanged(formState: FormState, folderName: String): FormState {
        return formState.copy(
            selectedSpamFolder = formState.spamFolders[folderName],
        )
    }

    private fun onTrashFolderChanged(formState: FormState, folderName: String): FormState {
        return formState.copy(
            selectedTrashFolder = formState.trashFolders[folderName],
        )
    }

    override suspend fun validate(formState: FormState): Boolean {
        return formState.selectedArchiveFolder != null &&
            formState.selectedDraftsFolder != null &&
            formState.selectedSentFolder != null &&
            formState.selectedSpamFolder != null &&
            formState.selectedTrashFolder != null
    }
}
