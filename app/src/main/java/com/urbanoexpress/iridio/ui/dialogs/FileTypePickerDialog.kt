package com.urbanoexpress.iridio.ui.dialogs

import android.app.Activity
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
import com.urbanoexpress.iridio.databinding.ModalFileTypePickerBinding
import com.urbanoexpress.iridio.urbanocore.DataCompletion
import com.urbanoexpress.iridio.urbanocore.onExclusiveClick
import com.urbanoexpress.iridio.urbanocore.values.AK
import com.urbanoexpress.iridio.util.CameraUtils
import com.urbanoexpress.iridio.util.CommonUtils
import com.urbanoexpress.iridio.util.FileUtils
import com.urbanoexpress.iridio.util.FileUtils.readBitmapToByteArray
import com.urbanoexpress.iridio.util.ImageRotator
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * This Dialog shows a Radiobutton group to select a method to
 * retrieve a file and returns its bytearray
 * TODO:Return file name
 */
class FileTypePickerDialog : BaseDialogFragment() {

    lateinit var bind: ModalFileTypePickerBinding

    var completion: DataCompletion? = null

    private var pdfVisibility = View.VISIBLE
    private var pickImgVisibility = View.VISIBLE
    private var takePhotoVisibility = View.VISIBLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            this.pdfVisibility = it.getInt(AK.PDF_VISIBILITY)
            this.pickImgVisibility = it.getInt(AK.PICK_IMG_VISIBILITY)
            this.takePhotoVisibility = it.getInt(AK.TAKE_PHOTO_VISIBILITY)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = ModalFileTypePickerBinding.inflate(inflater, container, false)

        bind.rbPdf.visibility = pdfVisibility
        bind.rbPickImg.visibility = pickImgVisibility
        bind.rbTakePhoto.visibility = takePhotoVisibility

        bind.btnContinue.onExclusiveClick { continueGettingFile() }
        bind.btnCancel.onExclusiveClick { dismiss() }

        return bind.root
    }

    private fun continueGettingFile() {

        when (bind.rbGroup.checkedRadioButtonId) {
            bind.rbPickImg.id -> {
                openGallery()
            }
            bind.rbTakePhoto.id -> {
                onTakePhotoClick()
            }
            bind.rbPdf.id -> {}
        }
    }

    /*File processing***********************************************************/
    /*Take Photo     ***********************************************************/

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

    private val startImageCaptureForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            onPhotoCaptureResultOK()
        }
    }

    fun onPhotoCaptureResultOK() {
//        prepareDataImageFile(photoCapture!!, TakedPhotoObserver(photoCapture!!.name))
        prepareDataImageFile(photoCapture!!, photoCapture!!.name)
    }

    lateinit var resultBytes: ByteArray

    private fun prepareDataImageFile(
        file: File,
        name: String//observer: RegistroFacturaFragment.TakedPhotoObserver<ByteArray?>
    ) {
//        val dataBytes: ByteArray = readFileToByteArray(file)
        resultBytes = readFileToByteArray(file)
        //TODO
        Log.i("TAG", "prepareDataImageFile  $name: " + resultBytes?.size)

        completion?.invoke(resultBytes, name)
        dismiss()
    }

    private fun readFileToByteArray(file: File): ByteArray {
        var bitmap = BitmapFactory
            .decodeFile(file.absolutePath)
        bitmap = ImageRotator.rotateImageIfRequired(bitmap, file.absolutePath)
        return readBitmapToByteArray(bitmap, 20)
    }

    /*Pick Image****************************************************************/
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
//        prepareDataImageBitmap(bitmap, TakedPhotoObserver<ByteArray?>(imageName))
        prepareDataImageBitmap(bitmap, imageName)
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

    private fun prepareDataImageBitmap(
        bitmap: Bitmap,
        name: String
    ) {
//        val bytesData = readBitmapToByteArray(bitmap, 20)//TODO asing data to instance
        resultBytes = readBitmapToByteArray(bitmap, 20)//TODO asing data to instance
        Log.i("TAG", "prepareDataImageFile  $name: " + resultBytes?.size)
        completion?.invoke(resultBytes, name)
        dismiss()
    }

    /*Builders*****************************************************************/

    /**
     * Use this Builder to config the FilePickerDialog.
     * @return A new instance of fragment FileTypePickerDialog.
     */
    class Builder {

        private var pdfconfig = View.VISIBLE
        private var pickImgconfig = View.VISIBLE
        private var takePhotoconfig = View.VISIBLE

        /**Visible by default
         * */
        fun pdfRbVisibility(visibility: Int): Builder {
            pdfconfig = visibility
            return this
        }

        /**Visible by default
         * */
        fun pickImgRbVisibility(visibility: Int): Builder {
            pickImgconfig = visibility
            return this
        }

        /**Visible by default
         * */
        fun takePhotoRbVisibility(visibility: Int): Builder {
            takePhotoconfig = visibility
            return this
        }

        fun build(): FileTypePickerDialog {
            val args = bundleOf(
                AK.PDF_VISIBILITY to pdfconfig,
                AK.PICK_IMG_VISIBILITY to pickImgconfig,
                AK.TAKE_PHOTO_VISIBILITY to takePhotoconfig
            )
            return FileTypePickerDialog().apply { arguments = args }
        }
    }

    companion object {

        /**
         * @return A new instance FileTypePickerDialog with all options enabled
         * */
        @JvmStatic
        fun defaultInstance() = FileTypePickerDialog().apply {
            arguments = bundleOf(
                AK.PDF_VISIBILITY to View.VISIBLE,
                AK.PICK_IMG_VISIBILITY to View.VISIBLE,
                AK.TAKE_PHOTO_VISIBILITY to View.VISIBLE
            )
        }
    }
}

