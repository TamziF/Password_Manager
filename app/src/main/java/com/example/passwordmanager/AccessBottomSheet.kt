package com.example.passwordmanager

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
import androidx.fragment.app.viewModels
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


    private fun isFirstAppEnter(): Boolean {
        val masterPassword =
            sharedPreferences.getString(getString(R.string.saved_master_password_key), "empty")
        return masterPassword == "empty"
    }

    private fun setMasterPassword(masterPassword: String) {
        with(sharedPreferences.edit()) {
            putString(getString(R.string.saved_master_password_key), masterPassword)
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
                    onDestroy()
                    Toast.makeText(requireContext(), "Succeeded", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    viewModel.setAuth(false)
                    onDestroy()
                    Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for password watching")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use master-password")
            .build()
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

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    private fun firstAppEnterScenario() {
        binding.useBiometricsTv.visibility = View.INVISIBLE
        binding.fingerprintIcon.visibility = View.INVISIBLE
        binding.checkPasswordButton.text = "Create master-password"

        binding.checkPasswordButton.setOnClickListener {
            if (binding.masterPassword.text?.isNotEmpty() == true)
                setMasterPassword(binding.masterPassword.text.toString())
        }
    }

    private fun bindFingerPrintIcon() {
        when (biometricReadinessState) {
            BiometricReadinessState.OK -> {
                binding.fingerprintIcon.visibility = View.VISIBLE
                binding.useBiometricsTv.visibility = View.VISIBLE
                binding.useBiometricsTv.text = "Push to use biometrics"

                binding.fingerprintIcon.setOnClickListener {
                    biometricPrompt.authenticate(promptInfo)
                }
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
    }

    private fun bindCheckMasterPasswordButton() {
        binding.checkPasswordButton.setOnClickListener {
            if (binding.masterPassword.text.toString() == sharedPreferences.getString(
                    getString(R.string.saved_master_password_key),
                    ""
                )
            ) {
                viewModel.setAuth(true)
                Toast.makeText(requireContext(), "Access", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.setAuth(false)
                Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT).show()
            }
        }
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