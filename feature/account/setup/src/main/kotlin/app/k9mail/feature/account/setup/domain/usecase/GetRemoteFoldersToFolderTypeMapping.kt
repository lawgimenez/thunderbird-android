package app.k9mail.feature.account.setup.domain.usecase

import app.k9mail.feature.account.setup.domain.DomainContract.UseCase
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.folders.RemoteFolder

// This uses the same implementation as the SpecialFolderSelectionStrategy. In case the implementation of the
// SpecialFolderSelectionStrategy changes, this use case should be updated accordingly.
class GetRemoteFoldersToFolderTypeMapping : UseCase.GetRemoteFoldersToFolderTypeMapping {
    override fun execute(folders: List<RemoteFolder>): Map<FolderType, RemoteFolder?> {
        return mapOf(
            FolderType.ARCHIVE to selectSpecialFolder(folders, FolderType.ARCHIVE),
            FolderType.DRAFTS to selectSpecialFolder(folders, FolderType.DRAFTS),
            FolderType.SENT to selectSpecialFolder(folders, FolderType.SENT),
            FolderType.SPAM to selectSpecialFolder(folders, FolderType.SPAM),
            FolderType.TRASH to selectSpecialFolder(folders, FolderType.TRASH),
        )
    }

    private fun selectSpecialFolder(folders: List<RemoteFolder>, type: FolderType): RemoteFolder? {
        return folders.firstOrNull { folder -> folder.type == type }
    }
}
