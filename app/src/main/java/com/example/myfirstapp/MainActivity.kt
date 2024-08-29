package com.example.myfirstapp

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.encoder.QRCode
import java.io.Writer

class MainActivity : AppCompatActivity() {

    private lateinit var ivQRCode: ImageView
    private lateinit var etData : EditText
    private lateinit var enterbt : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ivQRCode = findViewById(R.id.ivQRCode)
        etData = findViewById(R.id.etData)
        enterbt = findViewById(R.id.enterbt)

        enterbt.setOnClickListener {
            val data = etData.text.toString().trim()

            if (data.isEmpty()){
                Toast.makeText( this, "Digite algo", Toast.LENGTH_SHORT).show()
            }else{
                val writer = QRCodeWriter()
                try {

                    val bitMatrix = writer .encode(data, BarcodeFormat.QR_CODE, 512, 512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    for (x in 0 until width){
                        for (y in 0 until height){
                            bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    ivQRCode.setImageBitmap(bmp)
                }catch (e:WriterException){
                    e.printStackTrace()
                }
            }

        }
    }
}