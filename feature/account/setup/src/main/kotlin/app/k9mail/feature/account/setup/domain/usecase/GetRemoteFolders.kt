package app.k9mail.feature.account.setup.domain.usecase

import app.k9mail.feature.account.common.domain.AccountDomainContract
import app.k9mail.feature.account.setup.domain.DomainContract.UseCase
import com.fsck.k9.mail.folders.FolderFetcher
import com.fsck.k9.mail.folders.RemoteFolder
import com.fsck.k9.mail.oauth.AuthStateStorage

class GetRemoteFolders(
    private val folderFetcher: FolderFetcher,
    private val accountStateRepository: AccountDomainContract.AccountStateRepository,
    private val authStateStorage: AuthStateStorage,
) : UseCase.GetRemoteFolders {
    override suspend fun execute(): List<RemoteFolder> {
        val serverSettings = accountStateRepository.getState().incomingServerSettings
            ?: error("No incoming server settings available")

        return folderFetcher.getFolders(serverSettings, authStateStorage)
            .sortedBy { it.displayName }
    }
}
