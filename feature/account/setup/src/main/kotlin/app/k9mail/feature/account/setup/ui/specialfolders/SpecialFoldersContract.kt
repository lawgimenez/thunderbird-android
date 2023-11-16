package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.core.ui.compose.common.mvi.UnidirectionalViewModel
import app.k9mail.feature.account.common.ui.loadingerror.LoadingErrorState
import com.fsck.k9.mail.folders.RemoteFolder

interface SpecialFoldersContract {

    interface ViewModel : UnidirectionalViewModel<State, Event, Effect>

    interface FormUiModel {
        fun event(event: FormEvent, formState: FormState): FormState
        suspend fun validate(formState: FormState): Boolean
    }

    data class State(
        val formState: FormState = FormState(),

        val isSuccess: Boolean = false,
        override val error: Failure? = null,
        override val isLoading: Boolean = true,
    ) : LoadingErrorState<Failure>

    data class FormState(
        val archiveFolders: Map<String, RemoteFolder> = emptyMap(),
        val draftsFolders: Map<String, RemoteFolder> = emptyMap(),
        val sentFolders: Map<String, RemoteFolder> = emptyMap(),
        val spamFolders: Map<String, RemoteFolder> = emptyMap(),
        val trashFolders: Map<String, RemoteFolder> = emptyMap(),

        val selectedArchiveFolder: RemoteFolder? = null,
        val selectedDraftsFolder: RemoteFolder? = null,
        val selectedSentFolder: RemoteFolder? = null,
        val selectedSpamFolder: RemoteFolder? = null,
        val selectedTrashFolder: RemoteFolder? = null,
    )

    sealed interface Event {
        data object LoadSpecialFolders : Event
        data object OnNextClicked : Event
        data object OnBackClicked : Event
    }

    sealed interface FormEvent : Event {
        data class ArchiveFolderChanged(val folderName: String) : FormEvent
        data class DraftsFolderChanged(val folderName: String) : FormEvent
        data class SentFolderChanged(val folderName: String) : FormEvent
        data class SpamFolderChanged(val folderName: String) : FormEvent
        data class TrashFolderChanged(val folderName: String) : FormEvent
    }

    sealed interface Effect {
        data object NavigateNext : Effect
        data object NavigateBack : Effect
    }

    sealed interface Failure {
        data class UnknownError(val message: String) : Failure
    }
}
