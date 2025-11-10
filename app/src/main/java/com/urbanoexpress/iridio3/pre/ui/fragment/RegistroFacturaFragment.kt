package com.urbanoexpress.iridio3.pre.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.urbanoexpress.iridio3.pre.databinding.FragmentRegistroFacturaBinding
import com.urbanoexpress.iridio3.pre.model.dto.CERT_ESTADO
import com.urbanoexpress.iridio3.pre.model.dto.Period
import com.urbanoexpress.iridio3.pre.presenter.viewmodel.RegistrarFacturaViewModel
import com.urbanoexpress.iridio3.pre.ui.BaseActivity
import com.urbanoexpress.iridio3.pre.ui.dialogs.DATE_PICKER_MODE
import com.urbanoexpress.iridio3.pre.ui.dialogs.DatePickerDailogFragment
import com.urbanoexpress.iridio3.pre.ui.dialogs.MessageDialog
import com.urbanoexpress.iridio3.pre.ui.helpers.ModalHelper
import com.urbanoexpress.iridio3.pre.ui.widget.enableClickMode
import com.urbanoexpress.iridio3.urbanocore.extentions.*
import com.urbanoexpress.iridio3.urbanocore.values.AK
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistroFacturaFragment : AppThemeBaseFragment() {

    lateinit var bind: FragmentRegistroFacturaBinding

    val facturaVM: RegistrarFacturaViewModel by viewModels()

    var period: Period? = null

    val NO_EXISTE = "0"

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
            CERT_ESTADO.EN_PROCESO.stateId -> {
                findNavController().popBackStack()
            }

            CERT_ESTADO.LIQUIDADO.stateId -> {
                disableEdition()
                ModalHelper
                    .BottomPopup
                    .Builder(
                        bind.root, "Su pago se encuentra en proceso de aprobación"
                    ).build().show()
            }

            CERT_ESTADO.APROBADO.stateId -> {
                prepareForEdition()
                if (this.period?.fac_id != NO_EXISTE) {
                    showPreviousFactData()
                }

                ModalHelper
                    .BottomPopup
                    .Builder(
                        bind.root, "Adjunte su Recibo por Honorarios"
                    ).build().show()
            }

            CERT_ESTADO.FACTURADO.stateId -> {
                showPreviousFactData()
                disableEdition()
                ModalHelper.BottomPopup.Builder(
                    bind.root, "Su pago está procesandose"
                ).build().show()
            }

            CERT_ESTADO.PAGADO.stateId -> {
                showPreviousFactData()
                disableEdition()
                ModalHelper.BottomPopup.Builder(
                    bind.root, "El pago ha sido realizado"
                ).build().show()
            }
        }

        return bind.root
    }

    private fun disableEdition() {
        bind.etNumeroFact.disable()
        bind.fieldFechaFactura.disable()
        bind.etMontoFact.disable()
        bind.btnAddFile.disable()
        bind.btnRegistrar.disable()
    }

    private fun showPreviousFactData() {
        bind.etNumeroFact.setText(period?.fac_numero)
        bind.fieldFechaFactura.setText(period?.fac_fecha!!)
        bind.etMontoFact.setText(period?.fac_total)
    }

    private fun dataValid(): Boolean {

        val numFact = bind.tfNumeroFactura.editText?.text!!
        val fechaFact = bind.fieldFechaFactura.text()
        val montoFact = bind.tfMontoFac.editText?.text!!

        if (numFact.isEmpty()) {
            bind.tfNumeroFactura.error = "Complete Número"
            return false
        } else {
            bind.tfNumeroFactura.error = null
        }

        if (fechaFact.isEmpty()) {
            bind.fieldFechaFactura.inputError("Complete fecha")
            return false
        } else {
            bind.fieldFechaFactura.inputError(null)
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

    private fun prepareForEdition() {

        bind.tvFileName.text = fileName

        bind.etMontoFact.setText(period?.monto.toString())

        bind.fieldFechaFactura.enableClickMode(withText = getCurrentDay()) {
            val newFragment = DatePickerDailogFragment.newInstance(DATE_PICKER_MODE.CALENDAR)
            newFragment.dateListener =
                DatePickerDailogFragment.OnDatePickerDailogFragmentListener { view, year, month, dayOfMonth ->
                    bind.fieldFechaFactura.setText("$dayOfMonth/$month/$year")
                }
            newFragment.show(childFragmentManager, "datePicker")
        }

        bind.btnRegistrar.onExclusiveClick {

            if (dataValid()) {
                val numFact = bind.tfNumeroFactura.editText?.text.toString()
                val fechaFact = bind.fieldFechaFactura.text()
                val montoFact = bind.tfMontoFac.editText?.text.toString()
                facturaVM.postFactura(numFact, fechaFact, montoFact, period?.liquidacion!!, pdfData)
            }
        }
        bind.btnAddFile.onExclusiveClick {
            openPDFPicker()
        }

        bind.tvFileName.onExclusiveClick {
            showPDfInScreen()
        }
    }

    /**************************************/
    /*********** Pick PDF *****************/
    /**************************************/
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


    private fun showPDfInScreen() {
        pdfUri?.let {
            val viewPDFIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(it, "application/pdf")
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                startActivity(viewPDFIntent)
            } catch (e: ActivityNotFoundException) {
                showToast("Necesitas instalar una app para la lectura de PDF")
            }
        }
    }

}