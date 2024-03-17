package com.example.passwordmanager.presentation.view.access_bottom_sheet

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.passwordmanager.CryptoManager
import com.example.passwordmanager.PasswordManagerApp
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentAccessBinding
import com.example.passwordmanager.presentation.stateholders.PasswordEditViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.concurrent.Executor

enum class BiometricReadinessState { OK, NO_HARDWARE, NOT_AVAILABLE, NONE_ENROLLED }

class AccessBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAccessBinding

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private lateinit var biometricReadinessState: BiometricReadinessState

    private lateinit var sharedPreferences: SharedPreferences

    private var isFirstAppEnter = false

    private val applicationComponent
        get() = PasswordManagerApp.get(requireContext()).applicationComponent

    private val viewModel: PasswordEditViewModel by activityViewModels { applicationComponent.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!

        if (isFirstAppEnter()) {
            isFirstAppEnter = true
        } else {
            checkBiometricSupported()

            bindBiometricPrompt()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccessBinding.inflate(inflater, container, false)

        if (isFirstAppEnter) {
            firstAppEnterScenario()
        } else {
            bindFingerPrintIcon()
            bindCheckMasterPasswordButton()
        }

        return binding.root
    }


    private fun isFirstAppEnter(): Boolean {
        val masterPassword =
            sharedPreferences.getString(getString(R.string.saved_master_password_key), "empty")
        return masterPassword == "empty"
    }

    private fun setMasterPassword(masterPassword: String) {
        val encryptedPassword = CryptoManager.encrypt(masterPassword)
        with(sharedPreferences.edit()) {
            putString(getString(R.string.saved_master_password_key), encryptedPassword)
            apply()
        }
        viewModel.setAuth(true)
    }

    private fun bindBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != 13)
                        Toast.makeText(requireContext(), "Error: $errString", Toast.LENGTH_SHORT)
                            .show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.setAuth(true)
                    Toast.makeText(requireContext(), "Succeeded", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    viewModel.setAuth(false)
                    Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
                }
            })

        bindPromptInfo()
    }

    private fun bindPromptInfo() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for password watching")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use master-password")
            .build()
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    private fun firstAppEnterScenario() {
        hideBiometry()
        binding.checkPasswordButton.text = getString(R.string.create_master_password)

        binding.checkPasswordButton.setOnClickListener {
            if (binding.masterPassword.text?.isNotBlank() == true)
                setMasterPassword(binding.masterPassword.text.toString())
        }
    }

    private fun bindFingerPrintIcon() {
        when (biometricReadinessState) {
            BiometricReadinessState.OK -> {
                showBiometry()
                binding.useBiometricsTv.text = getString(R.string.push_to_use_biometrics)

                binding.fingerprintIcon.setOnClickListener {
                    biometricPrompt.authenticate(promptInfo)
                }
            }

            BiometricReadinessState.NO_HARDWARE -> {
                hideBiometry()
            }

            BiometricReadinessState.NOT_AVAILABLE -> {
                showBiometry()
                binding.useBiometricsTv.text = getString(R.string.finger_print_not_available)
            }

            BiometricReadinessState.NONE_ENROLLED -> {
                showBiometry()
                binding.useBiometricsTv.text = getString(R.string.there_are_no_finger_prints)
            }
        }
    }

    private fun hideBiometry() {
        binding.fingerprintIcon.visibility = View.INVISIBLE
        binding.useBiometricsTv.visibility = View.INVISIBLE
    }

    private fun showBiometry() {
        binding.fingerprintIcon.visibility = View.VISIBLE
        binding.useBiometricsTv.visibility = View.VISIBLE
    }

    private fun bindCheckMasterPasswordButton() {
        binding.checkPasswordButton.setOnClickListener {
            if (binding.masterPassword.text.toString() == getMasterPassword()
            ) {
                viewModel.setAuth(true)
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.setAuth(false)
                Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getMasterPassword(): String {
        val encryptedPassword = sharedPreferences.getString(
            getString(R.string.saved_master_password_key),
            ""
        )
        return CryptoManager.decrypt(encryptedPassword!!)
    }

    private fun checkBiometricSupported() {
        val biometricManager: BiometricManager = BiometricManager.from(requireContext())
        biometricReadinessState =
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricReadinessState.OK
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricReadinessState.NO_HARDWARE
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricReadinessState.NOT_AVAILABLE
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricReadinessState.NONE_ENROLLED
                else -> {
                    BiometricReadinessState.NO_HARDWARE
                }
            }
    }
}