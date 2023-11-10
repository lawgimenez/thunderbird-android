package app.k9mail.feature.account.setup.ui.specialfolders

import androidx.lifecycle.viewModelScope
import app.k9mail.core.ui.compose.common.mvi.BaseViewModel
import app.k9mail.feature.account.setup.domain.DomainContract.UseCase
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Effect
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Event
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.State
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.ViewModel
import com.fsck.k9.mail.FolderType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CONTINUE_NEXT_DELAY = 1500L

class SpecialFoldersViewModel(
    private val formUiModel: SpecialFoldersContract.FormUiModel,
    private val getRemoteFolders: UseCase.GetRemoteFolders,
    private val getRemoteFoldersToFolderTypeMapping: UseCase.GetRemoteFoldersToFolderTypeMapping,
    initialState: State = State(),
) : BaseViewModel<State, Event, Effect>(initialState),
    ViewModel {

    override fun event(event: Event) {
        when (event) {
            Event.LoadSpecialFolders -> handleOneTimeEvent(event, ::onLoadSpecialFolders)

            is FormEvent -> onFormEvent(event)

            Event.OnNextClicked -> onNextClicked()
            Event.OnBackClicked -> onBackClicked()
        }
    }

    private fun onFormEvent(event: FormEvent) {
        updateState {
            it.copy(
                formState = formUiModel.event(event, it.formState),
            )
        }
    }

    private fun onLoadSpecialFolders() {
        viewModelScope.launch {
            val folders = getRemoteFolders.execute()
            val folderTypeMapping = getRemoteFoldersToFolderTypeMapping.execute(folders)

            updateState { state ->
                state.copy(
                    formState = state.formState.copy(
                        archiveFolders = folders.associateBy { it.displayName },
                        draftsFolders = folders.associateBy { it.displayName },
                        sentFolders = folders.associateBy { it.displayName },
                        spamFolders = folders.associateBy { it.displayName },
                        trashFolders = folders.associateBy { it.displayName },

                        selectedArchiveFolder = folderTypeMapping[FolderType.ARCHIVE],
                        selectedDraftsFolder = folderTypeMapping[FolderType.DRAFTS],
                        selectedSentFolder = folderTypeMapping[FolderType.SENT],
                        selectedSpamFolder = folderTypeMapping[FolderType.SPAM],
                        selectedTrashFolder = folderTypeMapping[FolderType.TRASH],
                    ),
                )
            }

            // todo validate folders
            // if valid change to success else disable loading only and present special folders form
            delay(CONTINUE_NEXT_DELAY)
            updateState {
                it.copy(
                    isSuccess = false,
                    isLoading = false,
                )
            }
        }
    }

    private fun onNextClicked() {
        emitEffect(Effect.NavigateNext)
    }

    private fun onBackClicked() {
        emitEffect(Effect.NavigateBack)
    }
}
