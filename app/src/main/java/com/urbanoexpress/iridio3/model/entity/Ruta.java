package com.urbanoexpress.iridio3.model.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by mick on 07/07/16.
 */
public class Ruta extends SugarRecord implements Serializable {

    private static final long serialVersionUID = -4931237261866605984L;

    private String idUsuario;
    private String idServicio; // man_id_det
    private String idServicioRecoleccion; // man_id_det que indica si la guia (del tipo valija) tiene una recoleccion adjunta.
    private String idMotivo;
    private String idAgencia;
    private String idZona;
    private String idRuta;
    private String idGuia;
    private String idMedioPago;
    private String idCliente;
    private String idManifiesto;
    private String lineaNegocio;
    private String shiCodigo;
    private String fechaRuta;
    private int tipoZona;
    private String guia;
    private String tipo;
    private String secuencia;
    private String direccion;
    private String gpsLatitude;
    private String gpsLongitude;
    private String gpsRadio;
    private String distrito;
    private String shipper;
    private String centroActividad;
    private String estadoShipper;
    private String contacto;
    private String piezas;
    private String horarioEntrega; // Horario del PYP
    private long horarioAproximado; // Horario calculado mediante ubicacion de la guia
    private long horarioOrdenamiento; // Horario en que se realiza el ordamiento en la lista de pendientes
    private String telContactoGestion;
    private String nombreTelContactoGestion;
    private String telefono;
    private String celular;
    private String medioPago;
    private String importe;
    private String tipoEnvio;
    private String anotaciones;
    private String servicio_sms;
    private String habilitantes;
    private String idCHKUltimaGestion; // chk (9 => Entregado) de la ultima gestion de una guia con el mismo cliente y contrato
    private String gpsLatitudeUltimaGestion;
    private String gpsLongitudeUltimaGestion;
    private String solicitaKilometraje;
    private String requiereDatosCliente; // Flag para solicitar datos del cliente (Nombre y DNI) en la gestion efectiva de una guia
    private String guiaRequerimiento; // Flag que indica si una guia tiene un requerimiento activo
    private String guiaRequerimientoCHK;
    private String guiaRequerimientoMotivo;
    private String guiaRequerimientoComentario;
    private String guiaRequerimientoHorario;
    private String guiaRequerimientoNuevaDireccion; // Flag que indica si la guia con requerimiento tiene una nueva direccion
    private String premiosGestionGuia;
    private String firmaClienteGestionGuia; // Flag que indica si la guia necesita la firma del cliente de manera obligataria
    private String minimoFotosProductoGestionGuia;
    private String descripcion;
    private String observaciones;
    private String secuenciaRuteo;
    private int mostrarAlerta; // flag que indica si la guia/recoleccion debe ser alertada al usuario.

    private String idImagen;

    private int estadoDescarga;
    private int resultadoGestion;
    private int eliminado;

    /**
     * Variable para validar si los datos estan completos. Ejm:
     * En caso de que la guia(manifiesto) se generó desde el celular.
     */
    private int dataValidate;

    public Ruta() { }

