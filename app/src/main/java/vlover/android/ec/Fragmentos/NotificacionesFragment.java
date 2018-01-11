package vlover.android.ec.Fragmentos;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vlover.android.ec.Adapters.getNotificationsAdapter;
import vlover.android.ec.Adapters.getPostAdapter;
import vlover.android.ec.Adapters.recyclerViewNotificationsAdapter;
import vlover.android.ec.Adapters.recyclerViewPostAdapter;
import vlover.android.ec.R;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificacionesFragment extends Fragment {

    View nfView;

    private SQLite dbsqlite;
    String email, unique_id;

    ProgressDialog cargando;

    List<getNotificationsAdapter> GetDataAdapter1;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;

    RecyclerView.Adapter recyclerViewadapter;


    public NotificacionesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        nfView = inflater.inflate(R.layout.fragment_notificaciones, container, false);

                dbsqlite = new SQLite(getContext());
        cargando = new ProgressDialog(getContext());
        HashMap<String, String> user = dbsqlite.getUserDetails();

        email = user.get("email").toString();
        unique_id = user.get("uid").toString();


        GetDataAdapter1 = new ArrayList<>();

        recyclerView = (RecyclerView) nfView.findViewById(R.id.activity_notifications_recyclerview);


        // progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //button = (Button)findViewById(R.id.button) ;

        recyclerView.setHasFixedSize(true);

        recyclerViewlayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        get_all_frienship_request();
        // Inflate the layout for this fragment
        return nfView;


    }

    public void get_all_frienship_request() {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://vlover.ruvnot.com/getFriendshipRequestByuser.php", new Response.Listener<String>() {

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

                        JSONObject postt = jsonObject.getJSONObject("request");


                        JSONArray pray = postt.getJSONArray("accepted");
                      //  n_publicaciones.setText("" + pray.length());


                        for (int i = 0; i < pray.length(); i++) {


                            getNotificationsAdapter GetDataAdapter2 = new getNotificationsAdapter();

                            //JSONArray objPid = postt.getJSONArray("pid");
                            //final String pid = objPid.getString(i);

                            JSONArray objname = postt.getJSONArray("name");
                            String name = objname.getString(i);

                            JSONArray objunique_id = postt.getJSONArray("unique_id");
                            String uniq = objunique_id.getString(i);

                            JSONArray objuserimage = postt.getJSONArray("avatar");
                            String userimage = getString(R.string.url_global)+"uploads/"+
                                    uniq + "/avatar/" + "small_" + objuserimage.getString(i);

                            JSONArray objcontent = postt.getJSONArray("accepted");
                            String content = objcontent.getString(i);



                            JSONArray objdate = postt.getJSONArray("accepted");

                            GetDataAdapter2.setId('0');

                            GetDataAdapter2.setUnique_id(name);

                            GetDataAdapter2.setUserImage(userimage);

                            GetDataAdapter2.setContent(content);

                            GetDataAdapter2.setImage("");


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

                       // reload.setRefreshing(false);

                        recyclerViewadapter = new recyclerViewNotificationsAdapter(GetDataAdapter1, getContext());

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
                params.put("email", email);
                //params.put("image", url_image);
                //params.put("content", descripcion_post.getText().toString());
                //params.put("datetime", "fecha de prueba");
                return params;

            }

        };

        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
