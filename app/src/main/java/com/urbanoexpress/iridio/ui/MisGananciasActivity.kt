package com.urbanoexpress.iridio.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.urbanoexpress.iridio.databinding.ActivityMisGananciasBinding
import java.io.FileNotFoundException
import java.io.IOException

//Merge
//git branch -m old-name old-name.merged
class MisGananciasActivity : BaseActivity2() {

    lateinit var bind: ActivityMisGananciasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMisGananciasBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setupToolbar(bind.toolbar)//TODO checkColor
    }

/*    private fun openPDFPicker() {
        val intentPDF = Intent(Intent.ACTION_GET_CONTENT)
        intentPDF.type = "application/pdf"
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE)
        startPickImageForResult.launch(Intent.createChooser(intentPDF, "Select Picture"))
    }

    private val startPickImageForResult = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null && result.data!!.data != null) {
                try {
                    val imageUri = result.data!!.data
                    Log.i("TAG", "MisGananciasActivity : " + (imageUri != null))//true
                } catch (ex: FileNotFoundException) {
                    ex.printStackTrace()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            } else {
//                showToast("Lo sentimos, la imagen no fue seleccionada correctamente.")
            }
        }
    }*/
}