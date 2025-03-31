package com.urbanoexpress.iridio3.pe.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.Result
import com.urbanoexpress.iridio3.pe.R
import com.urbanoexpress.iridio3.pe.databinding.FragmentLoginQrBinding
import me.dm7.barcodescanner.zxing.ZXingScannerView

class LoginQrFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    private var binding: FragmentLoginQrBinding? = null
    private var scannerView: ZXingScannerView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginQrBinding.inflate(inflater, container, false)
        scannerView = binding?.barcodeScannerView
        scannerView?.setResultHandler(this)
        return binding?.root
    }


    override fun handleResult(rawResult: Result?) {
        rawResult?.let {
            val text = it.text
            goToPathCodeVerification(text)
        }
    }

    override fun onResume() {
        super.onResume()
        scannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView?.stopCamera()
    }

    override fun getViewContext(): Context? {
        return requireActivity().baseContext
    }


    private fun goToPathCodeVerification(pathCode: String) {
        this.dismissProgressDialog()
        val targetFragment = DriverVerificationCodeFragment()
        val bundle = Bundle()
        bundle.putString("pathCode", pathCode)
        targetFragment.arguments = bundle
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, targetFragment)
        transaction.commit()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}