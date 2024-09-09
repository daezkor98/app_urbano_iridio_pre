package com.urbanoexpress.iridio3.pe.presenter;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.pe.model.RecolectarValijaExpressViewModel;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.DescargaRuta;
import com.urbanoexpress.iridio3.pe.model.entity.GuiaGestionada;
import com.urbanoexpress.iridio3.pe.model.entity.Imagen;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.ui.adapter.model.GalleryButtonItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.model.GalleryPhotoItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.model.GalleryWrapperItem;
import com.urbanoexpress.iridio3.pe.util.LocationUtils;
import com.urbanoexpress.iridio3.pe.util.MultimediaManager;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.view.RVEPhotoValijaView;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RVEPhotoValijaPresenter {

    private RVEPhotoValijaView view;
    private RecolectarValijaExpressViewModel model;

    private List<MultimediaManager.Picture> pictures = new ArrayList<>();
    private List<GalleryWrapperItem> gallery = new ArrayList<>();

    private final CompositeDisposable compositeDisposable;

    public RVEPhotoValijaPresenter(RVEPhotoValijaView view, RecolectarValijaExpressViewModel model) {
        this.view = view;
        this.model = model;
        this.compositeDisposable = new CompositeDisposable();
    }

    public void init() {
        GalleryButtonItem button = new GalleryButtonItem(R.drawable.ic_camera_grey);
        gallery.add(button);
        view.setGallery(gallery);
    }

    public void onDestroy() {
        compositeDisposable.dispose();
    }

    public void onTakePhotoClick(int position) {
        if (canTakePhoto()) {
            view.openCamera(model.getIdRecoleccion().getValue(), "");
        } else {
            view.showMsgError(view.getViewContext().getString(
                    R.string.activity_detalle_ruta_message_no_puede_tomar_foto));
        }
    }

    public void onDeletePhotoClick(int position) {
        view.showConfirmDeletePhotoModal(position);
    }

    public void onConfirmDeleteImageClick(int position) {
        gallery.remove(position);
        view.notifyGalleryItemRemoved(position);
    }

    public void onRegisterButtonClick() {
        if (pictures.size() == 0) {
            view.showMsgError("Debes tomar una foto como mínimo para continuar con la recolección.");
            return;
        }

        view.showProgressDialog("");
        Completable observable = saveGestionRecoleccion().concatWith(saveImages())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        compositeDisposable.add(observable.subscribeWith(new RecolectarValijaObserver()));
    }

    public void subscribeToCompletableProcessDataImage(Single<MultimediaManager.Picture> single) {
        view.showProgressDialog("");
        Single<MultimediaManager.Picture> observable = single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        compositeDisposable.add(observable.subscribeWith(new ProcessDataImageObserver()));
    }

    private boolean canTakePhoto() {
        return gallery.size() <= 10;
    }

    private Completable saveImages() {
        return Completable.create(emitter -> {
            pictures.stream().forEach(picture -> {
                Imagen imagen = new Imagen(
                        Preferences.getInstance().getString("idUsuario", ""),
                        picture.getFile().getName(),
                        picture.getFile().getParent() + File.separator,
                        Imagen.Tipo.GESTION_GUIA,
                        model.getIdServicio().getValue(),
                        String.valueOf(picture.getCreationDateTime()
                                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()),
                        String.valueOf(LocationUtils.getLatitude()),
                        String.valueOf(LocationUtils.getLongitude()),
                        "imagen",
                        model.getIdServicio().getValue(),
                        "3",
                        Data.Validate.VALID,
                        Data.Sync.PENDING);
                imagen.save();
            });

            emitter.onComplete();
        });
    }

    private Completable saveGestionRecoleccion() {
        return Completable.create(emitter -> {
            LocalDateTime localDateTime = LocalDateTime.now();
            String fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDateTime);
            String hora = DateTimeFormatter.ofPattern("HH:mm:ss").format(localDateTime);

            GuiaGestionada guiaGestionada = new GuiaGestionada(
                    Preferences.getInstance().getString("idUsuario", ""),
                    model.getIdServicio().getValue(),
                    "168",
                    Ruta.ZONA.URBANO,
                    "R",
                    "3",
                    fecha, hora,
                    String.valueOf(LocationUtils.getLatitude()),
                    String.valueOf(LocationUtils.getLongitude()),
                    "",
                    "",
                    "",
                    "0",
                    "0",
                    String.valueOf(GuiaGestionada.Recoleccion.VALIJA),
                    "",
                    "",
                    "",
                    "",
                    "0", "",
                    Data.Delete.NO,
                    Data.Validate.VALID,
                    1
            );
            guiaGestionada.save();


            Ruta ruta = RutaPendienteInteractor.selectRuta(model.getIdServicio().getValue(), "3");
            if (ruta != null) {
                ruta.setEstadoDescarga(Ruta.EstadoDescarga.GESTIONADO);
                ruta.setIdMotivo("168");
                ruta.setResultadoGestion(Ruta.ResultadoGestion.EFECTIVA_COMPLETA);
                ruta.setMostrarAlerta(0);
                ruta.save();
            }

            DescargaRuta descargaRuta = RutaPendienteInteractor.selectDescargaRuta(
                    model.getIdServicio().getValue(), "3");
            if (descargaRuta != null) {
                descargaRuta.setProcesoDescarga(DescargaRuta.Entrega.FINALIZADO);
                descargaRuta.save();
            }

            emitter.onComplete();
        });
    }

    private class ProcessDataImageObserver extends DisposableSingleObserver<MultimediaManager.Picture> {

        @Override
        public void onSuccess(@NonNull MultimediaManager.Picture picture) {
            view.dismissProgressDialog();
            pictures.add(picture);
            GalleryPhotoItem item = new GalleryPhotoItem(picture.getFile().getAbsolutePath());
            gallery.add(item);
            view.notifyGalleryItemInserted(gallery.size());
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
            view.dismissProgressDialog();
            view.showMsgError("Lo sentimos, ocurrió un error al procesar la foto, intentalo de nuevo.");
        }
    }

    private class RecolectarValijaObserver extends DisposableCompletableObserver {

        @Override
        public void onComplete() {
            sendOnDescargaFinalizadaReceiver();
            view.dismissProgressDialog();
            model.setNextStep(RecolectarValijaExpressViewModel.Step.COMPLETED);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
            view.dismissProgressDialog();
            view.showMsgError("Lo sentimos, ocurrió un error al guardar las fotos, intentalo de nuevo.");
        }
    }

    /**
     * Receiver
     *
     * {@link RutaPendientePresenter#descargaFinalizadaReceiver}
     * {@link RutaGestionadaPresenter#descargaFinalizadaReceiver}
     */
    private void sendOnDescargaFinalizadaReceiver() {
        Ruta ruta = new Ruta();
        ruta.setIdServicio(model.getIdServicio().getValue());
        ruta.setTipo("R");
        ruta.setLineaNegocio("3");
        ruta.setFlagScanPck("0");
        view.sendBroadcastGestionValijaFinalizada(new ArrayList<>(Arrays.asList(ruta)));
    }
}
