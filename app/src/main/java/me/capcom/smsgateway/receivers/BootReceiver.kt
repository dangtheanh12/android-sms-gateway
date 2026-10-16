package me.capcom.smsgateway.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import me.capcom.smsgateway.modules.gateway.GatewaySettings
import me.capcom.smsgateway.modules.orchestrator.OrchestratorService
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    // ✅ THÊM: Inject GatewaySettings để kiểm tra server URL
    private val settings: GatewaySettings by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (!events.contains(intent.action)) return

        // ✅ GUARD: Nếu chưa cấu hình server URL, không khởi động orchestrator
        if (settings.serverUrl.isNullOrEmpty()) {
            return
        }

        get<OrchestratorService>().start(context, true)
    }

    companion object {
        private val events = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.ACTION_BOOT_COMPLETED",
            Intent.ACTION_REBOOT,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_SHUTDOWN,
        )
    }
}
