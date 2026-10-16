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

        // Hiển thị URL hiện tại nếu đã có (trường hợp người dùng muốn thay đổi)
        if (settings.serverUrl.isNotEmpty()) {
            binding.serverUrlInput.setText(settings.serverUrl)
        }

        binding.connectButton.setOnClickListener {
            val url = binding.serverUrlInput.text.toString().trim()

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

            // Lưu URL vào cấu hình
            settings.import(mapOf("cloud_url" to url))

            // Chuyển đến MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
