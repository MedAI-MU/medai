package org.example.project.core.presentation.mvi

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ScreenModel for MVI architecture.
 *
 * @param State The UI state of the screen.
 * @param Event The intents or actions triggered by the user.
 * @param Effect Side effects (e.g., navigation, showing toasts) that should be consumed only once.
 */
abstract class MviScreenModel<State, Event, Effect>(
    initialState: State
) : ScreenModel {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    abstract fun onEvent(event: Event)

    protected fun setState(reducer: State.() -> State) {
        _state.update(reducer)
    }

    protected fun sendEffect(effect: Effect) {
        screenModelScope.launch {
            _effect.send(effect)
        }
    }
}
