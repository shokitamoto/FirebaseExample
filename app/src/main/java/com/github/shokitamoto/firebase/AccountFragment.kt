package com.github.shokitamoto.firebase

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.shokitamoto.firebase.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAccountBinding.bind(view)
        binding.logoutButton.setOnClickListener {
            // todo: logout処理

            // todo: FirebaseAuthActivityへのintent処理
            signOut().addOnCompleteListener {
                val intent = Intent(this, FirebaseAuthActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }
}