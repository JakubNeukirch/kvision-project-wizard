package tech.stonks.kvizard.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import tech.stonks.kvizard.settings.AppSettingsState

class NotShowAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val appSettings = AppSettingsState.getInstance()
        appSettings?.state?.isNotificationDisabled = true
    }
}