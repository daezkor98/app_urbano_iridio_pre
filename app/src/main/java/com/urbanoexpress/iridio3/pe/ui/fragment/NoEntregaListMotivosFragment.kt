package com.urbanoexpress.iridio3.pe.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.urbanoexpress.iridio3.pe.R
import com.urbanoexpress.iridio3.pe.databinding.FragmentNoEntregaListMotivosBinding
import com.urbanoexpress.iridio3.pe.model.entity.Ruta
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor
import com.urbanoexpress.iridio3.pe.presenter.NoEntregaListMotivosContract
import com.urbanoexpress.iridio3.pe.presenter.NoEntregaListMotivosPresenter
import com.urbanoexpress.iridio3.pe.ui.adapter.TipoEntregaGuiaAdapter
import com.urbanoexpress.iridio3.pe.ui.dialogs.BaseDialogFragment
import com.urbanoexpress.iridio3.pe.ui.dialogs.NoEntregaGEDialog
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper
import com.urbanoexpress.iridio3.pe.ui.model.MotivoDescargaItem
import com.urbanoexpress.iridio3.pe.util.RecyclerTouchListener
import com.urbanoexpress.iridio3.pe.util.RecyclerTouchListener.ClickListener


class NoEntregaListMotivosFragment : BaseDialogFragment(),
    NoEntregaListMotivosContract.NoEntregaListMotivosView {

    private var param1: String? = null
    private var binding: FragmentNoEntregaListMotivosBinding? = null
    private var presenter: NoEntregaListMotivosPresenter? = null

    companion object {
        val TAG = NoEntregaListMotivosFragment::class.java.simpleName
        fun newInstance(
            rutas: ArrayList<Ruta?>?,
            numVecesGestionado: Int
        ): NoEntregaListMotivosFragment {
            val fragment = NoEntregaListMotivosFragment()
            val args = Bundle()
            args.putSerializable("guias", rutas)
            args.putInt("numVecesGestionado", numVecesGestionado)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoEntregaListMotivosBinding.inflate(inflater, container, false)
        initUI()
        if (presenter == null) {
            presenter = NoEntregaListMotivosPresenter(
                view = this,
                rutaPendienteInteractor = RutaPendienteInteractor(requireContext())
            )
            presenter?.getListMotivosNoEntrega()
        }
        return binding?.root
    }

    override fun showListMotivosNoEntrega(motivos: List<MotivoDescargaItem>) {
        try {
            val adapter = TipoEntregaGuiaAdapter(motivos)
            binding?.rvMotivos?.setAdapter(adapter)
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    override fun showErrorEmptyList() {
        ModalHelper.getBuilderAlertDialog(activity)
            .setTitle(R.string.text_advertencia)
            .setMessage(R.string.activity_detalle_ruta_message_no_puede_tomar_foto)
            .setPositiveButton(R.string.text_aceptar, null)
            .show()
    }

    private fun initUI() {
        binding!!.rvMotivos.layoutManager = LinearLayoutManager(activity)
        binding!!.rvMotivos.setHasFixedSize(true)
        binding!!.rvMotivos.addOnItemTouchListener(
            RecyclerTouchListener(
                activity,
                binding!!.rvMotivos, object : ClickListener {
                    override fun onClick(view: View, position: Int) {
                        view.isFocusable = true
                        view.isFocusableInTouchMode = true
                        val adapter = binding!!.rvMotivos.adapter as TipoEntregaGuiaAdapter?
                        if (adapter != null) {
                            val item = adapter.data[position]
                            val itemIdInteger = item.id.toInt()
                            next(idMotivo = itemIdInteger)
                        }
                    }

                    override fun onLongClick(view: View, position: Int) {
                        Unit
                    }
                })
        )

    }

    private fun next(idMotivo: Int) {
        arguments?.let {
            val ruta = it.getSerializable("guias") as? ArrayList<Ruta>
            val numVecesGestionado = it.getInt("numVecesGestionado")
            val dialogFragment = NoEntregaGEDialog.newInstance(ruta, numVecesGestionado, idMotivo)
            val tagFragment = NoEntregaGEDialog.TAG
            dialogFragment?.show(requireActivity().supportFragmentManager, tagFragment)
        }
    }
}

