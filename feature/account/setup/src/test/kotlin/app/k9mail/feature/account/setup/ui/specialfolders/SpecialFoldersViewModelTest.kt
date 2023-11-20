package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.core.ui.compose.testing.MainDispatcherRule
import app.k9mail.core.ui.compose.testing.mvi.assertThatAndEffectTurbineConsumed
import app.k9mail.core.ui.compose.testing.mvi.assertThatAndStateTurbineConsumed
import app.k9mail.core.ui.compose.testing.mvi.turbinesWithInitialStateCheck
import app.k9mail.feature.account.common.data.InMemoryAccountStateRepository
import app.k9mail.feature.account.common.domain.AccountDomainContract
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
        val initialState = State(
            isLoading = true,
        )
        val testSubject = createTestSubject(
            formUiModel = FakeSpecialFoldersFormUiModel(
                isValid = true,
            ),
            remoteFolders = REMOTE_FOLDERS,
            remoteFolderMapping = REMOTE_FOLDER_MAPPING,
            filteredRemoteFolders = FILTERED_REMOTE_FOLDERS,
            initialState = initialState,
        )
        val turbines = turbinesWithInitialStateCheck(testSubject, initialState)

        testSubject.event(Event.LoadSpecialFolders)

        val populatedState = initialState.copy(
            isLoading = true,
            isSuccess = true,
            formState = FormState(
                archiveFolders = FILTERED_REMOTE_FOLDERS_MAP,
                draftsFolders = FILTERED_REMOTE_FOLDERS_MAP,
                sentFolders = FILTERED_REMOTE_FOLDERS_MAP,
                spamFolders = FILTERED_REMOTE_FOLDERS_MAP,
                trashFolders = FILTERED_REMOTE_FOLDERS_MAP,

                selectedArchiveFolder = REMOTE_FOLDER_ARCHIVE,
                selectedDraftsFolder = REMOTE_FOLDER_DRAFTS,
                selectedSentFolder = REMOTE_FOLDER_SENT,
                selectedSpamFolder = REMOTE_FOLDER_SPAM,
                selectedTrashFolder = REMOTE_FOLDER_TRASH,
            ),
        )

        assertThat(turbines.awaitStateItem()).isEqualTo(populatedState)

        val successState = populatedState.copy(
            isLoading = false,
            isSuccess = true,
        )
        turbines.assertThatAndStateTurbineConsumed {
            isEqualTo(successState)
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
        val initialState = State(isSuccess = true)
        val testSubject = createTestSubject(initialState = initialState)
        val turbines = turbinesWithInitialStateCheck(testSubject, initialState)

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

    @Test
    fun `should show form when OnEditClicked event received`() = runTest {
        val initialState = State(isSuccess = true)
        val testSubject = createTestSubject(initialState = initialState)
        val turbines = turbinesWithInitialStateCheck(testSubject, initialState)

        testSubject.event(Event.OnEditClicked)

        turbines.assertThatAndStateTurbineConsumed {
            isEqualTo(initialState.copy(isSuccess = false))
        }
    }

    @Test
    fun `should show form when OnRetryClicked event received`() = runTest {
        val initialState = State(error = SpecialFoldersContract.Failure.SaveFailed("error"))
        val testSubject = createTestSubject(initialState = initialState)
        val turbines = turbinesWithInitialStateCheck(testSubject, initialState)

        testSubject.event(Event.OnRetryClicked)

        turbines.assertThatAndStateTurbineConsumed {
            isEqualTo(initialState.copy(error = null))
        }
    }

    private companion object {
        fun createTestSubject(
            formUiModel: SpecialFoldersContract.FormUiModel = FakeSpecialFoldersFormUiModel(),
            remoteFolders: List<RemoteFolder> = emptyList(),
            remoteFolderMapping: Map<FolderType, RemoteFolder> = emptyMap(),
            filteredRemoteFolders: List<RemoteFolder> = emptyList(),
            accountStateRepository: AccountDomainContract.AccountStateRepository = InMemoryAccountStateRepository(),
            initialState: State = State(),
        ) = SpecialFoldersViewModel(
            formUiModel = formUiModel,
            getRemoteFolders = {
                delay(50)
                remoteFolders
            },
            getRemoteFoldersToFolderTypeMapping = {
                remoteFolderMapping
            },
            filterRemoteFoldersForType = { _, _ ->
                filteredRemoteFolders
            },
            accountStateRepository = accountStateRepository,
            initialState = initialState,
        )

        val REMOTE_FOLDER_ARCHIVE = RemoteFolder(FolderServerId("archive"), "archive", FolderType.ARCHIVE)
        val REMOTE_FOLDER_DRAFTS = RemoteFolder(FolderServerId("drafts"), "drafts", FolderType.DRAFTS)
        val REMOTE_FOLDER_SENT = RemoteFolder(FolderServerId("sent"), "sent", FolderType.SENT)
        val REMOTE_FOLDER_SPAM = RemoteFolder(FolderServerId("spam"), "spam", FolderType.SPAM)
        val REMOTE_FOLDER_TRASH = RemoteFolder(FolderServerId("trash"), "trash", FolderType.TRASH)

        val REMOTE_FOLDERS = listOf(
            REMOTE_FOLDER_ARCHIVE,
            REMOTE_FOLDER_DRAFTS,
            REMOTE_FOLDER_SENT,
            REMOTE_FOLDER_SPAM,
            REMOTE_FOLDER_TRASH,
            RemoteFolder(FolderServerId("folder2"), "folder2", FolderType.REGULAR),
            RemoteFolder(FolderServerId("folder1"), "folder1", FolderType.REGULAR),
        )

        val REMOTE_FOLDER_MAPPING = mapOf(
            FolderType.ARCHIVE to REMOTE_FOLDERS[0],
            FolderType.DRAFTS to REMOTE_FOLDERS[1],
            FolderType.SENT to REMOTE_FOLDERS[2],
            FolderType.SPAM to REMOTE_FOLDERS[3],
            FolderType.TRASH to REMOTE_FOLDERS[4],
        )

        val FILTERED_REMOTE_FOLDERS = listOf(
            REMOTE_FOLDER_ARCHIVE,
            REMOTE_FOLDER_DRAFTS,
        )

        val FILTERED_REMOTE_FOLDERS_MAP = mapOf(
            "archive" to REMOTE_FOLDER_ARCHIVE,
            "drafts" to REMOTE_FOLDER_DRAFTS,
        )
    }
}
