package com.bringmetheapp.worldmonuments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text

class UserFragment: Fragment(R.layout.fragment_user)  {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val textViewNickname = view.findViewById<TextView>(R.id.userNickname)
        val textViewEmail = view.findViewById<TextView>(R.id.userEmail)
        val buttonLogout = view.findViewById<Button>(R.id.buttonLogout)


        val sharedPreferences = context?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val nickname = sharedPreferences?.getString("nickname", "").toString()
        val email = sharedPreferences?.getString("email", "").toString()
        Log.d("email", "ciao" + email)

        textViewNickname.text = nickname
        textViewEmail.text = email

        buttonLogout.setOnClickListener{

            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Log Out")
            alert.setCancelable(false)
            alert.setMessage("Are you sure you want to exit?")


            alert.setPositiveButton("Yes") { dialog, which ->
                val editor = sharedPreferences?.edit()
                editor?.apply {
                    putString("nickname", "")
                    putString("email", "")
                    Log.d("nick", "ok")
                    apply()
                }
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)

            }
            alert.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()

            }
            alert.show()
        }


    }
}