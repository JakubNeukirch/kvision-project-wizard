package tech.stonks.kvizard.action

import com.intellij.ide.BrowserUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import tech.stonks.kvizard.settings.AppSettingsState

class NewsAction(text: String) : NotificationAction(text) {
    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        BrowserUtil.browse("https://kotlin.news")
        notification.expire()
        val appSettings = AppSettingsState.getInstance()
        appSettings?.state?.isNotificationDisabled = true
    }
}

class NotShowAction(text: String) : NotificationAction(text) {
    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        notification.expire()
        val appSettings = AppSettingsState.getInstance()
        appSettings?.state?.isNotificationDisabled = true

    }
}