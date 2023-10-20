package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.core.ui.compose.testing.MainDispatcherRule
import app.k9mail.core.ui.compose.testing.mvi.assertThatAndEffectTurbineConsumed
import app.k9mail.core.ui.compose.testing.mvi.turbinesWithInitialStateCheck
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Effect
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Event
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.State
import app.k9mail.feature.account.setup.ui.specialfolders.fake.FakeSpecialFoldersFormUiModel
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SpecialFoldersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val formUiModel = FakeSpecialFoldersFormUiModel()

    private val testSubject = SpecialFoldersViewModel(
        formUiModel = formUiModel,
    )

    @Test
    fun `should delegate form events to form view model`() = runTest {
        testSubject.event(FormEvent.ArchiveFolderChanged("archiveFolder"))
        testSubject.event(FormEvent.DraftsFolderChanged("draftsFolder"))
        testSubject.event(FormEvent.SentFolderChanged("sentFolder"))
        testSubject.event(FormEvent.SpamFolderChanged("spamFolder"))
        testSubject.event(FormEvent.TrashFolderChanged("trashFolder"))

        assertThat(formUiModel.events).containsExactly(
            FormEvent.ArchiveFolderChanged("archiveFolder"),
            FormEvent.DraftsFolderChanged("draftsFolder"),
            FormEvent.SentFolderChanged("sentFolder"),
            FormEvent.SpamFolderChanged("spamFolder"),
            FormEvent.TrashFolderChanged("trashFolder"),
        )
    }

    @Test
    fun `should emit NavigateNext effect when OnNextClicked event received`() = runTest {
        val turbines = turbinesWithInitialStateCheck(testSubject, State())

        testSubject.event(Event.OnNextClicked)

        turbines.assertThatAndEffectTurbineConsumed {
            isEqualTo(Effect.NavigateNext)
        }
    }

    @Test
    fun `should emit NavigateBack effect when OnBackClicked event received`() = runTest {
        val turbines = turbinesWithInitialStateCheck(testSubject, State())

        testSubject.event(Event.OnBackClicked)

        turbines.assertThatAndEffectTurbineConsumed {
            isEqualTo(Effect.NavigateBack)
        }
    }
}
