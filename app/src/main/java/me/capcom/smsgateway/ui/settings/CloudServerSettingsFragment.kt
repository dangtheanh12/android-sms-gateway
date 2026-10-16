package me.capcom.smsgateway.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.preference.Preference
import me.capcom.smsgateway.R
import me.capcom.smsgateway.modules.gateway.GatewayService
import me.capcom.smsgateway.modules.gateway.GatewaySettings
import org.koin.android.ext.android.inject

class CloudServerSettingsFragment : BasePreferenceFragment() {
    private val settings: GatewaySettings by inject()
    private val service: GatewayService by inject()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.cloud_server_preferences, rootKey)

        // ✅ Ẩn tất cả các tùy chọn liên quan đến Cloud Server công cộng
        // Chỉ giữ lại trường nhập URL máy chủ riêng
        findPreference("transient.device_id")?.isVisible = false
        findPreference("gateway.username")?.isVisible = false
        findPreference("gateway.password")?.isVisible = false
        findPreference("gateway.clear_password")?.isVisible = false
        findPreference("gateway.login_code")?.isVisible = false

        // ✅ Cấu hình trường nhập URL máy chủ riêng
        findPreference("gateway.cloud_url")?.apply {
            // Đặt tiêu đề và mô tả phù hợp
            title = getString(R.string.server_url_title)
            summary = getString(R.string.server_url_summary)

            // Hiển thị URL hiện tại nếu có
            setSummaryProvider {
                val url = settings.serverUrl
                if (url.isNullOrEmpty()) {
                    getString(R.string.server_url_not_set)
                } else {
                    url
                }
            }

            // Xác thực URL khi người dùng thay đổi
            setOnPreferenceChangeListener { _, newValue ->
                val value = newValue as? String
                if (value.isNullOrEmpty()) {
                    showToast(getString(R.string.server_url_required))
                    return@setOnPreferenceChangeListener false
                }
                if (!value.startsWith("https://")) {
                    showToast(getString(R.string.url_must_start_with_https))
                    return@setOnPreferenceChangeListener false
                }
                true
            }
        }
    }
}
