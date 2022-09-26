package com.jalasoft.routesapp.ui.auth.phoneAuthentication.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jalasoft.routesapp.MainActivity
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.databinding.FragmentPhoneAuthenticationBinding
import com.jalasoft.routesapp.ui.auth.phoneAuthentication.viewModel.PhoneAuthenticationViewModel
import com.jalasoft.routesapp.util.ConverterFromPixelToDpUtil.toDp
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class PhoneAuthenticationFragment : Fragment() {
    private var _binding: FragmentPhoneAuthenticationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PhoneAuthenticationViewModel by viewModels()
    private lateinit var dialog: AlertDialog
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var textInputEditText: TextInputEditText
    private var dialogInitialized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonAction()
    }
    private fun initAlertDialog() {
        dialogInitialized = true
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setTitle(RoutesAppApplication.resource?.getString(R.string.reg_val_verification_alert_title).toString())
        constraintLayout = getEditTextLayout(requireActivity())
        alertDialog.setView(constraintLayout)
        textInputLayout = constraintLayout.findViewWithTag("textInputLayout")
        textInputEditText = constraintLayout.findViewWithTag("codeEditText")

        alertDialog.setPositiveButton(RoutesAppApplication.resource?.getString(R.string.reg_verify).toString(), null)
        alertDialog.setNegativeButton(RoutesAppApplication.resource?.getString(R.string.reg_val_cancel).toString(), null)
        alertDialog.setNeutralButton(RoutesAppApplication.resource?.getString(R.string.reg_val_resend).toString(), null)
        dialog = alertDialog.create()
        dialog.setCancelable(false)
        dialog.show()

        val verifyButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        verifyButton.setOnClickListener {
            if (textInputEditText.text.isNullOrEmpty()) {
                textInputLayout.error = RoutesAppApplication.resource?.getString(R.string.reg_val_verification_code_required).toString()
            } else {
                textInputLayout.error = ""
                viewModel.verifyCode(textInputEditText.text.toString(), requireActivity())
            }
        }
        val cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        val reSendButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        reSendButton.setOnClickListener {
            val countryCode = "+" + binding.ccp.selectedCountryCode
            val phoneNumber = binding.etRegPhoneNumber.text.toString()
            viewModel.sendVerificationCode(countryCode, phoneNumber, requireActivity())
        }
    }
    private fun buttonAction() {
        binding.btnRegVerifyPhone.setOnClickListener {
            val countryCode = "+" + binding.ccp.selectedCountryCode
            val phoneNumber = binding.etRegPhoneNumber.text.toString()
            viewModel.sendVerificationCode(countryCode, phoneNumber, requireActivity())
        }
    }
    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            binding.tvError.isVisible = true
            binding.tvError.text = errorMessage
        }
        val alertDialogErrorObserver = Observer<String> { errorMessage ->
            textInputLayout.error = errorMessage
        }
        val codeSubmittedObserver = Observer<Boolean> {
            if (dialogInitialized) {
                textInputLayout.error = RoutesAppApplication.resource?.getString(R.string.reg_val_phone_code_forwarded).toString()
            } else {
                initAlertDialog()
            }
        }
        val onAutomaticVerificationObserver = Observer<Boolean> {
            viewModel.verifyCode("", requireActivity())
        }
        val onVerificationCompletedObserver = Observer<Boolean> {
            dialog.dismiss()
            val intent = Intent(activity, MainActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.alertDialogErrorMessage.observe(this, alertDialogErrorObserver)
        viewModel.codeSubmitted.observe(this, codeSubmittedObserver)
        viewModel.onAutomaticVerification.observe(this, onAutomaticVerificationObserver)
        viewModel.onVerificationCompleted.observe(this, onVerificationCompletedObserver)
    }

    private fun getEditTextLayout(context: Context): ConstraintLayout {
        val constraintLayout = ConstraintLayout(context)
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        constraintLayout.layoutParams = layoutParams
        constraintLayout.id = View.generateViewId()

        val textInputLayout = TextInputLayout(context)
        textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        layoutParams.setMargins(
            32.toDp(context),
            8.toDp(context),
            32.toDp(context),
            8.toDp(context)
        )
        textInputLayout.layoutParams = layoutParams
        textInputLayout.hint = RoutesAppApplication.resource?.getString(R.string.reg_val_code).toString()
        textInputLayout.id = View.generateViewId()
        textInputLayout.tag = "textInputLayout"

        val codeEditText = TextInputEditText(context)
        codeEditText.id = View.generateViewId()
        codeEditText.tag = "codeEditText"
        codeEditText.inputType = InputType.TYPE_CLASS_NUMBER

        textInputLayout.addView(codeEditText)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintLayout.addView(textInputLayout)
        return constraintLayout
    }
}
