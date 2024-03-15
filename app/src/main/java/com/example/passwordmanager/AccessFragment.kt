package com.example.passwordmanager

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.passwordmanager.databinding.FragmentAccessBinding
import java.util.concurrent.Executor

enum class BiometricReadinessState { OK, NO_HARDWARE, NOT_AVAILABLE, NONE_ENROLLED }

class AccessFragment : Fragment() {

    private lateinit var binding: FragmentAccessBinding

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private lateinit var biometricReadinessState: BiometricReadinessState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkBiometricSupported()

        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == 13) {

                    } else
                        Toast.makeText(requireContext(), "Error: $errString", Toast.LENGTH_SHORT)
                            .show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(requireContext(), "Succeeded", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use master-password")
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccessBinding.inflate(inflater, container, false)

        when (biometricReadinessState) {
            BiometricReadinessState.OK -> {
                binding.fingerprintIcon.visibility = View.VISIBLE
                binding.useBiometricsTv.visibility = View.VISIBLE
                binding.useBiometricsTv.text = "Use biometrics"
            }

            BiometricReadinessState.NO_HARDWARE -> {
                binding.fingerprintIcon.visibility = View.INVISIBLE
                binding.useBiometricsTv.visibility = View.INVISIBLE
            }

            BiometricReadinessState.NOT_AVAILABLE -> {
                binding.fingerprintIcon.visibility = View.VISIBLE
                binding.useBiometricsTv.visibility = View.VISIBLE
                binding.useBiometricsTv.text = "Finger print not available"
            }

            BiometricReadinessState.NONE_ENROLLED -> {
                binding.fingerprintIcon.visibility = View.VISIBLE
                binding.useBiometricsTv.visibility = View.VISIBLE
                binding.useBiometricsTv.text = "There are no finger prints"
            }
        }

        biometricPrompt.authenticate(promptInfo)

        return binding.root
    }

    private fun checkBiometricSupported() {
        val biometricManager: BiometricManager = BiometricManager.from(requireContext())
        biometricReadinessState =
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricReadinessState.OK
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricReadinessState.NO_HARDWARE
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricReadinessState.NOT_AVAILABLE
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricReadinessState.NONE_ENROLLED
                // Prompts the user to create credentials that your app accepts.

                /*val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                private val REQUEST_CODE = 1
                startActivityForResult(enrollIntent, REQUEST_CODE)*/
                else -> {
                    BiometricReadinessState.NO_HARDWARE
                }
            }
    }
}