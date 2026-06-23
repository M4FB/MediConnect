package com.mediconnect.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mediconnect.app.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cbBiometrics.isChecked = viewModel.isBiometricsEnabled()

        binding.cbBiometrics.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setBiometricsEnabled(isChecked)
            val msg = if (isChecked) "Biometría habilitada para inicio de sesión" else "Biometría deshabilitada"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveProfile.setOnClickListener {
            val nombre = binding.etProfileNombre.text.toString().trim()
            val apellido = binding.etProfileApellido.text.toString().trim()
            val telefono = binding.etProfileTelefono.text.toString().trim().ifEmpty { null }
            val direccion = binding.etProfileDireccion.text.toString().trim().ifEmpty { null }

            if (nombre.isEmpty() || apellido.isEmpty()) {
                Toast.makeText(context, "Nombre y Apellido son requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateProfile(nombre, apellido, telefono, direccion)
        }

        binding.btnChangePassword.setOnClickListener {
            val currentPass = binding.etCurrentPassword.text.toString().trim()
            val newPass = binding.etNewPassword.text.toString().trim()

            if (currentPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(context, "Complete ambos campos para cambiar contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.changePassword(currentPass, newPass)
        }

        lifecycleScope.launch {
            viewModel.userProfile.collectLatest { profile ->
                if (profile != null) {
                    binding.etProfileNombre.setText(profile.nombre)
                    binding.etProfileApellido.setText(profile.apellido)
                    binding.etProfileTelefono.setText(profile.telefono ?: "")
                    binding.etProfileDireccion.setText(profile.direccion ?: "")
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updateSuccess.collectLatest { success ->
                if (success) {
                    Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                    viewModel.resetUpdateState()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.passwordSuccess.collectLatest { success ->
                if (success) {
                    Toast.makeText(context, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show()
                    binding.etCurrentPassword.text.clear()
                    binding.etNewPassword.text.clear()
                    viewModel.resetPasswordState()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                binding.btnSaveProfile.isEnabled = !loading
                binding.btnChangePassword.isEnabled = !loading
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                if (error != null) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
