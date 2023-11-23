package app.k9mail.feature.account.setup.ui.specialfolders

import androidx.lifecycle.viewModelScope
import app.k9mail.core.ui.compose.common.mvi.BaseViewModel
import app.k9mail.core.ui.compose.common.viewmodel.runWithCancelChildren
import app.k9mail.feature.account.common.domain.AccountDomainContract
import app.k9mail.feature.account.common.domain.entity.SpecialFolderSettings
import app.k9mail.feature.account.setup.domain.DomainContract.UseCase
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Effect
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Event
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.Failure.SaveFailed
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormEvent
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.State
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CONTINUE_NEXT_DELAY = 1500L

class SpecialFoldersViewModel(
    private val formUiModel: SpecialFoldersContract.FormUiModel,
    private val getFolders: UseCase.GetFolders,
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
            val specialFolders = getFolders()

            updateState { state ->
                state.copy(
                    formState = specialFolders.toFormState(),
                )
            }

            validateFormState()
        }
    }

    private fun validateFormState() {
        updateState {
            it.copy(
                isSuccess = false,
                isLoading = true,
                error = null,
            )
        }
        viewModelScope.launch {
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

    private fun navigateNext() = runWithCancelChildren {
        emitEffect(Effect.NavigateNext)
    }

    private fun onBackClicked() = runWithCancelChildren {
        emitEffect(Effect.NavigateBack)
    }

    private fun onEditClicked() = runWithCancelChildren {
        updateState { state ->
            state.copy(
                isSuccess = false,
            )
        }
    }

    private fun onRetryClicked() = runWithCancelChildren {
        updateState {
            it.copy(
                error = null,
            )
        }
    }
}
