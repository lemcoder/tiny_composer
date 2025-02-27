package pl.lemanski.tc.ui.projectAiGenerate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import pl.lemanski.tc.ui.common.StateComponent
import pl.lemanski.tc.ui.common.ToComposable
import pl.lemanski.tc.ui.common.composables.LoaderScaffold
import pl.lemanski.tc.ui.common.composables.ToComposable
import pl.lemanski.tc.ui.common.composables.ToTextArea
import pl.lemanski.tc.ui.projectAiGenerate.ProjectAiGenerateContract.PromptOption

@Composable
internal fun ProjectAiGenerateScreen(
    isLoading: Boolean,
    title: String,
    promptOptions: StateComponent.RadioGroup<PromptOption>,
    promptInput: StateComponent.Input,
    submitButton: StateComponent.Button,
    snackBar: StateComponent.SnackBar?,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LoaderScaffold(isLoading) { snackbarHostState ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.Top
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            promptOptions.ToComposable()

            promptInput.ToTextArea()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    keyboardController?.hide()
                    submitButton.onClick()
                }) {
                    Text(text = submitButton.text)
                }
            }

            snackBar?.ToComposable(snackbarHostState)
        }
    }
}