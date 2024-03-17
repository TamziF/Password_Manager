package com.example.passwordmanager.presentation.view.passwordsList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.passwordmanager.PasswordManagerApp
import com.example.passwordmanager.databinding.FragmentPasswordsListBinding
import com.example.passwordmanager.ioc.ApplicationComponent
import com.example.passwordmanager.ioc.passwordsList.PasswordsListFragmentComponent
import com.example.passwordmanager.ioc.passwordsList.PasswordsListFragmentViewComponent
import com.example.passwordmanager.presentation.stateholders.PasswordsListViewModel

class PasswordsListFragment : Fragment() {

    private lateinit var binding: FragmentPasswordsListBinding

    private val applicationComponent: ApplicationComponent
        get() = PasswordManagerApp.get(requireContext()).applicationComponent

    private lateinit var fragmentComponent: PasswordsListFragmentComponent

    private var fragmentViewComponent: PasswordsListFragmentViewComponent? = null

    private val viewModel: PasswordsListViewModel by viewModels { applicationComponent.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentComponent = PasswordsListFragmentComponent(
            viewModel = viewModel,
            fragment = this,
            context = requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordsListBinding.inflate(layoutInflater, container, false)

        fragmentViewComponent = PasswordsListFragmentViewComponent(
            binding = binding,
            fragmentComponent = fragmentComponent
        ).apply {
            viewController.bindViews()
        }

        return binding.root
    }
}