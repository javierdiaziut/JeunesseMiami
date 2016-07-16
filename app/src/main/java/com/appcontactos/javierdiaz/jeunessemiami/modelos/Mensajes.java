package com.appcontactos.javierdiaz.jeunessemiami.modelos;

/**
 * Created by SinAsignarT1 on 12/07/2016.
 */
public class Mensajes {

    private int id;
    private int tipo;
    private String descripcion;
    private String fecha;
    private int valido;
    private String imagen;
    private String link_video;
    private boolean checked;

    public Mensajes(int id, int tipo, String descripcion, String fecha, int valido, String imagen, String link_video, boolean checked) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.valido = valido;
        this.imagen = imagen;
        this.link_video = link_video;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getValido() {
        return valido;
    }

    public void setValido(int valido) {
        this.valido = valido;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getLink_video() {
        return link_video;
    }

    public void setLink_video(String link_video) {
        this.link_video = link_video;
    }
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }



}
