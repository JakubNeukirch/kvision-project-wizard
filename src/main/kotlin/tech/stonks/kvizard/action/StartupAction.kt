package tech.stonks.kvizard.action

import com.intellij.openapi.components.ProjectComponent
import tech.stonks.kvizard.utils.KVisionDialogUtil

@Suppress("DEPRECATION")
class StartupAction : ProjectComponent {
    override fun initComponent() {
        KVisionDialogUtil.showNewsDialog()
    }
}