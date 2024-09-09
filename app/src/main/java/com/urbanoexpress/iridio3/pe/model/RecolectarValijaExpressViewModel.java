package com.urbanoexpress.iridio3.pe.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecolectarValijaExpressViewModel extends ViewModel {

    public enum Step {
        SCAN_BARCODE,
        TAKE_PHOTO,
        COMPLETED
    }

    private final MutableLiveData<Step> nextStep = new MutableLiveData<>();
    private final MutableLiveData<String> idServicio = new MutableLiveData<>();
    private final MutableLiveData<String> idRecoleccion = new MutableLiveData<>();
    private final MutableLiveData<String> barraRecoleccion = new MutableLiveData<>();
    private final MutableLiveData<String> barraValija = new MutableLiveData<>();
    private final MutableLiveData<String> shipperName = new MutableLiveData<>();

    public void setNextStep(Step step) {
        nextStep.setValue(step);
    }

    public LiveData<Step> getNextStep() {
        return nextStep;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio.setValue(idServicio);
    }

    public LiveData<String> getIdServicio() {
        return idServicio;
    }

    public void setIdRecoleccion(String idRecoleccion) {
        this.idRecoleccion.setValue(idRecoleccion);
    }

    public LiveData<String> getIdRecoleccion() {
        return idRecoleccion;
    }

    public void setBarraRecoleccion(String barraRecoleccion) {
        this.barraRecoleccion.setValue(barraRecoleccion);
    }

    public LiveData<String> getBarraRecoleccion() {
        return barraRecoleccion;
    }

    public void setBarraValija(String barraValija) {
        this.barraValija.setValue(barraValija);
    }

    public LiveData<String> getBarraValija() {
        return barraValija;
    }

    public LiveData<String> getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName.setValue(shipperName);
    }

    public void newOperation() {
        nextStep.setValue(Step.SCAN_BARCODE);
        idServicio.setValue("");
        idRecoleccion.setValue("");
        barraRecoleccion.setValue("");
        barraValija.setValue("");
        shipperName.setValue("");
    }
}
