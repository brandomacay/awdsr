package vlover.android.ec.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.R;
import vlover.android.ec.services.Address;
import vlover.android.ec.services.Controller;
import vlover.android.ec.services.SQLite;
import vlover.android.ec.services.Session;

public class Registro extends AppCompatActivity {
    private static final String TAG = Registro.class.getSimpleName();
    private Button buttonRegister;
    private Button buttonToLogin;
    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog Loading;

    // class yang diambil dari packages services
    private Session session;
    private SQLite dbsqlite;

    private AwesomeValidation awesomeValidation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inputName      = (EditText) findViewById(R.id.signup_text_name);
        inputEmail     = (EditText) findViewById(R.id.signup_text_email);
        inputPassword  = (EditText) findViewById(R.id.signup_text_password);
        buttonRegister = (Button)   findViewById(R.id.signup_btn_register);
        buttonToLogin  = (Button)   findViewById(R.id.signup_btn_tologin);

        Loading = new ProgressDialog(this);
        Loading.setCancelable(false);


        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.signup_text_email, Patterns.EMAIL_ADDRESS, R.string.error_email);
        // SqLite database handler
        dbsqlite = new SQLite(getApplicationContext());

        // session manager
        session = new Session(getApplicationContext());

        // Check if user is already logged in or not
        // Memeriksa apakah user sedang login atau tidak, konsepnya seperti method onResume
        if (session.isLoggedIn()) {
            // Jika User tidak tercatat di sesiion atau telah login, Maka user automatis akan terlogout.
            Intent intent = new Intent(Registro.this,
                    MainActivity.class);
            startActivity(intent);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
        }

        // Register Button Click event
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               verificarSiLosCamposEstanCompletados();
            }
        });

        // Link to Login Screen
        buttonToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
               finish();
            }
        });
    }

    private void verificarelregistro(){
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (!name.isEmpty() || !email.isEmpty() || !password.isEmpty()) {
            registerUser(name, email, password);
        } else {
            //Toast.makeText(getApplicationContext(),
              //      "Please enter your details!", Toast.LENGTH_LONG)
                //    .show();
        }
    }
    private void verificarSiLosCamposEstanCompletados() {
        //primero valida si los campos estan correctos
        //si todo esta bien nos pasa a la siguiente activity
        String emailvacio = inputEmail.getText().toString();
        String name = inputName.getText().toString();

        if (name.equalsIgnoreCase("")){
            inputName.setError(getString(R.string.ingresatusnombres));
        }
        else if (emailvacio.equalsIgnoreCase("")) {
            inputEmail.setError(getString(R.string.ingresatucorreo));
        } else if (awesomeValidation.validate()) {
            String campoclave = inputPassword.getText().toString();
            if (campoclave.equalsIgnoreCase("")){
                inputPassword.setError(getString(R.string.ingresatuclave));

            }
            else if (campoclave.length()<6) {
                inputPassword.setError("La contraseña debe tener 6 o mas caracteres");
            } else if (isNetworkConnected()) {
                verificarelregistro();
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

    /**
     * to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * Menyimpan data user ke web
     */
    private void registerUser(final String name, final String email, final String password){
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        Loading.setMessage(getString(R.string.registrando));
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Address.URL_REGISTER, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");


                    if (!error) {


                        session.setLogin(true);
                        String uid = jsonObject.getString("uid");

                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        dbsqlite.addUser(name, email, uid, created_at);

                        Toast.makeText(getApplicationContext(), getString(R.string.bienvenido)+user.getString("name"), Toast.LENGTH_LONG).show();


                        Intent intent = new Intent(
                                Registro.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {


                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!Loading.isShowing())
            Loading.show();
    }

    private void hideDialog() {
        if (Loading.isShowing())
            Loading.dismiss();
    }
}
