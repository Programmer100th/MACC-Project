package com.bringmetheapp.worldmonuments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.app.Activity.RESULT_OK
import android.content.Context
import com.google.android.gms.auth.api.signin.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var email: TextInputLayout? = null
    private var password: TextInputLayout? = null

    private var btnSignUp: Button? = null
    private var btnLogIn: Button? = null
    private var btnGoogleLogin: SignInButton? = null

    private var mQueue: RequestQueue? = null

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mActivityResultLauncher: ActivityResultLauncher<Intent>

    //private lateinit var sharedPreferences : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Google sign-in
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), options)

        mActivityResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                val account = task.getResult(ApiException::class.java)

                handleSignInResult(account)
            } else
                Log.d("login", "Result launcher for sign in failed")
        }

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = view.findViewById(R.id.email_field)
        password = view.findViewById(R.id.password_field)

        btnSignUp = view.findViewById(R.id.btn_signup)
        btnLogIn = view.findViewById(R.id.btn_login)
        btnGoogleLogin = view.findViewById(R.id.btn_google_login)

        mQueue = Volley.newRequestQueue(context)

        btnSignUp?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
            //showSignUpDialog(eNewUser?.getText().toString(), eNewPassword?.getText().toString(), eNewEmail?.getText().toString(), users!!, view)
        }

        btnLogIn?.setOnClickListener {
            signIn(email!!.editText?.text.toString(), password!!.editText?.text.toString())
        }

        btnGoogleLogin?.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            mActivityResultLauncher.launch(signInIntent)
        }
    }

    private fun signIn(email: String, password: String) {

        val url =
            "https://world-monuments.herokuapp.com/users?email=" + email + "&password=" + password

        Log.d("Email", email)
        Log.d("Password", password)

        val stringRequest = StringRequest(
            Request.Method.GET, url, { response ->
                try {

                    val reply = JSONObject(response.toString())

                    if (reply!!.has("message")) {
                        Toast.makeText(
                            context,
                            "User doesn't exist!",
                            Toast.LENGTH_SHORT
                        )
                            .show()


                    } else {
                        val e = reply!!.getString("email")
                        val n = reply!!.getString("nickname")

                        Configuration.EMAIL = e
                        Configuration.NICKNAME = n

                        val sharedPreferences = context?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()
                        editor?.apply {
                            putString("nickname", n)
                            putString("email", e)
                            Log.d("nick", "ok")
                            apply()
                        }

                        Toast.makeText(context, "Login completed!", Toast.LENGTH_SHORT)
                            .show()

                        val intent = Intent(activity, MainActivity::class.java)
                        activity?.startActivity(intent)
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            {  error: VolleyError? ->
                Log.i("info", "Polling: " + error.toString())
            })


        mQueue?.add(stringRequest)
    }

    // Handle sign in result (for Google Games sign in)
    private fun handleSignInResult(account: GoogleSignInAccount?) {

        if (account == null)
            Toast.makeText(context, "login error", Toast.LENGTH_SHORT).show()
        else {
            Toast.makeText(context, "login ok", Toast.LENGTH_SHORT).show()

            //findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }
/*

        users.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(nn).exists()) {
                    if(!nn.isEmpty()) {
                        var login = snapshot.getValue() as User
                        if(login.password.equals(pwd)) {
                            Toast.makeText(context, "Login ok!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else {
                            Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    else {
                        Toast.makeText(context, "Enter your nickname", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                else {
                    Toast.makeText(context, "User not exists!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })*/
}
/*
    private fun showSignUpDialog(nn: String, pwd: String, email: String, users: DatabaseReference, view: View) {
        val alert = androidx.appcompat.app.AlertDialog.Builder(view.context)
        alert.setTitle("Sign Up")
        alert.setMessage("Please fill full information")

        val inflater = this.layoutInflater
        val sign_up_layout = inflater.inflate(R.layout.fragment_signup, null)

        alert.setView(sign_up_layout)

        alert.setNegativeButton("NO", { DialogInterface, i ->
            DialogInterface.dismiss()
        })

        alert.setPositiveButton("YES", { DialogInterface, i ->
            val user = User(nn, pwd, email)
            users.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(user.nickname).exists())
                        Toast.makeText(context, "User already exists!", Toast.LENGTH_SHORT)
                            .show()
                    else
                        users.child(user.nickname).setValue(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            DialogInterface.dismiss()
        })
        alert.show()
    }
}*/


