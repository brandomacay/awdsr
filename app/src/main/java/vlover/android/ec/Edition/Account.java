package vlover.android.ec.Edition;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
//import android.content.DialogInterface;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.bumptech.glide.Glide;
import com.hbb20.CountryCodePicker;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import de.hdodenhof.circleimageview.CircleImageView;
import vlover.android.ec.R;
import vlover.android.ec.Service.Address;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;
import vlover.android.ec.Service.Session;
import vlover.android.ec.VolleyMultipartRequest;


public class Account extends AppCompatActivity {

    EditText name_et, phone_et;
    TextView cancelar;
   // public TextView birthday_tv;
    //List<String> list;
    CountryCodePicker ccp;
    CircleImageView user_image;
    String email_user = "", uniqueid = "";
    //CropImageView user_image;


    private SQLite dbsqlite;
    private Session session;
    ProgressDialog cargando;
    String previous_avatar = "";
    //int profilecount = 0;
   // private boolean birthday_picked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        cancelar =(TextView)findViewById(R.id.salir);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        name_et = (EditText) findViewById(R.id.edit_account_name_et);
        phone_et = (EditText) findViewById(R.id.edit_account_phone_et);


        ccp = (CountryCodePicker) findViewById(R.id.ccp);



        user_image = (CircleImageView) findViewById(R.id.edit_account_user_image);
        user_image.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropMenuCropButtonTitle("Guardar")
                        .setCropShape(CropImageView.CropShape.OVAL)


                        .setMinCropResultSize(100,100)
                        .setMaxCropResultSize(1600,1600)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setOutputCompressQuality(75)

