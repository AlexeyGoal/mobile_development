package com.example.landscapeorientation

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var adapter: ArrayAdapter<CharSequence>

    companion object{
        private const val KEY = "currentIndex"
    }

    private lateinit var spinner: Spinner

    private lateinit var image: ImageView

    private var currentIndex = 0

    private var picturesList = intArrayOf(
        R.drawable.car1,
        R.drawable.car2,
        R.drawable.car3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentIndex = savedInstanceState?.getInt(KEY) ?: 0

        image = findViewById(R.id.picture)
        image.setImageResource(picturesList[currentIndex])


        adapter = ArrayAdapter.createFromResource(this, R.array.pictures,  android.R.layout.simple_spinner_item)
        spinner = findViewById<Spinner>(R.id.spinner)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this


    }

    fun onChangePictureClick(v: View) {
        var picture: Int
        if (currentIndex == 2){
            currentIndex = 0
            picture = currentIndex
        }else{
            picture = ++currentIndex
        }

        image.setImageResource(picturesList[picture])

        if (spinner.selectedItemPosition != picture) {
            spinner.setSelection(picture)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY,currentIndex)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        image.setImageResource(picturesList[position])
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this, "не выбран элемент", Toast.LENGTH_SHORT ).show()
    }
}