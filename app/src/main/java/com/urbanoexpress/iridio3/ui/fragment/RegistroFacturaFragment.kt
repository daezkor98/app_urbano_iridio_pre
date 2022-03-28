package com.urbanoexpress.iridio3.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.urbanoexpress.iridio3.R
import com.urbanoexpress.iridio3.databinding.FragmentRegistroFacturaBinding
import com.urbanoexpress.iridio3.model.dto.CERT_ESTADO
import com.urbanoexpress.iridio3.model.dto.Period
import com.urbanoexpress.iridio3.presenter.viewmodel.RegistrarFacturaViewModel
import com.urbanoexpress.iridio3.ui.BaseActivity
import com.urbanoexpress.iridio3.ui.dialogs.DATE_PICKER_MODE
import com.urbanoexpress.iridio3.ui.dialogs.DatePickerDailogFragment
import com.urbanoexpress.iridio3.ui.dialogs.MessageDialog
import com.urbanoexpress.iridio3.urbanocore.*
import com.urbanoexpress.iridio3.urbanocore.values.AK

class RegistroFacturaFragment : AppThemeBaseFragment() {

    lateinit var bind: FragmentRegistroFacturaBinding

    val facturaVM = RegistrarFacturaViewModel()

    var period: Period? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            period = it.getSerializable(AK.SELECTED_PERIOD) as Period?
        }

        observeViewModel()
    }

    private fun observeViewModel() {

        facturaVM.isLoadingLD.observe(this) { isLoading ->
            if (isLoading) {
                showProgressDialog()
            } else {
                dismissProgressDialog()
            }
        }

        facturaVM.uploadFacturaResultLD.observe(this) { wasSuccesful ->

            if (wasSuccesful) {
                val messageDialog = MessageDialog.newInstance("Factura registrada")
                messageDialog.completion = ::onFacturaPosted
                messageDialog.show(childFragmentManager, "messageD")
            } else {
                //TODO: move to string resource
                val messageDialog = MessageDialog
                    .newInstance("Lo sentimos, ocurrió un error al registrar su factura. Intentelo nuevamente")
                messageDialog.show(childFragmentManager, "messageD")
            }
        }
    }

    private fun onFacturaPosted() {
        findNavController().popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentRegistroFacturaBinding.inflate(inflater, container, false)

        when (period?.cert_estado) {
            CERT_ESTADO.EN_PROCESO.state_id -> {
                findNavController().popBackStack()
            }
            CERT_ESTADO.LIQUIDADO.state_id -> {
                prepareForEdition()
                if (this.period?.fac_id != "0") {
                    showPreviousFactData()
                }
            }
            CERT_ESTADO.APROBADO.state_id -> {
                showPreviousFactData()
                disableEdition()
            }
            CERT_ESTADO.FACTURADO.state_id -> {
                showPreviousFactData()
                disableEdition()
            }
        }

        return bind.root
    }

    private fun disableEdition() {
        bind.etNumeroFact.isEnabled = false
        bind.etFechaFactura.isEnabled = false
        bind.etMontoFact.isEnabled = false
        bind.btnAddFile.isEnabled = false
        bind.btnRegistrar.isEnabled = false

    }

    private fun showPreviousFactData() {
        bind.etNumeroFact.setText(period?.fac_numero)
        bind.etFechaFactura.setText(period?.fac_fecha)
        bind.etMontoFact.setText(period?.fac_total)
    }

    private fun dataValid(): Boolean {

        val numFact = bind.tfNumeroFactura.editText?.text!!
        val fechaFact = bind.tfFechaFactura.editText?.text!!
        val montoFact = bind.tfMontoFac.editText?.text!!

        if (numFact.isEmpty()) {
            bind.tfNumeroFactura.error = "Complete Número"
            return false
        } else {
            bind.tfNumeroFactura.error = null
        }

        if (fechaFact.isEmpty()) {
            bind.tfFechaFactura.error = "Complete fecha"
            return false
        } else {
            bind.tfFechaFactura.error = null
        }

        if (montoFact.isEmpty()) {
            bind.tfMontoFac.error = "Indique monto"
            return false
        } else {
            bind.tfMontoFac.error = null
        }

        if (pdfData == null) {
            hideKeyboard()
            showSnackBar("Adjunte archivo")
            return false
        }

        return true
    }

    var positionSelectedOptionEditPhoto = 0

    private fun prepareForEdition() {

        bind.tvFileName.text = fileName

        bind.etMontoFact.isEnabled = false
        bind.etMontoFact.setText(period?.monto.toString())

        bind.etFechaFactura.isEnabled = false
        bind.etFechaFactura.setText(getCurrentDay())
        bind.etFechaFactura.onExclusiveClick {

            val newFragment = DatePickerDailogFragment.newInstance(DATE_PICKER_MODE.CALENDAR)
            newFragment.dateListener =
                DatePickerDailogFragment.OnDatePickerDailogFragmentListener { view, year, month, dayOfMonth ->
                    bind.etFechaFactura.setText("$dayOfMonth/$month/$year")
                }
            newFragment.show(childFragmentManager, "datePicker")
        }

        bind.btnRegistrar.onExclusiveClick {

            if (dataValid()) {
                val numFact = bind.tfNumeroFactura.editText?.text.toString()
                val fechaFact = bind.tfFechaFactura.editText?.text.toString()
                val montoFact = bind.tfMontoFac.editText?.text.toString()
                facturaVM.postFactura(numFact, fechaFact, montoFact, period?.liquidacion!!, pdfData)
            }
        }
        bind.btnAddFile.onExclusiveClick {
//            showFilePickerDialog()
            openPDFPicker()
            //showImagePickerDialog()
        }

        bind.tvFileName.onExclusiveClick {
            pdfUri.ifSafe {
                findNavController().navigate(
                    R.id.action_registroFacturaFragment_to_pdfViewerFragment,
                    bundleOf("PDFURI" to pdfUri)
                )
            }.ifNull {
                showToast("Seleccione un documento")
            }
        }
    }

    /*****************************************************************************************/
    /**********************************************************************************************************************/
    /*Pick PDF */
    private fun openPDFPicker() {
        val intentPDF = Intent(Intent.ACTION_GET_CONTENT)
        intentPDF.type = "application/pdf"
        intentPDF.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE)
        startPdfPickerForResult.launch(Intent.createChooser(intentPDF, "Select Picture"))
    }

    var pdfData: ByteArray? = null

    private val startPdfPickerForResult = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == BaseActivity.RESULT_OK) {
            if (result.data != null && result.data!!.data != null) {
                handleFileURI(result.data!!.data!!)
            } else {
                showToast("Lo sentimos, el archivo no fue seleccionada correctamente.")
            }
        }
    }

    var pdfUri: Uri? = null
    var fileName: String = ""

    private fun handleFileURI(pdfUri: Uri) = secureFunc() {
        this.pdfUri = pdfUri
        pdfData = requireContext().readFileBytes(pdfUri)
        fileName = requireContext().getFileName(pdfUri) ?: "Archivo adjunto"
        bind.tvFileName.apply {
            text = fileName
            paint?.isUnderlineText = true
        }
    }
}