package com.example.flashlight

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var isLightOn = false

    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = findFlashCameraId()
        buildInterface()

        if (cameraId == null) {
            toggleButton.isEnabled = false
            statusText.text = "Flashlight not available"
            toggleButton.text = "NOT AVAILABLE"
            return
        }

        toggleButton.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                toggleFlashlight()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST
                )
            }
        }
    }

    private fun findFlashCameraId(): String? {
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (_: CameraAccessException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun buildInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.rgb(18, 22, 30))
        }

        val title = TextView(this).apply {
            text = "FLASHLIGHT"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        statusText = TextView(this).apply {
            text = "LIGHT IS OFF"
            textSize = 18f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 28)
        }

        toggleButton = Button(this).apply {
            text = "TURN ON"
            textSize = 22f
            isAllCaps = false
            setPadding(28, 24, 28, 24)
        }

        val hint = TextView(this).apply {
            text = "Tap the button to turn your phone's LED on or off."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(12, 28, 12, 0)
        }

        root.addView(title, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        root.addView(statusText, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        root.addView(toggleButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        root.addView(hint, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        setContentView(root)
    }

    private fun toggleFlashlight() {
        val id = cameraId ?: return
        val turnOn = !isLightOn
        try {
            cameraManager.setTorchMode(id, turnOn)
            isLightOn = turnOn
            updateInterface()
        } catch (_: CameraAccessException) {
            Toast.makeText(this, "Could not access the flashlight.", Toast.LENGTH_SHORT).show()
        } catch (_: SecurityException) {
            Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateInterface() {
        if (isLightOn) {
            statusText.text = "LIGHT IS ON"
            statusText.setTextColor(Color.rgb(255, 213, 79))
            toggleButton.text = "TURN OFF"
        } else {
            statusText.text = "LIGHT IS OFF"
            statusText.setTextColor(Color.LTGRAY)
            toggleButton.text = "TURN ON"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleFlashlight()
            } else {
                Toast.makeText(this, "Allow camera permission to use the flashlight.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStop() {
        if (isLightOn && cameraId != null) {
            try {
                cameraManager.setTorchMode(cameraId!!, false)
            } catch (_: Exception) {
                // Android may already have disabled the torch.
            }
            isLightOn = false
        }
        super.onStop()
    }
}
