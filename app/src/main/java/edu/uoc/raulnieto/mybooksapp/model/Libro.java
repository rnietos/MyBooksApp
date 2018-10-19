package edu.uoc.raulnieto.mybooksapp.model;

import io.realm.RealmObject;

public class Libro extends RealmObject {
    private int id;
    private String title;
    private String author;
    private String publicationdate;
    private String description;
    private String urlimage;

    public Libro(){}
    //Constructor de la clase con todos los parámetros.

    public Libro(int identificador, String titulo, String autor, String dataPublicacion, String descripcion, String URL) {
        this.id = identificador;
        this.title = titulo;
        this.author = autor;
        setpublicationdate(dataPublicacion);
        this.description = descripcion;
        this.urlimage = URL;
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
    public String getPublicationdate() {
        return publicationdate;
    }
    public void setpublicationdate(String dataPublicacion) {
            publicationdate = dataPublicacion;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String descripcion) {
        description = descripcion;
    }
    public String getUrlimage() {
        return urlimage;
    }
    public void setUrlimage(String URL) {
        urlimage = URL;
    }
}
