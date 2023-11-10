package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.core.ui.compose.testing.MainDispatcherRule
import app.k9mail.core.ui.compose.testing.mvi.assertThatAndEffectTurbineConsumed
import app.k9mail.core.ui.compose.testing.mvi.assertThatAndStateTurbineConsumed
import app.k9mail.core.ui.compose.testing.mvi.turbinesWithInitialStateCheck
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Effect
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Event
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.State
import app.k9mail.feature.account.setup.ui.specialfolders.fake.FakeSpecialFoldersFormUiModel
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.folders.FolderServerId
import com.fsck.k9.mail.folders.RemoteFolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SpecialFoldersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `should load remote folders and populate form state when LoadSpecialFolders event received`() = runTest {
        val remoteFolders = listOf(
            RemoteFolder(FolderServerId("folder1"), "folder1", FolderType.REGULAR),
            RemoteFolder(FolderServerId("folder2"), "folder2", FolderType.REGULAR),
        )
        val testSubject = createTestSubject(
            remoteFolders = remoteFolders,
        )
        val initialState = State()
        val turbines = turbinesWithInitialStateCheck(testSubject, initialState)

        testSubject.event(Event.LoadSpecialFolders)

        val populatedState = initialState.copy(
            formState = FormState(
                archiveFolders = remoteFolders.associateBy { it.displayName },
                draftsFolders = remoteFolders.associateBy { it.displayName },
                sentFolders = remoteFolders.associateBy { it.displayName },
                spamFolders = remoteFolders.associateBy { it.displayName },
                trashFolders = remoteFolders.associateBy { it.displayName },
            ),
        )

        assertThat(turbines.awaitStateItem()).isEqualTo(populatedState)

        val finishedLoadingState = populatedState.copy(
            isLoading = false,
        )
        turbines.assertThatAndStateTurbineConsumed {
            isEqualTo(finishedLoadingState)
        }
    }

    @Test
    fun `should delegate form events to form view model`() = runTest {
        val formUiModel = FakeSpecialFoldersFormUiModel()
        val testSubject = createTestSubject(
            formUiModel = formUiModel,
        )

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
        val testSubject = createTestSubject()
        val turbines = turbinesWithInitialStateCheck(testSubject, State())

        testSubject.event(Event.OnNextClicked)

        turbines.assertThatAndEffectTurbineConsumed {
            isEqualTo(Effect.NavigateNext)
        }
    }

    @Test
    fun `should emit NavigateBack effect when OnBackClicked event received`() = runTest {
        val testSubject = createTestSubject()
        val turbines = turbinesWithInitialStateCheck(testSubject, State())

        testSubject.event(Event.OnBackClicked)

        turbines.assertThatAndEffectTurbineConsumed {
            isEqualTo(Effect.NavigateBack)
        }
    }

    private companion object {
        fun createTestSubject(
            formUiModel: SpecialFoldersContract.FormUiModel = FakeSpecialFoldersFormUiModel(),
            remoteFolders: List<RemoteFolder> = emptyList(),
        ) = SpecialFoldersViewModel(
            formUiModel = formUiModel,
            getRemoteFolders = {
                delay(50)
                remoteFolders
            },
        )
    }
}