                        .start(Account.this);
            }
        });

        // user_image = (CropImageView) findViewById(R.id.edit_account_cropImageView);
        // user_image.setImageResource(R.drawable.user);

        //ccp.getSelectedCountryCode();
        //ccp.getSelectedCountryName();
        dbsqlite = new SQLite(this);

        // session manager
        session = new Session(this);

        HashMap<String, String> user = dbsqlite.getUserDetails();
        String email =user.get("email");
        getSupportActionBar().setTitle(email);
        cargando = new ProgressDialog(this);
        verificarInicioSesion();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                //Bitmap bitt = Bitmap.createBitmap(compressImage(resultUri.toString()));
               // Uri u = Uri.parse(compressImage(resultUri.toString()));
               //user_image.setImageURI(compressImage(resultUri.toString()));
                user_image.setImageURI(resultUri);
                try {
                    //getting bitmap object from uri
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 75, );
                    //user_image.get

                    //displaying selected image to imageview
                    //imageView.setImageBitmap(bitmap);

                    //calling the method uploadBitmap to upload image
                    cargando.setMessage("Subiendo...");
                    cargando.show();
                    cargando.setCancelable(false);
                    uploadBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

            if (name_et.getText().toString().isEmpty()) {
                name_et.setError("Escribe tu nombre");
            }
            else {
                //$email, $name, $genre, $country, $phonecode, $phone, $avatar, $update);

                if (isNetworkConnected()){
                    updateProfile(email_user, name_et.getText().toString(),
                            ccp.getSelectedCountryNameCode() , ccp.getSelectedCountryCode(), phone_et.getText().toString(),
                             "testupdate");

                    cargando.setMessage("Guardando...");
                    cargando.show();
                    cargando.setCancelable(false);
                }else
                {
                    Toast.makeText(Account.this,getString(R.string.error_internet),Toast.LENGTH_LONG).show();
                }


                return true;
            }

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
                        uniqueid = uid;
                        email_user = user.getString("email");
                        String phone = user.getString("phone");
                        //String genre = user.getString("genre");
                        String country = user.getString("country");
                        String created_at = user.getString("created_at");

                        if (!user.getString("avatar").isEmpty()) {
                            String avatar = "https://vlover.000webhostapp.com/uploads/"+
                                    uniqueid + "/avatar/" + user.getString("avatar");


                            Picasso.with(getApplication())
                                    .load(avatar)
                                    .resize(200, 200)
                                    .centerCrop()
                                    .into(user_image);
                            previous_avatar = user.getString("avatar");
                            /*

                            String sss = user.getString("avatar").substring(6);
                            String[] parts = sss.split(".");
                            String part1 = parts[0]; // 004
                            //String part2 = parts[1];
                            profilecount = Integer.parseInt(part1);
                            */
                            /*
                            Picasso.with(getApplication())
                                    .load(avatar)
                                    .resize(200, 200)
                                    .centerCrop()

                                    //.resize(width,height).
                                    .into(user_image);
                                    */
                            /*
                            Glide.with(Account.this)
                                    .load(avatar)

                                    .thumbnail(0.1f)

                                    .into(user_image);
                                    */
                            //Toast.makeText(getApplicationContext(), avatar, Toast.LENGTH_LONG).show();

                        }


                        //session.setLogin(false);


                        cargando.dismiss();
                        name_et.setText(name);
                        //int idspin = spinner_adapter_genre.getPosition(genre);
                        /*if (!genre.isEmpty()) {
                            genre_spin.setSelection(Integer.parseInt(genre), true);
                        }*/

                        if (country.isEmpty()) {
                            ccp.detectLocaleCountry(true);
                        }
                        else {
                            ccp.setCountryForNameCode(country);
                        }
                        phone_et.setText(phone);



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


        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    //$email, $name, $genre, $country, $phonecode, $phone, $avatar, $update);
    public void updateProfile(final String uEmail,final String uName , final String uCountry, final String uPhonecode,
                              final String uPhone, final String uUpdate){
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Address.URL_UPDATE_USER_PROFILE, new Response.Listener<String>() {

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



                       // String uid = jsonObject.getString("uid");

                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");

                        //String email = user.getString("email");
                        String phone = user.getString("phone");
                        String genre = user.getString("genre");
                        String country = user.getString("country");
                        String created_at = user.getString("created_at");
                        //dbsqlite.updateUser(name);

                        // Inserting row in users table
                        // memasukkan data kedalam SQLite
                        //dbsqlite.deleteUsers();

                        //dbsqlite.addUser(name,phone,genre,country,email);
                        cargando.dismiss();
                        name_et.setText(name);
                        //int idspin = spinner_adapter_genre.getPosition(genre);
                        /*if (!genre.isEmpty()) {
                            genre_spin.setSelection(Integer.parseInt(genre), true);
                        }*/


                        if (country.isEmpty()) {
                            ccp.detectLocaleCountry(true);
                        }
                        else {
                            ccp.setCountryForNameCode(country);
                        }
                        phone_et.setText(phone);



                        //Intent intent = new Intent(IniciarSesion.this,
                        //      MainActivity.class);
                        //startActivity(intent);
                        //finish();
                        cargando.dismiss();
                        Toast.makeText(getApplicationContext(), "Actualizacion exitosa!", Toast.LENGTH_LONG).show();
                        finish();

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
                //email, $name, $genre, $country, $phonecode, $phone, $avatar, $update);

                params.put("email", uEmail);
                params.put("name", uName);
               //params.put("genre", uGenre);
                params.put("country", uCountry);
                params.put("phonecode", uPhonecode);
                params.put("phone", uPhone);
                params.put("update", uUpdate);



                return params;
            }

        };
        // Adding request to request queue
        // menambahkan request dalam antrian system request data
        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {

        //getting the tag from the edittext
       // final String tags = editTextTags.getText().toString().trim();
        final String tags = previous_avatar;
        Toast.makeText(getApplicationContext(), previous_avatar , Toast.LENGTH_SHORT).show();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Address.UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message") , Toast.LENGTH_SHORT).show();
                            if (obj.getString("error").equals("false")){
                                cargando.dismiss();
                               // Toast.makeText(getApplicationContext(), "....guardando en bd", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            cargando.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cargando.dismiss();
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
                params.put("userid", uniqueid);
                params.put("tags", tags);
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }



}
