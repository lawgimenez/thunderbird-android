package app.k9mail.feature.account.setup.domain.usecase

import assertk.assertThat
import assertk.assertions.containsOnly
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.folders.FolderServerId
import com.fsck.k9.mail.folders.RemoteFolder
import org.junit.Test

class GetRemoteFoldersToFolderTypeMappingTest {

    @Test
    fun `should map remote folders to folderType`() {
        val remoteFolders = listOf(
            createRemoteFolder("Archive", FolderType.ARCHIVE),
            createRemoteFolder("Drafts", FolderType.DRAFTS),
            createRemoteFolder("Sent", FolderType.SENT),
            createRemoteFolder("Spam", FolderType.SPAM),
            createRemoteFolder("Trash", FolderType.TRASH),
        )

        val result = GetRemoteFoldersToFolderTypeMapping().execute(remoteFolders)

        assertThat(result).containsOnly(
            FolderType.ARCHIVE to remoteFolders[0],
            FolderType.DRAFTS to remoteFolders[1],
            FolderType.SENT to remoteFolders[2],
            FolderType.SPAM to remoteFolders[3],
            FolderType.TRASH to remoteFolders[4],
        )
    }

    @Test
    fun `should map to first folder or null`() {
        val remoteFolders = listOf(
            createRemoteFolder("Archive1", FolderType.ARCHIVE),
            createRemoteFolder("Archive2", FolderType.ARCHIVE),
            createRemoteFolder("Drafts1", FolderType.DRAFTS),
            createRemoteFolder("Drafts2", FolderType.DRAFTS),
            createRemoteFolder("Spam1", FolderType.SPAM),
            createRemoteFolder("Spam2", FolderType.SPAM),
        )

        val result = GetRemoteFoldersToFolderTypeMapping().execute(remoteFolders)

        assertThat(result).containsOnly(
            FolderType.ARCHIVE to remoteFolders[0],
            FolderType.DRAFTS to remoteFolders[2],
            FolderType.SENT to null,
            FolderType.SPAM to remoteFolders[4],
            FolderType.TRASH to null,
        )
    }

    private fun createRemoteFolder(folderName: String, folderType: FolderType): RemoteFolder {
        return RemoteFolder(
            serverId = FolderServerId(folderName),
            displayName = folderName,
            type = folderType,
        )
    }
}
