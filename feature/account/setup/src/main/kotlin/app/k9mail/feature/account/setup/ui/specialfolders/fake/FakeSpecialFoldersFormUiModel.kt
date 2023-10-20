package app.k9mail.feature.account.setup.ui.specialfolders.fake

import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract

class FakeSpecialFoldersFormUiModel : SpecialFoldersContract.FormUiModel {

    val events = mutableListOf<SpecialFoldersContract.FormEvent>()

    override fun event(
        event: SpecialFoldersContract.FormEvent,
        formState: SpecialFoldersContract.FormState,
    ): SpecialFoldersContract.FormState {
        events.add(event)
        return formState
    }
}
