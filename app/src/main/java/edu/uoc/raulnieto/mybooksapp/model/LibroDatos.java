package edu.uoc.raulnieto.mybooksapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class LibroDatos {

    //Identificador del canal de notificaciones
    public static String CHANNEL_ID = "canalFirebase";

    //Bornoes de las notificaciones.
    public static String ACTION_BORRAR = "BORRAR";
    public static String ACTION_VER = "VER DETALLE";

    public static ArrayList<Libro> listalibros;
    //Definimos la variable de conexión
    public static Realm conexion;
    /*public static RealmConfiguration config = new RealmConfiguration.Builder().name("milibros.realm").build();
        Por si necesitamos cambiar la configuración de la base de datos
    */
    //Función para devolver la lista
    public static List<Libro> getBooks(){
        /*List<Libro> lTemporal = new ArrayList<Libro>();
        conexion.beginTransaction();
        final RealmResults<Libro> ls = LibroDatos.conexion.where(Libro.class).findAll();
        //Recuperamos todos los elementos, y con ellos rellenamos el listado
        LibroDatos.conexion.commitTransaction();
        for(int i=0;i<ls.size();i++) {
            Libro lib = new Libro(i, ls.get(i).getTitle(),ls.get(i).getAuthor(),ls.get(i).getPublicationdate(),ls.get(i).getDescription(),ls.get(i).getUrlimage());
            Log.d("TAG","Libros: " + lib.getAuthor());
            lTemporal.add(lib);
        }*/
        return   conexion.copyFromRealm(LibroDatos.conexion.where(Libro.class).findAll());
    }
    //Función para devolver la lista
   /* public static List<Libro> getBooks(){
        return LibroDatos.conexion.where(Libro.class).findAll();
    }*/

    //Comprueba si existe un libro
    public static  boolean exists(Libro libro){
        RealmResults<Libro> l = conexion.where(Libro.class).equalTo("title", libro.getTitle()).findAll();
        //Devuelve resultado en función de si encuentra el titulo o no
        if (l.size() > 0)
            return true;
        else
            return false;
    }

    //Eliminamos el libro
    public static  void eliminar(int  bookpos){
        RealmResults<Libro> l;
        if (bookpos < LibroDatos.listalibros.size()) {
            conexion.beginTransaction();
            l = conexion.where(Libro.class).equalTo("title", LibroDatos.listalibros.get(bookpos).getTitle()).findAll();
            l.deleteAllFromRealm();
            conexion.commitTransaction();
        }
    }

    //Clase asincrona para cargar la imagen, recibe el imageview en que la cargará a partir de la URL
    public static class cargaImagendeURL extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public cargaImagendeURL(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



}
