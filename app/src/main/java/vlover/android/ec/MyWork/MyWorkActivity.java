package vlover.android.ec.MyWork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
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

public class MyWorkActivity extends AppCompatActivity {
    CardView registermywork;
    ImageButton back;
    TextView open_text;
    CircleImageView open_edit;
    private SQLite dbsqlite;
    String email_user = "", unique_id = "", logo = "";
    ProgressDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_work);
        cargando = new ProgressDialog(this);
        open_edit = (CircleImageView) findViewById(R.id.post_user_image);
        open_text = (TextView) findViewById(R.id.name_business);
        registermywork = (CardView) findViewById(R.id.startRegister);
        dbsqlite = new SQLite(this);
        HashMap<String, String> user = dbsqlite.getUserDetails();
        email_user = user.get("email");
        unique_id = user.get("uid");
        open_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyWorkActivity.this, RegisterMyWorkActivity.class));
            }
        });
        open_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyWorkActivity.this, RegisterMyWorkActivity.class));
            }
        });
        back = (ImageButton) findViewById(R.id.salir);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        registermywork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyWorkActivity.this, RegisterMyWorkActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getBusiness(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        cargando.setMessage(getString(R.string.cargando));
        cargando.show();

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
                        open_text.setText(name);
                        if (!user.getString("logo").isEmpty()) {
                            String avatar = getString(R.string.url_global) + "uploads/" +
                                    unique_id + "/logobusiness/" + user.getString("logo");
                            Picasso.with(getApplication())
                                    .load(avatar)
                                    .resize(200, 200)
                                    .centerCrop()
                                    .into(open_edit);
                        }
                        /*String phone = user.getString("phone");
                        //String genre = user.getString("genre");
                        String country = user.getString("country");
                        String created_at = user.getString("created_at");*/
                        cargando.hide();
                    } else {
                        // Error in login. Get the error message
                        // Jika terjadi error dalam pengambilan data
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        cargando.hide();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    cargando.hide();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // terjadi ketidak sesuain data user pada saat login
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                cargando.hide();

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
    protected void onResume() {
        super.onResume();
        getBusiness(email_user);
    }
}
