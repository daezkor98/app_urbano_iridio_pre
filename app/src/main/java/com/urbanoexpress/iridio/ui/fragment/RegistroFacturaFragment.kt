package com.urbanoexpress.iridio.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.urbanoexpress.iridio.databinding.FragmentRegistroFacturaBinding

class RegistroFacturaFragment : Fragment() {

    lateinit var bind: FragmentRegistroFacturaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //TODO amount
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentRegistroFacturaBinding.inflate(inflater, container, false)
        return bind.root
    }
}