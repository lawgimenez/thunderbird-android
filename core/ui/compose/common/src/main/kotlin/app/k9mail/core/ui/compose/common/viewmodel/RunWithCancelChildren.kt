package app.k9mail.core.ui.compose.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancelChildren

fun ViewModel.runWithCancelChildren(block: () -> Unit) {
    viewModelScope.coroutineContext.cancelChildren()
    block()
}
