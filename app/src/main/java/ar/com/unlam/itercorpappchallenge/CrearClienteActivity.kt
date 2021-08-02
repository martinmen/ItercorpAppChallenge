package ar.com.unlam.itercorpappchallenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import ar.com.unlam.itercorpappchallenge.model.Cliente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_crear_cliente.*

class CreacionClienteActivity : AppCompatActivity() {
    private val db = FirebaseDatabase.getInstance().getReference()
    val crearClienteViewModel: CrearClienteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cliente)
        et_fecha_nacimiento.setOnClickListener { showDatePickerDialog() }
        setListeners()
    }

    private fun showDatePickerDialog() {
            val datePicker = DatePickerFragment{day,month,year->onDateSelected(day,month,year)}
        datePicker.show(supportFragmentManager,"datePicker")
    }
    fun onDateSelected(day:Int,month:Int,year:Int){
        et_fecha_nacimiento.setText(day.toString()+"/"+month.toString()+"/"+year.toString())
    }

    private fun setListeners() {
        buttonLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        button_crear_cliente.setOnClickListener {
            if (et_nombre.text.isNotEmpty() &&
                et_apellido.text.isNotEmpty() &&
                et_edad.text.isNotEmpty() &&
                et_fecha_nacimiento.text.isNotEmpty()
            ) {
                db.push().key.toString()
                val cliente = Cliente(
                    db.push().key.toString(),
                    et_nombre.text.toString(),
                    et_apellido.text.toString(),
                    et_edad.text.toString().toInt(),
                    et_fecha_nacimiento.text.toString(),
                )
                crearClienteViewModel.crearNuevoCliente(cliente)
                Toast.makeText(this, "Cliente creado", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(
                    this,
                    "Completar todos los campos para crear cliente",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
}

enum class ProviderType {
    BASIC,
    GOOGLE
}