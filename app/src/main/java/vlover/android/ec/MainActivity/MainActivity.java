package vlover.android.ec.MainActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vlover.android.ec.AdapterFish;
import vlover.android.ec.Adapters.ViewPagerAdapter;
import vlover.android.ec.DataFish;
import vlover.android.ec.Fragmentos.AvisosFragment;
import vlover.android.ec.Fragmentos.InicioFragment;
import vlover.android.ec.Fragmentos.MapaFragment;
import vlover.android.ec.Fragmentos.MiCuentaFragment;
import vlover.android.ec.Fragmentos.NotificacionesFragment;
import vlover.android.ec.Mensajes.MensajesActivity;
import vlover.android.ec.MyWork.MyWorkActivity;
import vlover.android.ec.R;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    BottomNavigationView navegacion;
    ViewPager page;
    MenuItem prevMenuItem;
    RelativeLayout bloqueo;
    RelativeLayout cargando;
    FloatingActionButton fabreload;
    InicioFragment inicios;
    AvisosFragment avisoss;
    MapaFragment vs;
    NotificacionesFragment notificacioness;
    MiCuentaFragment cuentas;
    CoordinatorLayout con;
    RecyclerView recyclerView;
    private Toolbar toolbar;
    private SearchView searchView = null;
    private MenuItem searchMenuItem;
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    public RecyclerView mRVFish;
    public AdapterFish mAdapter;
    List<DataFish> data;

    //SearchView searchView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data=new ArrayList<>();
        mRVFish = (RecyclerView) findViewById(R.id.fishPriceList);
        mAdapter = new AdapterFish(MainActivity.this, data);

        page = findViewById(R.id.pagina);
        navegacion = findViewById(R.id.navegacion);
        bloqueo = findViewById(R.id.blocked);
        cargando = (RelativeLayout) findViewById(R.id.progreso);
        fabreload = findViewById(R.id.fab_reload);
        fabreload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recargar = new Intent(MainActivity.this,MainActivity.class);
                startActivity(recargar);
                finish();
                overridePendingTransition(0,0);
                cargando.setVisibility(View.VISIBLE);
                if (isNetworkConnected()){
                    Toast.makeText(MainActivity.this,"Coneccion establecida!",Toast.LENGTH_SHORT).show();
                }else {
                    cargando.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this,"ERROR DE INTERNET",Toast.LENGTH_SHORT).show();
                }
            }
        });
        bloqueo.setVisibility(View.GONE);
        con =findViewById(R.id.coor);
        //getWindow().setStatusBarColor(getResources().getColor(R.color.foreground_material_light));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //getSupportActionBar().hide();
        navegacion.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.inicio:
                        page.setCurrentItem(0);
                        break;
                    case R.id.avisos:
                        page.setCurrentItem(1);
                        break;
                    case R.id.vlover:
                        page.setCurrentItem(2);
                        break;
                    case R.id.notificaciones:
                        page.setCurrentItem(3);
                        break;
                    case R.id.cuenta:
                        page.setCurrentItem(4);
                        break;
                }
                return false;
            }
        });

        page.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navegacion.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                navegacion.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navegacion.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        condicionPage();

    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        inicios=new InicioFragment();
        avisoss=new AvisosFragment();
        vs=new MapaFragment();
        notificacioness=new NotificacionesFragment();
        cuentas=new MiCuentaFragment();

        adapter.addFragment(inicios);
        adapter.addFragment(avisoss);
        adapter.addFragment(vs);
        adapter.addFragment(notificacioness);
        adapter.addFragment(cuentas);
        viewPager.setAdapter(adapter);
    }

    private void condicionPage(){
        if (isNetworkConnected()){
            setupViewPager(page);
            bloqueo.setVisibility(View.INVISIBLE);
        }else {
            bloqueo.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setIconified(false);
        }


        searchView.setSubmitButtonEnabled(true);

        // searchView.seton

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        // if (id == R.id.menu_buscar) {
        //   return true;
        //}
        if (id == R.id.chatear){
            startActivity(new Intent(MainActivity.this, MensajesActivity.class));
            return true;
        }if (id== R.id.trabajo){
            startActivity(new Intent(MainActivity.this, MyWorkActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    // Every time when you press search button on keypad an Activity is recreated which in turn calls this function
    @Override
    protected void onNewIntent(Intent intent) {
        // Get search query and create object of class AsyncFetch
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }
            new AsyncFetch(query).execute();

        }
    }

    // Create class AsyncFetch
    private class AsyncFetch extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;
        String searchQuery;

        public AsyncFetch(String searchQuery){
            this.searchQuery=searchQuery;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://vlover.ruvnot.com/fish-search.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput to true as we send and recieve data
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // add parameter to our above url
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("searchQuery", searchQuery);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            pdLoading.dismiss();


            pdLoading.dismiss();
            if(result.equals("no rows")) {
                Toast.makeText(MainActivity.this, "No Results found for entered query", Toast.LENGTH_LONG).show();
            }else{

                try {

                    JSONArray jArray = new JSONArray(result);

                    // Extract data from json and store into ArrayList as class objects
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        DataFish fishData = new DataFish();
                        fishData.fishName = json_data.getString("name");
                        fishData.catName = json_data.getString("email");
                        //fishData.sizeName = json_data.getString("size_name");
                        //fishData.price = json_data.getInt("price");
                        data.add(fishData);
                    }

                    // Setup and Handover data to recyclerview

                    mRVFish.setAdapter(mAdapter);
                    mRVFish.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                } catch (JSONException e) {
                    // You to understand what actually error is and handle it appropriately
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
                }

            }

        }

    }

    public void clean_response () {
        mRVFish.setAdapter(mAdapter);
    }
}
