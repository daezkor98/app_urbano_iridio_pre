package com.urbanoexpress.iridio3.pe.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.urbanoexpress.iridio3.pe.R
import com.urbanoexpress.iridio3.pe.databinding.FragmentCodeVerificationPathBinding
import com.urbanoexpress.iridio3.pe.model.interactor.DriverVerCodeInteractor
import com.urbanoexpress.iridio3.pe.presenter.DriverContract
import com.urbanoexpress.iridio3.pe.presenter.DriverVerCodePresenter
import com.urbanoexpress.iridio3.pe.ui.MainActivity
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper
import com.urbanoexpress.iridio3.pe.util.Preferences
import com.urbanoexpress.iridio3.pe.util.constant.DriverVerificationConstants.EMPTY_VALUE
import com.urbanoexpress.iridio3.pe.util.constant.DriverVerificationConstants.LOADING_LOGIN
import com.urbanoexpress.iridio3.pe.util.constant.DriverVerificationConstants.PREFERENCES_PHONE
import com.urbanoexpress.iridio3.pe.util.constant.DriverVerificationConstants.PREFERENCES_USER_PROFILE


private const val ARG_PARAM1 = "pathCode"

class DriverVerificationCodeFragment : BaseFragment(), DriverContract.DriverVerificationView {

    private var binding: FragmentCodeVerificationPathBinding? = null
    private lateinit var loginQrPresenter: DriverVerCodePresenter
    private var param1: String? = null
    private lateinit var editTexts: List<EditText?>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCodeVerificationPathBinding.inflate(inflater, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        editTexts = listOf(
            binding?.editTextCode1,
            binding?.editTextCode2,
            binding?.editTextCode3,
            binding?.editTextCode4,
            binding?.editTextCode5,
            binding?.editTextCode6
        )
        editTexts.forEachIndexed { index, it -> it?.addTextChangedListener(createTextWatcher(index)) }

        setUpPresenter()

        setUpVerificationButton()

        return binding?.root

    }

    private fun setUpPresenter() {
        Preferences.getInstance().init(requireContext(), PREFERENCES_USER_PROFILE)
        val driverVerCodeInteractor = DriverVerCodeInteractor(requireContext())
        loginQrPresenter = DriverVerCodePresenter(view = this, interactor = driverVerCodeInteractor)
    }

    private fun setUpVerificationButton() {
        binding?.button?.isEnabled = false
        binding?.button?.setOnClickListener {
            this.hideKeyboard()
            val phone = getDriverPhone()
            Preferences.getInstance().init(requireContext(), "UserProfile")
            param1?.let { idRutaQR ->
                loginQrPresenter.loginDriverUser (
                    idRutaQR = idRutaQR,
                    verCodeQR = getVerificationCode(),
                    driverPhone = phone
                )
            }
        }
    }


    private fun createTextWatcher(currentIndex: Int) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                if (s.trim().isNotEmpty() && currentIndex < editTexts.size - 1) {
                    editTexts[currentIndex + 1]?.requestFocus()
                }
                binding?.button?.isEnabled = areAllEditTextsFilled()
            }
        }

    }

    private fun areAllEditTextsFilled(): Boolean {
        return editTexts.all { it?.text?.trim()?.isNotEmpty() ?: false }
    }

    override fun getViewContext(): Context? {
        return requireActivity().baseContext
    }


    private fun getDriverPhone(): String {
        Preferences.getInstance().init(requireContext(), "GlobalConfigApp")
        return Preferences.getInstance().getString(PREFERENCES_PHONE, EMPTY_VALUE) ?: EMPTY_VALUE
    }

    private fun getVerificationCode(): String {
        return editTexts.joinToString(EMPTY_VALUE) { it?.text.toString() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        loginQrPresenter.detachView()

    }

    override fun showLoginProgressDialog() {
        this.showProgressDialog(LOADING_LOGIN)
    }

    override fun dismissLoginProgressDialog() {
        this.dismissProgressDialog()
    }

    override fun fromLoginToMainMenu() {
        dismissProgressDialog()
        requireContext().startActivity(
            Intent(requireContext(), MainActivity::class.java)
        )
    }

    override fun showError(error: String) {
        ModalHelper.getBuilderAlertDialog(requireContext())
            .setTitle(R.string.ver_code_title_authentication_error)
            .setMessage(error)
            .setPositiveButton(R.string.ver_code_phone_accept, null)
            .show()
    }
}