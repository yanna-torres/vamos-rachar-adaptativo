package com.example.vamos_rachar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.floor

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, TextWatcher {
    private lateinit var tts : TextToSpeech
    private var resultValue : Double = 0.00
    private val df = DecimalFormat("0.00")
    private lateinit var priceInput : EditText
    private lateinit var peopleInput : EditText
    private lateinit var resultTxt : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing components variables
        priceInput = findViewById<EditText>(R.id.totalTxt)
        peopleInput = findViewById<EditText>(R.id.peopleTxt)
        resultTxt = findViewById<TextView>(R.id.resultTxt)
        val shareBtn = findViewById<Button>(R.id.shareBtn)
        val talkBtn = findViewById<Button>(R.id.playBtn)

        // Initializing the tts
        tts = TextToSpeech(this, this)

        // Adding TextWatchers
        priceInput.addTextChangedListener(this)
        peopleInput.addTextChangedListener(this)

        // Call TTS for value result
        talkBtn.setOnClickListener(View.OnClickListener {
            speakResult()
        })

        // Sharing result
        shareBtn.setOnClickListener {
            val textResult = resultTxt.text.toString()
            val textToShare = "Vamos Rachar? \n Sua parte da conta: $textResult"
            if (textToShare.isNotEmpty()) {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, textToShare)
                    type = "text/plain"
                }

                val shareTitle = "VamosRachar"
                val shareIntent = Intent.createChooser(sendIntent, shareTitle)
                startActivity(shareIntent)
            }
        }
    }
    private fun speakResult(){
        val integerPart = floor(resultValue).toInt()
        val decimalPart = ((resultValue - integerPart) * 100).toInt()

        val integerPartSpoken = when {
            resultValue == 0.0 -> "0 reais"
            integerPart == 1 -> "1 real"
            integerPart > 1 -> "$integerPart reais"
            else -> ""
        }
        val decimalPartSpoken = if (decimalPart > 0) "e $decimalPart centavos" else ""

        val spokenResult = "$integerPartSpoken $decimalPartSpoken"
        tts.speak(spokenResult, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
            Log.d("PDM23","TTS engine initialized successfully")
        } else {
            Log.e("PDM23", "Failed to initialize TTS engine.")
        }
    }
    override fun onDestroy() {
        // Release TTS engine resources
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Log.d("PDM23","Antes de mudar")
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Log.d("PDM23","Mudando")
    }

    override fun afterTextChanged(p0: Editable?) {
        updateResult()
    }

    private fun updateResult() {
        if (priceInput.text.isNotEmpty() and peopleInput.text.isNotEmpty()) {
            val billPrice : Double = priceInput.text.toString().toDouble()
            val people : Double = peopleInput.text.toString().toDouble()

            if (people > 1) {
                resultValue = billPrice / people
                val txt : String = "R$ ${df.format(resultValue)}"
                resultTxt.text = txt
            } else {
                resultTxt.text = "A quantidade de pessoas deve ser maior que 1"
            }
        }
    }
}