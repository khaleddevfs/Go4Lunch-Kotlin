package com.example.go4lunch24kotlin.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import com.example.go4lunch24kotlin.BaseActivity

import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.databinding.ActivityLoginBinding
import com.example.go4lunch24kotlin.factory.Go4LunchFactory
import com.example.go4lunch24kotlin.viewModel.LoginViewModel
import com.google.firebase.FirebaseApp

/*
class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initView()
        initViewModel()
        initListener()
        checkSessionUser()
    }

    private fun initView() {
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
    }

    private fun initViewModel() {
        // Assurez-vous que la factory retourne le bon type de ViewModel
      /*  val viewModelFactory = Go4LunchFactory.instance
        viewModel = ViewModelProvider(this, viewModelFactory!!)[LoginViewModel::class.java]

       */
        // Exemple de vérification avant utilisation
        val viewModelFactory = Go4LunchFactory.instance
        if (viewModelFactory != null) {
            viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        } else {
            // Gérez le cas où la factory est null - peut-être afficher un message d'erreur ou tenter une réinitialisation
        }

    }

    private fun initListener() {
        loginBinding.emailLoginButton.setOnClickListener { viewModel.startLoginActivityEmail(this) }
        loginBinding.gmailLoginButton.setOnClickListener { viewModel.startLoginActivityGoogle(this) }
        loginBinding.twitterLoginButton.setOnClickListener { viewModel.startLoginActivityTwitter(this) }
    }

    private fun checkSessionUser() {
        if (viewModel.isCurrentUserLogged()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            viewModel.updateCurrentUser()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoginViewModel.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                viewModel.updateCurrentUser()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Ici, vous pourriez vouloir gérer les différents cas d'erreur
                Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

 */

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private lateinit var viewModel: LoginViewModel

    override val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        initListener()
        checkSessionUser()
    }

    private fun initViewModel() {
        val viewModelFactory = Go4LunchFactory.instance
        viewModelFactory?.let {
            viewModel = ViewModelProvider(this, it)[LoginViewModel::class.java]
        } ?: run {
            // Gestion de l'erreur si la factory est null
        }
    }

    private fun initListener() {
        binding.emailLoginButton.setOnClickListener { viewModel.startLoginActivityEmail(this) }
        binding.gmailLoginButton.setOnClickListener { viewModel.startLoginActivityGoogle(this) }
        binding.twitterLoginButton.setOnClickListener { viewModel.startLoginActivityTwitter(this) }
    }

    private fun checkSessionUser() {
        if (viewModel.isCurrentUserLogged()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            viewModel.updateCurrentUser()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoginViewModel.RC_SIGN_IN && resultCode == RESULT_OK) {
            viewModel.updateCurrentUser()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show()
        }
    }
}