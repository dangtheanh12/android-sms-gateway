package me.capcom.smsgateway

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.capcom.smsgateway.databinding.ActivityServerSetupBinding
import me.capcom.smsgateway.modules.gateway.GatewaySettings
import org.koin.android.ext.android.inject

class ServerSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerSetupBinding
    private val settings: GatewaySettings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hiển thị URL và token hiện tại nếu đã có
        if (settings.serverUrl.isNotEmpty()) {
            binding.serverUrlInput.setText(settings.serverUrl)
        }
        settings.privateToken?.let {
            binding.privateTokenInput.setText(it)
        }

        binding.connectButton.setOnClickListener {
            val url = binding.serverUrlInput.text.toString().trim()
            val token = binding.privateTokenInput.text.toString().trim()

            // Xác thực URL
            if (url.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.server_url_required),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!url.startsWith("https://")) {
                Toast.makeText(
                    this,
                    getString(R.string.url_must_start_with_https),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!url.contains("/api/mobile/")) {
                Toast.makeText(
                    this,
                    getString(R.string.server_url_note),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Lưu cấu hình
            try {
                settings.saveServerConfig(
                    url = url,
                    token = token.ifEmpty { null }
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Lỗi lưu cấu hình: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Khởi động lại app sạch
            restartApp()
        }
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finishAffinity()
    }
}
