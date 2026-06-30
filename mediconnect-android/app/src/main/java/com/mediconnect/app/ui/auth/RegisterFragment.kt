package com.mediconnect.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mediconnect.app.R
import com.mediconnect.app.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val apellido = binding.etApellido.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim().ifEmpty { null }
            val direccion = binding.etDireccion.text.toString().trim().ifEmpty { null }

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
                binding.tvError.text = "Los campos Nombre, Apellido, Correo y Contraseña son requeridos."
                binding.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tvError.text = "Formato de correo electrónico inválido"
                binding.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.tvError.text = "La contraseña debe tener al menos 6 caracteres"
                binding.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            viewModel.register(nombre, apellido, email, password, telefono, direccion)
        }

        binding.btnGoLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                binding.btnRegister.isEnabled = !state.isLoading

                if (state.error != null) {
                    binding.tvError.text = state.error
                    binding.tvError.visibility = View.VISIBLE
                    viewModel.clearError()
                } else {
                    binding.tvError.visibility = View.GONE
                }

                if (state.authResponse != null) {
                    Toast.makeText(context, "Registro Exitoso", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_dashboardFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
