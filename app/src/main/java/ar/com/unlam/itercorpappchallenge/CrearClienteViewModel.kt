package ar.com.unlam.itercorpappchallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.com.unlam.itercorpappchallenge.model.Cliente
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class CrearClienteViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance().reference


    fun crearNuevoCliente(cliente: Cliente) {
        viewModelScope.launch {
            if (cliente.id != null) {
                db.child("Cliente").child(cliente.id).setValue(cliente)
                    .addOnCompleteListener {

                    }
            }
        }
    }
}