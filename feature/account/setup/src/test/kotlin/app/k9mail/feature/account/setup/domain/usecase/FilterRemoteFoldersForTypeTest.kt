package app.k9mail.feature.account.setup.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.folders.FolderServerId
import com.fsck.k9.mail.folders.RemoteFolder
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FilterRemoteFoldersForTypeTest {

    private val testSubject = FilterRemoteFoldersForType()

    @Test
    fun `should filter empty folders`() = runTest {
        val result = testSubject.execute(
            folderType = FolderType.INBOX,
            folders = emptyList(),
        )

        assertThat(result).isEqualTo(emptyList())
    }

    @Test
    fun `should filter by type and always include regular folder type`() = runTest {
        val result = testSubject.execute(
            folderType = FolderType.INBOX,
            folders = listOf(
                createRemoteFolder("Inbox", FolderType.INBOX),
                createRemoteFolder("Archive", FolderType.ARCHIVE),
                createRemoteFolder("Drafts", FolderType.DRAFTS),
                createRemoteFolder("Sent", FolderType.SENT),
                createRemoteFolder("Spam", FolderType.SPAM),
                createRemoteFolder("Trash", FolderType.TRASH),
                createRemoteFolder("Regular", FolderType.REGULAR),
            ),
        )

        assertThat(result).isEqualTo(
            listOf(
                createRemoteFolder("Inbox", FolderType.INBOX),
                createRemoteFolder("Regular", FolderType.REGULAR),
            ),
        )
    }

    @Test
    fun `should sort by folder type and then by display name`() = runTest {
        val result = testSubject.execute(
            folderType = FolderType.INBOX,
            folders = listOf(
                createRemoteFolder("Regular6", FolderType.REGULAR),
                createRemoteFolder("Regular3", FolderType.REGULAR),
                createRemoteFolder("Regular1", FolderType.REGULAR),
                createRemoteFolder("Regular5", FolderType.REGULAR),
                createRemoteFolder("Regular2", FolderType.REGULAR),
                createRemoteFolder("Regular4", FolderType.REGULAR),
                createRemoteFolder("Inbox2", FolderType.INBOX),
                createRemoteFolder("Inbox1", FolderType.INBOX),
            ),
        )

        assertThat(result).isEqualTo(
            listOf(
                createRemoteFolder("Inbox1", FolderType.INBOX),
                createRemoteFolder("Inbox2", FolderType.INBOX),
                createRemoteFolder("Regular1", FolderType.REGULAR),
                createRemoteFolder("Regular2", FolderType.REGULAR),
                createRemoteFolder("Regular3", FolderType.REGULAR),
                createRemoteFolder("Regular4", FolderType.REGULAR),
                createRemoteFolder("Regular5", FolderType.REGULAR),
                createRemoteFolder("Regular6", FolderType.REGULAR),
            ),
        )
    }

    private fun createRemoteFolder(name: String, folderType: FolderType): RemoteFolder {
        return RemoteFolder(
            serverId = FolderServerId(name),
            displayName = name,
            type = folderType,
        )
    }
}
