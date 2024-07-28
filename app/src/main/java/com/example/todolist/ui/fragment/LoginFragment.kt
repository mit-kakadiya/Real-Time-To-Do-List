package com.example.todolist.ui.fragment

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.common.Extension.hideProgressDialog
import com.example.todolist.common.Extension.showProgressDialog
import com.example.todolist.common.constants.Constants
import com.example.todolist.databinding.FragmentLoginBinding
import com.example.todolist.ui.activity.AuthenticationActivity
import com.example.todolist.ui.activity.ListActivity
import com.example.todolist.ui.viewModel.LoginViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : BaseFragment<FragmentLoginBinding>() {


    private val mLoginViewModel: LoginViewModel by viewModels()
    private var mUserName: String = ""
    private var mPassword: String = ""
    private val auth = Firebase.auth
    private lateinit var sharedPreferences: SharedPreferences
    private var isPasswordVisible = false
    override fun inflateLayout(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.loginVm = mLoginViewModel
        sharedPreferences =
            requireActivity().getSharedPreferences(Constants.PREFERENCE, MODE_PRIVATE)
        setUpToolbar()
        setUpPasswordVisibility()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpToolbar() {
        (activity as AuthenticationActivity).apply {
            this.getBindingFromActivity().apply {
                layoutToolbar.apply {
                    ivBack.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            findNavController().popBackStack()
                        }
                    }
                    tvTitle.visibility = View.VISIBLE
                    tvTitle.text = getString(R.string.login)
                }
            }
        }
    }

    override fun setUpClickEvents() {
        binding.login.setOnClickListener {
            binding.login.isEnabled = false
            if (mPassword.isNotEmpty() && mUserName.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(mUserName).matches()) {
                    if (mPassword.length >= 8) {
                        requireContext().showProgressDialog()
                        loginUser(mUserName, mPassword)
                    } else {
                        binding.login.isEnabled = true
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text =
                            getString(R.string.password_length_must_be_greater_than_8)
                    }
                } else {
                    binding.login.isEnabled = true
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = getString(R.string.wrong_email_address)
                }
            } else {
                binding.login.isEnabled = true
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = getString(R.string.password_or_email_is_empty)
            }
        }
        mLoginViewModel.visibility.observe(viewLifecycleOwner) {
            isPasswordVisible = !isPasswordVisible
            setUpPasswordVisibility()
        }
    }

    private fun setUpPasswordVisibility() {
        if (isPasswordVisible) {
            binding.ivHide.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_visibility_24))
            binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
        } else {
            binding.ivHide.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_visibility_off_24))
            binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        mLoginViewModel.password.value?.length?.let { binding.password.setSelection(it) }
    }

    private fun loginUser(userName: String, password: String) {
        auth.signInWithEmailAndPassword(userName, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                requireContext().hideProgressDialog()
                sharedPreferences.edit().putBoolean(Constants.IS_LOGIN, true).apply()
                Intent(requireActivity(), ListActivity::class.java).apply {
                    startActivity(this)
                    requireActivity().finishAffinity()
                }
                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_successfully), Toast.LENGTH_SHORT
                ).show()
            } else {
                requireContext().hideProgressDialog()
                binding.login.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    getString(R.string.wrong_credentials), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onObservers() {
        mLoginViewModel.apply {
            userName.observe(viewLifecycleOwner) {
                binding.tvError.visibility = View.GONE
                mUserName = it
            }
            password.observe(viewLifecycleOwner) {
                binding.tvError.visibility = View.GONE
                mPassword = it
            }
        }
    }

}