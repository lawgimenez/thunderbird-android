package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.core.ui.compose.testing.MainDispatcherRule
import app.k9mail.core.ui.compose.testing.mvi.assertThatAndEffectTurbineConsumed
import app.k9mail.core.ui.compose.testing.mvi.assertThatAndStateTurbineConsumed
import app.k9mail.core.ui.compose.testing.mvi.turbinesWithInitialStateCheck
import app.k9mail.feature.account.common.data.InMemoryAccountStateRepository
import app.k9mail.feature.account.common.domain.AccountDomainContract
import app.k9mail.feature.account.common.domain.entity.AccountState
import app.k9mail.feature.account.common.domain.entity.Folder
import app.k9mail.feature.account.common.domain.entity.Folders
import app.k9mail.feature.account.common.domain.entity.SpecialFolder
import app.k9mail.feature.account.common.domain.entity.SpecialFolderSettings
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
    fun `should load folders, validate and save successfully when LoadSpecialFolders event received and setup valid`() =
        runTest {
            val accountStateRepository = InMemoryAccountStateRepository()
            val initialState = State(
                isLoading = true,
            )
            val testSubject = createTestSubject(
                formUiModel = FakeSpecialFoldersFormUiModel(
                    isValid = true,
                ),
                folders = FOLDERS,
                accountStateRepository = accountStateRepository,
                initialState = initialState,
            )
            val turbines = turbinesWithInitialStateCheck(testSubject, initialState)

            testSubject.event(Event.LoadSpecialFolders)

            val populatedState = initialState.copy(
                isLoading = true,
                isSuccess = false,
                formState = FormState(
                    archiveFolders = FOLDERS.archiveFolders,
                    draftsFolders = FOLDERS.draftsFolders,
                    sentFolders = FOLDERS.sentFolders,
                    spamFolders = FOLDERS.spamFolders,
                    trashFolders = FOLDERS.trashFolders,

                    selectedArchiveFolder = SPECIAL_FOLDER_ARCHIVE.copy(isAutomatic = true),
                    selectedDraftsFolder = SPECIAL_FOLDER_DRAFTS.copy(isAutomatic = true),
                    selectedSentFolder = SPECIAL_FOLDER_SENT.copy(isAutomatic = true),
                    selectedSpamFolder = SPECIAL_FOLDER_SPAM.copy(isAutomatic = true),
                    selectedTrashFolder = SPECIAL_FOLDER_TRASH.copy(isAutomatic = true),
                ),
            )

            assertThat(turbines.awaitStateItem()).isEqualTo(populatedState)

            val validatingState = populatedState.copy(
                isLoading = true,
                isSuccess = true,
            )
            assertThat(turbines.awaitStateItem()).isEqualTo(validatingState)

            val successState = populatedState.copy(
                isLoading = false,
                isSuccess = true,
            )
            turbines.assertThatAndStateTurbineConsumed {
                isEqualTo(successState)
            }

            turbines.assertThatAndEffectTurbineConsumed {
                isEqualTo(Effect.NavigateNext)
            }

            assertThat(accountStateRepository.getState()).isEqualTo(
                AccountState(
                    specialFolderSettings = SpecialFolderSettings(
                        archiveFolder = SPECIAL_FOLDER_ARCHIVE.copy(isAutomatic = true),
                        draftsFolder = SPECIAL_FOLDER_DRAFTS.copy(isAutomatic = true),
                        sentFolder = SPECIAL_FOLDER_SENT.copy(isAutomatic = true),
                        spamFolder = SPECIAL_FOLDER_SPAM.copy(isAutomatic = true),
                        trashFolder = SPECIAL_FOLDER_TRASH.copy(isAutomatic = true),
                    ),
                ),
            )
        }

    @Test
    fun `should load folders and validate unsuccessful when LoadSpecialFolders event received`() = runTest {
        val accountStateRepository = InMemoryAccountStateRepository()
        val initialState = State(
            isLoading = true,
        )
        val testSubject = createTestSubject(
            formUiModel = FakeSpecialFoldersFormUiModel(
                isValid = false,
            ),
            folders = FOLDERS,
            accountStateRepository = accountStateRepository,
            initialState = initialState,
        )
        val turbines = turbinesWithInitialStateCheck(testSubject, initialState)

        testSubject.event(Event.LoadSpecialFolders)

        val populatedState = initialState.copy(
            isLoading = true,
            isSuccess = false,
            formState = FormState(
                archiveFolders = FOLDERS.archiveFolders,
                draftsFolders = FOLDERS.draftsFolders,
                sentFolders = FOLDERS.sentFolders,
                spamFolders = FOLDERS.spamFolders,
                trashFolders = FOLDERS.trashFolders,

                selectedArchiveFolder = SPECIAL_FOLDER_ARCHIVE.copy(isAutomatic = true),
                selectedDraftsFolder = SPECIAL_FOLDER_DRAFTS.copy(isAutomatic = true),
                selectedSentFolder = SPECIAL_FOLDER_SENT.copy(isAutomatic = true),
                selectedSpamFolder = SPECIAL_FOLDER_SPAM.copy(isAutomatic = true),
                selectedTrashFolder = SPECIAL_FOLDER_TRASH.copy(isAutomatic = true),
            ),
        )

        assertThat(turbines.awaitStateItem()).isEqualTo(populatedState)

        val validatingState = populatedState.copy(
            isLoading = false,
            isSuccess = false,
        )
        turbines.assertThatAndStateTurbineConsumed {
            isEqualTo(validatingState)
        }

        turbines.effectTurbine.ensureAllEventsConsumed()

        assertThat(accountStateRepository.getState()).isEqualTo(AccountState())
    }

    @Test
    fun `should delegate form events to form view model`() = runTest {
        val formUiModel = FakeSpecialFoldersFormUiModel()
        val testSubject = createTestSubject(
            formUiModel = formUiModel,
        )

        testSubject.event(FormEvent.ArchiveFolderChanged(SPECIAL_FOLDER_ARCHIVE))
        testSubject.event(FormEvent.DraftsFolderChanged(SPECIAL_FOLDER_DRAFTS))
        testSubject.event(FormEvent.SentFolderChanged(SPECIAL_FOLDER_SENT))
        testSubject.event(FormEvent.SpamFolderChanged(SPECIAL_FOLDER_SPAM))
        testSubject.event(FormEvent.TrashFolderChanged(SPECIAL_FOLDER_TRASH))

        assertThat(formUiModel.events).containsExactly(
            FormEvent.ArchiveFolderChanged(SPECIAL_FOLDER_ARCHIVE),
            FormEvent.DraftsFolderChanged(SPECIAL_FOLDER_DRAFTS),
            FormEvent.SentFolderChanged(SPECIAL_FOLDER_SENT),
            FormEvent.SpamFolderChanged(SPECIAL_FOLDER_SPAM),
            FormEvent.TrashFolderChanged(SPECIAL_FOLDER_TRASH),
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
            folders: Folders = FOLDERS,
            accountStateRepository: AccountDomainContract.AccountStateRepository = InMemoryAccountStateRepository(),
            initialState: State = State(),
        ) = SpecialFoldersViewModel(
            formUiModel = formUiModel,
            getFolders = {
                delay(50)
                folders
            },
            accountStateRepository = accountStateRepository,
            initialState = initialState,
        )

        val REMOTE_FOLDER = RemoteFolder(FolderServerId("archive"), "archive", FolderType.ARCHIVE)

        val SPECIAL_FOLDER_ARCHIVE = SpecialFolder.Archive(REMOTE_FOLDER)
        val SPECIAL_FOLDER_DRAFTS = SpecialFolder.Drafts(REMOTE_FOLDER)
        val SPECIAL_FOLDER_SENT = SpecialFolder.Sent(REMOTE_FOLDER)
        val SPECIAL_FOLDER_SPAM = SpecialFolder.Spam(REMOTE_FOLDER)
        val SPECIAL_FOLDER_TRASH = SpecialFolder.Trash(REMOTE_FOLDER)

        val FOLDERS = Folders(
            archiveFolders = listOf(
                SPECIAL_FOLDER_ARCHIVE.copy(isAutomatic = true),
                Folder.None(),
                SPECIAL_FOLDER_ARCHIVE,
                Folder.Regular(REMOTE_FOLDER),
            ),
            draftsFolders = listOf(
                SPECIAL_FOLDER_DRAFTS.copy(isAutomatic = true),
                Folder.None(),
                SPECIAL_FOLDER_DRAFTS,
                Folder.Regular(REMOTE_FOLDER),
            ),
            sentFolders = listOf(
                SPECIAL_FOLDER_SENT.copy(isAutomatic = true),
                Folder.None(),
                SPECIAL_FOLDER_SENT,
                Folder.Regular(REMOTE_FOLDER),
            ),
            spamFolders = listOf(
                SPECIAL_FOLDER_SPAM.copy(isAutomatic = true),
                Folder.None(),
                SPECIAL_FOLDER_SPAM,
                Folder.Regular(REMOTE_FOLDER),
            ),
            trashFolders = listOf(
                SPECIAL_FOLDER_TRASH.copy(isAutomatic = true),
                Folder.None(),
                SPECIAL_FOLDER_TRASH,
                Folder.Regular(REMOTE_FOLDER),
            ),
        )
    }
}
