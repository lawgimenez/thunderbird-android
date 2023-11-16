package app.k9mail.feature.account.setup.ui.specialfolders.fake

import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormUiModel
import kotlinx.coroutines.delay

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

    @Suppress("MagicNumber")
    override suspend fun validate(formState: FormState): Boolean {
        delay(50)
        return isValid
    }
}
