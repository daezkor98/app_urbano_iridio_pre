package com.urbanoexpress.iridio.ui.fragment

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.urbanoexpress.iridio.R
import com.urbanoexpress.iridio.databinding.FragmentRegistroFacturaBinding
import com.urbanoexpress.iridio.model.RegistrarFacturaViewModel
import com.urbanoexpress.iridio.model.dto.CERT_ESTADO
import com.urbanoexpress.iridio.model.dto.Period
import com.urbanoexpress.iridio.ui.BaseActivity
import com.urbanoexpress.iridio.ui.dialogs.DatePickerDailogFragment
import com.urbanoexpress.iridio.ui.dialogs.FileTypePickerDialog
import com.urbanoexpress.iridio.ui.dialogs.MessageDialog
import com.urbanoexpress.iridio.ui.helpers.ModalHelper
import com.urbanoexpress.iridio.urbanocore.*
import com.urbanoexpress.iridio.urbanocore.values.AK
import com.urbanoexpress.iridio.util.CameraUtils
import com.urbanoexpress.iridio.util.CommonUtils
import com.urbanoexpress.iridio.util.FileUtils
import com.urbanoexpress.iridio.util.ImageRotator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
*TODO
* Licencia
* SOAT
* Foto de perfil - No
* */
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
        bind.btnEnviar.isEnabled = false

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

            val newFragment = DatePickerDailogFragment.newInstance()
            newFragment.dateListener =
                DatePickerDailogFragment.OnDatePickerDailogFragmentListener { view, year, month, dayOfMonth ->
                    bind.etFechaFactura.setText("$dayOfMonth/$month/$year")
                }
            newFragment.show(childFragmentManager, "datePicker")
        }

        bind.btnEnviar.onExclusiveClick {

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
    /*****************************************************************************************/
    /*****************************************************************************************/
    /*****************************************************************************************/
    private fun showFilePickerDialog() {
        val diagg = FileTypePickerDialog.defaultInstance()
        diagg.show(childFragmentManager, "32135")
    }

    private fun showImagePickerDialog() {

        positionSelectedOptionEditPhoto = 0
        val options = arrayOf("Tomar foto", "Seleccionar foto")
        ModalHelper.getBuilderAlertDialog(requireContext())
            .setTitle("Adjuntar imagen")
            .setSingleChoiceItems(
                options, 0
            ) { dialog: DialogInterface?, which: Int ->
                positionSelectedOptionEditPhoto = which
            }
            .setPositiveButton(R.string.text_continuar) { dialog, which ->
                if (positionSelectedOptionEditPhoto == 0) {
                    onTakePhotoClick()
                } else if (positionSelectedOptionEditPhoto == 1) {
                    openGallery()
                }
            }
            .setNegativeButton(R.string.text_cancelar, null)
            .show()

    }

    /**********************************************************************************************************************/
    /*Pick PDF */
    private fun openPDFPicker() {
        val intentPDF = Intent(Intent.ACTION_GET_CONTENT)
//        val intentPDF = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intentPDF.type = "application/pdf"

        intentPDF.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intentPDF.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        // intent.type = "image/*"
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

    //añadir dias
    /***********************************************************************************************************************/
    /*Select Images Funcs*/
    /**
     * Pick from gallery
     * */
    private fun openGallery() {
        val intent: Intent = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            Intent(
                Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        } else {
            Intent(
                Intent.ACTION_OPEN_DOCUMENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startPickImageForResult.launch(intent)
    }


    private val startPickImageForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null && result.data!!.data != null) {
                try {
                    val imageUri = result.data!!.data
                    val bitmap: Bitmap = getBitmapFromSelectedImage(imageUri!!)!!
                    //TODO ImageRotator is failling
//                     bitmap = ImageRotator.rotateImageIfRequired(this, bitmap, imageUri);
                    //presenter.
                    onPickImageResultOK(bitmap)
                } catch (ex: FileNotFoundException) {
                    ex.printStackTrace()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            } else {
                //   showToast("Lo sentimos, la imagen no fue seleccionada correctamente.")
            }
        }
    }

    @Throws(FileNotFoundException::class, IOException::class)
    private fun getBitmapFromSelectedImage(imageUri: Uri): Bitmap? {
        return if (CommonUtils.isAndroid10()) {
            val source = ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
        }
    }

    fun onPickImageResultOK(bitmap: Bitmap) {
        val imageName: String = generateImageName("")!!
        //TakedPhotoObserver<ByteArray?
        prepareDataImageBitmap(bitmap, TakedPhotoObserver<ByteArray?>(imageName))
    }

    private fun prepareDataImageBitmap(
        bitmap: Bitmap,
        observer: DisposableSingleObserver<ByteArray?>
    ) {
        subscribeToPrepareDataImage(readBitmapToByteArray(bitmap), observer)
    }

    private fun readBitmapToByteArray(bitmap: Bitmap): Single<ByteArray?>? {
        return Single.create { emitter: SingleEmitter<ByteArray?> ->
            val byteArray =
                FileUtils.readBitmapToByteArray(bitmap, 20)
            if (byteArray != null) {
                emitter.onSuccess(byteArray)
            } else {
                emitter.onError(IOException("Lo sentimos, ocurrió un error al leer el archivo de la foto."))
            }
        }
    }

    /**
     *  Post TakedImage
     *  */
    private var photoCapture: File? = null
    private val photoDirPath = ""


    fun onTakePhotoClick() {
        photoCapture = FileUtils.generateFile(
            requireContext(), generateImageName(""), photoDirPath
        )
        openCamera(photoCapture)
    }

    fun openCamera(photoFile: File?) {
        val intent = CameraUtils.getIntentImageCapture(requireContext(), photoFile)
        startImageCaptureForResult.launch(intent)
    }

    private fun generateImageName(prefix: String): String? {
        val timeStamp =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        var photoName = "Imagen_$timeStamp.jpg"
        if (prefix.isNotEmpty()) {
            photoName = prefix + "_" + timeStamp + ".jpg"
        }
        return photoName
    }

    private val startImageCaptureForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            onPhotoCaptureResultOK()
        }
    }

    fun onPhotoCaptureResultOK() {
        prepareDataImageFile(photoCapture!!, TakedPhotoObserver(photoCapture!!.name))
    }

    private fun prepareDataImageFile(file: File, observer: TakedPhotoObserver<ByteArray?>) {
        subscribeToPrepareDataImage(readFileToByteArray(file), observer)
    }

    private fun readFileToByteArray(file: File): Single<ByteArray?>? {
        return Single.create { emitter: SingleEmitter<ByteArray?> ->
            var bitmap =
                BitmapFactory.decodeFile(file.absolutePath)
            bitmap = ImageRotator.rotateImageIfRequired(bitmap, file.absolutePath)
            val byteArray =
                FileUtils.readBitmapToByteArray(bitmap, 20)
            if (byteArray != null) {
                emitter.onSuccess(byteArray)
            } else {
                emitter.onError(IOException("Lo sentimos, ocurrió un error al leer el archivo de la foto."))
            }
        }
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private fun subscribeToPrepareDataImage(
        single: Single<ByteArray?>?,
        observer: DisposableSingleObserver<ByteArray?>
    ) {
        val observable = single
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
        compositeDisposable.add(observable?.subscribeWith(observer))
    }

    var imageBytes: ByteArray? = null

    private inner class TakedPhotoObserver<T> constructor(private val imageName: String) :
        DisposableSingleObserver<ByteArray?>() {

        override fun onSuccess(bytes: @NonNull ByteArray?) {

            Log.i("TAG", "onSuccess: " + bytes?.size)
            imageBytes = bytes
//            requestUploadPhotoUserProfile(imageName, bytes)
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
            Log.e("TAG", "onError: ", e)
        }
    }
}