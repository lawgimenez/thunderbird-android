package app.k9mail.feature.account.setup.ui.specialfolders

import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.FormState
import app.k9mail.feature.account.setup.ui.specialfolders.SpecialFoldersContract.State
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class SpecialFoldersStateTest {

    @Test
    fun `should set default values`() {
        val state = State()

        assertThat(state).isEqualTo(
            State(
                formState = FormState(),
                isSuccess = false,
                error = null,
                isLoading = true,
            ),
        )
    }

    @Test
    fun `should set default form values`() {
        val formState = FormState()

        assertThat(formState).isEqualTo(
            FormState(
                archiveFolders = emptyList(),
                draftsFolders = emptyList(),
                sentFolders = emptyList(),
                spamFolders = emptyList(),
                trashFolders = emptyList(),

                selectedArchiveFolder = null,
                selectedDraftsFolder = null,
                selectedSentFolder = null,
                selectedSpamFolder = null,
                selectedTrashFolder = null,
            ),
        )
    }
}
