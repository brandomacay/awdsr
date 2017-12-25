package vlover.android.ec.Post;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import vlover.android.ec.Edition.Account;
import vlover.android.ec.Fragmentos.MiCuentaFragment;
import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.R;
import vlover.android.ec.Service.Address;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;
import vlover.android.ec.Service.Session;
import vlover.android.ec.VolleyMultipartRequest;

public class PostActivity extends AppCompatActivity {
    ProgressDialog cargando;
    CircleImageView user_image;
    TextView nombre_usuario,user_email;
    String uniqueid = "", email_user = "",postid = "";
    ImageView imagen,regresar;
    FloatingActionButton seleccionar_imagen;
    EditText descripcion_post;
    int likes = 0,comentarios =0;
    Button enviar;
    private SQLite dbsqlite;
    private Session session;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        user_email =(TextView)findViewById(R.id.email);
        cargando = new ProgressDialog(this);
        imagen=(ImageView)findViewById(R.id.post_photo);
        regresar=(ImageView)findViewById(R.id.back);
        enviar=(Button) findViewById(R.id.send);
        descripcion_post =(EditText) findViewById(R.id.descripcion);
        user_image = (CircleImageView) findViewById(R.id.img_avatar);
        seleccionar_imagen = (FloatingActionButton) findViewById(R.id.select_image);
        nombre_usuario = (TextView) findViewById(R.id.name_user);
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dbsqlite = new SQLite(PostActivity.this);

        // session manager
        session = new Session(PostActivity.this);
        seleccionar_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropMenuCropButtonTitle("Elegir")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setOutputCompressQuality(75)
                        .start(PostActivity.this);
            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //  sendPost(nombre_usuario.getText().toString(),descripcion_post.getText().toString());
            }
        });



    }



    private void  mostrardatodeusuario() {

        HashMap<String, String> user = dbsqlite.getUserDetails();
        getProfile(email_user);
        String email = user.get("email");
        user_email.setText(email);
        getProfile(email);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imagen.setImageURI(resultUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public void getProfile(final String email){
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Address.URL_GET_USER_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {

                        String uid = jsonObject.getString("uid");

                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");
                        uniqueid = uid;
                        email_user = user.getString("email");
                        if (!user.getString("avatar").isEmpty()) {
                            String avatar = "https://vlover.000webhostapp.com/uploads/"+
                                    uniqueid + "/avatar/" + user.getString("avatar");
                            Picasso.with(PostActivity.this)
                                    .load(avatar)
                                    .resize(50, 50)
                                    .centerCrop()
                                    .into(user_image);
                        }
                        cargando.dismiss();
                        nombre_usuario.setText(name);

                        //generoview.setText(genre);
                    } else {
                        /*String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(PostActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();*/
                        cargando.dismiss();
                    }
                }  catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(PostActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    cargando.dismiss();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(PostActivity.this,
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



    @Override
    public void onResume() {
        //Log.e("DEBUG", "onResume of LoginFragment");
        super.onResume();
        mostrardatodeusuario();
    }

}
