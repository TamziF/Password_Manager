package com.example.passwordmanager.ioc.passwordsList

import com.example.passwordmanager.databinding.FragmentPasswordsListBinding
import com.example.passwordmanager.presentation.view.passwordsList.PasswordsListFragmentViewController

class PasswordsListFragmentViewComponent(
    binding: FragmentPasswordsListBinding,
    fragmentComponent: PasswordsListFragmentComponent
) {
    val viewController = PasswordsListFragmentViewController(
        fragment = fragmentComponent.fragment,
        viewModel = fragmentComponent.viewModel,
        context = fragmentComponent.context,
        binding = binding
    )
}