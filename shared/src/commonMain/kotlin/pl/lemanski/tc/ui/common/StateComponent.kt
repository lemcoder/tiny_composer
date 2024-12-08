package pl.lemanski.tc.ui.common

sealed class StateComponent {
    data class Button(
        val text: String,
        val onClick: () -> Unit
    ) : StateComponent()

    data class Input(
        val value: String,
        val type: Type,
        val hint: String,
        val error: String? = null,
        val onValueChange: (String) -> Unit
    ) {
        enum class Type {
            NUMBER,
            TEXT,
        }
    }

    data class SelectInput<T>(
        val selected: Option<T>,
        val hint: String,
        val onSelected: (Option<T>) -> Unit,
        val options: Set<Option<T>>
    ) {
        data class Option<T>(
            val name: String,
            val value: T
        )
    }

    data class SnackBar(
        val message: String,
        val action: String?,
        val onAction: (() -> Unit)?
    ) : StateComponent()

    data class ValuePicker(
        val value: Int,
        val onValueChange: (Int) -> Unit
    ) : StateComponent()

    data class TabComponent<T>(
        val selected: Tab<T>,
        val options: Set<Tab<T>>,
        val onTabSelected: (T) -> Unit
    ): StateComponent() {
        data class Tab<T>(
            val name: String,
            val value: T
        )
    }
}