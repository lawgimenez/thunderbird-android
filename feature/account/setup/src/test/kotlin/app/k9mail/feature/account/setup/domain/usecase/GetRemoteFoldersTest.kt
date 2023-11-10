package app.k9mail.feature.account.setup.domain.usecase

import app.k9mail.feature.account.common.data.InMemoryAccountStateRepository
import app.k9mail.feature.account.common.domain.entity.AccountState
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.fsck.k9.mail.AuthType
import com.fsck.k9.mail.ConnectionSecurity
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.ServerSettings
import com.fsck.k9.mail.folders.FolderServerId
import com.fsck.k9.mail.folders.RemoteFolder
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetRemoteFoldersTest {

    @Test
    fun `should return folders sorted by display name`() = runTest {
        val inMemoryAccountStateRepository = InMemoryAccountStateRepository(
            state = AccountState(
                incomingServerSettings = SERVER_SETTINGS,
            ),
        )
        val folders = listOf(
            createRemoteFolder("folder2", FolderType.SENT),
            createRemoteFolder("folder1", FolderType.INBOX),
            createRemoteFolder("folder4", FolderType.SENT),
            createRemoteFolder("folder3", FolderType.SENT),
        )

        val testSubject = GetRemoteFolders(
            folderFetcher = FakeFolderFetcher(folders),
            accountStateRepository = inMemoryAccountStateRepository,
            authStateStorage = inMemoryAccountStateRepository,
        )

        val result = testSubject.execute()

        assertThat(result).isEqualTo(
            listOf(
                createRemoteFolder("folder1", FolderType.INBOX),
                createRemoteFolder("folder2", FolderType.SENT),
                createRemoteFolder("folder3", FolderType.SENT),
                createRemoteFolder("folder4", FolderType.SENT),
            ),
        )
    }

    @Test
    fun `should throw error when no incoming server settings available`() = runTest {
        val inMemoryAccountStateRepository = InMemoryAccountStateRepository(
            state = AccountState(
                incomingServerSettings = null,
            ),
        )
        val testSubject = GetRemoteFolders(
            folderFetcher = FakeFolderFetcher(emptyList()),
            accountStateRepository = inMemoryAccountStateRepository,
            authStateStorage = inMemoryAccountStateRepository,
        )

        assertFailure {
            testSubject.execute()
        }.isInstanceOf<IllegalStateException>()
            .hasMessage("No incoming server settings available")
    }

    private fun createRemoteFolder(folderName: String, folderType: FolderType): RemoteFolder {
        return RemoteFolder(
            serverId = FolderServerId(folderName),
            displayName = folderName,
            type = folderType,
        )
    }

    private companion object {
        val SERVER_SETTINGS = ServerSettings(
            type = "imap",
            host = "imap.example.org",
            port = 993,
            connectionSecurity = ConnectionSecurity.SSL_TLS_REQUIRED,
            username = "example",
            password = "password",
            clientCertificateAlias = null,
            authenticationType = AuthType.PLAIN,
        )
    }
}
