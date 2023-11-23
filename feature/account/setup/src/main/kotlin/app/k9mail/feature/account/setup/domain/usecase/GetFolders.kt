package app.k9mail.feature.account.setup.domain.usecase

import app.k9mail.feature.account.common.domain.AccountDomainContract
import app.k9mail.feature.account.common.domain.entity.Folder
import app.k9mail.feature.account.common.domain.entity.Folders
import app.k9mail.feature.account.common.domain.entity.SpecialFolder
import app.k9mail.feature.account.setup.domain.DomainContract.UseCase
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.folders.FolderFetcher
import com.fsck.k9.mail.folders.RemoteFolder
import com.fsck.k9.mail.oauth.AuthStateStorage

class GetFolders(
    private val folderFetcher: FolderFetcher,
    private val accountStateRepository: AccountDomainContract.AccountStateRepository,
    private val authStateStorage: AuthStateStorage,
) : UseCase.GetFolders {
    override suspend fun invoke(): Folders {
        val serverSettings = accountStateRepository.getState().incomingServerSettings
            ?: error("No incoming server settings available")

        val remoteFolders = folderFetcher.getFolders(serverSettings, authStateStorage)

        return Folders(
            archiveFolders = mapByFolderType(FolderType.ARCHIVE, remoteFolders),
            draftsFolders = mapByFolderType(FolderType.DRAFTS, remoteFolders),
            sentFolders = mapByFolderType(FolderType.SENT, remoteFolders),
            spamFolders = mapByFolderType(FolderType.SPAM, remoteFolders),
            trashFolders = mapByFolderType(FolderType.TRASH, remoteFolders),
        )
    }

    private fun mapByFolderType(
        folderType: FolderType,
        remoteFolders: List<RemoteFolder>,
    ): List<Folder> {
        val automaticFolder = selectAutomaticFolderByType(folderType, remoteFolders)
        val folders = remoteFolders.map { remoteFolder ->
            getFolderByType(remoteFolder)
        }

        return (listOf(automaticFolder, Folder.None()) + folders)
    }

    // This uses the same implementation as the SpecialFolderSelectionStrategy. In case the implementation of the
    // SpecialFolderSelectionStrategy changes, this use case should be updated accordingly.
    private fun selectAutomaticFolderByType(
        folderType: FolderType,
        remoteFolders: List<RemoteFolder>,
    ): Folder = remoteFolders.firstOrNull { folder -> folder.type == folderType }
        ?.let {
            getFolderByType(
                remoteFolder = it,
                isAutomatic = true,
            )
        } ?: Folder.None(isAutomatic = true)

    private fun getFolderByType(
        remoteFolder: RemoteFolder,
        isAutomatic: Boolean = false,
    ): Folder {
        return when (remoteFolder.type) {
            FolderType.INBOX -> SpecialFolder.Inbox(remoteFolder, isAutomatic)
            FolderType.OUTBOX -> SpecialFolder.Outbox(remoteFolder, isAutomatic)
            FolderType.ARCHIVE -> SpecialFolder.Archive(remoteFolder, isAutomatic)
            FolderType.DRAFTS -> SpecialFolder.Drafts(remoteFolder, isAutomatic)
            FolderType.SENT -> SpecialFolder.Sent(remoteFolder, isAutomatic)
            FolderType.SPAM -> SpecialFolder.Spam(remoteFolder, isAutomatic)
            FolderType.TRASH -> SpecialFolder.Trash(remoteFolder, isAutomatic)
            FolderType.REGULAR -> Folder.Regular(remoteFolder)
        }
    }
}
