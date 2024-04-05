package com.example.oauth2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.oauth2.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("379465323401-i8uic5jp4b7d5dae7gv9n01hkgbj0ejc.apps.googleusercontent.com").requestProfile().build()

        googleSignInClient = GoogleSignIn.getClient(this ,gso)

        updateUI(isUserLoggedIn())

        binding.signIn.setOnClickListener{
            if (isUserLoggedIn()) {
                signOut()
            } else {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, 123)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123 && resultCode == Activity.RESULT_OK ){
          try {
              val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
              handleResult(task)
          }catch (e:Exception){
              e.printStackTrace()
          }
        }
    }

    private fun updateUI(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            binding.signIn.text = "Logout"
            binding.tv1.text = "User is logged in"
        } else {
            binding.signIn.text = "Sign In"
            binding.tv1.text = "User is not logged in"
        }
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(false)
        }
    }

    fun isUserLoggedIn():Boolean{
        val account = GoogleSignIn.getLastSignedInAccount(this)
        return account!=null
    }

    fun handleResult(task: Task<GoogleSignInAccount>){
        val account = task.getResult(ApiException::class.java)
        if(account!=null){
            val id = account.id
            val f_name = account.givenName
            val l_name = account.familyName
            if (account.photoUrl != null) {
                val pic_url = account.photoUrl.toString()
                val email = account.email
                binding.tv1.text = id+"\n"+f_name+"\n"+l_name+"\n"+email
                println(account.idToken)
                updateUI(isUserLoggedIn())
            }

        }
    }

}