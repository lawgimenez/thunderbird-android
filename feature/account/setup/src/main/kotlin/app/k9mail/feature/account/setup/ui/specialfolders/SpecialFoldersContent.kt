package app.k9mail.feature.account.setup.ui.specialfolders

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import app.k9mail.core.ui.compose.designsystem.molecule.ContentLoadingErrorView
import app.k9mail.core.ui.compose.designsystem.molecule.ErrorView
import app.k9mail.core.ui.compose.designsystem.molecule.LoadingView
import app.k9mail.core.ui.compose.designsystem.template.ResponsiveWidthContainer
import app.k9mail.core.ui.compose.theme.PreviewWithThemes
import app.k9mail.feature.account.common.ui.loadingerror.rememberContentLoadingErrorViewState
import app.k9mail.feature.account.common.ui.view.SuccessView
import app.k9mail.feature.account.setup.R
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Event
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.State

@Composable
fun SpecialFoldersContent(
    state: State,
    onEvent: (Event) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    ResponsiveWidthContainer(
        modifier = Modifier
            .testTag("SpecialFoldersContent")
            .padding(contentPadding)
            .then(modifier),
    ) {
        ContentLoadingErrorView(
            state = rememberContentLoadingErrorViewState(state = state),
            loading = {
                LoadingView(
                    message = stringResource(id = R.string.account_setup_special_folders_validating_message),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            error = {
                ErrorView(
                    title = stringResource(id = R.string.account_setup_special_folders_error_message),
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            if (state.isSuccess) {
                SuccessView(
                    message = stringResource(id = R.string.account_setup_special_folders_success_message),
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                SpecialFoldersFormContent(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun SpecialFoldersContentPreview() {
    PreviewWithThemes {
        SpecialFoldersContent(
            state = State(
                isLoading = false,
                error = null,
            ),
            onEvent = {},
            contentPadding = PaddingValues(),
        )
    }
}
