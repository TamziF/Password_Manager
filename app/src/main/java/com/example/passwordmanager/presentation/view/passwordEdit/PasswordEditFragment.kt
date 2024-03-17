package com.example.passwordmanager.presentation.view.passwordEdit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.example.passwordmanager.PasswordManagerApp
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentPasswordEditBinding
import com.example.passwordmanager.ioc.passwordEdit.PasswordEditFragmentComponent
import com.example.passwordmanager.ioc.passwordEdit.PasswordEditFragmentViewComponent
import com.example.passwordmanager.presentation.stateholders.PasswordEditViewModel

class PasswordEditFragment : Fragment() {

    private lateinit var binding: FragmentPasswordEditBinding

    private val applicationComponent
        get() = PasswordManagerApp.get(requireContext()).applicationComponent

    private lateinit var fragmentComponent: PasswordEditFragmentComponent
    private var fragmentViewComponent: PasswordEditFragmentViewComponent? = null

    private val viewModel: PasswordEditViewModel by activityViewModels { applicationComponent.viewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {


        fragmentComponent = PasswordEditFragmentComponent(
            viewModel = viewModel,
            fragment = this
        )

        setFragmentResultListener(R.string.request_key.toString()) { _, bundle ->
            bundle.getInt(R.string.item_id.toString()).let { viewModel.setItem(it) }
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordEditBinding.inflate(inflater, container, false)

        fragmentViewComponent = PasswordEditFragmentViewComponent(
            binding = binding,
            fragmentComponent = fragmentComponent
        ).apply {
            viewController.bindViews()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentViewComponent = null
    }

    override fun onDestroy() {
        viewModel.clearData()
        super.onDestroy()
    }
}