package ar.com.unlam.itercorpappchallenge

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.common.GoogleSignatureVerifier
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setOnClickListeners()

    }

    private fun setOnClickListeners() {
        button_login_google.setOnClickListener {
            val googleConf =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
        button_registrar.setOnClickListener {
            if (email_login.text.isNotEmpty() && passwprd_login.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                        email_login.text.toString(),
                        passwprd_login.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            goCreacionCliente(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
                        }

                    }
            } else {
                Toast.makeText(
                    this,
                    "Usuario y contraseña deben estar completos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        buttonLogin.setOnClickListener {
            if (email_login.text.isNotEmpty() && passwprd_login.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        email_login.text.toString(),
                        passwprd_login.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            goCreacionCliente(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            Toast.makeText(
                                this,
                                "Error: credenciales incorrectas",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
            } else {
                Toast.makeText(
                    this,
                    "Usuario y contraseña deben estar completos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        button_login_phone.setOnClickListener {
            val intent = Intent(
                this@MainActivity,
                PhoneLogin::class.java
            )
            startActivity(intent)
        }
        login_fb_button.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        result?.let {
                            val token = it.accessToken

                            val credential = FacebookAuthProvider.getCredential(token.token)
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        val intent = Intent(
                                            this@MainActivity,
                                            CreacionClienteActivity::class.java
                                        )
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Error en login face",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }

                        }
                    }

                    override fun onCancel() {
                        TODO("Not yet implemented")
                    }

                    override fun onError(error: FacebookException?) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }

    private fun goCreacionCliente(email: String, provider: ProviderType) {
        val creacionClienteIntent = Intent(this, CreacionClienteActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(creacionClienteIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                goCreacionCliente(account.email ?: "", ProviderType.GOOGLE)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Error: Login con Google incorrecto",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                Toast.makeText(
                    this,
                    e.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}
