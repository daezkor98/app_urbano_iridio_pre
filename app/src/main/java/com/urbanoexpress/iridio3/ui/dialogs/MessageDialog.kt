package com.urbanoexpress.iridio3.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.urbanoexpress.iridio3.databinding.ModalMessageBinding
import com.urbanoexpress.iridio3.urbanocore.SimpleEvent
import com.urbanoexpress.iridio3.urbanocore.extentions.onExclusiveClick
import com.urbanoexpress.iridio3.urbanocore.values.AK

/**
 * A simple [DialogFragment] that shows a message
 * and notifies with lambda completion
 */
class MessageDialog : BaseDialogFragment() {

    lateinit var bind: ModalMessageBinding

    private var message: String? = null

    var completion: SimpleEvent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            message = it.getString(AK.MESSAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = ModalMessageBinding.inflate(inflater, container, false)

        bind.tvMessage.text = message

        bind.btnDonde.onExclusiveClick {
            completion?.invoke()
            dismiss()
        }

        return bind.root
    }

    companion object {
        /**
         * Factory method
         * @param message : message to display.
         * @return A new instance of fragment MessageDialog.
         */
        @JvmStatic
        fun newInstance(message: String): MessageDialog = MessageDialog().apply {
            arguments = bundleOf(AK.MESSAGE to message)
        }
    }
}