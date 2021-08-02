package ar.com.unlam.itercorpappchallenge

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_phone_login.*
import java.util.*
import java.util.concurrent.TimeUnit

class PhoneLogin : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var storageVerificationId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)
        auth = FirebaseAuth.getInstance()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        btn_volver_de_phone.setOnClickListener {
            val intent = Intent(
                this@PhoneLogin,
                MainActivity::class.java
            )
            startActivity(intent)
        }
        btn_enviar_numero.setOnClickListener {
            val prhoneNumber = et_phone_number.text.toString()
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(prhoneNumber)       // Phone number to verify
                .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)// OnVerificationStateChangedCallbacks
                .build()

            auth.setLanguageCode(Locale.getDefault().language)
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        verificar_codigo.setOnClickListener {
            val code = et_code_number.text.toString()
            val credential = PhoneAuthProvider.getCredential(storageVerificationId!!, code)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")

                        val user = task.result?.user

                        val intent = Intent(
                            this@PhoneLogin,
                            CreacionClienteActivity::class.java
                        )
                        startActivity(intent)
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                        Toast.makeText(
                            this,
                            "Error: código incorrecto",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Update UI
                    }
                }
        }
    }

    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            // signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storageVerificationId = verificationId
            // resendToken = token
        }
    }
}
