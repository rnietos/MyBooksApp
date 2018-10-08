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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import edu.uoc.raulnieto.mybooksapp.model.BookItem;
import edu.uoc.raulnieto.mybooksapp.model.BookItemDatos;
import edu.uoc.raulnieto.mybooksapp.model.Libro;
import edu.uoc.raulnieto.mybooksapp.model.LibroDatos;



import java.util.ArrayList;
import java.util.List;

/**
 * Actividad que muestra la lista de libros
 * Se muestra de forma diferente dependiendo de si estamos en un movíl
 * o en una tablet.
 * Para móvil muestra una lista de elementos que cuando son seleccionados
 * los muestra mediante la actividad ItemDetalActivity.
 * En una tablet muestra la lista y los detalles utilizando dos paneles verticales.
 */
public class BookListActivity extends AppCompatActivity {

    /**
     * Controla número de paneles a mostrar
     */
    private boolean mTwoPane;
    //Variables relacionadas con FireBase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        //Nuevas característivas de Firebase en el proyecto
        FirebaseApp.initializeApp(BookListActivity.this);

        FirebaseDatabase basededatos;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("rnieto@uoc.edu", "Pa$$w0rd")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("TAG", "Completada autenticación");
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            database = FirebaseDatabase.getInstance();
                            Log.i("TAG", "Identificado");
                            DatabaseReference myRef = database.getReference().child("books");
                            // Read from the database
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<ArrayList<Libro>> genericTypeIndicator =new GenericTypeIndicator<ArrayList<Libro>>(){};
                                    LibroDatos.listalibros=dataSnapshot.getValue(genericTypeIndicator);
                                    for (int i=0;i<LibroDatos.listalibros.size();i++) {
                                        Log.i("TAG", "Value is: " + LibroDatos.listalibros.get(i).getTitle());
                                    }
                                    /*View recyclerView = findViewById(R.id.item_list);
                                    assert recyclerView != null;
                                    setupRecyclerView((RecyclerView) recyclerView);*/
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.i("TAG", "Failed to read value.", error.toException());
                                }
                            });

                        } else {
                            Log.i("TAG", "Error conexion firebase");
                        }
                    }
                });


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


        FirebaseApp.initializeApp(BookListActivity.this);
        //Inicializamos las clase necesarias para conectar a FireBAse
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        mAuth.signInWithEmailAndPassword("rnieto@uoc.edu", "Pa$$w0rd")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String mensaje;
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mensaje="Identificado";
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            mensaje="signInWithEmail:ERROR " + task.getException();
                        }
                        Toast.makeText(getApplicationContext(), mensaje,
                                Toast.LENGTH_LONG).show();

                        // ...
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
        private final BookListActivity mParentActivity;
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
                    arguments.putInt(BookDetailFragment.ARG_ITEM_ID, item.getIdentificador());
                    BookDetailFragment fragment = new BookDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    //Caso para móvil, que llama a la nueva actividad de Detail
                    Context context = view.getContext();
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra(BookDetailFragment.ARG_ITEM_ID, item.getIdentificador());
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(BookListActivity parent,
                                      List<BookItem> items,
                                      boolean twoPane) {
            //Constructor del adaptador
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            //Dependiendo del valor del parámetro viewType utilizamos un layout diferente
            //esto determina que podamos usar diferentes layouts para pares o impares
            if (viewType == 1) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_list_content, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_list_content_pares, parent, false);
            }
            return new ViewHolder(view);
        }

        @Override
        public int getItemViewType(int position){
            //Cómo queremos un layout distinto para pares e impares, aquí utilizamos
            //position que nos indica la posición del elemento de la lista para
            //gestionar el viewType de la función onCreateViewHolder
            return position % 2;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.titulolista.setText(mValues.get(position).getTitulo());
            holder.autorlista.setText(mValues.get(position).getAutor());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView titulolista;
            final TextView autorlista;

            ViewHolder(View view) {
                super(view);
                titulolista = (TextView) view.findViewById(R.id.id_titulo);
                autorlista = (TextView) view.findViewById(R.id.autor);
            }
        }
    }
}
