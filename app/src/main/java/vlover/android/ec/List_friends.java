package vlover.android.ec;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

import vlover.android.ec.Adapters.getFriendsAdapter;
import vlover.android.ec.Adapters.getNotificationsAdapter;
import vlover.android.ec.Adapters.recyclerViewFriendsAdapter;
import vlover.android.ec.Adapters.recyclerViewNotificationsAdapter;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;

public class List_friends extends AppCompatActivity {

    private SQLite dbsqlite;
    String email, unique_id;

    ProgressDialog cargando;

    List<getFriendsAdapter> GetDataAdapter1;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;

    RecyclerView.Adapter recyclerViewadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friends);

        dbsqlite = new SQLite(this);
        cargando = new ProgressDialog(this);
        HashMap<String, String> user = dbsqlite.getUserDetails();

        email = user.get("email");
        unique_id = user.get("uid");


        GetDataAdapter1 = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_friends);


        // progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //button = (Button)findViewById(R.id.button) ;

        recyclerView.setHasFixedSize(true);

        recyclerViewlayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        get_all_friens();
    }

    private void get_all_friens() {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://vlover.ruvnot.com/getAllFriendsByEmail.php", new Response.Listener<String>() {

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

                        JSONObject postt = jsonObject.getJSONObject("friends");


                        JSONArray pray = postt.getJSONArray("user_send");
                        //  n_publicaciones.setText("" + pray.length());


                        for (int i = 0; i < pray.length(); i++) {


                            getFriendsAdapter GetDataAdapter2 = new getFriendsAdapter();

                            //JSONArray objPid = postt.getJSONArray("pid");
                            //final String pid = objPid.getString(i);

                            JSONArray objname = postt.getJSONArray("name");
                            String name = objname.getString(i);

                            JSONArray objunique_id = postt.getJSONArray("unique_id");
                            String uniq = objunique_id.getString(i);

                            JSONArray objuserimage = postt.getJSONArray("avatar");
                            String userimage = getString(R.string.url_global) + "uploads/" +
                                    uniq + "/avatar/" + "small_" + objuserimage.getString(i);

  //                          JSONArray objcontent = postt.getJSONArray("accepted");
//                            String content = objcontent.getString(i);


                           // JSONArray objdate = postt.getJSONArray("accepted");


                            JSONArray objuser_send = postt.getJSONArray("user_send");
                            String user_sendd = objuser_send.getString(i);

                            //GetDataAdapter2.setUserSend(user_sendd);

                            GetDataAdapter2.setUnique_id(name);

                            GetDataAdapter2.setUserImage(userimage);

                            //GetDataAdapter2.setContent(content);

                            //GetDataAdapter2.setImage("");


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

                        recyclerViewadapter = new recyclerViewFriendsAdapter(GetDataAdapter1, List_friends.this);

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
                        Toast.makeText(List_friends.this,
                                errorMsg, Toast.LENGTH_LONG).show();
                        cargando.dismiss();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(List_friends.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    cargando.dismiss();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(List_friends.this,
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
