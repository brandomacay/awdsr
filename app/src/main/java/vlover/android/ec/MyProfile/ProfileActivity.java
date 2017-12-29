package vlover.android.ec.MyProfile;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vlover.android.ec.Post.PostActivity;
import vlover.android.ec.R;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;

public class ProfileActivity extends AppCompatActivity {

    TextView t, n_publicaciones;
    private SQLite dbsqlite;
    String unique_id;
    ProgressDialog cargando;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbsqlite = new SQLite(getApplicationContext());
        cargando = new ProgressDialog(this);



        HashMap<String, String> user = dbsqlite.getUserDetails();


        t = (TextView) findViewById(R.id.testtt);
        n_publicaciones = (TextView) findViewById(R.id.tv_publicaciones);
        unique_id = user.get("uid").toString();

        get_all_post();


    }


    public void get_all_post(){
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://vlover.ruvnot.com/get_post_byuser.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {

                        //Toast.makeText(ProfileActivity.this,
                          //      "No hubo error :)", Toast.LENGTH_LONG).show();
                        StringBuilder s = new StringBuilder();

                        JSONObject postt = jsonObject.getJSONObject("post");
                        n_publicaciones.setText("" + postt.length());

                        for (int i = 0; i<postt.length(); i++) {
                            JSONArray objcontent = postt.getJSONArray("content");
                            String content = objcontent.getString(i);
                            s.append(content + "\n");

                        JSONArray objimg = postt.getJSONArray("image");
                         String image = objimg.getString(i);
                         s.append(image+"\n");
                        //Toast.makeText(ProfileActivity.this,
                          //    s, Toast.LENGTH_LONG).show();


                         }
                        t.setText(s);



                       // String contentt = postt.getString("content");
                        //t.setText(contentt);
                        //Toast.makeText(ProfileActivity.this,
                          //      contentt, Toast.LENGTH_LONG).show();
                        //JSONArray post = postt.getJSONArray("pid");


                       // String uid = post.getString("pid");



                        //for (int i = 0; i<post.length(); i++) {
                            //JSONObject obj = post.getJSONObject(i);
                           // String content = obj.getString("pid");
                            //Toast.makeText(ProfileActivity.this,
                              //      content, Toast.LENGTH_LONG).show();

                       // }

                        //t.setText(post.getString("content"));
                        //  imagen.setImageDrawable(null);
                        //imagen.setImageResource(0);
                      //  finish();

                        //generoview.setText(genre);
                    } else {
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(ProfileActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();
                        cargando.dismiss();
                    }
                }  catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    cargando.dismiss();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(ProfileActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                cargando.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("unique_id", unique_id);
                //params.put("image", url_image);
                //params.put("content", descripcion_post.getText().toString());
                //params.put("datetime", "fecha de prueba");
                return params;

            }

        };

        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
