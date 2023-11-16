package app.k9mail.feature.account.setup.ui.specialfolders.fake

import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormUiModel

class FakeSpecialFoldersFormUiModel(
    private val isValid: Boolean = false,
) : FormUiModel {

    val events = mutableListOf<FormEvent>()

    override fun event(
        event: FormEvent,
        formState: FormState,
    ): FormState {
        events.add(event)
        return formState
    }

    override fun validate(formState: FormState): Boolean {
        return isValid
    }
}
