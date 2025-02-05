package com.urbanoexpress.iridio3.pe.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.zxing.Result
import com.urbanoexpress.iridio3.pe.databinding.FragmentLoginQrBinding
import com.urbanoexpress.iridio3.pe.presenter.LoginQrPresenter
import com.urbanoexpress.iridio3.pe.presenter.LoginQrView
import com.urbanoexpress.iridio3.pe.ui.RutaActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView

class LoginQrFragment : Fragment(), ZXingScannerView.ResultHandler, LoginQrView {

    private lateinit var binding: FragmentLoginQrBinding
    private lateinit var scannerView: ZXingScannerView
    private lateinit var loginQrPresenter: LoginQrPresenter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginQrBinding.inflate(inflater,container,false)
        scannerView = binding.barcodeScannerView
        scannerView.setResultHandler(this)
        loginQrPresenter = LoginQrPresenter(this)
        return binding.root
    }


    override fun handleResult(rawResult: Result?) {
        rawResult?.let {
            val text = it.text
            loginQrPresenter.logIn("ADMIN","12345678")
            Log.d("Hola","este es el texto: "+text);
            val intent = Intent(requireContext(), RutaActivity::class.java)
            intent.putExtra("isfromLoginQr",true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun getViewContext(): Context? {
        return requireActivity().baseContext
    }
}