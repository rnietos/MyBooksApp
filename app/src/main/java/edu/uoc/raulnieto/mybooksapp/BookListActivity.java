package edu.uoc.raulnieto.mybooksapp;


import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import edu.uoc.raulnieto.mybooksapp.model.Libro;
import edu.uoc.raulnieto.mybooksapp.model.LibroDatos;
import io.reactivex.annotations.NonNull;
import io.realm.Realm;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    //Adapter que utilizamos para mostrar la lista de libros
    private SimpleItemRecyclerViewAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        //Inicializamos canal de notificaciones.
        if (getIntent() != null && getIntent().getAction() != null) {
            int bookpos = getIntent().getIntExtra("book_position",0);
            Log.i("TAG", "Dato recibido notificación: " + bookpos);
            if (getIntent().getAction().equalsIgnoreCase(LibroDatos.ACTION_BORRAR)) {
                // Acción eliminar de la notificación recibida
                Log.d("TAG","Eliminar" + bookpos);
                LibroDatos.eliminar(bookpos);
                Toast.makeText(this, "Acción eliminar", Toast.LENGTH_SHORT).show();
            } else if (getIntent().getAction().equalsIgnoreCase(LibroDatos.ACTION_VER)) {
                // Acción reenviar de la notificación recibida
                Log.d("TAG","Ver" + bookpos);
                actualizaNotificacion(bookpos);
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //notificationManager.cancel(LibroDatos.NOTIF_ID);
            notificationManager.cancel(LibroDatos.TAGNOTIF_ID,LibroDatos.NOTIF_ID);
            Log.d("TAG","Borrar Notif");
        }


        //Inicializamos mensajería FireBase
       // FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        //Inicializamos la base de datos local
        Realm.init(getApplicationContext());
        //Estableemos la conexion con la base de datos
        LibroDatos.conexion = Realm.getDefaultInstance();

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

        //Preparamos menu lateral
        //Opciones de menu
        PrimaryDrawerItem opcion1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.Opcion1);
        PrimaryDrawerItem opcion2 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.Opcion2);
        PrimaryDrawerItem opcion3 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.Opcion3);
        //SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.Opcion2);
        //Cabecera
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ic_launcher_background)
                .addProfiles(
                        new ProfileDrawerItem().withName("Raul Nieto").withEmail("rnieto@uoc.edu").withIcon(getResources().getDrawable(R.drawable.libro))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

//create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        opcion1,
                        opcion2,
                        opcion3
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Intent sendIntent;
                        File f;
                        Uri uri;
                        switch (position){
                            case 1:
                                //Cuando seleccionan la opción de compartir datos
                                    f = nuevoFicheroImagen();
                                    uri = FileProvider.getUriForFile(BookListActivity.this, "edu.uoc.raulnieto.fileprovider", f);
                                    sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Aplicació Android sobre llibres");
                                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    sendIntent.setType("image/*");
                                    startActivity(Intent.createChooser(sendIntent, "Compartir con"));
                                break;
                            case 2:
                                //Copiar el texto al portapapeles
                                ClipboardManager clipboard = (ClipboardManager)
                                        getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("texto", "Aplicació Android sobre llibres");
                                clipboard.setPrimaryClip(clip);
                                Snackbar.make(view, "Texto copiado al portapapeles", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                break;
                            default: break;
                            case 3:
                                f = nuevoFicheroImagen();
                                uri = FileProvider.getUriForFile(BookListActivity.this, "edu.uoc.raulnieto.fileprovider", f);
                                sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.setPackage("com.whatsapp");
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "Aplicació Android sobre llibres");
                                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                sendIntent.setType("image/*");
                                startActivity(sendIntent);
                                break;
                        }
                        return false;
                    }
                })
                .build();




        if (findViewById(R.id.item_detail_container) != null) {
            // Cuando es una tablet (res/values-w900dp).
            // Lo indicamos en la variable que utilizamos para controlarlo.
            mTwoPane = true;
        }



        //Empezamos el proceso de carga de los elementos de la lista
        iniciaCarga(false);

        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Controlamos el SwipeRefreshLayout, y definimos que hacer al actualizar
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Toast.makeText(getApplicationContext(),"Actualizando",Toast.LENGTH_LONG).show();
                iniciaCarga(true);
                swipeContainer.setRefreshing(false);
            }
        });

    }

    private File nuevoFicheroImagen(){
        try {
        //Generamos el adjunto que enviaremos
        File imagePath = new File(getApplicationContext().getFilesDir(),"");
        File f = new File(imagePath, "adjunto.png");
        //si el archuvos que adjuntamos no existe lo preparamos.
        if (!f.exists()) {
            Log.d("TAG","CREADO");
            f.createNewFile();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }
        return f;
        } catch (IOException e){
            Log.d("TAG", e.getMessage());
        }
        //Preparamos la Uri que vamos a compartir, a partir del adjunto que hemos generado
        return null;
    }

     private void actualizaNotificacion(int bookpos){
        if (bookpos < LibroDatos.listalibros.size()) {
            if (mTwoPane) {
                //Caso para tablet, que actualiza el panel de Detail
                Bundle arguments = new Bundle();
                arguments.putInt(BookDetailFragment.ARG_ITEM_ID, bookpos);
                BookDetailFragment fragment = new BookDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                //Caso para móvil, que llama a la nueva actividad de Detail
                Intent intent = new Intent(this, BookDetailActivity.class);
                intent.putExtra(BookDetailFragment.ARG_ITEM_ID, bookpos);
                startActivity(intent);
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        iniciaCarga(false);
    }

    private  void iniciaCarga(boolean actualiza){
        /* Control de Internet
         * En caso de que no haya conexión a la RED no se realizará la carga de los datos
         * desde el servidor FireBase
         * El valor de actualiza es el que indica si estamos creando el adaptador o se
         * refresca la lista
         * */
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        if (actNetInfo != null && actNetInfo.isConnected() && actNetInfo.isAvailable()) {
            Toast.makeText(this, "Red activada, cargando datos desde FireBase", Toast.LENGTH_LONG).show();
            cargaDatosFirebase(actualiza);
        }
        else{
            Toast.makeText(this, "No hay acceso a Internet, se carga la información de la base de datos local", Toast.LENGTH_LONG).show();

            cargarRealm(actualiza);
        }
    }

    private void cargaDatosFirebase(final boolean actualiza){
        //Nuevas característivas de Firebase en el proyecto
        FirebaseApp.initializeApp(BookListActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("rnieto@uoc.edu", "Pa$$w0rd")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference().child("books");
                            // Leemos la información de la Base de Datos
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<ArrayList<Libro>> genericTypeIndicator =new GenericTypeIndicator<ArrayList<Libro>>(){};
                                    //Obtenemos el listado y lo asignamos a lalista que utilizamos ne la aplicación
                                    LibroDatos.listalibros=dataSnapshot.getValue(genericTypeIndicator);
                                    for (int i=0;i<LibroDatos.listalibros.size();i++) {
                                        //Actualizamos el id puesto que no esta en Firebase
                                        LibroDatos.listalibros.get(i).setId(i);
                                        if (!LibroDatos.exists(LibroDatos.listalibros.get(i))){
                                            //Si el libro no existe lo añadimos a la base de datos local
                                            LibroDatos.conexion.beginTransaction();
                                            LibroDatos.conexion.insert(LibroDatos.listalibros.get(i));
                                            LibroDatos.conexion.commitTransaction();
                                        }
                                    }
                                    //El parámetro actualiza indica si es una nueva carga, o actualizar la lista
                                    if (!actualiza)
                                        cargaReciclerView();
                                    else
                                        adaptador.setItems(LibroDatos.listalibros);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    //Si no se ha posidido leer del servidor firebase
                                    Toast.makeText(BookListActivity.this, "No se leído desde el servidor, se carga la información de la base de datos local", Toast.LENGTH_LONG).show();
                                    cargarRealm(actualiza);
                                    Log.i("TAG", "Error de lectura.", error.toException());
                                }
                            });

                        } else {
                            //Error en ela conexión a internet
                            Toast.makeText(BookListActivity.this, "ERROR en la conexión a Firebase, se carga la información de la base de datos local", Toast.LENGTH_SHORT).show();
                            Log.i("TAG", "Error conexion firebase");
                            cargarRealm(actualiza);
                        }
                    }
                });

    }

    //Función encargada de obtener los datos desde la base de datos local, y rellenar la lista
    private void cargarRealm(boolean actualiza){
        /*LibroDatos.conexion.beginTransaction();
        //Recuperamos todos los libros de a base de datos
        final RealmResults<Libro> ls = LibroDatos.conexion.where(Libro.class).findAll();
        LibroDatos.conexion.commitTransaction();*/
        LibroDatos.listalibros = (ArrayList)LibroDatos.getBooks();
        Log.d("TAG","datos" + LibroDatos.listalibros.size());
        //El parámetro actualiza indica si es una nueva carga, o actualizar la lista
        if (!actualiza)
            cargaReciclerView();
        else
            adaptador.setItems(LibroDatos.listalibros);
    }

    //Función que genera el recyclerview
    void cargaReciclerView(){
        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //Preparamos los datos a mostrar indicando de donde se obtienen los datos
        // y el número de paneles
        adaptador = new SimpleItemRecyclerViewAdapter(this, LibroDatos.listalibros, mTwoPane);
        recyclerView.setAdapter(adaptador);
    }

    // Este es el adaptador que rellena la lista a partir de nuestra lista de libros
    public static class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        private final BookListActivity mParentActivity;
        private List<Libro> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Control cuando seleccionan un libro
                Libro item = (Libro) view.getTag();
                //Independientemente de los paneles guardamos
                // el parámetro (constante ARG_ITEM_ID) con el identificador Libro que se haya seleccionado
                if (mTwoPane) {
                    //Caso para tablet, que actualiza el panel de Detail
                    Bundle arguments = new Bundle();
                    arguments.putInt(BookDetailFragment.ARG_ITEM_ID, item.getId());
                    BookDetailFragment fragment = new BookDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    //Caso para móvil, que llama a la nueva actividad de Detail
                    Context context = view.getContext();
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra(BookDetailFragment.ARG_ITEM_ID, item.getId());
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(BookListActivity parent,
                                      List<Libro> items,
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
            holder.titulolista.setText(mValues.get(position).getTitle());
            holder.autorlista.setText(mValues.get(position).getAuthor());

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
        //Método que actuliza los datos de lista.
        public void setItems(List<Libro> items) {
            Log.d("TAG", "actualizando");
            mValues = items;
            notifyDataSetChanged();
            //Indicamos que se ha actualizado la lista y que se tiene que refrescar
        }
    }
}
