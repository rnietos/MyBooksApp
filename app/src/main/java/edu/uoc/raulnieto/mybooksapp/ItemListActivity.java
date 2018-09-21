package edu.uoc.raulnieto.mybooksapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uoc.raulnieto.mybooksapp.model.BookItem;
import edu.uoc.raulnieto.mybooksapp.model.BookItemDatos;

import java.util.List;

/**
 * Actividad que muestra la lista de libros
 * Se muestra de forma diferente dependiendo de si estamos en un movíl
 * o en una tablet.
 * Para móvil muestra una lista de elementos que cuando son seleccionados
 * los muestra mediante la actividad ItemDetalActivity.
 * En una tablet muestra la lista y los detalles utilizando dos paneles verticales.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Controla número de paneles a mostrar
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            // Cuando es una tablet (res/values-w900dp).
            // Lo indicamos en la variable que utilizamos para controlarlo.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //Preparamos los datos a mostrar indicando de donde se obtienen los datos
        // y el número de paneles
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, BookItemDatos.ITEMS, mTwoPane));
    }

    // Este es el adaptador que rellena la lia a partir de nuestra lista de libros
    public static class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        private final ItemListActivity mParentActivity;
        private final List<BookItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Control cuando seleccionan un libro
                BookItem item = (BookItem) view.getTag();
                //Independientemente de los paneles guardamos
                // el parámetro (constante ARG_ITEM_ID) con el identificador Libro que se haya seleccionado
                if (mTwoPane) {
                    //Caso para tablet, que actualiza el panel de Detail
                    Bundle arguments = new Bundle();
                    arguments.putInt(ItemDetailFragment.ARG_ITEM_ID, item.getIdentificador());
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    //Caso para móvil, que llama a la nueva actividad de Detail
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.getIdentificador());
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<BookItem> items,
                                      boolean twoPane) {
            //Constructor del adaptador
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(""+mValues.get(position).getIdentificador());
            holder.mContentView.setText(mValues.get(position).getTitulo());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
