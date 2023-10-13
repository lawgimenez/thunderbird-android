package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.core.ui.compose.common.mvi.BaseViewModel
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Effect
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Event
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.State
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.ViewModel

class SpecialFoldersViewModel(
    initialState: State = State(),
) : BaseViewModel<State, Event, Effect>(initialState),
    ViewModel {

    override fun event(event: Event) {
        when (event) {
            Event.LoadSpecialFolders -> onLoadSpecialFolders()
            Event.OnNextClicked -> onNextClicked()
            Event.OnBackClicked -> onBackClicked()
        }
    }

    private fun onLoadSpecialFolders() {
        // load folders and validate -> use case
        // if valid change to success else disable loading only and present special folders form
    }

    private fun onNextClicked() {
        emitEffect(Effect.NavigateNext)
    }

    private fun onBackClicked() {
        emitEffect(Effect.NavigateBack)
    }
}
