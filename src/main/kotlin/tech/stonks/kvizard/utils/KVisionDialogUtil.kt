package tech.stonks.kvizard.utils

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.IconLoader
import tech.stonks.kvizard.action.NewsAction
import tech.stonks.kvizard.action.NotShowAction
import tech.stonks.kvizard.settings.AppSettingsState

object KVisionDialogUtil {
    fun showNewsDialog() {
        ApplicationManager.getApplication().invokeLater {
            val appSettings = AppSettingsState.getInstance()
            if (appSettings?.state?.isNotificationDisabled == false && appSettings.state?.wasPublishedRecently() == false) {

                Notifications.Bus.notify(
                        Notification(
                                Notifications.SYSTEM_MESSAGES_GROUP_ID,
                                "Invitation",
                                "You have got invitation to join our community! Join Kotlin.News and become ninja developer in Kotlin.",
                                NotificationType.INFORMATION
                        ).apply {
                            icon = IconLoader.getIcon("/images/email-black-18dp.svg")
                            this.addAction(NewsAction("Join Kotlin.News"))
                            this.addAction(NotShowAction("Do not show again"))
                        }
                )
                appSettings.state?.lastDisplayed = System.currentTimeMillis()
            }
        }
    }

    private fun AppSettingsState.State.wasPublishedRecently(): Boolean {
        return (System.currentTimeMillis() - this.lastDisplayed) < 1000 * 60 * 60 * 12
    }
}