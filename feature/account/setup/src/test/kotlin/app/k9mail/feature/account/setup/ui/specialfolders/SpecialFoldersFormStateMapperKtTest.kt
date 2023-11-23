package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.feature.account.common.domain.entity.Folder
import app.k9mail.feature.account.common.domain.entity.Folders
import app.k9mail.feature.account.common.domain.entity.SpecialFolder
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.folders.FolderServerId
import com.fsck.k9.mail.folders.RemoteFolder
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class SpecialFoldersFormStateMapperKtTest {

    @Test
    fun `should map folders to form state and assign selected folders`() = runTest {
        val folders = Folders(
            archiveFolders = createFolderList(
                SpecialFolder.Archive(
                    createRemoteFolder("archive1"),
                    isAutomatic = true,
                ),
            ),
            draftsFolders = createFolderList(SpecialFolder.Drafts(createRemoteFolder("drafts1"), isAutomatic = true)),
            sentFolders = createFolderList(SpecialFolder.Sent(createRemoteFolder("sent1"), isAutomatic = true)),
            spamFolders = createFolderList(SpecialFolder.Spam(createRemoteFolder("spam1"), isAutomatic = true)),
            trashFolders = createFolderList(SpecialFolder.Trash(createRemoteFolder("trash1"), isAutomatic = true)),
        )

        val result = folders.toFormState()

        assertThat(result).isEqualTo(
            SpecialFoldersContract.FormState(
                archiveFolders = folders.archiveFolders,
                draftsFolders = folders.draftsFolders,
                sentFolders = folders.sentFolders,
                spamFolders = folders.spamFolders,
                trashFolders = folders.trashFolders,

                selectedArchiveFolder = folders.archiveFolders.first(),
                selectedDraftsFolder = folders.draftsFolders.first(),
                selectedSentFolder = folders.sentFolders.first(),
                selectedSpamFolder = folders.spamFolders.first(),
                selectedTrashFolder = folders.trashFolders.first(),
            ),
        )
    }

    @Test
    fun `should map folders to form state and not assign selected folders when there is none automatic`() {
        val folders = Folders(
            archiveFolders = createFolderList(Folder.None(isAutomatic = true)),
            draftsFolders = createFolderList(Folder.None(isAutomatic = true)),
            sentFolders = createFolderList(Folder.None(isAutomatic = true)),
            spamFolders = createFolderList(Folder.None(isAutomatic = true)),
            trashFolders = createFolderList(Folder.None(isAutomatic = true)),
        )

        val result = folders.toFormState()

        assertThat(result).isEqualTo(
            SpecialFoldersContract.FormState(
                archiveFolders = folders.archiveFolders,
                draftsFolders = folders.draftsFolders,
                sentFolders = folders.sentFolders,
                spamFolders = folders.spamFolders,
                trashFolders = folders.trashFolders,

                selectedArchiveFolder = null,
                selectedDraftsFolder = null,
                selectedSentFolder = null,
                selectedSpamFolder = null,
                selectedTrashFolder = null,
            ),
        )
    }

    private companion object {
        fun createRemoteFolder(name: String): RemoteFolder {
            return RemoteFolder(
                serverId = FolderServerId(name),
                displayName = name,
                type = FolderType.REGULAR,
            )
        }

        fun createFolderList(automaticFolder: Folder): List<Folder> {
            return listOf(
                automaticFolder,
                Folder.None(),
                Folder.Regular(createRemoteFolder("regular1")),
            )
        }
    }
}
