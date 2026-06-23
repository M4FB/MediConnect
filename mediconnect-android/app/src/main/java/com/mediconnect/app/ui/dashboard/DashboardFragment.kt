package com.mediconnect.app.ui.dashboard

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
import com.mediconnect.app.databinding.FragmentDashboardBinding
import com.mediconnect.app.util.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNetworkStatus()
        viewModel.loadProfile()

        binding.btnDoctors.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_doctorsFragment)
        }

        binding.btnAppointments.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_appointmentsFragment)
        }

        binding.btnPrescriptions.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_prescriptionsFragment)
        }

        binding.btnHistory.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_historyFragment)
        }

        binding.btnNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_notificationsFragment)
        }

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_dashboardFragment_to_loginFragment)
        }

        lifecycleScope.launch {
            viewModel.userProfile.collectLatest { profile ->
                if (profile != null) {
                    binding.tvWelcome.text = "¡Hola, ${profile.nombre} ${profile.apellido}!"
                    binding.tvStatus.text = "Rol: ${profile.rol} | Email: ${profile.email}"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                if (error != null) {
                    binding.tvStatus.text = "Estado: Offline / Sin acceso a perfil en tiempo real"
                }
            }
        }
    }

    private fun checkNetworkStatus() {
        val online = NetworkUtils.isInternetAvailable(requireContext())
        if (!online) {
            binding.tvOfflineBanner.visibility = View.VISIBLE
        } else {
            binding.tvOfflineBanner.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        checkNetworkStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
