package vlover.android.ec.Login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.R;
import vlover.android.ec.Service.Address;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;
import vlover.android.ec.Service.Session;

public class IniciarSesion extends AppCompatActivity {
    private static final String TAG = Registro.class.getSimpleName();
    ProgressDialog cargando;
    private EditText correo, contra;
    private TextView registrarse;
    private CardView iniciarsesion;
    private FirebaseAnalytics mFirebaseAnalytics;


    private Session session;
    private SQLite dbsqlite;

    TextView cambiarpass;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        correo = findViewById(R.id.email);
        contra = findViewById(R.id.clave);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.error_email);
        //awesomeValidation.addValidation(this, R.id.clave, Pattern.compile("Brandon"), R.string.error_contraseña);

        dbsqlite = new SQLite(getApplicationContext());

        session = new Session(getApplicationContext());
        if (session.isLoggedIn()) {

            Intent intent = new Intent(IniciarSesion.this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        cargando = new ProgressDialog(this);
        iniciarsesion = findViewById(R.id.iniciar);
        cambiarpass = findViewById(R.id.cambiar_clave);
        registrarse = findViewById(R.id.registrar);
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IniciarSesion.this, Registro.class));
            }
        });
        cambiarpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IniciarSesion.this, CambiarClave.class));
            }
        });
        cargando = new ProgressDialog(this);
        iniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verificarSiLosCamposEstanCompletados();
            }
        });

    }

    private void verificarInicioSesion() {

        cargando.setMessage(getString(R.string.cargando));
        cargando.setCancelable(false);
        cargando.show();
            String email = correo.getText().toString().trim();
            String password = contra.getText().toString().trim();
            // Check for empty data in the form
            if (!email.isEmpty() && !password.isEmpty()) {
                // login user
                // proses email dan password
                checkLogin(email, password);
            } else {
                // Prompt user to enter credentials
                // jika tidak di isi
                Toast.makeText(getApplicationContext(),
                        "Please enter the credentials!", Toast.LENGTH_LONG)
                        .show();
            }
    }

    public void checkLogin(final String email, final String password){
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Address.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    // jika tidak ada eror, mulai mengeksekusi proses mengam data
                    if (!error) {
                        // user successfully logged in
                        // Create login session - membuat session



                        String uid = jsonObject.getString("uid");

                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");
                       int verified = user.getInt("verified");

                        if (verified == 0){
                            session.setLogin(false);
                            cargando.dismiss();
                            AlertDialog.Builder alert = new AlertDialog.Builder(IniciarSesion.this);
                            alert.setTitle("Correo no verificado");
                            alert.setMessage("Tu registro aun no esta completo. Verifica tu email en bandeja de entrada, SPAM u otros.");
                            alert.setPositiveButton("Aceptar", null);
                            alert.show();
                        }
                        else {
                            session.setLogin(true);
                            // Inserting row in users table
                            // memasukkan data kedalam SQLite
                            dbsqlite.addUser(name, email, uid, created_at);

                            cargando.dismiss();

                            Intent intent = new Intent(IniciarSesion.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), getString(R.string.bienvenido)+user.getString("name"), Toast.LENGTH_LONG).show();

                        }


                    } else {
                        // Error in login. Get the error message
                        // Jika terjadi error dalam pengambilan data
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        cargando.dismiss();
                    }
                }  catch (JSONException e) {
                    // JSON error
                    // Jika terjadi eror pada proses json
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    cargando.dismiss();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // terjadi ketidak sesuain data user pada saat login
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                cargando.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };
        // Adding request to request queue
        // menambahkan request dalam antrian system request data
        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // menampilkan dialog untuk loading


    private void verificarSiLosCamposEstanCompletados() {
        //primero valida si los campos estan correctos
        //si todo esta bien nos pasa a la siguiente activity
        String emailvacio = correo.getText().toString();
        if (emailvacio.equalsIgnoreCase("")) {
            correo.setError(getString(R.string.ingresatucorreo));
        } else if (awesomeValidation.validate()) {
            String campoclave = contra.getText().toString();
            if (campoclave.equalsIgnoreCase("")) {
                contra.setError(getString(R.string.ingresatuclave));
            } else if (isNetworkConnected()) {
            verificarInicioSesion();
            } else {
                Toast.makeText(this, "Sin coneccion a Internet", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (session.isLoggedIn()) {
            // Jika User tidak tercatat di sesiion atau telah login, Maka user automatis akan terlogout.
           finish();
        }
    }
}
