package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.feature.account.common.domain.entity.Folder
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormUiModel

class SpecialFoldersFormUiModel : FormUiModel {

    override fun event(event: FormEvent, formState: FormState): FormState {
        return when (event) {
            is FormEvent.ArchiveFolderChanged -> onArchiveFolderChanged(formState, event.folder)
            is FormEvent.DraftsFolderChanged -> onDraftsFolderChanged(formState, event.folder)
            is FormEvent.SentFolderChanged -> onSentFolderChanged(formState, event.folder)
            is FormEvent.SpamFolderChanged -> onSpamFolderChanged(formState, event.folder)
            is FormEvent.TrashFolderChanged -> onTrashFolderChanged(formState, event.folder)
        }
    }

    private fun onArchiveFolderChanged(formState: FormState, folder: Folder): FormState {
        return formState.copy(
            selectedArchiveFolder = folder,
        )
    }

    private fun onDraftsFolderChanged(formState: FormState, folder: Folder): FormState {
        return formState.copy(
            selectedDraftsFolder = folder,
        )
    }

    private fun onSentFolderChanged(formState: FormState, folder: Folder): FormState {
        return formState.copy(
            selectedSentFolder = folder,
        )
    }

    private fun onSpamFolderChanged(formState: FormState, folder: Folder): FormState {
        return formState.copy(
            selectedSpamFolder = folder,
        )
    }

    private fun onTrashFolderChanged(formState: FormState, folder: Folder): FormState {
        return formState.copy(
            selectedTrashFolder = folder,
        )
    }

    override fun validate(formState: FormState): Boolean {
        return formState.selectedArchiveFolder != null &&
            formState.selectedDraftsFolder != null &&
            formState.selectedSentFolder != null &&
            formState.selectedSpamFolder != null &&
            formState.selectedTrashFolder != null
    }
}
