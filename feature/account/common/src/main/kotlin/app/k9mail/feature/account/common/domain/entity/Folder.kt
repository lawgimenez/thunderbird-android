package app.k9mail.feature.account.common.domain.entity

import com.fsck.k9.mail.folders.RemoteFolder

sealed interface Folder {
    data class None(
        val isAutomatic: Boolean = false,
    ) : Folder

    data class Regular(
        val remoteFolder: RemoteFolder,
    ) : Folder
}

sealed interface SpecialFolder : Folder {
    val remoteFolder: RemoteFolder
    val isAutomatic: Boolean get() = false

    data class Inbox(
        override val remoteFolder: RemoteFolder,
        override val isAutomatic: Boolean = false,
    ) : SpecialFolder

    data class Outbox(
        override val remoteFolder: RemoteFolder,
        override val isAutomatic: Boolean = false,
    ) : SpecialFolder

    data class Archive(
        override val remoteFolder: RemoteFolder,
        override val isAutomatic: Boolean = false,
    ) : SpecialFolder

    data class Drafts(
        override val remoteFolder: RemoteFolder,
        override val isAutomatic: Boolean = false,
    ) : SpecialFolder

    data class Sent(
        override val remoteFolder: RemoteFolder,
        override val isAutomatic: Boolean = false,
    ) : SpecialFolder

    data class Spam(
        override val remoteFolder: RemoteFolder,
        override val isAutomatic: Boolean = false,
    ) : SpecialFolder

    data class Trash(
        override val remoteFolder: RemoteFolder,
        override val isAutomatic: Boolean = false,
    ) : SpecialFolder
}
