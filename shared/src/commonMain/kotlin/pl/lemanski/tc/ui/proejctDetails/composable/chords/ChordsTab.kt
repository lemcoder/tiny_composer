package pl.lemanski.tc.ui.proejctDetails.composable.chords

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import pl.lemanski.tc.ui.proejctDetails.ProjectDetailsContract
import pl.lemanski.tc.ui.proejctDetails.composable.Wheel

@Composable
internal fun ChordsTab(
    state: ProjectDetailsContract.State.PageState
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp),
        contentAlignment = if (state.wheelPicker != null) Alignment.Center else Alignment.TopCenter
    ) {
        if (state.wheelPicker != null) {
            Wheel(
                selected = state.wheelPicker.selectedValue,
                options = state.wheelPicker.values,
                onNoteSelected = state.wheelPicker.onValueSelected,
                size = DpSize(this@BoxWithConstraints.maxWidth, this@BoxWithConstraints.maxWidth)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(state.barLength)
            ) {
                items(state.chordBeats) {
                    it.ChordBeatItem()
                }
            }
        }
    }

    state.bottomSheet?.let {
        when (it) {
            is ProjectDetailsContract.State.BottomSheet.ChordBottomSheet -> ChordDetailsBottomSheet(it)
            else                                                         -> Unit // Do not render melody bottom sheet here
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProjectDetailsContract.State.PageState.ChordComponent.ChordBeatItem() {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = Modifier.height(48.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(color = if (isActive) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .5f) else Color.Unspecified)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onChordLongClick(id)
                    },
                    onDoubleClick = { onChordDoubleClick(id) },
                ) {
                    onChordClick(id)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            if (isPrimary) {
                Text(
                    text = chord.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            } else {
                Text(
                    text = "%",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        VerticalDivider(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxHeight(),
            thickness = 2.dp
        )
    }
}