    public Ruta(String idUsuario, String idServicio, String idServicioRecoleccion, String idMotivo,
                String idAgencia, String idZona, String idRuta, String idGuia, String idMedioPago,
                String idCliente, String idManifiesto, String lineaNegocio, String shiCodigo,
                String fechaRuta, int tipoZona, String guia, String tipo, String secuencia, String direccion,
                String gpsLatitude, String gpsLongitude, String gpsRadio, String distrito,
                String shipper, String centroActividad, String estadoShipper, String contacto,
                String piezas, String horarioEntrega, long horarioAproximado, long horarioOrdenamiento,
                String telContactoGestion, String nombreTelContactoGestion, String telefono, String celular,
                String medioPago, String importe, String tipoEnvio, String anotaciones,
                String servicio_sms, String habilitantes, String idCHKUltimaGestion,
                String gpsLatitudeUltimaGestion, String gpsLongitudeUltimaGestion,
                String solicitaKilometraje, String requiereDatosCliente, String guiaRequerimiento,
                String guiaRequerimientoCHK, String guiaRequerimientoMotivo, String guiaRequerimientoComentario,
                String guiaRequerimientoHorario, String guiaRequerimientoNuevaDireccion,
                String premiosGestionGuia, String firmaClienteGestionGuia, String minimoFotosProductoGestionGuia,
                String descripcion, String observaciones, String secuenciaRuteo, int mostrarAlerta,
                int estadoDescarga, int resultadoGestion, int eliminado, int dataValidate) {
        this.idUsuario = idUsuario;
        this.idServicio = idServicio;
        this.idServicioRecoleccion = idServicioRecoleccion;
        this.idMotivo = idMotivo;
        this.idAgencia = idAgencia;
        this.idZona = idZona;
        this.idRuta = idRuta;
        this.idGuia = idGuia;
        this.idMedioPago = idMedioPago;
        this.lineaNegocio = lineaNegocio;
        this.idCliente = idCliente;
        this.idManifiesto = idManifiesto;
        this.shiCodigo = shiCodigo;
        this.fechaRuta = fechaRuta;
        this.tipoZona = tipoZona;
        this.guia = guia;
        this.tipo = tipo;
        this.secuencia = secuencia;
        this.direccion = direccion;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
        this.gpsRadio = gpsRadio;
        this.distrito = distrito;
        this.shipper = shipper;
        this.centroActividad = centroActividad;
        this.estadoShipper = estadoShipper;
        this.contacto = contacto;
        this.piezas = piezas;
        this.horarioEntrega = horarioEntrega;
        this.horarioAproximado = horarioAproximado;
        this.horarioOrdenamiento = horarioOrdenamiento;
        this.telContactoGestion = telContactoGestion;
        this.nombreTelContactoGestion = nombreTelContactoGestion;
        this.telefono = telefono;
        this.celular = celular;
        this.medioPago = medioPago;
        this.importe = importe;
        this.tipoEnvio = tipoEnvio;
        this.anotaciones = anotaciones;
        this.servicio_sms = servicio_sms;
        this.habilitantes = habilitantes;
        this.idCHKUltimaGestion = idCHKUltimaGestion;
        this.gpsLatitudeUltimaGestion = gpsLatitudeUltimaGestion;
        this.gpsLongitudeUltimaGestion = gpsLongitudeUltimaGestion;
        this.solicitaKilometraje = solicitaKilometraje;
        this.requiereDatosCliente = requiereDatosCliente;
        this.guiaRequerimiento = guiaRequerimiento;
        this.guiaRequerimientoCHK = guiaRequerimientoCHK;
        this.guiaRequerimientoMotivo = guiaRequerimientoMotivo;
        this.guiaRequerimientoComentario = guiaRequerimientoComentario;
        this.guiaRequerimientoHorario = guiaRequerimientoHorario;
        this.guiaRequerimientoNuevaDireccion = guiaRequerimientoNuevaDireccion;
        this.premiosGestionGuia = premiosGestionGuia;
        this.firmaClienteGestionGuia = firmaClienteGestionGuia;
        this.minimoFotosProductoGestionGuia = minimoFotosProductoGestionGuia;
        this.descripcion = descripcion;
        this.observaciones = observaciones;
        this.secuenciaRuteo = secuenciaRuteo;
        this.mostrarAlerta = mostrarAlerta;
        this.estadoDescarga = estadoDescarga;
        this.resultadoGestion = resultadoGestion;
        this.eliminado = eliminado;
        this.dataValidate = dataValidate;
        this.idImagen = "";
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
    }

    public String getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(String idMotivo) {
        this.idMotivo = idMotivo;
    }

    public String getIdAgencia() {
        return idAgencia;
    }

    public void setIdAgencia(String idAgencia) {
        this.idAgencia = idAgencia;
    }

    public String getIdZona() {
        return idZona;
    }

    public void setIdZona(String idZona) {
        this.idZona = idZona;
    }

    public String getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public String getIdGuia() {
        return idGuia;
    }

    public void setIdGuia(String idGuia) {
        this.idGuia = idGuia;
    }

    public String getIdMedioPago() {
        return idMedioPago;
    }

