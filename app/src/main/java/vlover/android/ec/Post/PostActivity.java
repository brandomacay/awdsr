package vlover.android.ec.Post;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;

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

import javax.xml.datatype.Duration;

import de.hdodenhof.circleimageview.CircleImageView;
import vlover.android.ec.Edition.Account;
import vlover.android.ec.Fragmentos.MiCuentaFragment;
import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.R;
import vlover.android.ec.Service.Address;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;
import vlover.android.ec.Service.Session;
import vlover.android.ec.Upload;
import vlover.android.ec.VolleyMultipartRequest;

public class PostActivity extends AppCompatActivity {
    ProgressDialog cargando;
    CircleImageView user_image;
    TextView nombre_usuario,user_email;
    String uniqueid = "", email_user = "", postid = "", email;
    ImageView imagen,regresar;
    FloatingActionButton seleccionar_imagen, aggvideo;
    EditText descripcion_post;
    int likes = 0,comentarios =0;
    Button enviar;
    private SQLite dbsqlite;
    private Session session;
    boolean imageIsSet = false;
    String url_image = "";
    Bitmap bitmappost;
    VideoView vidView;
    private static final int SELECT_VIDEO = 3;

    private String selectedPath;
    String vidAddress = "http://vlover.ruvnot.com/uploads/20180106_122338.mp4";
    Uri vidUri = Uri.parse(vidAddress);
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        user_email =(TextView)findViewById(R.id.email);
        cargando = new ProgressDialog(this);
        imagen=(ImageView)findViewById(R.id.post_photo);
        regresar=(ImageView)findViewById(R.id.back);
        enviar=(Button) findViewById(R.id.send);
        // vidView = (VideoView)findViewById(R.id.myVideo);
        descripcion_post =(EditText) findViewById(R.id.descripcion);
        user_image = (CircleImageView) findViewById(R.id.img_avatar);
        seleccionar_imagen = (FloatingActionButton) findViewById(R.id.select_image);
        aggvideo = (FloatingActionButton) findViewById(R.id.add_video);
        nombre_usuario = (TextView) findViewById(R.id.name_user);
        /*try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    PostActivity.this);
            mediacontroller.setAnchorView(vidView);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(vidAddress);
            vidView.setMediaController(mediacontroller);
            vidView.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        vidView.requestFocus();
        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                vidView.start();
            }
        });*/
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

        aggvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVideo();
            }
        });

        HashMap<String, String> user = dbsqlite.getUserDetails();
        email = user.get("email");
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String descripcion = descripcion_post.getText().toString();
                if (descripcion.equalsIgnoreCase("") && imageIsSet == false) {
                    descripcion_post.setError("Escriba algo, por favor");
                } else {

                    if (imageIsSet == true) {
                    //insert_post();
                        upload_image_first();
                        finish();
                        Toast.makeText(PostActivity.this, "Publicando...", Toast.LENGTH_LONG).show();
                     }
                     else {
                      insert_post();
                        finish();
                        Toast.makeText(PostActivity.this, "Publicando...", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



    }


    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(PostActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();

            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    /*public void uploadvideo(View v) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, RECEIVER_VISIBLE_TO_INSTANT_APPS);
        }
    }*/

    private void  mostrardatodeusuario() {

        HashMap<String, String> user = dbsqlite.getUserDetails();
        getProfile(email_user);
        String email = user.get("email");
        user_email.setText(email);
        getProfile(email);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_VIDEO) {
            System.out.println("SELECT_VIDEO");
            Uri selectedImageUri = data.getData();
            selectedPath = getPath(selectedImageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //Desde aqui ya lo cargas, pero hagamoslo en el try para capturar error por si fallo algo
               // imagen.setImageURI(resultUri);
                try {
                    bitmappost = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    imagen.setImageBitmap(bitmappost);
                    descripcion_post.setError(null);
                    imageIsSet = true;

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
                            String avatar = getString(R.string.url_global)+"uploads/"+
                                    uniqueid + "/avatar/" + user.getString("avatar");
                            Picasso.with(PostActivity.this)
                                    .load(avatar)
                                    .resize(50, 50)
                                    .centerCrop()
                                    .into(user_image);
                        }
                        cargando.dismiss();
                        nombre_usuario.setText(name);

                    } else {

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

    public void insert_post(){
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://vlover.ruvnot.com/insert_post.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {

                        //String uid = jsonObject.getString("uid");

                        JSONObject post = jsonObject.getJSONObject("post");
                        String uid = post.getString("id");

                        Toast.makeText(PostActivity.this,
                                "Publicacion subida correctamente", Toast.LENGTH_LONG).show();
                        descripcion_post.setText("");
                      //  imagen.setImageDrawable(null);
                        //imagen.setImageResource(0);
                        finish();

                        //generoview.setText(genre);
                    } else {
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(PostActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();
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
                params.put("user_email", email);
                params.put("image", url_image);
                params.put("content", descripcion_post.getText().toString());
                //params.put("datetime", "fecha de prueba");
                return params;

            }

        };

        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void upload_image_first() {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Address.UPLOAD_URLPOST,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message") , Toast.LENGTH_SHORT).show();
                            if (obj.getString("error").equals("false")){
                                cargando.dismiss();
                                // Toast.makeText(getApplicationContext(), obj.getString("url"), Toast.LENGTH_SHORT).show();
                                 url_image = obj.getString("url");
                                 insert_post();

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
                params.put("fileToUpload", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmappost)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

}
