package edu.uoc.raulnieto.mybooksapp.model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Libro {
    private int id;
    private String title;
    private String author;
    private Date publication_date;
    private String description;
    private String url_image;

    Libro(){}
    //Constructor de la clase con todos los parámetros.
    public Libro(int identificador, String titulo, String autor, String dataPublicacion, String descripcion, String URL) {
        id = identificador;
        title = titulo;
        author = autor;
        setpublication_date(dataPublicacion);
        description = descripcion;
        url_image = URL;
    }

    //Get y sets de las propiedades.
    public int getId() {
        return id;
    }
    public void setId(int identificador) {
        id = identificador;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String titulo) {
        title = titulo;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String autor) {
        author = autor;
    }
    public Date getPublication_date() {
        return publication_date;
    }
    public void setpublication_date(String dataPublicacion) {
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
        try {
            publication_date = formatoDelTexto.parse(dataPublicacion);
        } catch (ParseException e) {
            publication_date = null;
            e.printStackTrace();
        }
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String descripcion) {
        description = descripcion;
    }
    public String getURL() {
        return url_image;
    }
    public void setURL(String URL) {
        url_image = URL;
    }
}
