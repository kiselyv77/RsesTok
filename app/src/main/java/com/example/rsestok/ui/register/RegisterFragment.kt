package com.example.rsestok.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rsestok.createUser
import com.example.rsestok.databinding.FragmentRegisterBinding
import com.example.rsestok.ui.add.AddViewModel
import com.example.rsestok.utilits.showToast

class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel
    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        registerViewModel =
            ViewModelProvider(this).get(RegisterViewModel::class.java)

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        registerViewModel.text.observe(viewLifecycleOwner, Observer {

        })

        binding.btnRegister.setOnClickListener{
            val fullName = binding.inputName.text.toString().replace(" ", "")+" "+binding.inputSurname.text.toString().replace(" ","")
            createUser(binding.inputLogin.text.toString(),binding.inputPassword.text.toString(), fullName, binding.inputUsername.text.toString())
        }
        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}