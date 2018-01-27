package vlover.android.ec.MyWork;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import vlover.android.ec.Login.Registro;
import vlover.android.ec.R;
import vlover.android.ec.Service.Address;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;
import vlover.android.ec.VolleyMultipartRequest;

public class RegisterMyWorkActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private static final String TAG = RegisterMyWorkActivity.class.getSimpleName();
    private SQLite dbsqlite;
    CircleImageView logo;
    Bitmap bitmaplogo;
    boolean imageIsSet = false;
    private MapFragment googleMap;
    EditText vname, vcountry, vstate, vcity, vaddress, vdescription, vlink, vtelephone, vcellphone, vcategory,
            vsubcategory, vtimework;
    String vemail = "", vlogo = "", vcoorx = "xxx", vcoory = "yyy", unique_id = "";
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_my_work);
        dbsqlite = new SQLite(this);
        HashMap<String, String> user = dbsqlite.getUserDetails();
        vemail = user.get("email");
        unique_id = user.get("uid");
        getBusiness(vemail);
        vname = (EditText) findViewById(R.id.nombre);
        vcountry = (EditText) findViewById(R.id.pais);
        vstate = (EditText) findViewById(R.id.estado);
        vcity = (EditText) findViewById(R.id.ciudad);
        vaddress = (EditText) findViewById(R.id.direccion);
        vdescription = (EditText) findViewById(R.id.descripcion);
        vlink = (EditText) findViewById(R.id.link);
        vtelephone = (EditText) findViewById(R.id.telefono);
        vcellphone = (EditText) findViewById(R.id.celular);
        vcategory = (EditText) findViewById(R.id.categoria);
        vsubcategory = (EditText) findViewById(R.id.subcategoria);
        vtimework = (EditText) findViewById(R.id.horario);
        register = (Button) findViewById(R.id.registrar);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre, pais, estado, ciudad, direccion, descripcion, link, telefono, celular, rrcategoria, subcategoria,
                        horario;
                nombre = vname.getText().toString().trim();
                pais = vcountry.getText().toString().trim();
                estado = vstate.getText().toString().trim();
                ciudad = vcity.getText().toString().trim();
                direccion = vaddress.getText().toString().trim();
                descripcion = vdescription.getText().toString().trim();
                link = vlink.getText().toString().trim();
                telefono = vtelephone.getText().toString().trim();
                celular = vcellphone.getText().toString().trim();
                rrcategoria = vcategory.getText().toString().trim();
                subcategoria = vsubcategory.getText().toString().trim();
                horario = vtimework.getText().toString().trim();
                updateBusiness(vemail, nombre, pais, estado, ciudad, direccion);
                registerWork(vemail, nombre, vlogo, pais, estado, ciudad, direccion, descripcion, link, telefono, celular, rrcategoria, subcategoria, horario, vcoorx, vcoory);
            }
        });
        logo = (CircleImageView) findViewById(R.id.pick_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropMenuCropButtonTitle("Elejir logo")
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setMinCropResultSize(100, 100)
                        .setMaxCropResultSize(1600, 1600)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setOutputCompressQuality(75)
                        .start(RegisterMyWorkActivity.this);
            }
        });

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), (CharSequence) e, Toast.LENGTH_SHORT).show();
        }
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)
            );

            // en caso de error
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Error :c", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void upload_logo() {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Address.UPLOAD_LOGO,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            if (obj.getString("error").equals("false")) {
                                // Toast.makeText(getApplicationContext(), obj.getString("url"), Toast.LENGTH_SHORT).show();
                                vlogo = obj.getString("url");
                                insert_link_logo(vemail, vlogo);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userid", unique_id);
                params.put("logo", vlogo);
                // params.put("tags", tags);
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("fileToUpload", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmaplogo)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //Desde aqui ya lo cargas, pero hagamoslo en el try para capturar error por si fallo algo
                // imagen.setImageURI(resultUri);
                try {
                    bitmaplogo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    logo.setImageBitmap(bitmaplogo);
                    imageIsSet = true;
                    upload_logo();

                } catch (IOException e) {
                    e.printStackTrace();
                    imageIsSet = false;
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                imageIsSet = false;
            }
        }

    }

    private void insert_link_logo(final String my_email, final String link) {
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://vlover.ruvnot.com/insert_link_logo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");


                    if (!error) {

                        Toast.makeText(getApplicationContext(),
                                "Exito!!!!!!",
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
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", my_email);
                params.put("link", link);
                return params;
            }

        };

        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void getBusiness(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://vlover.ruvnot.com/get_business_byemail.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {

                        String id = jsonObject.getString("id");

                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");
                        id = id;
                        //email_user = user.getString("email");
                        vname.setText(name);
                        if (!user.getString("logo").isEmpty()) {
                            String avatar = getString(R.string.url_global) + "uploads/" +
                                    unique_id + "/logobusiness/" + user.getString("logo");
                            Picasso.with(getApplication())
                                    .load(avatar)
                                    .resize(200, 200)
                                    .centerCrop()
                                    .into(logo);
                        }
                        /*String phone = user.getString("phone");
                        //String genre = user.getString("genre");
                        String country = user.getString("country");
                        String created_at = user.getString("created_at");*/

                    } else {
                        // Error in login. Get the error message
                        // Jika terjadi error dalam pengambilan data
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // terjadi ketidak sesuain data user pada saat login
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void registerWork(final String my_email, final String r_name, final String r_logo,
                              final String r_country, final String r_state, final String r_city,
                              final String r_address, final String r_descripcion,
                              final String r_link, final String r_telephone,
                              final String r_cellphone, final String r_category,
                              final String r_subcategory, final String r_timework, final String r_x,
                              final String r_y) {
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://vlover.ruvnot.com/register_business.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");


                    if (!error) {

                        Toast.makeText(getApplicationContext(),
                                "Felicidades tu negocio a sido registrado!",
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
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email_user", my_email);
                params.put("name", r_name);
                params.put("logo", r_logo);
                params.put("country", r_country);
                params.put("state", r_state);
                params.put("city", r_city);
                params.put("address", r_address);
                params.put("description", r_descripcion);
                params.put("link", r_link);
                params.put("telephone", r_telephone);
                params.put("cellphone", r_cellphone);
                params.put("category", r_category);
                params.put("subcategory", r_subcategory);
                params.put("timework", r_timework);
                params.put("positionX", r_x);
                params.put("positionY", r_y);

                return params;
            }

        };

        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void updateBusiness(final String uEmail, final String uname, final String ucountry, final String ustate,
                               final String ucity, final String uaddress) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://vlover.ruvnot.com/update_business.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    // jika tidak ada eror, mulai mengeksekusi proses mengam data
                    if (!error) {
                        // user successfully logged in
                        // Create login session - membuat session
                        //  session.setLogin(true);


                        // String uid = jsonObject.getString("uid");

                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");
                        vname.setText(name);
                        Toast.makeText(getApplicationContext(), "Actualizacion exitosa!", Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        // Error in login. Get the error message
                        // Jika terjadi error dalam pengambilan data
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    // Jika terjadi eror pada proses json
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // terjadi ketidak sesuain data user pada saat login
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                //email, $name, $genre, $country, $phonecode, $phone, $avatar, $update);

                params.put("email", uEmail);
                params.put("name", uname);
                params.put("country", ucountry);
                params.put("state", ustate);
                params.put("city", ucity);
                params.put("address", uaddress);
                return params;
            }

        };
        // Adding request to request queue
        // menambahkan request dalam antrian system request data
        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onMapLoaded() {

    }
}
