package com.github.shokitamoto.firebase

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.shokitamoto.firebase.databinding.ActivityFirebaseAuthBinding
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthActivity: AppCompatActivity() {

//    private var loginRegisterType = LoginRegisterType.LOGIN

    private lateinit var binding: ActivityFirebaseAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_firebase_auth)
        initialize()
    }

    private fun initialize() {
        initLayout()
    }

    private fun initLayout() {
        initClick()
        updateView()
    }

    private fun initClick() {
        binding.apply {
            loginButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            forgetPasswordButton.setOnClickListener {
                loginView.visibility = View.INVISIBLE
                registerView.visibility = View.INVISIBLE
                forgetPasswordView.visibility = View.VISIBLE
            }
            registerButton.setOnClickListener {
                loginView.visibility = View.INVISIBLE
                registerView.visibility = View.INVISIBLE
                forgetPasswordView.visibility = View.VISIBLE
            }
            registerSendButton.setOnClickListener {

            }
            forgetPasswordSendButton.setOnClickListener {

            }
        }
    }

    private fun updateView() {
        var user = FirebaseAuth.getInstance().currentUser
        var isLogin = (user != null) // userがnullでなければ、isLogin = true
    }

    enum class LoginRegisterType {
        LOGIN, REGISTER, FORGET_MAIL
    }
}