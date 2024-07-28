package com.example.todolist.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
import com.example.todolist.common.Extension.setClickableSpan
import com.example.todolist.common.Extension.showProgressDialog
import com.example.todolist.common.constants.Constants
import com.example.todolist.databinding.FragmentRegistrationBinding
import com.example.todolist.ui.activity.AuthenticationActivity
import com.example.todolist.ui.activity.ListActivity
import com.example.todolist.ui.viewModel.RegistrationViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegistrationFragment : BaseFragment<FragmentRegistrationBinding>() {

    private val mRegistrationViewModel: RegistrationViewModel by viewModels()
    private var mUserName: String = ""
    private var mPassword: String = ""
    private var mConfirmPassword: String = ""
    private val auth = Firebase.auth
    private lateinit var sharedPreferences: SharedPreferences
    private var isPasswordVisible = false
    private var isCPasswordVisible = false
    override fun inflateLayout(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegistrationBinding {
        return FragmentRegistrationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.registrationVm = mRegistrationViewModel
        sharedPreferences = requireActivity().getSharedPreferences(
            Constants.PREFERENCE,
            Context.MODE_PRIVATE
        )
        setUpClickableString()
        setUpToolbar()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpToolbar() {
        (activity as AuthenticationActivity).apply {
            this.getBindingFromActivity().apply {
                layoutToolbar.apply {
                    tvTitle.visibility = View.VISIBLE
                    tvTitle.text = getString(R.string.registration)
                    ivBack.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setUpClickableString() {
        binding.tvLogin.setClickableSpan(
            getString(R.string.do_you_have_already_an_account_login), true,
            getString(R.string.login) to {
                findNavController().navigate(R.id.action_registration_Fragment_to_loginFragment)
            })
    }

    override fun setUpClickEvents() {
        binding.register.setOnClickListener {
            if (mPassword.isNotEmpty() && mUserName.isNotEmpty() && mConfirmPassword.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(mUserName).matches()) {
                    if (mPassword.length >= 8) {
                        if (mPassword == mConfirmPassword) {
                            requireContext().showProgressDialog()
                            createUser(mUserName, mPassword)
                        } else {
                            binding.tvError.visibility = View.VISIBLE
                            binding.tvError.text = getString(R.string.both_password_are_not_same)
                        }
                    } else {
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text =
                            getString(R.string.password_length_must_be_greater_than_8)
                    }
                } else {
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = getString(R.string.wrong_email_address)
                }
            } else {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = getString(R.string.password_or_email_is_empty)
            }
        }
        mRegistrationViewModel.passwordVisible.observe(viewLifecycleOwner) {
            isPasswordVisible = !isPasswordVisible
            setUpPasswordVisibility()
        }
        mRegistrationViewModel.cPasswordVisible.observe(viewLifecycleOwner) {
            isCPasswordVisible = !isCPasswordVisible
            setUpCPasswordVisibility()
        }
    }

    private fun setUpPasswordVisibility() {
        if (isPasswordVisible) {
            binding.ivHideOne.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_visibility_24
                )
            )
            binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
        } else {
            binding.ivHideOne.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_visibility_off_24
                )
            )
            binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        mRegistrationViewModel.password.value?.length?.let { binding.password.setSelection(it) }
    }

    private fun setUpCPasswordVisibility() {
        if (isCPasswordVisible) {
            binding.ivHideSecond.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_visibility_24
                )
            )
            binding.confirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        } else {
            binding.ivHideSecond.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_visibility_off_24
                )
            )
            binding.confirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        mRegistrationViewModel.confirmPassword.value?.length?.let { binding.confirmPassword.setSelection(it) }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                requireContext().hideProgressDialog()
                sharedPreferences.edit().putBoolean(Constants.IS_LOGIN, true).apply()
                Intent(requireActivity(), ListActivity::class.java).apply {
                    startActivity(this)
                    requireActivity().finishAffinity()
                }
                Toast.makeText(
                    requireContext(),
                    getString(R.string.register_successfully), Toast.LENGTH_SHORT
                ).show()
            } else {
                requireContext().hideProgressDialog()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.something_went_wrong), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onObservers() {
        mRegistrationViewModel.apply {
            userName.observe(viewLifecycleOwner) {
                binding.tvError.visibility = View.GONE
                mUserName = it
            }
            password.observe(viewLifecycleOwner) {
                binding.tvError.visibility = View.GONE
                mPassword = it
            }
            confirmPassword.observe(viewLifecycleOwner) {
                binding.tvError.visibility = View.GONE
                mConfirmPassword = it
            }
        }
    }
}