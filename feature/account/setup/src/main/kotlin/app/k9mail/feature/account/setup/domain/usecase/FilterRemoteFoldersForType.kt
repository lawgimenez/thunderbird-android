package app.k9mail.feature.account.setup.domain.usecase

import app.k9mail.feature.account.setup.domain.DomainContract.UseCase
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.folders.RemoteFolder

class FilterRemoteFoldersForType : UseCase.FilterRemoteFoldersForType {
    override suspend fun execute(folderType: FolderType, folders: List<RemoteFolder>): List<RemoteFolder> {
        return folders.filter { it.type == folderType || it.type == FolderType.REGULAR }
            .sortedWith(
                compareByDescending<RemoteFolder> {
                    it.type == folderType
                }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.displayName },
            )
    }
}
