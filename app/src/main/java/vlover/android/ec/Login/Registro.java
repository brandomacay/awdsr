package vlover.android.ec.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.R;
import vlover.android.ec.Service.Address;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;
import vlover.android.ec.Service.Session;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.widget.DatePicker;
import java.util.Calendar;
import android.app.AlertDialog;


public class Registro extends AppCompatActivity {
    private static final String TAG = Registro.class.getSimpleName();
    private Button buttonRegister;
    private Button buttonToLogin;
    private static TextView inputBirthday;
    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog Loading;
    Spinner genre_spin;
    ArrayAdapter<String> spinner_adapter_genre;
    List<String> list;

    // class yang diambil dari packages services
    private Session session;
    private SQLite dbsqlite;

    private AwesomeValidation awesomeValidation;
    private static boolean birthday_picked = false;
    private static int minAge = 14;
    private static int mYear, mMonth, mDay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inputName      = (EditText) findViewById(R.id.signup_text_name);
        inputEmail     = (EditText) findViewById(R.id.email);
        inputPassword  = (EditText) findViewById(R.id.signup_text_password);
        buttonRegister = (Button)   findViewById(R.id.signup_btn_register);
        buttonToLogin  = (Button)   findViewById(R.id.signup_btn_tologin);

        inputBirthday = (TextView) findViewById(R.id.register_birthday_tv);
        // Cargar la fecha de nacimiento desde la web
        genre_spin = (Spinner)findViewById(R.id.edit_account_genre_spin);
        inputBirthday.setText(getString(R.string.birthday));
        inputBirthday.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {


                DialogFragment newfrag = new DatePickerFragment();

                //	newfrag.setArguments(null);
                newfrag.show(getFragmentManager(), "datePicker");

            }
        });


        Loading = new ProgressDialog(this);
        Loading.setCancelable(false);

        load_spin_genre();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.error_email);
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
        birthday_picked = false;
    }
    private void load_spin_genre () {
        list = new ArrayList<String>();
        // 0 = hombre, 1 = Mujer
        list.add(0, getString(R.string.male));
        list.add(1, getString(R.string.female));

        spinner_adapter_genre = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);

        spinner_adapter_genre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genre_spin.setAdapter(spinner_adapter_genre);
        genre_spin.setWillNotDraw(false);

    }


    private void verificarelregistro(){

        String ubirthday = inputBirthday.getText().toString();
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if (!name.isEmpty() || !email.isEmpty() || !password.isEmpty()) {
            registerUser(ubirthday,"" + genre_spin.getSelectedItemPosition(), name, email, password);
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

        if (!birthday_picked) {
            inputBirthday.setError(getString(R.string.birthday));
            Toast.makeText(this, getString(R.string.birthday), Toast.LENGTH_SHORT).show();
        }
        else if (getAge(mYear, mMonth, mDay) < minAge) {

            AlertDialog.Builder alert = new AlertDialog.Builder(Registro.this);
            alert.setTitle("Edad no valida");
            alert.setMessage("Debes ser mayor de " + minAge + " años para usar Vlover");
            alert.setPositiveButton("Aceptar", null);
            alert.show();
        }

        else if (name.equalsIgnoreCase("")){
            inputName.setError(getString(R.string.ingresatusnombres));
            inputBirthday.setError(null);
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
    private void registerUser(final String mbirthday,final String ugenero, final String name,
                              final String email, final String password){
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

                        Toast.makeText(getApplicationContext(),
                                "Hemos enviado un mensaje de verificacion a tu correo"+"",
                                Toast.LENGTH_LONG).show();
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
                params.put("birthday", mbirthday);
                params.put("genre", ugenero);
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

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // set default date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // get selected date
            mYear = year;
            mMonth = month;
            mDay = day;

            // show selected date to date button
            inputBirthday.setText(new StringBuilder()
                    .append(mYear).append("-")
                    .append(mMonth + 1).append("-")
                    .append(mDay).append(" "));
            birthday_picked = true;

            //Toast.makeText(getActivity(),"Edad: " + getAge(mYear, mMonth, mDay), Toast.LENGTH_LONG).show();
        }
    }

    public static int getAge(int year, int month, int day) {
        //calculating age from dob
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

}
