package com.example.myfirstapp

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.QRCodeWriter

class MainActivity : AppCompatActivity() {
    private lateinit var bindingQRCode: ActivityMainBinding
    private lateinit var ivQRCode: ImageView
    private lateinit var etData : EditText
    private lateinit var enterbt : Button
    private lateinit var toggleBtn : ToggleButton
    private lateinit var bmp: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bindingQRCode = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingQRCode.root)

        ivQRCode = bindingQRCode.ivQRCode
        etData = bindingQRCode.etData
        enterbt = bindingQRCode.enterbt

        bindingQRCode.downloadBtn.setOnClickListener {
            if (ivQRCode.drawable != null){
                saveImg(this)
            }
        }

        enterbt.setOnClickListener {
            toggleBtn = bindingQRCode.toggleBtn
            ivQRCode.visibility = View.VISIBLE
            val data = etData.text.toString().trim()

            if (toggleBtn.isChecked){
                qrcode(data)
            }else{
                barcode(data)
            }
        }
    }

    fun qrcode(data: String){
        if (data.isEmpty()){
            Toast.makeText( this, "Digite algo", Toast.LENGTH_SHORT).show()
        }else{
            val writer = QRCodeWriter()
            try {
                val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE , 500, 500)
                val width = bitMatrix.width
                val height = bitMatrix.height
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width){
                    for (y in 0 until height){
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                ivQRCode.setImageBitmap(bmp)
            }catch (e: WriterException){
                e.printStackTrace()
            }
        }
    }

    fun barcode(data: String){
        if (data.isEmpty()){
            Toast.makeText( this, "Digite algo", Toast.LENGTH_SHORT).show()
        }else{
            val writer = Code128Writer()
            try {
                val bitMatrix = writer.encode(data, BarcodeFormat.CODE_128 , 500, 500)
                val width = bitMatrix.width
                val height = bitMatrix.height
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width){
                    for (y in 0 until height){
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                ivQRCode.setImageBitmap(bmp)
            }catch (e: WriterException){
                e.printStackTrace()
            }
        }
    }
    fun saveImg(context: Context){
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply{
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LocalPDV")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val contentResolver = context.contentResolver
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)

            snackbarMsg("Salvo com sucesso!", 1)
        } ?: snackbarMsg("Erro ao salvar imagem!", 2)
    }

    fun snackbarMsg(message: String, warn: Int){

        when(warn){
            1 -> {
                val snackbarError = Snackbar.make(bindingQRCode.root, message, Snackbar.LENGTH_SHORT).setAction("Action", null)
                snackbarError.setTextColor(Color.BLACK)
                snackbarError.setBackgroundTint(Color.GREEN).show()
            }
            2 -> {
                val snackbarError = Snackbar.make(bindingQRCode.root, message, Snackbar.LENGTH_SHORT).setAction("Action", null)
                snackbarError.setTextColor(Color.WHITE)
                snackbarError.setBackgroundTint(Color.RED).show()
            }
            else -> {
                val snackbarError = Snackbar.make(bindingQRCode.root, message, Snackbar.LENGTH_SHORT).setAction("Action", null)
                snackbarError.setActionTextColor(Color.BLACK)
                snackbarError.setBackgroundTint(Color.YELLOW).show()
            }
        }
    }
}