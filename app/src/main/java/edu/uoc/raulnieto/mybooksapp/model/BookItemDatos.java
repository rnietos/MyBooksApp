package edu.uoc.raulnieto.mybooksapp.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookItemDatos {
    /**
     * Creamos una lista de elementos utilizando la clase BookItem, es la que utilizaremos
     * para rellenar inicialmente la aplicación.
     */
    public static final List<BookItem> ITEMS = new ArrayList<BookItem>();

    /**
     * Mapa que utilizaremos para opteber los datos para mostar en la lista
     */
    public static final Map<Integer, BookItem> ITEM_MAP = new HashMap<Integer, BookItem>();

    private static final int COUNT = 10;

    static {
        // Añadimos los elementos en la lista
        for (int i = 1; i <= COUNT; i++) {
            addItem(createBookItem(i));
        }
    }

    private static void addItem(BookItem item) {
        //Añadimos el elemento a la lista y al mapa
        ITEMS.add(item);
        ITEM_MAP.put(item.getIdentificador(), item);
    }

    private static BookItem createBookItem(int position) {
        //Método que crea el libro a añadir con todos los datos.
        return new BookItem(position,"Title"+position,"Author"+position, null,"Descripción"+position,"URL"+position);
    }
}
