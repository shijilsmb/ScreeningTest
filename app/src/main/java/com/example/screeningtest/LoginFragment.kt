package com.example.screeningtest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.screeningtest.utils.CommonUtils
import com.example.screeningtest.utils.SessionUtils
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bt_login.setOnClickListener {

            val email = edt_email.text.toString().trim()
            val password = edt_password.text.toString()

            tl_email.isErrorEnabled = false
            tl_password.isErrorEnabled = false

            when {
                !CommonUtils.isEmailValid(email) -> {
                    tl_email.isErrorEnabled = true
                    tl_email.error = "Please enter valid email"
                }
                password.isEmpty() -> {
                    tl_password.isErrorEnabled = true
                    tl_password.error = "Please enter"
                }
                SessionUtils.checkLogin(requireActivity(), email, password) -> {
                    SessionUtils.saveSession(true)
                    findNavController().navigate(LoginFragmentDirections.showMain())
                }
                else -> {
                    tl_password.error = "Email or password invalid"
                }
            }
        }
    }

}