package com.urbanoexpress.iridio3.pe.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.urbanoexpress.iridio3.pe.R
import com.urbanoexpress.iridio3.pe.databinding.FragmentPlanRutaTransporteBinding
import com.urbanoexpress.iridio3.pe.model.interactor.PlanRutasDetallesInteractor
import com.urbanoexpress.iridio3.pe.presenter.PlanRutaTransporteContract
import com.urbanoexpress.iridio3.pe.presenter.PlanRutaTransportePresenter
import com.urbanoexpress.iridio3.pe.ui.RutaActivity
import com.urbanoexpress.iridio3.pe.ui.model.PlacaGeoModel
import com.urbanoexpress.iridio3.pe.util.Preferences
import com.urbanoexpress.iridio3.pe.util.Session
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.EMPTY_VALUE
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.ERROR_409
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.MODULE_NAME
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.MODULE_TITLE
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.PREFERENCES_GLOBAL_CONFIG
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.PREFERENCES_ID_PER
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.PREFERENCES_PHONE
import com.urbanoexpress.iridio3.pe.util.constant.PlanRutaConstants.PREFERENCES_USER_PROFILE
import com.urbanoexpress.iridio3.pe.view.BaseModalsView


class PlanRutaTransporteFragment : BaseFragment(), PlanRutaTransporteContract.View {

    private lateinit var binding: FragmentPlanRutaTransporteBinding
    private lateinit var presenter: PlanRutaTransportePresenter
    val args: PlanRutaTransporteFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanRutaTransporteBinding.inflate(inflater, container, false)

        presenter = PlanRutaTransportePresenter(
            planRutaDetallesView = this,
            planRutasDetallesInteractor = PlanRutasDetallesInteractor(requireContext())
        )
        val rouId = args.idRouteId

        binding.etPlaca.addTextChangedListener(getTextWatcher())
        binding.etPiezasContadas.addTextChangedListener(getTextWatcher())
        binding.btnValidar.setOnClickListener {
            this.showProgressDialog(getString(R.string.plan_ruta_validando))
            presenter.validateRoad(
                PlacaGeoModel(
                    rouId = rouId.toInt(),
                    totPza = binding.etPiezasContadas.text.toString().toInt(),
                    undPlaca = binding.etPlaca.text.toString(),
                    celular = getUserPhone(),
                    perId = getPerId(),
                    vpIdUser = Session.getUser().idUsuario
                )

            )
        }
        return binding.root
    }

    private fun getTextWatcher(): TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding.btnValidar.isEnabled =
                    binding.etPlaca.text.isNotEmpty() && binding.etPiezasContadas.text.isNotEmpty()
            }

            override fun afterTextChanged(editable: Editable?) {}
        }
        return textWatcher
    }

    override fun showGuideList(successMsg: String) {
        this.dismissProgressDialog()
        val intent = Intent(requireContext(), RutaActivity::class.java)
        intent.putExtra(MODULE_NAME, MODULE_TITLE)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        requireActivity().finish()
    }

    private fun getUserPhone(): String {
        Preferences.getInstance().init(requireContext(), PREFERENCES_GLOBAL_CONFIG)
        return Preferences.getInstance().getString(PREFERENCES_PHONE, EMPTY_VALUE) ?: EMPTY_VALUE
    }

    private fun getPerId(): String {
        Preferences.getInstance().init(requireContext(), PREFERENCES_USER_PROFILE)
        return Preferences.getInstance().getString(PREFERENCES_ID_PER, EMPTY_VALUE) ?: EMPTY_VALUE
    }

    override fun showError(error: String) {
        this.dismissProgressDialog()
        if (error.contains(ERROR_409)) {
            BaseModalsView.showAlertDialog(
                requireContext(),
                getString(R.string.plan_ruta_titulo_error_piezas),
                getString(R.string.plan_ruta_des_error_piezas),
                getString(R.string.plan_ruta_aceptar)
            ) { _ , _ -> requireActivity().finish() }
        } else {
            BaseModalsView.showAlertDialog(
                requireContext(),
                getString(R.string.plan_ruta_titulo_error_gen),
                error,
                getString(R.string.plan_ruta_aceptar),
                null
            )
        }

    }

}