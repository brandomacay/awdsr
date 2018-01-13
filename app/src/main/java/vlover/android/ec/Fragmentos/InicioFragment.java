package vlover.android.ec.Fragmentos;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vlover.android.ec.Adapters.getPostAdapter;
import vlover.android.ec.Adapters.recyclerViewPostAdapter;
import vlover.android.ec.Edition.Account;
import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.MyProfile.ProfileActivity;
import vlover.android.ec.Post.PostActivity;
import vlover.android.ec.R;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;

/**
 * A simple {@link Fragment} subclass.
 */
public class InicioFragment extends Fragment {

    View mView;
    TextView n_publicaciones;
    private SQLite dbsqlite;
    String unique_id, email;

    ProgressDialog cargando;

    List<getPostAdapter> GetDataAdapter1;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;

    RecyclerView.Adapter recyclerViewadapter;
    //ProgressBar progressBar;

    SwipeRefreshLayout reload;

    String GET_JSON_DATA_HTTP_URL = "http://vlover.ruvnot.com/get_post_byuser.php";
    String JSON_ID = "id";
    String JSON_NAME = "name";
    String JSON_SUBJECT = "subject";
    String JSON_PHONE_NUMBER = "phone_number";

    // Button button;

    JsonArrayRequest jsonArrayRequest;

    RequestQueue requestQueue;

    ImageButton opciones;
    FloatingActionButton fab;

    public InicioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.activity_profile, container, false);
        fab = mView.findViewById(R.id.floating_action_button);
        reload = (SwipeRefreshLayout) mView.findViewById(R.id.recargar);
        reload.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final int DURACION = 3 * 1000;
                try {
                    Thread.sleep(DURACION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                get_all_post();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()){
                    Intent i = new Intent(getContext(), PostActivity.class);
                    startActivity(i);
                }else{
                    Snackbar.make(view, "Accion denegada. Sin Internet!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null)
                            .show();         }

            }
        });


        dbsqlite = new SQLite(getContext());
        cargando = new ProgressDialog(getContext());
        HashMap<String, String> user = dbsqlite.getUserDetails();
        n_publicaciones = (TextView) mView.findViewById(R.id.tv_publicaciones);
        unique_id = user.get("uid").toString();
        email = user.get("email");


        GetDataAdapter1 = new ArrayList<>();

        recyclerView = (RecyclerView) mView.findViewById(R.id.activity_profile_recyclerview);
        fab = (FloatingActionButton) mView.findViewById(R.id.floating_action_button);

        opciones = (ImageButton) mView.findViewById(R.id.options);

        // progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //button = (Button)findViewById(R.id.button) ;

        recyclerView.setHasFixedSize(true);

        recyclerViewlayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        //recyclerView.isScrollbarFadingEnabled();

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

        return mView;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void get_all_post() {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://vlover.ruvnot.com/getAllPostByUser.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {
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


                        for (int i = 0; i < pray.length(); i++) {


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


                            String image;
                            if (objimg.getString(i).isEmpty()) {
                                image = "";
                            }
                            else {
                                image = getString(R.string.url_global) + "uploads/" +
                                        unique_id + "/post/" + objimg.getString(i);
                            }

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

                        reload.setRefreshing(false);

                        recyclerViewadapter = new recyclerViewPostAdapter(GetDataAdapter1, getContext());

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
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        cargando.dismiss();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    cargando.dismiss();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                cargando.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_email", email);
                //params.put("image", url_image);
                //params.put("content", descripcion_post.getText().toString());
                //params.put("datetime", "fecha de prueba");
                return params;

            }

        };

        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void JSON_DATA_WEB_CALL() {

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

        requestQueue = Volley.newRequestQueue(getContext());

        requestQueue.add(jsonArrayRequest);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {

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

        recyclerViewadapter = new recyclerViewPostAdapter(GetDataAdapter1, getContext());

        recyclerView.setAdapter(recyclerViewadapter);

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
