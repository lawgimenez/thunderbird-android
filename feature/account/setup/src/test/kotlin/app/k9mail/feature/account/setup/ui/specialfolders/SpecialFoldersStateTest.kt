package app.k9mail.feature.account.setup.ui.specialfolders

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
                isSuccess = false,
                error = null,
                isLoading = true,
            ),
        )
    }
}
