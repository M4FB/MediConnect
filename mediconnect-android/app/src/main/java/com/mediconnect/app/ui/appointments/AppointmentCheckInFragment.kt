package com.mediconnect.app.ui.appointments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.mediconnect.app.databinding.FragmentAppointmentCheckInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppointmentCheckInFragment : Fragment() {

    private var _binding: FragmentAppointmentCheckInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppointmentsViewModel by viewModels()

    private var citaId: String = ""
    private var codigoCheckIn: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            citaId = it.getString("citaId") ?: ""
            codigoCheckIn = it.getString("codigoCheckIn") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentCheckInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCodeString.text = "Código: $codigoCheckIn"

        val qrBitmap = generateQrCode(codigoCheckIn)
        if (qrBitmap != null) {
            binding.ivQrCode.setImageBitmap(qrBitmap)
        } else {
            Toast.makeText(context, "Error al generar código QR", Toast.LENGTH_SHORT).show()
        }

        binding.btnSimulateCheckIn.setOnClickListener {
            viewModel.checkIn(citaId, codigoCheckIn)
        }

        lifecycleScope.launch {
            viewModel.checkInSuccess.collectLatest { appointment ->
                if (appointment != null) {
                    Toast.makeText(context, "Check-in completado. Estado de cita: ${appointment.estado}", Toast.LENGTH_LONG).show()
                    viewModel.resetCheckInState()
                    findNavController().navigateUp()
                }
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

    private fun generateQrCode(text: String, size: Int = 512): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
