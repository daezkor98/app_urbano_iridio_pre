package com.urbanoexpress.iridio.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.urbanoexpress.iridio.databinding.ModalFileTypePickerBinding

/**
 * This Dialog shows a Radiobutton group to select a method to
 * retrieve a file and returns its bytearray
 */
//TODO
class FileTypePickerDialog : BaseDialogFragment() {

    lateinit var bind: ModalFileTypePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
/*            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.modal_file_type_picker, container, false)
        bind = ModalFileTypePickerBinding.inflate(inflater, container, false)
        return bind.root
    }

    companion object {
        /**
         * Use this factory method to config  a new instance of
         * this fragment using the provided parameters.
         * @param param1 Parameter 1.
         * @return A new instance of fragment FileTypePickerDialog.
         */
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FileTypePickerDialog().apply {
                arguments = Bundle().apply {
/*                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }
}