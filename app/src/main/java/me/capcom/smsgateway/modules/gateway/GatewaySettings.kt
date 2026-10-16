package me.capcom.smsgateway.modules.gateway

import me.capcom.smsgateway.modules.settings.Exporter
import me.capcom.smsgateway.modules.settings.Importer
import me.capcom.smsgateway.modules.settings.KeyValueStorage
import me.capcom.smsgateway.modules.settings.get

class GatewaySettings(
    private val storage: KeyValueStorage,
) : Exporter, Importer {
    enum class NotificationChannel {
        AUTO,
        SSE_ONLY,
    }

    var enabled: Boolean
        get() = storage.get(ENABLED) ?: false
        set(value) = storage.set(ENABLED, value)

    val deviceId: String?
        get() = registrationInfo?.id

    var registrationInfo: GatewayApi.DeviceRegisterResponse?
        get() = storage.get(REGISTRATION_INFO)
        set(value) = storage.set(REGISTRATION_INFO, value)

    var fcmToken: String?
        get() = storage.get(FCM_TOKEN)
        set(value) = storage.set(FCM_TOKEN, value)

    val username: String?
        get() = registrationInfo?.login

    val password: String?
        get() = registrationInfo?.password

    val hasPassword: Boolean
        get() = registrationInfo?.password != null

    fun clearPassword() {
        registrationInfo = registrationInfo?.copy(password = null)
    }

    // ✅ ĐÃ SỬA: Trả về chuỗi rỗng nếu chưa có cấu hình, KHÔNG dùng PUBLIC_URL mặc định
    val serverUrl: String
        get() = storage.get(CLOUD_URL) ?: ""

    val privateToken: String?
        get() = storage.get(PRIVATE_TOKEN)

    val notificationChannel: NotificationChannel
        get() = storage.get(NOTIFICATION_CHANNEL) ?: NotificationChannel.AUTO

    companion object {
        private const val REGISTRATION_INFO = "REGISTRATION_INFO"
        private const val ENABLED = "ENABLED"
        private const val FCM_TOKEN = "fcm_token"
        private const val CLOUD_URL = "cloud_url"
        private const val PRIVATE_TOKEN = "private_token"
        private const val NOTIFICATION_CHANNEL = "notification_channel"

        // ✅ ĐÃ SỬA: Loại bỏ URL máy chủ công cộng
        // Giá trị rỗng buộc ứng dụng yêu cầu người dùng nhập server riêng
        const val PUBLIC_URL = ""
    }

    override fun export(): Map<String, Any?> {
        return mapOf(
            CLOUD_URL to serverUrl,
            NOTIFICATION_CHANNEL to notificationChannel.name,
        )
    }

    override fun import(data: Map<String, Any?>): Boolean {
        return data.map { (key, value) ->
            when (key) {
                CLOUD_URL -> {
                    val url = value?.toString() ?: PUBLIC_URL
                    if (url.isNotEmpty() && !url.startsWith("https://")) {
                        throw IllegalArgumentException("url must start with https://")
                    }
                    val changed = serverUrl != url
                    storage.set(key, url)
                    changed
                }

                PRIVATE_TOKEN -> {
                    val newValue = value?.toString()
                    val changed = privateToken != newValue
                    storage.set(key, newValue)
                    changed
                }

                NOTIFICATION_CHANNEL -> {
                    val newValue = value?.let { NotificationChannel.valueOf(it.toString()) }
                        ?: NotificationChannel.AUTO
                    val changed = notificationChannel != newValue
                    storage.set(key, newValue.name)
                    changed
                }

                else -> false
            }
        }.any { it }
    }
}
