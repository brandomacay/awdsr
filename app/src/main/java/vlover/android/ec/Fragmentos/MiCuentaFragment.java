package vlover.android.ec.Fragmentos;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import vlover.android.ec.Edition.Account;
import vlover.android.ec.Login.IniciarSesion;
import vlover.android.ec.MyProfile.ProfileActivity;
import vlover.android.ec.R;
import vlover.android.ec.Service.Address;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;
import vlover.android.ec.Service.Session;


public class   MiCuentaFragment extends Fragment {

    View mView;
    Button cerrar, edit_account_ib;
    private TextView emailview, nameview;
    private SQLite dbsqlite;
    private Session session;
    ProgressDialog cargando;
    String email_user = "", uniqueid = "";
    CircleImageView user_image;
    List<String> list;
    RelativeLayout bloqueo;
    RelativeLayout loading;
    FloatingActionButton fabreload;
    NestedScrollView linear;
    private CardView micuenta;



    public MiCuentaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_mi_cuenta, container, false);
        bloqueo = (RelativeLayout)mView.findViewById(R.id.error);
        loading = (RelativeLayout)mView.findViewById(R.id.cargando);
        fabreload= (FloatingActionButton)mView.findViewById(R.id.fabR);
        emailview = (TextView) mView.findViewById(R.id.emaildelusuario);
        linear = (NestedScrollView) mView.findViewById(R.id.linear);
        nameview = (TextView) mView.findViewById(R.id.nombres);
        user_image = (CircleImageView) mView.findViewById(R.id.myaccount_user_image_iv);
        micuenta = (CardView) mView.findViewById(R.id.my_profile);
        dbsqlite = new SQLite(getContext());

        // session manager
        session = new Session(getContext());


        edit_account_ib = mView.findViewById(R.id.edit_account);
        edit_account_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkConnected()){
                    Intent i = new Intent(getContext(), Account.class);
                    startActivity(i);
                }else{
                    Snackbar.make(view, "Sin Internet!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();         }



            }
        });

        cerrar = mView.findViewById(R.id.cerrar_sesion);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            cerrar_sesion();
            }
        });

        cargando = new ProgressDialog(getActivity());

        micuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        fabreload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()){
                    linear.setVisibility(View.VISIBLE);
                    bloqueo.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);
                    onResume();
                }else{
                    Toast.makeText(getContext(),getString(R.string.error_internet),Toast.LENGTH_LONG).show();
                }
            }
        });

        return mView;
    }

    private void  mostrardatodeusuario() {

        HashMap<String, String> user = dbsqlite.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");
        //nameview.setText(name);
        emailview.setText(email);
        getProfile(email);
    }

    /*private void condicionInternet(){
        if(isNetworkConnected()){
            bloqueo.setVisibility(View.INVISIBLE);
            mostrardatodeusuario();

        }else{
            bloqueo.setVisibility(View.VISIBLE);
        }
    }*/





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


                        String uid = jsonObject.getString("uid");

                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");
                        uniqueid = uid;
                        email_user = user.getString("email");
                        /*String phonecode = user.getString("phonecode");
                        String phone = user.getString("phone");
                        String genre = user.getString("genre");
                        String country = user.getString("country");
                        String created_at = user.getString("created_at");*/

                        if (!user.getString("avatar").isEmpty()) {
                            String avatar = getString(R.string.url_global)+"uploads/"+
                                    uniqueid + "/avatar/" + "small_" + user.getString("avatar");
                                Picasso.with(getActivity())
                                        .load(avatar)
                                        .resize(200, 200)
                                        .centerCrop()
                                        .into(user_image);


                        }

                        loading.setVisibility(View.INVISIBLE);
                        nameview.setText(name);
                        /*phoneview.setText("+" + phonecode + " " + phone);
                        generoview.setText(genre);
                        adressview.setText(country);*/

                       } else {
                        // Error in login. Get the error message
                        // Jika terjadi error dalam pengambilan data
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        linear.setVisibility(View.INVISIBLE);
                        loading.setVisibility(View.INVISIBLE);
                        bloqueo.setVisibility(View.VISIBLE);
                    }
                }  catch (JSONException e) {
                    // JSON error
                    // Jika terjadi eror pada proses json
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.INVISIBLE);
                    bloqueo.setVisibility(View.VISIBLE);
                    linear.setVisibility(View.INVISIBLE);


                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // terjadi ketidak sesuain data user pada saat login
                //Log.e(TAG, "Login Error: " + error.getMessage());
                bloqueo.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                linear.setVisibility(View.INVISIBLE);
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
        // Adding request to request queue
        // menambahkan request dalam antrian system request data
        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void logout(){
        session.setLogin(false);

        dbsqlite.deleteUsers();
       salir();

    }
    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void salir(){
        Intent intent = new Intent(getContext(), IniciarSesion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void cerrar_sesion() {
        AlertDialog.Builder myBulid = new AlertDialog.Builder(getContext()).setCancelable(false);
        myBulid.setMessage(getString(R.string.enverdaddeceacerrarsesion));
        myBulid.setIcon(R.mipmap.ic_launcher);
        myBulid.setTitle(getString(R.string.cerrarsesion));
        myBulid.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        myBulid.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = myBulid.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        //Log.e("DEBUG", "onResume of LoginFragment");
        super.onResume();
        mostrardatodeusuario();
        //condicionInternet();

    }
}
