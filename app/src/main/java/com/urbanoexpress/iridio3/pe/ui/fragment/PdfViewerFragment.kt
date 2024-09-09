package com.urbanoexpress.iridio3.pe.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.urbanoexpress.iridio3.databinding.FragmentPdfViewerBinding

class PdfViewerFragment : Fragment() {

    lateinit var bind: FragmentPdfViewerBinding

    var pdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pdfUri = it.getParcelable("PDFURI")
/*            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bind = FragmentPdfViewerBinding.inflate(inflater, container, false)
        bind.pdfView.fromUri(pdfUri).load()

        return bind.root
    }
}