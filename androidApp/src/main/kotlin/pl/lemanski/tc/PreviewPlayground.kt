package pl.lemanski.tc

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pl.lemanski.tc.theme.TcTheme
import pl.lemanski.tc.ui.common.StateComponent
import pl.lemanski.tc.ui.proejctDetails.ProjectDetailsRouter
import pl.lemanski.tc.ui.proejctDetails.ProjectDetailsScreen

@Composable
@Preview
fun PreviewPlayground() {
    TcTheme {
        ProjectDetailsScreen(
            isLoading = false,
            projectName = "Test",
            projectRhythm = "adipiscing",
            tempoInput = StateComponent.Input(value = "delectus", type = StateComponent.Input.Type.TEXT, hint = "vehicula", error = null, onValueChange = {}),
            chordsTextArea = StateComponent.Input(value = "mandamus", type = StateComponent.Input.Type.TEXT, hint = "mea", error = null, onValueChange = {}),
            playButton = null,
            stopButton = null,
            backButton = StateComponent.Button(text = "movet", onClick = {}),
            aiGenerateButton = StateComponent.Button(text = "an", onClick = {}),
            snackBar = null
        )
    }
}