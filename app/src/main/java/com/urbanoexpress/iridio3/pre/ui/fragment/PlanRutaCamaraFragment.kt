package com.urbanoexpress.iridio3.pre.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.zxing.Result
import com.urbanoexpress.iridio3.pre.R
import com.urbanoexpress.iridio3.pre.databinding.FragmentPlanRutaCamaraBinding
import com.urbanoexpress.iridio3.pre.model.interactor.PlanRutaCamaraInteractor
import com.urbanoexpress.iridio3.pre.model.interactor.RutaPendienteInteractor
import com.urbanoexpress.iridio3.pre.presenter.PlanRutaCamaraContract
import com.urbanoexpress.iridio3.pre.presenter.PlanRutaCamaraPresenter
import com.urbanoexpress.iridio3.pre.ui.model.DataItemForView
import com.urbanoexpress.iridio3.pre.ui.RutaActivity
import com.urbanoexpress.iridio3.pre.util.constant.PlanRutaConstants.MODULE_NAME
import com.urbanoexpress.iridio3.pre.util.constant.PlanRutaConstants.MODULE_TITLE
import com.urbanoexpress.iridio3.pre.view.BaseModalsView
import me.dm7.barcodescanner.zxing.ZXingScannerView


class PlanRutaCamaraFragment : BaseFragment(), ZXingScannerView.ResultHandler,
    PlanRutaCamaraContract.View {

    private lateinit var binding: FragmentPlanRutaCamaraBinding
    private lateinit var scannerView: ZXingScannerView
    private lateinit var planRutaCamaraPresenter: PlanRutaCamaraPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPlanRutaCamaraBinding.inflate(inflater, container, false)
        planRutaCamaraPresenter = PlanRutaCamaraPresenter(
            planRutaCamaraView = this,
            planRutaCamaraInteractor = PlanRutaCamaraInteractor(requireContext()),
            rutaPendienteInteractor = RutaPendienteInteractor(requireContext())
        )
        scannerView = binding.barcodeScannerView
        scannerView.setResultHandler(this)
        this.showProgressDialog(getString(R.string.plan_ruta_validando))
        planRutaCamaraPresenter.validateGuideList()
        return binding.root
    }

    override fun handleResult(rawResult: Result?) {
        this.showProgressDialog(getString(R.string.plan_ruta_validando))
        rawResult?.let { ruta ->
            planRutaCamaraPresenter.getRutaDetail(idRutaQR = ruta.text)
        }
    }

    override fun onResume() {
        super.onResume()
        if (scannerView.visibility == View.VISIBLE) {
            scannerView.startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        if (scannerView.visibility == View.VISIBLE) {
            scannerView.stopCamera()
        }
    }

    override fun showRutaDetail(firstDataItem: DataItemForView) {
        this.dismissProgressDialog()

        when (firstDataItem.estado) {
            0 -> routeCancelled()
            1 -> goToDetails(detallesRuta = firstDataItem)
            2, 4 -> goToGuideList()
            3 -> wrongNumberOfParts()
            else -> Unit
        }


    }

    private fun goToGuideList() {
        val intent = Intent(requireContext(), RutaActivity::class.java)
        intent.putExtra(MODULE_NAME, MODULE_TITLE)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        requireActivity().finish()
    }

    private fun goToDetails(detallesRuta: DataItemForView) {
        findNavController().navigate(
            PlanRutaCamaraFragmentDirections.actionPlanRutaCamaraFragmentToPlanRutaDetallesFragment(
                detallesRuta = detallesRuta
            ),
            navOptions {
                popUpTo(R.id.planRutaCamaraFragment) { inclusive = true }
            }
        )
    }

    private fun routeCancelled() {
        BaseModalsView.showAlertDialog(
            requireContext(),
            getString(R.string.plan_ruta_titulo_error_anulada),
            getString(R.string.plan_ruta_des_error_anulada),
            getString(R.string.plan_ruta_aceptar)
        )
        { _, _ -> requireActivity().finish() }

    }

    private fun wrongNumberOfParts() {
        BaseModalsView.showAlertDialog(
            requireContext(),
            getString(R.string.plan_ruta_titulo_error_piezas),
            getString(R.string.plan_ruta_des_error_piezas),
            getString(R.string.plan_ruta_aceptar)
        )
        { _, _ -> goToGuideList() }
    }

    override fun showGuideList() {
        this.dismissProgressDialog()
        goToGuideList()
    }

    override fun enableQrCamera() {
        this.dismissProgressDialog()
        scannerView.visibility = View.VISIBLE
        scannerView.startCamera()
    }

    override fun showError(error: String) {
        this.dismissProgressDialog()
        BaseModalsView.showAlertDialog(
            requireContext(),
            getString(R.string.plan_ruta_titulo_error_gen),
            error,
            requireContext().getString(R.string.plan_ruta_aceptar), null
        )

    }

}