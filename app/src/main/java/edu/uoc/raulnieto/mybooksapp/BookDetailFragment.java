package edu.uoc.raulnieto.mybooksapp;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import edu.uoc.raulnieto.mybooksapp.model.Libro;
import edu.uoc.raulnieto.mybooksapp.model.LibroDatos;

/**
 * Fragment para cada elemento, en la parte Detail.
 * Se utiliza de forma indistinta cuando se utiliza en una tablet (ItemListActivity)
 * o en un móvil {ItemDetailActivity}
  */
public class BookDetailFragment extends Fragment {
    /**
     * item_id es el nombre del parámetro que contendrá el identificador
     * del libro que queramos mostar, se guarda cómo parámetro para
     * utilizarlo desde distintas actividades
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * Libro que mostraremos.
     */
    private Libro mItem;

    /**
     * Constructor de la clase
     */
    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Si existe el parámetro lo cargamos y utilizamos para obtener el libro
            mItem = LibroDatos.listalibros.get(getArguments().getInt(ARG_ITEM_ID));
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mItem.getTitle());
        }

        View rootView = inflater.inflate(R.layout.book_item_detail, container, false);

        // Si hay un libro lo mostramos la etiqueta.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.libro_autor_detail)).setText(mItem.getAuthor());
            ((TextView) rootView.findViewById(R.id.libro_fecha_detail)).setText(mItem.getPublicationdate());
            ((TextView) rootView.findViewById(R.id.libro_descripcion_detail)).setText(mItem.getDescription());
            ImageView cImagen = ((ImageView) rootView.findViewById(R.id.libro_imagen_detail));
            Log.d("TAG",mItem.getUrlimage());
            new LibroDatos.cargaImagendeURL(cImagen).execute(mItem.getUrlimage());
        }

        return rootView;
    }
}
