package app.k9mail.feature.account.setup.ui.specialfolders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import app.k9mail.core.ui.compose.designsystem.atom.text.TextBody1
import app.k9mail.core.ui.compose.designsystem.molecule.input.SelectInput
import app.k9mail.core.ui.compose.theme.MainTheme
import app.k9mail.core.ui.compose.theme.PreviewWithThemes
import app.k9mail.feature.account.common.ui.item.defaultItemPadding
import app.k9mail.feature.account.setup.R
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState
import kotlinx.collections.immutable.toImmutableList

@Suppress("LongMethod")
@Composable
fun SpecialFoldersFormContent(
    state: FormState,
    onEvent: (FormEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = Modifier
            .imePadding()
            .then(modifier),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(MainTheme.spacings.double),
    ) {
        item {
            Spacer(modifier = Modifier.requiredHeight(MainTheme.sizes.smaller))
        }

        item {
            TextBody1(
                text = stringResource(id = R.string.account_setup_special_folders_form_description),
                modifier = Modifier.padding(defaultItemPadding()),
            )
        }

        item {
            SelectInput(
                options = state.archiveFolders.keys.toImmutableList(),
                selectedOption = state.selectedArchiveFolder?.displayName
                    ?: stringResource(id = R.string.account_setup_special_folders_folder_none_selected),
                onOptionChange = { onEvent(FormEvent.ArchiveFolderChanged(it)) },
                label = stringResource(R.string.account_setup_special_folders_archive_folder_label),
                contentPadding = defaultItemPadding(),
            )
        }

        item {
            SelectInput(
                options = state.draftsFolders.keys.toImmutableList(),
                selectedOption = state.selectedDraftsFolder?.displayName
                    ?: stringResource(id = R.string.account_setup_special_folders_folder_none_selected),
                onOptionChange = { onEvent(FormEvent.DraftsFolderChanged(it)) },
                label = stringResource(id = R.string.account_setup_special_folders_drafts_folder_label),
                contentPadding = defaultItemPadding(),
            )
        }

        item {
            SelectInput(
                options = state.sentFolders.keys.toImmutableList(),
                selectedOption = state.selectedSentFolder?.displayName
                    ?: stringResource(id = R.string.account_setup_special_folders_folder_none_selected),
                onOptionChange = { onEvent(FormEvent.SentFolderChanged(it)) },
                label = stringResource(id = R.string.account_setup_special_folders_sent_folder_label),
                contentPadding = defaultItemPadding(),
            )
        }

        item {
            SelectInput(
                options = state.spamFolders.keys.toImmutableList(),
                selectedOption = state.selectedSpamFolder?.displayName
                    ?: stringResource(id = R.string.account_setup_special_folders_folder_none_selected),
                onOptionChange = { onEvent(FormEvent.SpamFolderChanged(it)) },
                label = stringResource(id = R.string.account_setup_special_folders_spam_folder_label),
                contentPadding = defaultItemPadding(),
            )
        }

        item {
            SelectInput(
                options = state.trashFolders.keys.toImmutableList(),
                selectedOption = state.selectedTrashFolder?.displayName
                    ?: stringResource(id = R.string.account_setup_special_folders_folder_none_selected),
                onOptionChange = { onEvent(FormEvent.TrashFolderChanged(it)) },
                label = stringResource(id = R.string.account_setup_special_folders_trash_folder_label),
                contentPadding = defaultItemPadding(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun SpecialFoldersFormContentPreview() {
    PreviewWithThemes {
        SpecialFoldersFormContent(
            state = FormState(),
            onEvent = {},
        )
    }
}