    public void setIdMedioPago(String idMedioPago) {
        this.idMedioPago = idMedioPago;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdManifiesto() {
        return idManifiesto;
    }

    public void setIdManifiesto(String idManifiesto) {
        this.idManifiesto = idManifiesto;
    }

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

    public String getShiCodigo() {
        return shiCodigo;
    }

    public void setShiCodigo(String shiCodigo) {
        this.shiCodigo = shiCodigo;
    }

    public String getFechaRuta() {
        return fechaRuta;
    }

    public void setFechaRuta(String fechaRuta) {
        this.fechaRuta = fechaRuta;
    }

    public int getTipoZona() {
        return tipoZona;
    }

    public void setTipoZona(int tipoZona) {
        this.tipoZona = tipoZona;
    }

    public String getGuia() {
        return guia;
    }

    public void setGuia(String guia) {
        this.guia = guia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(String secuencia) {
        this.secuencia = secuencia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public String getGpsRadio() {
        return gpsRadio;
    }

    public void setGpsRadio(String gpsRadio) {
        this.gpsRadio = gpsRadio;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getCentroActividad() {
        return centroActividad;
    }

    public void setCentroActividad(String centroActividad) {
        this.centroActividad = centroActividad;
    }

    public String getEstadoShipper() {
        return estadoShipper;
    }

    public void setEstadoShipper(String estadoShipper) {
        this.estadoShipper = estadoShipper;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public String getHorarioEntrega() {
        return horarioEntrega;
    }

    public void setHorarioEntrega(String horarioEntrega) {
        this.horarioEntrega = horarioEntrega;
    }

    public long getHorarioAproximado() {
        return horarioAproximado;
    }

    public void setHorarioAproximado(long horarioAproximado) {
        this.horarioAproximado = horarioAproximado;
    }

    public long getHorarioOrdenamiento() {
        return horarioOrdenamiento;
    }

    public void setHorarioOrdenamiento(long horarioOrdenamiento) {
        this.horarioOrdenamiento = horarioOrdenamiento;
    }

    public String getTelContactoGestion() {
        return telContactoGestion;
    }

    public void setTelContactoGestion(String telContactoGestion) {
        this.telContactoGestion = telContactoGestion;
    }

    public String getNombreTelContactoGestion() {
        return nombreTelContactoGestion;
    }

    public void setNombreTelContactoGestion(String nombreTelContactoGestion) {
        this.nombreTelContactoGestion = nombreTelContactoGestion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getTipoEnvio() {
        return tipoEnvio;
    }

    public void setTipoEnvio(String tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
    }

    public String getAnotaciones() {
        return anotaciones;
    }

    public void setAnotaciones(String anotaciones) {
        this.anotaciones = anotaciones;
    }

    public String getServicio_sms() {
        return servicio_sms;
    }

    public void setServicio_sms(String servicio_sms) {
        this.servicio_sms = servicio_sms;
    }

    public String getHabilitantes() {
        return habilitantes;
    }

    public void setHabilitantes(String habilitantes) {
        this.habilitantes = habilitantes;
    }

    public String getIdCHKUltimaGestion() {
        return idCHKUltimaGestion;
    }

    public void setIdCHKUltimaGestion(String idCHKUltimaGestion) {
        this.idCHKUltimaGestion = idCHKUltimaGestion;
    }

    public String getGpsLatitudeUltimaGestion() {
        return gpsLatitudeUltimaGestion;
    }

    public void setGpsLatitudeUltimaGestion(String gpsLatitudeUltimaGestion) {
        this.gpsLatitudeUltimaGestion = gpsLatitudeUltimaGestion;
    }

    public String getGpsLongitudeUltimaGestion() {
        return gpsLongitudeUltimaGestion;
    }

    public void setGpsLongitudeUltimaGestion(String gpsLongitudeUltimaGestion) {
        this.gpsLongitudeUltimaGestion = gpsLongitudeUltimaGestion;
    }

    public String getSolicitaKilometraje() {
        return solicitaKilometraje;
    }

    public void setSolicitaKilometraje(String solicitaKilometraje) {
        this.solicitaKilometraje = solicitaKilometraje;
    }

    public String getRequiereDatosCliente() {
        return requiereDatosCliente;
    }

    public void setRequiereDatosCliente(String requiereDatosCliente) {
        this.requiereDatosCliente = requiereDatosCliente;
    }

    public String getGuiaRequerimiento() {
        return guiaRequerimiento;
    }

    public void setGuiaRequerimiento(String guiaRequerimiento) {
        this.guiaRequerimiento = guiaRequerimiento;
    }

    public String getGuiaRequerimientoCHK() {
        return guiaRequerimientoCHK;
    }

    public void setGuiaRequerimientoCHK(String guiaRequerimientoCHK) {
        this.guiaRequerimientoCHK = guiaRequerimientoCHK;
    }

    public String getGuiaRequerimientoMotivo() {
        return guiaRequerimientoMotivo;
    }

    public void setGuiaRequerimientoMotivo(String guiaRequerimientoMotivo) {
        this.guiaRequerimientoMotivo = guiaRequerimientoMotivo;
    }

    public String getGuiaRequerimientoComentario() {
        return guiaRequerimientoComentario;
    }

    public void setGuiaRequerimientoComentario(String guiaRequerimientoComentario) {
        this.guiaRequerimientoComentario = guiaRequerimientoComentario;
    }

    public String getGuiaRequerimientoHorario() {
        return guiaRequerimientoHorario;
    }

    public void setGuiaRequerimientoHorario(String guiaRequerimientoHorario) {
        this.guiaRequerimientoHorario = guiaRequerimientoHorario;
    }

    public String getGuiaRequerimientoNuevaDireccion() {
        return guiaRequerimientoNuevaDireccion;
    }

    public void setGuiaRequerimientoNuevaDireccion(String guiaRequerimientoNuevaDireccion) {
        this.guiaRequerimientoNuevaDireccion = guiaRequerimientoNuevaDireccion;
    }

    public String getPremiosGestionGuia() {
        return premiosGestionGuia;
    }

    public void setPremiosGestionGuia(String premiosGestionGuia) {
        this.premiosGestionGuia = premiosGestionGuia;
    }

    public String getFirmaClienteGestionGuia() {
        return firmaClienteGestionGuia;
    }

    public void setFirmaClienteGestionGuia(String firmaClienteGestionGuia) {
        this.firmaClienteGestionGuia = firmaClienteGestionGuia;
    }

    public String getMinimoFotosProductoGestionGuia() {
        return minimoFotosProductoGestionGuia;
    }

    public void setMinimoFotosProductoGestionGuia(String minimoFotosProductoGestionGuia) {
        this.minimoFotosProductoGestionGuia = minimoFotosProductoGestionGuia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getSecuenciaRuteo() {
        return secuenciaRuteo;
    }

    public void setSecuenciaRuteo(String secuenciaRuteo) {
        this.secuenciaRuteo = secuenciaRuteo;
    }

    public int getMostrarAlerta() {
        return mostrarAlerta;
    }

    public void setMostrarAlerta(int mostrarAlerta) {
        this.mostrarAlerta = mostrarAlerta;
    }

    public String getIdServicioRecoleccion() {
        return idServicioRecoleccion;
    }

    public void setIdServicioRecoleccion(String idServicioRecoleccion) {
        this.idServicioRecoleccion = idServicioRecoleccion;
    }

    public String getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(String idImagen) {
        this.idImagen = idImagen;
    }

    public int getEstadoDescarga() {
        return estadoDescarga;
    }

    public void setEstadoDescarga(int estadoDescarga) {
        this.estadoDescarga = estadoDescarga;
    }

    public int getResultadoGestion() {
        return resultadoGestion;
    }

    public void setResultadoGestion(int resultadoGestion) {
        this.resultadoGestion = resultadoGestion;
    }

    public int getEliminado() {
        return eliminado;
    }

    public void setEliminado(int eliminado) {
        this.eliminado = eliminado;
    }

    public int getDataValidate() {
        return dataValidate;
    }

    public void setDataValidate(int dataValidate) {
        this.dataValidate = dataValidate;
    }

    public interface ZONA {
        int URBANO = 100;
        int RURAL = 101;
    }

    public interface Tipo {
        int ENTREGA     = 0;
        int RECOLECCION = 1;
        int NO_DEFINIDO = 2;
    }

    public interface TipoEnvio {
        String PAQUETE              = "P";
        String VALIJA               = "V";
        String LIQUIDACION          = "L";
        String DEVOLUCION           = "D";
        String LOGISTICA_INVERSA    = "I";
        String RECOLECCION_EXPRESS  = "E";
        String SELLER               = "S";
        String COUNTER              = "C";
    }

    public interface EstadoDescarga {
        int PENDIENTE   = 10;
        int GESTIONADO  = 20;
    }

    public interface ResultadoGestion {
        int NO_DEFINIDO = 0; // Inicializador
        int EFECTIVA_COMPLETA = 1; // Entregado, Recolectado
        int EFECTIVA_PARCIAL = 3; // Entrega Parcial, Devolucion Parcial
        int NO_EFECTIVA = 2; // No Entregado, No Recolectado
    }
}
