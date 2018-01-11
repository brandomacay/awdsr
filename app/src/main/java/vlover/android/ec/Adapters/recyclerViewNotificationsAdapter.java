package vlover.android.ec.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.MyProfile.ProfileActivity;
import vlover.android.ec.R;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;

import static android.content.ContentValues.TAG;
//import static java.security.AccessController.getContext;

public class recyclerViewNotificationsAdapter extends RecyclerView.Adapter<recyclerViewNotificationsAdapter.ViewHolder> {

    private Context context;
    String aceptar = "1", rechazar = "2";
    List<getNotificationsAdapter> getDataAdapter;

    public recyclerViewNotificationsAdapter(List<getNotificationsAdapter> getDataAdapter, Context context) {

        super();

        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_notifications_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void addAll(List<getNotificationsAdapter> post) {
        getDataAdapter.addAll(post);
        notifyDataSetChanged();
    }

    public void clear() {
        getDataAdapter.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        getDataAdapter.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        getNotificationsAdapter getDataAdapter1 = getDataAdapter.get(position);

        holder.unique_idTextView.setText(getDataAdapter1.getUnique_id());

        holder.IdTextView.setText(String.valueOf(getDataAdapter1.getUserSend()));

        String _userimg = getDataAdapter1.getUserImage();

        if (!_userimg.isEmpty()) {
            //  holder.userimageTextView.setVisibility(View.VISIBLE);

            Picasso.with(context)
                    .load(_userimg)
                    .fit()
                    //.resize(600,6000)
                    .centerInside()
                    //.placeholder(R.drawable.agregar_imagen)
                    //.error(R.drawable.vlover)
                    //.networkPolicy(NetworkPolicy.NO_STORE)
                    //.memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(holder.userimageTextView);
        }
        else {
            //  holder.imageTextView.setVisibility(View.GONE);
        }




        // holder.deletePost.setOnClickListener(context);
    }


    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private SQLite dbsqlite;

        public TextView IdTextView;
        public TextView unique_idTextView;
        public ImageView userimageTextView;
        public FloatingActionButton imageTextView, opciones;
        public SwipeRefreshLayout refreshLayout;

        String myemail;
        public ViewHolder(View itemView) {

            super(itemView);

            IdTextView = (TextView) itemView.findViewById(R.id.textView2);
            IdTextView.setVisibility(View.GONE);
            dbsqlite = new SQLite(context);

            HashMap<String, String> user = dbsqlite.getUserDetails();
            myemail = user.get("email");
            unique_idTextView = (TextView) itemView.findViewById(R.id.textView4);


            userimageTextView = (ImageView) itemView.findViewById(R.id.post_user_image);
            imageTextView = (FloatingActionButton) itemView.findViewById(R.id.textView6);
            imageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    aceptar_solicitud();

                }
            });

            opciones = (FloatingActionButton) itemView.findViewById(R.id.options);
            opciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    rechazar_solicitud();


                }
            });


        }

        public void itemClean() {
            getDataAdapter.remove(getAdapterPosition());
            // notifyDataSetChanged();
            notifyItemChanged(getAdapterPosition());
        }


        public void aceptar_solicitud() {
            // Tag used to cancel the request
            String tag_string_req = "req_login";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    context.getString(R.string.url_global) + "friendship_state.php", new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    //Log.d(TAG, "Login Response: " + response.toString());

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        // Check for error node in json
                        // jika tidak ada eror, mulai mengeksekusi proses mengam data
                        if (!error) {
                            //exito
                            getDataAdapter.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            notifyItemChanged(getAdapterPosition());
                            Toast.makeText(context, "Aceptado", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error in login. Get the error message
                            // Jika terjadi error dalam pengambilan data
                            String errorMsg = jsonObject.getString("error_msg");
                            Toast.makeText(context,
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // JSON error
                        // Jika terjadi eror pada proses json
                        e.printStackTrace();
                        Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // terjadi ketidak sesuain data user pada saat login
                    //Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast.makeText(context,
                            error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_get", myemail);
                    params.put("user_send", IdTextView.getText().toString());
                    params.put("choice", aceptar);


                    return params;
                }

            };


            Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

        public void rechazar_solicitud() {
            // Tag used to cancel the request
            String tag_string_req = "req_login";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    context.getString(R.string.url_global) + "friendship_state.php", new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    //Log.d(TAG, "Login Response: " + response.toString());

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        // Check for error node in json
                        // jika tidak ada eror, mulai mengeksekusi proses mengam data
                        if (!error) {
                            //exito
                            getDataAdapter.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            notifyItemChanged(getAdapterPosition());
                            Toast.makeText(context, "Rechazado", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error in login. Get the error message
                            // Jika terjadi error dalam pengambilan data
                            String errorMsg = jsonObject.getString("error_msg");
                            Toast.makeText(context,
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // JSON error
                        // Jika terjadi eror pada proses json
                        e.printStackTrace();
                        Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // terjadi ketidak sesuain data user pada saat login
                    //Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast.makeText(context,
                            error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_get", myemail);
                    params.put("user_send", IdTextView.getText().toString());
                    params.put("choice", rechazar);


                    return params;
                }

            };


            Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
        }


    }

}