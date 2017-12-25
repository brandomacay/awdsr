package vlover.android.ec.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import vlover.android.ec.Adapters.ViewPagerAdapter;
import vlover.android.ec.Fragmentos.AvisosFragment;
import vlover.android.ec.Fragmentos.InicioFragment;
import vlover.android.ec.Fragmentos.MapaFragment;
import vlover.android.ec.Fragmentos.MiCuentaFragment;
import vlover.android.ec.Fragmentos.NotificacionesFragment;
import vlover.android.ec.Mensajes.MensajesActivity;
import vlover.android.ec.MyWork.MyWorkActivity;
import vlover.android.ec.R;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_buscar) {
            return true;
        }if (id == R.id.chatear){
            startActivity(new Intent(MainActivity.this, MensajesActivity.class));
            return true;
        }if (id== R.id.trabajo){
            startActivity(new Intent(MainActivity.this, MyWorkActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


}
