package tech.stonks.kvizard.action

import com.intellij.ide.BrowserUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import tech.stonks.kvizard.settings.AppSettingsState

class NewsAction(text: String) : NotificationAction(text) {
    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        BrowserUtil.browse("https://kotlin.news")
        ApplicationManager.getApplication().invokeLater {
            notification.expire()
            val appSettings = AppSettingsState.getInstance()
            appSettings?.state?.isNotificationDisabled = true
        }

    }
}

class NotShowAction(text: String) : NotificationAction(text) {
    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        ApplicationManager.getApplication().invokeLater {
            notification.expire()
            val appSettings = AppSettingsState.getInstance()
            appSettings?.state?.isNotificationDisabled = true
        }
    }
}