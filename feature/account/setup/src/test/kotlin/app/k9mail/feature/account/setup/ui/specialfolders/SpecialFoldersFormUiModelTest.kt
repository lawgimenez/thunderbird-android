package app.k9mail.feature.account.setup.ui.specialfolders

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
        val result = testSubject.event(FormEvent.ArchiveFolderChanged("archiveFolder"), FORM_STATE)

        assertThat(result).isEqualTo(
            FORM_STATE.copy(
                selectedArchiveFolder = createRemoteFolder("archiveFolder"),
            ),
        )
    }

    @Test
    fun `should change drafts folder on DraftsFolderChanged`() {
        val result = testSubject.event(FormEvent.DraftsFolderChanged("draftsFolder"), FORM_STATE)

        assertThat(result).isEqualTo(
            FORM_STATE.copy(
                selectedDraftsFolder = createRemoteFolder("draftsFolder"),
            ),
        )
    }

    @Test
    fun `should change sent folder on SentFolderChanged`() {
        val result = testSubject.event(FormEvent.SentFolderChanged("sentFolder"), FORM_STATE)

        assertThat(result).isEqualTo(
            FORM_STATE.copy(
                selectedSentFolder = createRemoteFolder("sentFolder"),
            ),
        )
    }

    @Test
    fun `should change spam folder on SpamFolderChanged`() {
        val result = testSubject.event(FormEvent.SpamFolderChanged("spamFolder"), FORM_STATE)

        assertThat(result).isEqualTo(
            FORM_STATE.copy(
                selectedSpamFolder = createRemoteFolder("spamFolder"),
            ),
        )
    }

    @Test
    fun `should change trash folder on TrashFolderChanged`() {
        val result = testSubject.event(FormEvent.TrashFolderChanged("trashFolder"), FORM_STATE)

        assertThat(result).isEqualTo(
            FORM_STATE.copy(
                selectedTrashFolder = createRemoteFolder("trashFolder"),
            ),
        )
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
            archiveFolders = mapOf(
                "archiveFolder" to createRemoteFolder("archiveFolder"),
            ),
            draftsFolders = mapOf(
                "draftsFolder" to createRemoteFolder("draftsFolder"),
            ),
            sentFolders = mapOf(
                "sentFolder" to createRemoteFolder("sentFolder"),
            ),
            spamFolders = mapOf(
                "spamFolder" to createRemoteFolder("spamFolder"),
            ),
            trashFolders = mapOf(
                "trashFolder" to createRemoteFolder("trashFolder"),
            ),
        )

        val FORM_STATE_WITH_SELECTION = FORM_STATE.copy(
            selectedArchiveFolder = createRemoteFolder("archiveFolder"),
            selectedDraftsFolder = createRemoteFolder("draftsFolder"),
            selectedSentFolder = createRemoteFolder("sentFolder"),
            selectedSpamFolder = createRemoteFolder("spamFolder"),
            selectedTrashFolder = createRemoteFolder("trashFolder"),
        )

        fun createRemoteFolder(folderName: String): RemoteFolder {
            return RemoteFolder(
                serverId = FolderServerId(folderName),
                displayName = folderName,
                type = FolderType.REGULAR,
            )
        }
    }
}
