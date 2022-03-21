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
import androidx.fragment.app.Fragment
import com.urbanoexpress.iridio.R
import com.urbanoexpress.iridio.databinding.FragmentRegistroFacturaBinding
import com.urbanoexpress.iridio.model.RegistrarFacturaViewModel
import com.urbanoexpress.iridio.ui.helpers.ModalHelper
import com.urbanoexpress.iridio.urbanocore.onExclusiveClick
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

class RegistroFacturaFragment : Fragment() {

    lateinit var bind: FragmentRegistroFacturaBinding

    val facturaVM = RegistrarFacturaViewModel()

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
        configUI()
        return bind.root
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

/*        TODO validate the file
if (uriPath == null) {
            hideKeyboard()
            showSnackBar("Adjunte archivo")
            return false
        }*/

        return true
    }

    var positionSelectedOptionEditPhoto = 0

    private fun configUI() {

        bind.btnEnviar.onExclusiveClick {

            if (dataValid()) {
                val numFact = bind.tfNumeroFactura.editText?.text.toString()
                val fechaFact = bind.tfFechaFactura.editText?.text.toString()
                val montoFact = bind.tfMontoFac.editText?.text.toString()
                facturaVM.postFactura(numFact, fechaFact, montoFact, imageBytes)
            }

        }

        bind.btnAddFile.onExclusiveClick {
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

    }

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
//                            bitmap = ImageRotator.rotateImageIfRequired(this, bitmap, imageUri);
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

    open fun onTakePhotoClick() {
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