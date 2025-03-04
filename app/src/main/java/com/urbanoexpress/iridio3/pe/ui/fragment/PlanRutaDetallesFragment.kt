package com.urbanoexpress.iridio3.pe.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.urbanoexpress.iridio3.pe.R
import com.urbanoexpress.iridio3.pe.databinding.FragmentPlanRutaDetallesBinding
import com.urbanoexpress.iridio3.pe.ui.model.DataItemForView


class PlanRutaDetallesFragment : Fragment() {

    private lateinit var binding: FragmentPlanRutaDetallesBinding
    val args: PlanRutaDetallesFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanRutaDetallesBinding.inflate(inflater, container, false)

        val planRuta = args.detallesRuta
        setData(planRuta)

        binding.btnValidar.setOnClickListener {
            goToTransportFragment(planRuta)
        }
        return binding.root
    }

    private fun goToTransportFragment(planRuta: DataItemForView) {
        findNavController().navigate(
            PlanRutaDetallesFragmentDirections.actionPlanRutaDetallesFragmentToPlanRutaTransporteFragment(
                idRouteId = planRuta.rouId.toString()
            ),
            navOptions {
                popUpTo(R.id.planRutaDetallesFragment) { inclusive = true }
            }
        )
    }

    private fun setData(planRuta: DataItemForView) {
        with(binding) {
            tvTituloRutaIcon.text = planRuta.ciuNombre.first().toString()
            tvTituloRuta.text = planRuta.ciuNombre
            etZona.text = planRuta.zonaCodigo
            etPedidos.text = planRuta.totGuias.toString()
            etPiezas.text = planRuta.totPiezas.toString().obfuscateLast()
            etTiempo.text = planRuta.timeRuta
            etKilometro.text = "${planRuta.kmRuta} Km."
            etParadas.text = planRuta.totParadas.toString()

        }
    }

    private fun String.obfuscateLast():String{
        return if (this.isNotEmpty()){
            if (this.length>1) this.dropLast(1)+"*" else "*"
        }else {
            this
        }
    }
}