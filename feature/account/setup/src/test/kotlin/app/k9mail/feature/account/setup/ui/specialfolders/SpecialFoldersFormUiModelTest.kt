package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.folders.FolderServerId
import com.fsck.k9.mail.folders.RemoteFolder
import org.junit.Test

class SpecialFoldersFormUiModelTest {

    private val testSubject = SpecialFoldersFormUiModel()

    @Test
    fun `should change archive folder on ArchiveFolderChanged`() {
        val result = testSubject.event(FormEvent.ArchiveFolderChanged("archiveFolder"), formState)

        assertThat(result).isEqualTo(
            formState.copy(
                selectedArchiveFolder = createRemoteFolder("archiveFolder"),
            ),
        )
    }

    @Test
    fun `should change drafts folder on DraftsFolderChanged`() {
        val result = testSubject.event(FormEvent.DraftsFolderChanged("draftsFolder"), formState)

        assertThat(result).isEqualTo(
            formState.copy(
                selectedDraftsFolder = createRemoteFolder("draftsFolder"),
            ),
        )
    }

    @Test
    fun `should change sent folder on SentFolderChanged`() {
        val result = testSubject.event(FormEvent.SentFolderChanged("sentFolder"), formState)

        assertThat(result).isEqualTo(
            formState.copy(
                selectedSentFolder = createRemoteFolder("sentFolder"),
            ),
        )
    }

    @Test
    fun `should change spam folder on SpamFolderChanged`() {
        val result = testSubject.event(FormEvent.SpamFolderChanged("spamFolder"), formState)

        assertThat(result).isEqualTo(
            formState.copy(
                selectedSpamFolder = createRemoteFolder("spamFolder"),
            ),
        )
    }

    @Test
    fun `should change trash folder on TrashFolderChanged`() {
        val result = testSubject.event(FormEvent.TrashFolderChanged("trashFolder"), formState)

        assertThat(result).isEqualTo(
            formState.copy(
                selectedTrashFolder = createRemoteFolder("trashFolder"),
            ),
        )
    }

    private companion object {
        val formState = FormState(
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

        fun createRemoteFolder(folderName: String): RemoteFolder {
            return RemoteFolder(
                serverId = FolderServerId(folderName),
                displayName = folderName,
                type = FolderType.REGULAR,
            )
        }
    }
}
