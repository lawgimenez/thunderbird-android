package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.core.ui.compose.common.mvi.UnidirectionalViewModel
import app.k9mail.feature.account.common.domain.entity.Folder
import app.k9mail.feature.account.common.ui.loadingerror.LoadingErrorState

interface SpecialFoldersContract {

    interface ViewModel : UnidirectionalViewModel<State, Event, Effect>

    interface FormUiModel {
        fun event(event: FormEvent, formState: FormState): FormState
        fun validate(formState: FormState): Boolean
    }

    data class State(
        val formState: FormState = FormState(),

        val isSuccess: Boolean = false,
        override val error: Failure? = null,
        override val isLoading: Boolean = true,
    ) : LoadingErrorState<Failure>

    data class FormState(
        val archiveFolders: List<Folder> = emptyList(),
        val draftsFolders: List<Folder> = emptyList(),
        val sentFolders: List<Folder> = emptyList(),
        val spamFolders: List<Folder> = emptyList(),
        val trashFolders: List<Folder> = emptyList(),

        val selectedArchiveFolder: Folder? = null,
        val selectedDraftsFolder: Folder? = null,
        val selectedSentFolder: Folder? = null,
        val selectedSpamFolder: Folder? = null,
        val selectedTrashFolder: Folder? = null,
    )

    sealed interface Event {
        data object LoadSpecialFolders : Event
        data object OnEditClicked : Event
        data object OnRetryClicked : Event
        data object OnNextClicked : Event
        data object OnBackClicked : Event
    }

    sealed interface FormEvent : Event {
        data class ArchiveFolderChanged(val folder: Folder) : FormEvent
        data class DraftsFolderChanged(val folder: Folder) : FormEvent
        data class SentFolderChanged(val folder: Folder) : FormEvent
        data class SpamFolderChanged(val folder: Folder) : FormEvent
        data class TrashFolderChanged(val folder: Folder) : FormEvent
    }

    sealed interface Effect {
        data object NavigateNext : Effect
        data object NavigateBack : Effect
    }

    sealed interface Failure {
        data class UnknownError(val message: String) : Failure
        data class SaveFailed(val message: String) : Failure
    }
}
