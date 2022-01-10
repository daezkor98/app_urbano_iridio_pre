package com.urbanoexpress.iridio.view;

import java.util.ArrayList;
import java.util.List;

import com.urbanoexpress.iridio.model.entity.ParadaProgramada;
import com.urbanoexpress.iridio.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio.ui.model.ParadaProgramadaItem;

/**
 * Created by mick on 30/05/16.
 */
public interface PlanDeViajeView extends BaseView2 {

    void showDatosPlanDeViaje(PlanDeViaje planDeViaje);
    void showDatosParadasProgramadas(List<ParadaProgramadaItem> paradasProgramadas);
    void showNoDatosPlanDeViaje();
    void changeTextLabelPlaca(String placa);
    void setImagenesSincronizadas(String imagenesSincronizadas);
    void setIncidentesSincronizados(String incidentesSincronizados);
    void hideFormEditarPlaca();
    void clearFormEditarPlaca();
    void setEnabledBtnInciarRuta(boolean enabled, int drawable);
    void setEnabledBtnTerminarRuta(boolean enabled, int drawable);
    void setVisibilityBtnEditarPlaca(int visibility);
    void clearPlanDeViaje();

    void setVisibilitySwipeRefreshLayout(boolean visible);

    void navigateToIniciarTerminarRutaDialog(PlanDeViaje planDeViaje,
                                             ArrayList<ParadaProgramada> paradasProgramadas);
    void navigateToDetailParadaProgramadaDialog(PlanDeViaje planDeViaje,
                                                ParadaProgramada paradaProgramada);
    void navigateToSelectOrigenDialog(ArrayList<PlanDeViaje> planDeViajes);
    void navigateToAsignarDespachoDialog();
    void navigateToParadasOnMapActivity(PlanDeViaje planDeViaje,
                                        ArrayList<ParadaProgramada> paradasProgramadas,
                                        String latitude, String longitude);
}
