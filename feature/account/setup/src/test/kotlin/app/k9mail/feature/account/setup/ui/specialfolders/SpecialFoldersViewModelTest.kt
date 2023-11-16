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
        val testSubject = createTestSubject(
            remoteFolders = REMOTE_FOLDERS,
            remoteFolderMapping = REMOTE_FOLDER_MAPPING,
            filteredRemoteFolders = FILTERED_REMOTE_FOLDERS,
        )
        val initialState = State()
        val turbines = turbinesWithInitialStateCheck(testSubject, initialState)

        testSubject.event(Event.LoadSpecialFolders)

        val populatedState = initialState.copy(
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
            remoteFolderMapping: Map<FolderType, RemoteFolder> = emptyMap(),
            filteredRemoteFolders: List<RemoteFolder> = emptyList(),
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
