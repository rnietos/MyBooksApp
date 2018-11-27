package edu.uoc.raulnieto.mybooksapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.NavUtils;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Actividad para los móviles en los que se muestra la lista de los elementos
 */
public class BookDetailActivity extends AppCompatActivity {

    FloatingActionButton fab = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mostramos el webview cargando la página del sistema
                WebView webView = findViewById(R.id.visor_web);
                webView.setVisibility(View.VISIBLE);
                webView.setWebViewClient(new ResultadoWebClient());
                webView.loadUrl("file:///android_asset/form.html");
               fab.hide();
            }
        });

        // Botones del actionBar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState: Comprobación de si se ha guardado el estado de la aplicación
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(BookDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(BookDetailFragment.ARG_ITEM_ID,0));
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Control de la barra de navegación y que botón activan.
        int id = item.getItemId();
        if (id == android.R.id.home) {

            NavUtils.navigateUpTo(this, new Intent(this, BookListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class ResultadoWebClient extends WebViewClient {
        String nombre;
        String numero;
        String fecha;
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Gestionamos los parámetros
            nombre = Uri.parse(url).getQueryParameter("name");
            numero = Uri.parse(url).getQueryParameter("num");
            fecha = Uri.parse(url).getQueryParameter("date");

            //Si no hay error mostramos mensaje, en caso contrario mostramos el mensaje
            String mensajeError = compruebaError();
            if (mensajeError.length()==0) {
                //Al no haber error mostramos el mensaje y visualizamos la vista principal y el botón
                Snackbar.make(view, "Gracias por su compra!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                view.setVisibility(View.GONE);
                fab.show();
                return true;
            }
            else{
                //En caso de error mostramos el mensaje y seguimos preguntando.
                 Toast.makeText(BookDetailActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //Función que comprueba si los datos son correctos.
        private String compruebaError(){
            String res = "";
            if (nombre == "") {
                res += "El nombre no puede estar en blanco\n\n";
            }
            if (numero == "") {
                res += "El número de cuenta no puede estar en blanco\n\n";
            }
            if (fecha == "") {
                res += "La fecha no puede estar vacia";
            }
            return res;
        }
    }


}
