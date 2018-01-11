package vlover.android.ec.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import vlover.android.ec.R;
import vlover.android.ec.Service.Address;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;

public class UserActivity extends AppCompatActivity {

    public TextView correo, nombre, sexo, dayb;
    Bundle email, nombres;
    private String getEmail, getName, uniqueid = "";
    ProgressDialog cargando;
    String previous_avatar = "";
    CircleImageView user_image;
    private SQLite dbsqlite;
    FloatingActionButton send_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        dbsqlite = new SQLite(this);
        cargando = new ProgressDialog(this);
        send_s = (FloatingActionButton) findViewById(R.id.enviarsolicitud);


        HashMap<String, String> user = dbsqlite.getUserDetails();
        String myemail = user.get("email");
        send_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> user = dbsqlite.getUserDetails();
                String myemail = user.get("email");
                send_solicitud(myemail, getEmail);
            }
        });
        correo = (TextView) findViewById(R.id.email);
        nombre = (TextView) findViewById(R.id.name_user);
        user_image = (CircleImageView) findViewById(R.id.image_user);
        sexo = (TextView) findViewById(R.id.genero);
        dayb = (TextView) findViewById(R.id.dateb);
        Intent intent = getIntent();
        email = intent.getExtras();
        nombres = intent.getExtras();
        getEmail = (String) email.get("Email");
        getName = (String) nombres.get("Name");
        correo.setText(getEmail);
        nombre.setText(getName);
        if (myemail.equals(getEmail)) {
            send_s.setVisibility(View.GONE);
        }
        getProfile(getEmail);
    }

    public void getProfile(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Address.URL_GET_USER_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");


                    if (!error) {
                        String uid = jsonObject.getString("uid");
                        JSONObject user = jsonObject.getJSONObject("user");
                        uniqueid = uid;
                        getEmail = user.getString("email");
                        String phone = user.getString("phone");
                        String genre = user.getString("genre");
                        String country = user.getString("country");
                        String birthday = user.getString("birthday");
                        if (!user.getString("avatar").isEmpty()) {
                            String avatar = getString(R.string.url_global) + "uploads/" +
                                    uniqueid + "/avatar/" + user.getString("avatar");
                            Picasso.with(getApplication())
                                    .load(avatar)
                                    .resize(200, 200)
                                    .centerCrop()
                                    .into(user_image);
                            previous_avatar = user.getString("avatar");

                        }

                        if ("0".equals(genre)) {
                            sexo.setText(getString(R.string.male));
                        } else {
                            sexo.setText(getString(R.string.female));
                        }

                        dayb.setText(birthday);
                        //session.setLogin(false);


                        cargando.dismiss();
                        //int idspin = spinner_adapter_genre.getPosition(genre);
                        /*if (!genre.isEmpty()) {
                            genre_spin.setSelection(Integer.parseInt(genre), true);
                        }*/


                        //Intent intent = new Intent(IniciarSesion.this,
                        //      MainActivity.class);
                        //startActivity(intent);
                        //finish();
                        // Toast.makeText(getApplicationContext(), name + email_user +  genre + country + phone, Toast.LENGTH_LONG).show();
                    } else {
                        // Error in login. Get the error message
                        // Jika terjadi error dalam pengambilan data
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        cargando.dismiss();
                    }
                } catch (JSONException e) {
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


        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void send_solicitud(final String myemail, final String emailuser) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                getString(R.string.url_global) + "friendship_request.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    // jika tidak ada eror, mulai mengeksekusi proses mengam data
                    if (!error) {
                        //exito
                    } else {
                        // Error in login. Get the error message
                        // Jika terjadi error dalam pengambilan data
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        cargando.dismiss();
                    }
                } catch (JSONException e) {
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
                params.put("user_send", myemail);
                params.put("user_get", emailuser);


                return params;
            }

        };


        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
