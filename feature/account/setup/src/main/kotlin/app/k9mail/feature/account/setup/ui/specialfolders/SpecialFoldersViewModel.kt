package app.k9mail.feature.account.setup.ui.specialfolders

import androidx.lifecycle.viewModelScope
import app.k9mail.core.ui.compose.common.mvi.BaseViewModel
import app.k9mail.feature.account.common.domain.AccountDomainContract
import app.k9mail.feature.account.common.domain.entity.SpecialFolderSettings
import app.k9mail.feature.account.setup.domain.DomainContract.UseCase
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Effect
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Event
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Failure.SaveFailed
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.State
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.ViewModel
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mail.folders.RemoteFolder
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CONTINUE_NEXT_DELAY = 1500L

@Suppress("TooManyFunctions")
class SpecialFoldersViewModel(
    private val formUiModel: SpecialFoldersContract.FormUiModel,
    private val getRemoteFolders: UseCase.GetRemoteFolders,
    private val getRemoteFoldersToFolderTypeMapping: UseCase.GetRemoteFoldersToFolderTypeMapping,
    private val filterRemoteFoldersForType: UseCase.FilterRemoteFoldersForType,
    private val accountStateRepository: AccountDomainContract.AccountStateRepository,
    initialState: State = State(),
) : BaseViewModel<State, Event, Effect>(initialState),
    ViewModel {

    override fun event(event: Event) {
        when (event) {
            Event.LoadSpecialFolders -> handleOneTimeEvent(event, ::onLoadSpecialFolders)

            is FormEvent -> onFormEvent(event)

            Event.OnNextClicked -> onNextClicked()
            Event.OnBackClicked -> onBackClicked()
            Event.OnEditClicked -> onEditClicked()
            Event.OnRetryClicked -> onRetryClicked()
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
            val formState = mapToFormState(folders, folderTypeMapping)

            updateState { state ->
                state.copy(
                    formState = formState,
                )
            }

            validateFormState()
        }
    }

    private suspend fun mapToFormState(
        folders: List<RemoteFolder>,
        folderTypeMapping: Map<FolderType, RemoteFolder?>,
    ): FormState {
        val archiveFolders = filterRemoteFoldersForType.execute(FolderType.ARCHIVE, folders)
        val draftsFolders = filterRemoteFoldersForType.execute(FolderType.DRAFTS, folders)
        val sentFolders = filterRemoteFoldersForType.execute(FolderType.SENT, folders)
        val spamFolders = filterRemoteFoldersForType.execute(FolderType.SPAM, folders)
        val trashFolders = filterRemoteFoldersForType.execute(FolderType.TRASH, folders)

        return FormState(
            archiveFolders = archiveFolders.associateBy { it.displayName },
            draftsFolders = draftsFolders.associateBy { it.displayName },
            sentFolders = sentFolders.associateBy { it.displayName },
            spamFolders = spamFolders.associateBy { it.displayName },
            trashFolders = trashFolders.associateBy { it.displayName },

            selectedArchiveFolder = folderTypeMapping[FolderType.ARCHIVE],
            selectedDraftsFolder = folderTypeMapping[FolderType.DRAFTS],
            selectedSentFolder = folderTypeMapping[FolderType.SENT],
            selectedSpamFolder = folderTypeMapping[FolderType.SPAM],
            selectedTrashFolder = folderTypeMapping[FolderType.TRASH],
        )
    }

    private fun validateFormState() {
        updateState {
            it.copy(
                isSuccess = false,
                isLoading = true,
                error = null,
            )
        }
        val isValid = formUiModel.validate(state.value.formState)
        updateState { state ->
            state.copy(
                isSuccess = isValid,
                isLoading = isValid,
            )
        }
        if (isValid) {
            saveSpecialFolderSettings()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun saveSpecialFolderSettings() {
        updateState { state ->
            state.copy(
                isLoading = true,
            )
        }

        viewModelScope.launch {
            val formState = state.value.formState

            try {
                accountStateRepository.setSpecialFolderSettings(
                    SpecialFolderSettings(
                        archiveFolder = formState.selectedArchiveFolder!!,
                        draftsFolder = formState.selectedDraftsFolder!!,
                        sentFolder = formState.selectedSentFolder!!,
                        spamFolder = formState.selectedSpamFolder!!,
                        trashFolder = formState.selectedTrashFolder!!,
                    ),
                )
                updateState { state ->
                    state.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
                }
            } catch (e: Exception) {
                updateState { state ->
                    state.copy(
                        isLoading = false,
                        error = SaveFailed(e.message ?: "unknown error"),
                    )
                }
                return@launch
            }
        }

        viewModelScope.launch {
            if (state.value.isSuccess) {
                delay(CONTINUE_NEXT_DELAY)
                navigateNext()
            }
        }
    }

    private fun onNextClicked() {
        if (state.value.isSuccess) {
            navigateNext()
        } else {
            validateFormState()
        }
    }

    private fun navigateNext() {
        viewModelScope.coroutineContext.cancelChildren()
        emitEffect(Effect.NavigateNext)
    }

    private fun onBackClicked() {
        viewModelScope.coroutineContext.cancelChildren()
        emitEffect(Effect.NavigateBack)
    }

    private fun onEditClicked() {
        viewModelScope.coroutineContext.cancelChildren()
        updateState { state ->
            state.copy(
                isSuccess = false,
            )
        }
    }

    private fun onRetryClicked() {
        viewModelScope.coroutineContext.cancelChildren()
        updateState {
            it.copy(
                error = null,
            )
        }
    }
}
