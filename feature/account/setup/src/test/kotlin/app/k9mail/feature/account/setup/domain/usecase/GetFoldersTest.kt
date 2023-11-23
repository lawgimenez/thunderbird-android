package app.k9mail.feature.account.setup.domain.usecase

import app.k9mail.feature.account.common.data.InMemoryAccountStateRepository
import app.k9mail.feature.account.common.domain.AccountDomainContract.AccountStateRepository
import app.k9mail.feature.account.common.domain.entity.AccountState
import app.k9mail.feature.account.common.domain.entity.Folder
import app.k9mail.feature.account.common.domain.entity.SpecialFolder
import app.k9mail.feature.account.setup.domain.DomainContract.UseCase
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.fsck.k9.mail.AuthType
import com.fsck.k9.mail.ConnectionSecurity
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.ServerSettings
import com.fsck.k9.mail.folders.FolderFetcher
import com.fsck.k9.mail.folders.FolderServerId
import com.fsck.k9.mail.folders.RemoteFolder
import com.fsck.k9.mail.oauth.AuthStateStorage
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetFoldersTest {

    @Test
    fun `should fail when no incoming server settings found`() = runTest {
        val testSubject = createTestSubject(
            accountStateRepository = InMemoryAccountStateRepository(
                state = AccountState(
                    incomingServerSettings = null,
                ),
            ),
        )

        assertFailure { testSubject() }
            .isInstanceOf<IllegalStateException>()
            .hasMessage("No incoming server settings available")
    }

    @Test
    fun `should map remote folders to Folders`() = runTest {
        val testSubject = createTestSubject(
            folderFetcher = FakeFolderFetcher(
                folders = listOf(
                    ARCHIVE_FOLDER_1,
                    DRAFTS_FOLDER_1,
                    SENT_FOLDER_1,
                    SPAM_FOLDER_1,
                    TRASH_FOLDER_1,
                    REGULAR_FOLDER_1,
                    REGULAR_FOLDER_2,
                ),
            ),
            accountStateRepository = InMemoryAccountStateRepository(
                state = AccountState(
                    incomingServerSettings = SERVER_SETTINGS,
                ),
            ),
        )

        val folders = testSubject()

        assertThat(folders.archiveFolders).containsExactly(
            *getArrayOfFolders(SpecialFolder.Archive(ARCHIVE_FOLDER_1, isAutomatic = true)),
        )
        assertThat(folders.draftsFolders).containsExactly(
            *getArrayOfFolders(SpecialFolder.Drafts(DRAFTS_FOLDER_1, isAutomatic = true)),
        )
        assertThat(folders.sentFolders).containsExactly(
            *getArrayOfFolders(SpecialFolder.Sent(SENT_FOLDER_1, isAutomatic = true)),
        )
        assertThat(folders.spamFolders).containsExactly(
            *getArrayOfFolders(SpecialFolder.Spam(SPAM_FOLDER_1, isAutomatic = true)),
        )
        assertThat(folders.trashFolders).containsExactly(
            *getArrayOfFolders(SpecialFolder.Trash(TRASH_FOLDER_1, isAutomatic = true)),
        )
    }

    @Test
    fun `should map remote folders to Folders and take first special folder for type`() = runTest {
        val testSubject = createTestSubject(
            folderFetcher = FakeFolderFetcher(
                folders = listOf(
                    ARCHIVE_FOLDER_2,
                    ARCHIVE_FOLDER_1,
                    DRAFTS_FOLDER_2,
                    DRAFTS_FOLDER_1,
                    SENT_FOLDER_2,
                    SENT_FOLDER_1,
                    SPAM_FOLDER_2,
                    SPAM_FOLDER_1,
                    TRASH_FOLDER_2,
                    TRASH_FOLDER_1,
                    REGULAR_FOLDER_1,
                    REGULAR_FOLDER_2,
                ),
            ),
            accountStateRepository = InMemoryAccountStateRepository(
                state = AccountState(
                    incomingServerSettings = SERVER_SETTINGS,
                ),
            ),
        )

        val folders = testSubject()

        assertThat(folders.archiveFolders[0]).isEqualTo(SpecialFolder.Archive(ARCHIVE_FOLDER_2, isAutomatic = true))
        assertThat(folders.draftsFolders[0]).isEqualTo(SpecialFolder.Drafts(DRAFTS_FOLDER_2, isAutomatic = true))
        assertThat(folders.sentFolders[0]).isEqualTo(SpecialFolder.Sent(SENT_FOLDER_2, isAutomatic = true))
        assertThat(folders.spamFolders[0]).isEqualTo(SpecialFolder.Spam(SPAM_FOLDER_2, isAutomatic = true))
        assertThat(folders.trashFolders[0]).isEqualTo(SpecialFolder.Trash(TRASH_FOLDER_2, isAutomatic = true))
    }

    @Test
    fun `should map remote folders to Folders when no special folder present`() = runTest {
        val testSubject = createTestSubject(
            folderFetcher = FakeFolderFetcher(
                folders = listOf(
                    REGULAR_FOLDER_1,
                    REGULAR_FOLDER_2,
                ),
            ),
            accountStateRepository = InMemoryAccountStateRepository(
                state = AccountState(
                    incomingServerSettings = SERVER_SETTINGS,
                ),
            ),
        )
        val expectedFolders = listOf(
            Folder.None(isAutomatic = true),
            Folder.None(),
            Folder.Regular(REGULAR_FOLDER_1),
            Folder.Regular(REGULAR_FOLDER_2),
        ).toTypedArray()

        val folders = testSubject()

        assertThat(folders.archiveFolders).containsExactly(
            *expectedFolders,
        )
        assertThat(folders.draftsFolders).containsExactly(
            *expectedFolders,
        )
        assertThat(folders.sentFolders).containsExactly(
            *expectedFolders,
        )
        assertThat(folders.spamFolders).containsExactly(
            *expectedFolders,
        )
        assertThat(folders.trashFolders).containsExactly(
            *expectedFolders,
        )
    }

    private companion object {
        fun createTestSubject(
            folderFetcher: FolderFetcher = FakeFolderFetcher(),
            accountStateRepository: AccountStateRepository = InMemoryAccountStateRepository(),
        ): UseCase.GetFolders {
            return GetFolders(
                folderFetcher = folderFetcher,
                accountStateRepository = accountStateRepository,
                authStateStorage = accountStateRepository as AuthStateStorage,
            )
        }

        val ARCHIVE_FOLDER_1 = RemoteFolder(
            serverId = FolderServerId("Archive"),
            displayName = "Archive",
            type = FolderType.ARCHIVE,
        )

        val ARCHIVE_FOLDER_2 = RemoteFolder(
            serverId = FolderServerId("Archive2"),
            displayName = "Archive2",
            type = FolderType.ARCHIVE,
        )

        val DRAFTS_FOLDER_1 = RemoteFolder(
            serverId = FolderServerId("Drafts"),
            displayName = "Drafts",
            type = FolderType.DRAFTS,
        )

        val DRAFTS_FOLDER_2 = RemoteFolder(
            serverId = FolderServerId("Drafts2"),
            displayName = "Drafts2",
            type = FolderType.DRAFTS,
        )

        val SENT_FOLDER_1 = RemoteFolder(
            serverId = FolderServerId("Sent"),
            displayName = "Sent",
            type = FolderType.SENT,
        )

        val SENT_FOLDER_2 = RemoteFolder(
            serverId = FolderServerId("Sent2"),
            displayName = "Sent2",
            type = FolderType.SENT,
        )

        val SPAM_FOLDER_1 = RemoteFolder(
            serverId = FolderServerId("Spam"),
            displayName = "Spam",
            type = FolderType.SPAM,
        )

        val SPAM_FOLDER_2 = RemoteFolder(
            serverId = FolderServerId("Spam2"),
            displayName = "Spam2",
            type = FolderType.SPAM,
        )

        val TRASH_FOLDER_1 = RemoteFolder(
            serverId = FolderServerId("Trash"),
            displayName = "Trash",
            type = FolderType.TRASH,
        )

        val TRASH_FOLDER_2 = RemoteFolder(
            serverId = FolderServerId("Trash2"),
            displayName = "Trash2",
            type = FolderType.TRASH,
        )

        val REGULAR_FOLDER_1 = RemoteFolder(
            serverId = FolderServerId("Regular1"),
            displayName = "Regular1",
            type = FolderType.REGULAR,
        )

        val REGULAR_FOLDER_2 = RemoteFolder(
            serverId = FolderServerId("Regular2"),
            displayName = "Regular2",
            type = FolderType.REGULAR,
        )

        fun getArrayOfFolders(defaultFolder: Folder?): Array<Folder> {
            return listOfNotNull(
                defaultFolder,
                Folder.None(),
                SpecialFolder.Archive(ARCHIVE_FOLDER_1),
                SpecialFolder.Drafts(DRAFTS_FOLDER_1),
                SpecialFolder.Sent(SENT_FOLDER_1),
                SpecialFolder.Spam(SPAM_FOLDER_1),
                SpecialFolder.Trash(TRASH_FOLDER_1),
                Folder.Regular(REGULAR_FOLDER_1),
                Folder.Regular(REGULAR_FOLDER_2),
            ).toTypedArray()
        }

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
