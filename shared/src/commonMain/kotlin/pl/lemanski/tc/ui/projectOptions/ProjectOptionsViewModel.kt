package pl.lemanski.tc.ui.projectOptions

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.lemanski.tc.domain.model.navigation.ProjectOptionsDestination
import pl.lemanski.tc.domain.service.navigation.NavigationService
import pl.lemanski.tc.domain.service.navigation.back
import pl.lemanski.tc.domain.useCase.getSoundFontPresets.GetSoundFontPresetsUseCase
import pl.lemanski.tc.domain.useCase.loadProject.LoadProjectUseCase
import pl.lemanski.tc.domain.useCase.projectPresetsControl.PresetsControlUseCase
import pl.lemanski.tc.domain.useCase.updateProject.UpdateProjectUseCase
import pl.lemanski.tc.ui.common.StateComponent
import pl.lemanski.tc.ui.common.i18n.I18n
import pl.lemanski.tc.utils.Logger
import pl.lemanski.tc.utils.exception.ViewModelInitException

internal class ProjectOptionsViewModel(
    override val key: ProjectOptionsDestination,
    private val i18n: I18n,
    private val navigationService: NavigationService,
    private val loadProjectUseCase: LoadProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val getSoundFontPresetsUseCase: GetSoundFontPresetsUseCase,
    private val presetsControlUseCase: PresetsControlUseCase
) : ProjectOptionsContract.ViewModel() {

    private var project = loadProjectUseCase(key.projectId) ?: throw ViewModelInitException("Project with id ${key.projectId} not found")

    private val presetOptions = getSoundFontPresetsUseCase().map { mapToPresetOption(it.key to it.value) }.toSet()

    private val chordPreset = presetsControlUseCase.getChordPreset(key.projectId)
    private val melodyPreset = presetsControlUseCase.getMelodyPreset(key.projectId)
    private val logger = Logger(this::class)

    private val initialState = ProjectOptionsContract.State(
        isLoading = false,
        title = i18n.projectOptions.title,
        snackBar = null,
        tempoInput = StateComponent.Input(
            value = project.bpm.toString(),
            type = StateComponent.Input.Type.NUMBER,
            hint = i18n.projectOptions.tempo,
            error = null,
            onValueChange = ::onTempoChanged
        ),
        chordsPresetSelect = StateComponent.SelectInput(
            selected = StateComponent.SelectInput.Option(
                value = chordPreset,
                name = presetOptions.elementAt(chordPreset).name
            ),
            label = i18n.projectOptions.chordsPreset,
            onSelected = ::onChordsPresetSelected,
            options = presetOptions
        ),
        melodyPresetSelect = StateComponent.SelectInput(
            selected = StateComponent.SelectInput.Option(
                value = melodyPreset,
                name = presetOptions.elementAt(melodyPreset).name
            ),
            label = i18n.projectOptions.melodyPreset,
            onSelected = ::onMelodyPresetSelected,
            options = presetOptions
        ),
        exportButton = StateComponent.Button(
            text = i18n.projectOptions.export,
            onClick = ::onExportClicked
        )
    )

    private val _stateFlow: MutableStateFlow<ProjectOptionsContract.State> = MutableStateFlow(initialState)
    override val stateFlow: StateFlow<ProjectOptionsContract.State> = _stateFlow.asStateFlow()

    init {
        logger.debug("Init")
    }

    override fun onAttached() {
        logger.debug("Attached")

        _stateFlow.update { state ->
            state.copy(
                isLoading = false,
            )
        }
    }

    override fun back() {
        navigationService.back()
    }

    override fun onTempoChanged(tempo: String) {
        val tempoInt = tempo.toIntOrNull()

        val error = if (tempoInt == null || tempoInt !in 30..300) {
            i18n.projectOptions.tempoError
        } else {
            null
        }

        _stateFlow.update { state ->
            state.copy(
                tempoInput = state.tempoInput.copy(
                    value = tempo,
                    error = error
                )
            )
        }

        if (error == null && tempoInt != null) {
            project = project.copy(bpm = tempoInt)
            updateProjectUseCase(UpdateProjectErrorHandler(), project, project.id)
        }
    }

    override fun onChordsPresetSelected(preset: StateComponent.SelectInput.Option<Int>) = viewModelScope.launch {
        _stateFlow.update { state ->
            state.copy(
                chordsPresetSelect = state.chordsPresetSelect.copy(selected = preset)
            )
        }

        presetsControlUseCase.setPresets(
            projectId = project.id,
            chordPreset = preset.value
        )
    }

    override fun onMelodyPresetSelected(preset: StateComponent.SelectInput.Option<Int>) = viewModelScope.launch {
        _stateFlow.update { state ->
            state.copy(
                melodyPresetSelect = state.melodyPresetSelect.copy(selected = preset)
            )
        }

        presetsControlUseCase.setPresets(
            projectId = project.id,
            melodyPreset = preset.value
        )
    }

    override fun onExportClicked() {
        // TODO export to wav
    }

    override fun showSnackBar(message: String, action: String?, onAction: (() -> Unit)?) {
        _stateFlow.update { state ->
            state.copy(
                snackBar = StateComponent.SnackBar(
                    message = message,
                    action = action,
                    onAction = onAction
                )
            )
        }
    }

    override fun hideSnackBar() {
        _stateFlow.update { state ->
            state.copy(
                snackBar = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        logger.debug("Cleared")
    }

    //---

    fun mapToPresetOption(preset: Pair<Int, String>): StateComponent.SelectInput.Option<Int> {
        return StateComponent.SelectInput.Option(
            value = preset.first,
            name = preset.second
        )
    }

    //---

    inner class UpdateProjectErrorHandler : UpdateProjectUseCase.ErrorHandler {
        override fun onInvalidProjectName() {
            // will not happen
        }

        override fun onInvalidProjectBpm() {
            _stateFlow.update { state ->
                state.copy(
                    tempoInput = state.tempoInput.copy(
                        error = i18n.projectOptions.tempoError
                    )
                )
            }
        }
    }
}

