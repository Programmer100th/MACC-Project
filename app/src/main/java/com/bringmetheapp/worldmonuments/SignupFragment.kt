package com.bringmetheapp.worldmonuments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private var nickname : TextInputLayout? = null
    private var email : TextInputLayout? = null
    private var password : TextInputLayout? = null

    private var btnSignup : Button? = null

    private var mQueue: RequestQueue? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nickname = view.findViewById(R.id.signup_nickname_field)
        email = view.findViewById(R.id.signup_email_field)
        password = view.findViewById(R.id.signup_password_field)

        btnSignup = view.findViewById(R.id.signup_btn)

        mQueue = Volley.newRequestQueue(context)

        btnSignup?.setOnClickListener {

            val nicknameString = nickname!!.editText?.text.toString()
            val emailString = email!!.editText?.text.toString()
            val passwordString = password!!.editText?.text.toString()


            val user = User(nicknameString, emailString, passwordString)

            val url =
                "https://world-monuments.herokuapp.com/users?" +
                        "nickname=" + user.nickname + "&email=" + user.email + "&password=" + user.password

            val request = StringRequest(
                Request.Method.PUT, url, { response ->
                    try {



                        val jsonArray = response

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                { Toast.makeText(context, "KO", Toast.LENGTH_SHORT).show() })

            mQueue?.add(request)

            Toast.makeText(context, user.nickname, Toast.LENGTH_SHORT).show()

            view.findNavController().navigate(R.id.action_signupFragment_to_loginFragment)

            /*users!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(user.nickname).exists())
                        Toast.makeText(context, "User already exists!", Toast.LENGTH_SHORT)
                            .show()
                    else
                        users!!.child(user.username).setValue(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })*/
        }
    }
}