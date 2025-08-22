package com.tech.perfumos.ui.auth

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tech.perfumos.R
import com.tech.perfumos.databinding.FragmentLoginBinding
import com.tech.perfumos.ui.base.BaseFragment
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.utils.Utils

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: AuthCommonVM by viewModels()

    override fun onCreateView(view: View) {
        initView()
        initOnClick()
        initObserver()
    }

    private fun initObserver() {

    }

    private fun initOnClick() {

    }

    private fun initView() {
        viewModel.onClick.observe(viewLifecycleOwner, Observer {
            when (it?.id) {
                R.id.buttonLogin -> {
                    Utils.navigateWithSlideAnimations(findNavController(), R.id.navigateToSignupFragment)
                }
            }
        })
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }


}