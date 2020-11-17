package tech.stonks.kvizard.action

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import tech.stonks.kvizard.settings.AppSettingsState

class NewsAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        BrowserUtil.browse("https://kotlin.news")
        val appSettings = AppSettingsState.getInstance()
        appSettings?.state?.isNotificationDisabled = true
    }
}