package edu.uoc.raulnieto.mybooksapp.model;

import java.util.Date;

public class BookItem {

    //Propiedades de la clase, privadas para utilizar la encapsulación
    private int Identificador;
    private String Titulo;
    private String Autor;
    private Date DataPublicacion;
    private String Descripcion;
    private String URL;

    //Constructor de la clase con todos los parámetros.
    public BookItem(int identificador, String titulo, String autor, Date dataPublicacion, String descripcion, String URL) {
        Identificador = identificador;
        Titulo = titulo;
        Autor = autor;
        DataPublicacion = dataPublicacion;
        Descripcion = descripcion;
        this.URL = URL;
    }

    //Get y sets de las propiedades.
    public int getIdentificador() {
        return Identificador;
    }
    public void setIdentificador(int identificador) {
        Identificador = identificador;
    }
    public String getTitulo() {
        return Titulo;
    }
    public void setTitulo(String titulo) {
        Titulo = titulo;
    }
    public String getAutor() {
        return Autor;
    }
    public void setAutor(String autor) {
        Autor = autor;
    }
    public Date getDataPublicacion() {
        return DataPublicacion;
    }
    public void setDataPublicacion(Date dataPublicacion) {
        DataPublicacion = dataPublicacion;
    }
    public String getDescripcion() {
        return Descripcion;
    }
    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }
    public String getURL() {
        return URL;
    }
    public void setURL(String URL) {
        this.URL = URL;
    }
}
