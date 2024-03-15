package com.example.passwordmanager.ioc.passwordEdit

import com.example.passwordmanager.databinding.FragmentPasswordEditBinding
import com.example.passwordmanager.presentation.view.passwordEdit.PasswordEditFragmentViewController

class PasswordEditFragmentViewComponent(
    binding: FragmentPasswordEditBinding,
    fragmentComponent: PasswordEditFragmentComponent
) {
    val viewController = PasswordEditFragmentViewController(
        viewModel = fragmentComponent.viewModel,
        fragment = fragmentComponent.fragment,
        binding = binding
    )
}