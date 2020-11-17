package tech.stonks.kvizard.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.serviceContainer.ComponentManagerImpl

@State(
        name = "KVisionWizardSettings",
        storages = [Storage("KVisionWizardSettings.xml", )]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState.State> {
    data class State(
            var lastDisplayed: Long = 0L,
            var isNotificationDisabled: Boolean = false
    )

    companion object {
        fun getInstance(): AppSettingsState? {
            return ServiceManager.getService(AppSettingsState::class.java)
        }
    }

    private var _state: State = State()

    override fun getState(): State? {
        return _state
    }

    override fun loadState(state: State) {
        _state = state
    }
}