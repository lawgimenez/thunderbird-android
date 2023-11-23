package app.k9mail.feature.account.setup.ui.specialfolders

import android.content.res.Resources
import app.k9mail.feature.account.common.domain.entity.Folder
import app.k9mail.feature.account.common.domain.entity.SpecialFolder
import app.k9mail.feature.account.setup.R

internal fun Folder?.toResourceString(resources: Resources) = when (this) {
    is Folder.None -> resources.getString(R.string.account_setup_special_folders_folder_none)
    is Folder.Regular -> remoteFolder.displayName
    is SpecialFolder -> {
        if (isAutomatic) {
            resources.getString(R.string.account_setup_special_folders_folder_automatic, remoteFolder.displayName)
        } else {
            remoteFolder.displayName
        }
    }

    null -> resources.getString(R.string.account_setup_special_folders_folder_empty_selection)
}
