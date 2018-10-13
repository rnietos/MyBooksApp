package edu.uoc.raulnieto.mybooksapp.model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Libro {
    private int id;
    private String title;
    private String author;
    private Date publicationdate;
    private String description;
    private String urlimage;

    Libro(){}
    //Constructor de la clase con todos los parámetros.
    public Libro(int identificador, String titulo, String autor, String dataPublicacion, String descripcion, String URL) {
        id = identificador;
        title = titulo;
        author = autor;
        setpublicationdate(dataPublicacion);
        description = descripcion;
        urlimage = URL;
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
    public Date getPublicationdate() {
        return publicationdate;
    }
    public void setpublicationdate(String dataPublicacion) {
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
        try {
            publicationdate = formatoDelTexto.parse(dataPublicacion);
            Log.d("TAG","--"+publicationdate);
        } catch (ParseException e) {
            publicationdate = null;
            e.printStackTrace();
            Log.d("TAG","ERROR: "+publicationdate);
        }
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String descripcion) {
        description = descripcion;
    }
    public String getURL() {
        return urlimage;
    }
    public void setURL(String URL) {
        urlimage = URL;
    }
}
