package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.core.ui.compose.common.mvi.UnidirectionalViewModel
import app.k9mail.feature.account.common.ui.loadingerror.LoadingErrorState

interface SpecialFoldersContract {

    interface ViewModel : UnidirectionalViewModel<State, Event, Effect>

    data class State(
        val isSuccess: Boolean = false,
        override val error: Failure? = null,
        override val isLoading: Boolean = true,
    ) : LoadingErrorState<Failure>

    sealed interface Event {
        data object LoadSpecialFolders : Event

        data object OnNextClicked : Event
        data object OnBackClicked : Event
    }

    sealed interface Effect {
        data object NavigateNext : Effect

        data object NavigateBack : Effect
    }

    sealed interface Failure {
        data class UnknownError(val message: String) : Failure
    }
}
