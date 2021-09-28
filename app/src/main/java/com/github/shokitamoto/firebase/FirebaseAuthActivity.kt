package com.github.shokitamoto.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
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
        binding.apply {
            mailPasswordView.visibility = View.VISIBLE
            loginButton.visibility = View.VISIBLE
            registerSendButton.visibility = View.INVISIBLE
            forgetPasswordSendButton.visibility = View.INVISIBLE
            registerForgetPasswordView.visibility = View.VISIBLE
        }
        initLayout()
    }

    private fun initLayout() {
        initClick()
//        updateView()
    }

    private fun initClick() {
        binding.apply {
            loginButton.setOnClickListener {
                // todo: ここでログイン処理を書く
                login()

            }
            registerButton.setOnClickListener {
                mailPasswordView.visibility = View.VISIBLE
                loginButton.visibility = View.INVISIBLE
                registerSendButton.visibility = View.VISIBLE
                forgetPasswordSendButton.visibility = View.INVISIBLE
                registerForgetPasswordView.visibility = View.INVISIBLE

            }
            forgetPasswordButton.setOnClickListener {
                mailView.visibility = View.VISIBLE
                passwordView.visibility = View.INVISIBLE
                loginButton.visibility = View.INVISIBLE
                registerSendButton.visibility = View.INVISIBLE
                forgetPasswordSendButton.visibility = View.VISIBLE
                registerForgetPasswordView.visibility = View.INVISIBLE

            }

//            registerSendButton.setOnClickListener {
//                // todo: ここでログイン処理を書く
//                login()
//
//            }
//            forgetPasswordSendButton.setOnClickListener {
//                // todo: ここでパスワード再設定の処理を書く
//                forgetPassword()
//            }
        }
    }

    private fun updateView() {
        var user = FirebaseAuth.getInstance().currentUser
        var isLogin = (user != null) // userがnullでなければ、isLogin = true
        if (isLogin) loginSuccess() else loginFailure()
    }
    private fun loginSuccess() {
        val intent = Intent(this@FirebaseAuthActivity, MainActivity::class.java)
        startActivity(intent)
    }
    private fun loginFailure() {

    }

    private fun login() {
        val pair = getPair()
        pair?.also {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(it.first, it.second)
                .addOnCompleteListener {
//                    addOnCompleteListener.d("login task:$it")
                    toast(if (it.isSuccessful) R.string.firebase_auth_success else R.string.firebase_auth_error)
                    updateView()
                }
        }
    }

    private fun forgetPassword() {

    }
    private fun getPair(isOnlyMailAddress: Boolean = false): Pair<String,String>? {
        val mailAddress = getMailAddress()
        if (mailAddress.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mailAddress).matches()){
            toast(R.string.firebase_auth_warn_mail)
            return null
        }
        val password = getPassword()
        if (!isOnlyMailAddress && (password.isEmpty() || password.length < 8)) {
            toast(R.string.firebase_auth_warn_password)
            return null
        }
        return Pair(mailAddress, password)
    }



    private fun getMailAddress(): String = binding.mailEditText.text.toString()
    private fun getPassword(): String = binding.passwordEditText.text.toString()

    private fun toast(textId: Int) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
    }
    enum class LoginRegisterType {
        LOGIN, REGISTER, FORGET_MAIL
    }
}