package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.feature.account.common.domain.entity.Folder
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.folders.FolderServerId
import com.fsck.k9.mail.folders.RemoteFolder
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SpecialFoldersFormUiModelTest {

    private val testSubject = SpecialFoldersFormUiModel()

    @Test
    fun `should change archive folder on ArchiveFolderChanged`() {
        val folder = createFolder("archiveFolder")

        val result = testSubject.event(FormEvent.ArchiveFolderChanged(folder), FORM_STATE)

        assertThat(result).isEqualTo(FORM_STATE.copy(selectedArchiveFolder = folder))
    }

    @Test
    fun `should change drafts folder on DraftsFolderChanged`() {
        val folder = createFolder("draftsFolder")

        val result = testSubject.event(FormEvent.DraftsFolderChanged(folder), FORM_STATE)

        assertThat(result).isEqualTo(FORM_STATE.copy(selectedDraftsFolder = folder))
    }

    @Test
    fun `should change sent folder on SentFolderChanged`() {
        val folder = createFolder("sentFolder")

        val result = testSubject.event(FormEvent.SentFolderChanged(folder), FORM_STATE)

        assertThat(result).isEqualTo(FORM_STATE.copy(selectedSentFolder = folder))
    }

    @Test
    fun `should change spam folder on SpamFolderChanged`() {
        val folder = createFolder("spamFolder")

        val result = testSubject.event(FormEvent.SpamFolderChanged(folder), FORM_STATE)

        assertThat(result).isEqualTo(FORM_STATE.copy(selectedSpamFolder = createFolder("spamFolder")))
    }

    @Test
    fun `should change trash folder on TrashFolderChanged`() {
        val folder = createFolder("trashFolder")

        val result = testSubject.event(FormEvent.TrashFolderChanged(folder), FORM_STATE)

        assertThat(result).isEqualTo(FORM_STATE.copy(selectedTrashFolder = folder))
    }

    @Test
    fun `validate should return true when all folders are selected`() = runTest {
        val result = testSubject.validate(FORM_STATE_WITH_SELECTION)

        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `validate should return false when one of the folders is not selected`() = runTest {
        val archiveResult = testSubject.validate(
            FORM_STATE_WITH_SELECTION.copy(
                selectedArchiveFolder = null,
            ),
        )
        val draftsResult = testSubject.validate(
            FORM_STATE_WITH_SELECTION.copy(
                selectedDraftsFolder = null,
            ),
        )
        val sentResult = testSubject.validate(
            FORM_STATE_WITH_SELECTION.copy(
                selectedSentFolder = null,
            ),
        )
        val spamResult = testSubject.validate(
            FORM_STATE_WITH_SELECTION.copy(
                selectedSpamFolder = null,
            ),
        )
        val trashResult = testSubject.validate(
            FORM_STATE_WITH_SELECTION.copy(
                selectedTrashFolder = null,
            ),
        )

        assertThat(archiveResult).isEqualTo(false)
        assertThat(draftsResult).isEqualTo(false)
        assertThat(sentResult).isEqualTo(false)
        assertThat(spamResult).isEqualTo(false)
        assertThat(trashResult).isEqualTo(false)
    }

    private companion object {
        val FORM_STATE = FormState(
            archiveFolders = listOf(createFolder("archiveFolder")),
            draftsFolders = listOf(createFolder("draftsFolder")),
            sentFolders = listOf(createFolder("sentFolder")),
            spamFolders = listOf(createFolder("spamFolder")),
            trashFolders = listOf(createFolder("trashFolder")),
        )

        val FORM_STATE_WITH_SELECTION = FORM_STATE.copy(
            selectedArchiveFolder = createFolder("archiveFolder"),
            selectedDraftsFolder = createFolder("draftsFolder"),
            selectedSentFolder = createFolder("sentFolder"),
            selectedSpamFolder = createFolder("spamFolder"),
            selectedTrashFolder = createFolder("trashFolder"),
        )

        fun createFolder(folderName: String): Folder {
            return Folder.Regular(
                RemoteFolder(
                    serverId = FolderServerId(folderName),
                    displayName = folderName,
                    type = FolderType.REGULAR,
                ),
            )
        }
    }
}
