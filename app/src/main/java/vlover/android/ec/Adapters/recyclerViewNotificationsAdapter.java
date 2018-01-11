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

import static android.content.ContentValues.TAG;
//import static java.security.AccessController.getContext;

public class recyclerViewNotificationsAdapter extends RecyclerView.Adapter<recyclerViewNotificationsAdapter.ViewHolder> {

    private Context context;

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

        holder.IdTextView.setText(String.valueOf(getDataAdapter1.getId()));

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

        public TextView IdTextView;
        public TextView unique_idTextView;
        public ImageView userimageTextView;
        public FloatingActionButton imageTextView, opciones;
        public SwipeRefreshLayout refreshLayout;

        public ViewHolder(View itemView) {

            super(itemView);

            IdTextView = (TextView) itemView.findViewById(R.id.textView2);
            IdTextView.setVisibility(View.GONE);

            unique_idTextView = (TextView) itemView.findViewById(R.id.textView4);


            userimageTextView = (ImageView) itemView.findViewById(R.id.post_user_image);
            imageTextView = (FloatingActionButton) itemView.findViewById(R.id.textView6);
            imageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(context, "Aceptando...", Toast.LENGTH_SHORT).show();





                }
            });

            opciones = (FloatingActionButton) itemView.findViewById(R.id.options);
            opciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(context, "Rechazando...", Toast.LENGTH_SHORT).show();





                }
            });


        }

        public void itemClean() {
            getDataAdapter.remove(getAdapterPosition());
            // notifyDataSetChanged();
            notifyItemChanged(getAdapterPosition());
        }

        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                Toast.makeText(context, "Ya puedes alamacenar imagenes, intenta de nuevo", Toast.LENGTH_SHORT).show();
                getNotificationsAdapter getDataAdapter1 = getDataAdapter.get(getAdapterPosition());
                String IMG = getDataAdapter1.getImage();

                file_download(IMG);
            }
        }

        public boolean isStoragePermissionGranted() {
            if (Build.VERSION.SDK_INT >= 23) {
                if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission is granted");
                    return true;
                } else {

                    Log.v(TAG, "Permission is revoked");
                    // context.startActivity(new Intent(context, ProfileActivity.class));

                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return false;
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                Log.v(TAG, "Permission is granted");
                return true;
            }
        }


    }


    public void editPost() {

    }

    private void borrarPost(final String id) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "URLEMISOR", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");


                    if (!error) {
                        Toast.makeText(context, "Post borrado! exitosamente", Toast.LENGTH_LONG).show();
                    } else {

                        String errorMsg = jsonObject.getString("error_msg");

                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();


                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("_id", id);
                return params;
            }

        };

        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void file_download(String uRl) {


        File direct = new File(Environment.getExternalStorageDirectory()
                + "/vlover");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Descargando imagen...")
                .setDestinationInExternalPublicDir("/vlover", "test.jpg");

        mgr.enqueue(request);

    }





}