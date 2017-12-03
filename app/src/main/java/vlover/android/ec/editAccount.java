package vlover.android.ec;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.app.DatePickerDialog;
//import android.content.DialogInterface;
import android.app.Dialog;
import android.app.DialogFragment;
import android.widget.DatePicker;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
//import java.util.Set;
//import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import vlover.android.ec.Login.IniciarSesion;
import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.services.Address;
import vlover.android.ec.services.Controller;
import vlover.android.ec.services.SQLite;
import vlover.android.ec.services.Session;


public class editAccount extends AppCompatActivity {

    EditText name_et;
   // public TextView birthday_tv;
    //List<String> list;
    CountryCodePicker ccp;
    Spinner genre_spin;
    ArrayAdapter<String> spinner_adapter_genre;
    List<String> list;
    ImageView user_image;
    //CropImageView user_image;


    private SQLite dbsqlite;
    private Session session;
    ProgressDialog cargando;
   // private boolean birthday_picked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        name_et = (EditText) findViewById(R.id.edit_account_name_et);
        genre_spin = (Spinner)findViewById(R.id.edit_account_genre_spin);


        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        ccp.detectLocaleCountry(true);

        user_image = (ImageView) findViewById(R.id.edit_account_user_image);
        user_image.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(editAccount.this);
            }
        });

        // user_image = (CropImageView) findViewById(R.id.edit_account_cropImageView);
        // user_image.setImageResource(R.drawable.user);

        //ccp.getSelectedCountryCode();
        //ccp.getSelectedCountryName();
        dbsqlite = new SQLite(this);

        // session manager
        session = new Session(this);

        //HashMap<String, String> user = dbsqlite.getUserDetails();
        //String name = user.get("name");
        //name_et.setText(name);

        /*
        birthday_tv = (TextView) findViewById(R.id.edit_account_birthday_tv);
        // Cargar la fecha de nacimiento desde la web
        birthday_tv.setText(getString(R.string.birthday));
        birthday_tv.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {


                DialogFragment newfrag = new DatePickerFragment();

                //	newfrag.setArguments(null);
                newfrag.show(getFragmentManager(), "datePicker");

            }
        });
        */

        load_spin_genre();
        cargando = new ProgressDialog(this);
        verificarInicioSesion();


    }

    /*

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        //birthday_tv.this =

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int	month = c.get(Calendar.MONTH);
            int	day = c.get(Calendar.DAY_OF_MONTH);


            return new DatePickerDialog(editAccount.this, this, year, month, day);
        }
        public  void onDateSet(DatePicker view, int year, int month, int day) {

            //	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            //	String today = formatter.format( ""+day+"/"+(month+1)+"/"+year );

            if (month < 9 ) {
                if (day < 9)
                    birthday_tv.setText(  ""+year+"-"+"0"+(month+1)+"-"+"0"+day );
                else
                    birthday_tv.setText(  ""+year+"-"+"0"+(month+1)+"-"+day );
            }
            else {
                if (day < 10)
                    birthday_tv.setText(  ""+year+"-"+(month+1)+"-"+"0"+day );
                else
                    birthday_tv.setText(  ""+year+"-"+(month+1)+"-"+day );
            }


        }
    }
    */

    private void load_spin_genre () {


        list = new ArrayList<String>();

        list.add(0, getString(R.string.male));
        list.add(1, getString(R.string.female));

        spinner_adapter_genre = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);

        spinner_adapter_genre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genre_spin.setAdapter(spinner_adapter_genre);
        genre_spin.setWillNotDraw(false);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                user_image.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_account_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action buttons
        switch(item.getItemId()) {

        case R.id.edit_account_menu_save:

            Toast.makeText(this, "Guardando...", Toast.LENGTH_SHORT).show();

            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void verificarInicioSesion() {

        cargando.setMessage(getString(R.string.cargando));
        cargando.setCancelable(false);
        cargando.show();

       // String email = correo.getText().toString().trim();
       // String password = contra.getText().toString().trim();
        // Check for empty data in the form
       // if (!name_et.getText().toString().isEmpty() ) {
            // login user
            // proses email dan password
            HashMap<String, String> userr = dbsqlite.getUserDetails();

            String mEmail = userr.get("email");


            getProfile(mEmail);
        //} else {
            // Prompt user to enter credentials
            // jika tidak di isi
           // Toast.makeText(getApplicationContext(),
             //       "Please enter name!", Toast.LENGTH_LONG)
               //     .show();
       // }
    }

    public void getProfile(final String email){
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Address.URL_GET_USER_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Login Response: " + response.toString());

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    // jika tidak ada eror, mulai mengeksekusi proses mengam data
                    if (!error) {
                        // user successfully logged in
                        // Create login session - membuat session
                      //  session.setLogin(true);


                        String uid = jsonObject.getString("uid");

                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String phone = user.getString("phone");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                        // memasukkan data kedalam SQLite
                        //dbsqlite.addUser(name, email, uid, created_at);

                        cargando.dismiss();

                        //Intent intent = new Intent(IniciarSesion.this,
                          //      MainActivity.class);
                        //startActivity(intent);
                        //finish();
                        Toast.makeText(getApplicationContext(), name + email + phone, Toast.LENGTH_LONG).show();
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
                //Log.e(TAG, "Login Error: " + error.getMessage());
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


                return params;
            }

        };
        // Adding request to request queue
        // menambahkan request dalam antrian system request data
        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
