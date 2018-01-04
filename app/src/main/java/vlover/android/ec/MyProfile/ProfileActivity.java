package vlover.android.ec.MyProfile;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vlover.android.ec.R;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;
import vlover.android.ec.Adapters.getPostAdapter;
import vlover.android.ec.Adapters.recyclerViewPostAdapter;

public class ProfileActivity extends AppCompatActivity {

    TextView n_publicaciones;
    private SQLite dbsqlite;
    String unique_id;

    ProgressDialog cargando;

    List<getPostAdapter> GetDataAdapter1;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;

    RecyclerView.Adapter recyclerViewadapter;
    //ProgressBar progressBar;

    String GET_JSON_DATA_HTTP_URL = "http://vlover.ruvnot.com/get_post_byuser.php";
    String JSON_ID = "id";
    String JSON_NAME = "name";
    String JSON_SUBJECT = "subject";
    String JSON_PHONE_NUMBER = "phone_number";

    // Button button;

    JsonArrayRequest jsonArrayRequest ;

    RequestQueue requestQueue ;

    ImageButton opciones;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbsqlite = new SQLite(getApplicationContext());
        cargando = new ProgressDialog(this);
        HashMap<String, String> user = dbsqlite.getUserDetails();
        n_publicaciones = (TextView) findViewById(R.id.tv_publicaciones);
        unique_id = user.get("uid").toString();

        GetDataAdapter1 = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.activity_profile_recyclerview);
        fab = (FloatingActionButton) findViewById(R.id.floating_action_button);

        opciones = (ImageButton) findViewById(R.id.options);

        // progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //button = (Button)findViewById(R.id.button) ;

        recyclerView.setHasFixedSize(true);

        recyclerViewlayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        recyclerView.isScrollbarFadingEnabled();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        get_all_post();


        //JSON_DATA_WEB_CALL();


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
                        // StringBuilder s = new StringBuilder();
                        //s.append("");

                        JSONObject postt = jsonObject.getJSONObject("post");

                        JSONArray pray = postt.getJSONArray("pid");
                        n_publicaciones.setText("" + pray.length());



                        for (int i = 0; i<pray.length(); i++) {


                            getPostAdapter GetDataAdapter2 = new getPostAdapter();

                            JSONArray objPid = postt.getJSONArray("pid");
                            final String pid = objPid.getString(i);

                            JSONArray objname = postt.getJSONArray("name");
                            String name = objname.getString(i);

                            JSONArray objuserimage = postt.getJSONArray("avatar");
                            String userimage = getString(R.string.url_global)+"uploads/"+
                                    unique_id + "/avatar/" + "small_" + objuserimage.getString(i);

                            JSONArray objcontent = postt.getJSONArray("content");
                            String content = objcontent.getString(i);

                            JSONArray objimg = postt.getJSONArray("image");
                            String image = getString(R.string.url_global) + "uploads/" +
                                    unique_id + "/post/"  + objimg.getString(i);
                            JSONArray objdate = postt.getJSONArray("datetime");
                            String date = objdate.getString(i);

                            GetDataAdapter2.setId(Integer.parseInt(pid));

                            GetDataAdapter2.setUnique_id(name);

                            GetDataAdapter2.setUserImage(userimage);

                            GetDataAdapter2.setContent(content);

                            GetDataAdapter2.setImage(image);

                            GetDataAdapter2.setDate(date);

                            GetDataAdapter1.add(GetDataAdapter2);


                            // JSONArray objcontent = postt.getJSONArray("content");
                            // String content = objcontent.getString(i);
                            //  s.append(content + "\n");

                            //JSONArray objimg = postt.getJSONArray("image");
                            //String image = objimg.getString(i);
                            // s.append(image+"\n\n");
                            //Toast.makeText(ProfileActivity.this,
                            //    s, Toast.LENGTH_LONG).show();


                        }


                        recyclerViewadapter = new recyclerViewPostAdapter(GetDataAdapter1, ProfileActivity.this);

                        recyclerView.setAdapter(recyclerViewadapter);


                        // t.setText(s);


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


    public void JSON_DATA_WEB_CALL(){

        jsonArrayRequest = new JsonArrayRequest(GET_JSON_DATA_HTTP_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        //progressBar.setVisibility(View.GONE);

                        JSON_PARSE_DATA_AFTER_WEBCALL(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonArrayRequest);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array){

        for(int i = 0; i<array.length(); i++) {

            getPostAdapter GetDataAdapter2 = new getPostAdapter();

            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                GetDataAdapter2.setId(json.getInt(JSON_ID));

                GetDataAdapter2.setUnique_id(json.getString(JSON_NAME));

                GetDataAdapter2.setContent(json.getString(JSON_SUBJECT));

                GetDataAdapter2.setImage(json.getString(JSON_PHONE_NUMBER));

            } catch (JSONException e) {

                e.printStackTrace();
            }
            GetDataAdapter1.add(GetDataAdapter2);
        }

        recyclerViewadapter = new recyclerViewPostAdapter(GetDataAdapter1, this);

        recyclerView.setAdapter(recyclerViewadapter);

    }

    public void delete_post () {
        Toast.makeText(ProfileActivity.this, "Eliminando", Toast.LENGTH_LONG).show();


    }


    public void borrarPost(final String id) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://vlover.ruvnot.com/delete_post.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    // jika tidak ada eror, mulai mengeksekusi proses mengam data
                    if (!error) {

                    } else {
                        // Error in login. Get the error message
                        // Jika terjadi error dalam pengambilan data
                        String errorMsg = jsonObject.getString("error_msg");

                    }
                } catch (JSONException e) {
                    // JSON error
                    // Jika terjadi eror pada proses json
                    e.printStackTrace();
                    // Toast.makeText(recyclerViewPostAdapter.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();


                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // terjadi ketidak sesuain data user pada saat login
                //Log.e(TAG, "Login Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("_id", id);


                return params;
            }

        };
        // Adding request to request queue
        // menambahkan request dalam antrian system request data
        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



}