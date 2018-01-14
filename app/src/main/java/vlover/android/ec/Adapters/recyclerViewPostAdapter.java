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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.MyProfile.ProfileActivity;
import vlover.android.ec.R;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.User.UserActivity;

import static android.content.ContentValues.TAG;
//import static java.security.AccessController.getContext;

public class recyclerViewPostAdapter extends RecyclerView.Adapter<recyclerViewPostAdapter.ViewHolder> {

    private Context context;

    List<getPostAdapter> getDataAdapter;

    public recyclerViewPostAdapter(List<getPostAdapter> getDataAdapter, Context context){

        super();

        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void addAll(List<getPostAdapter> post) {
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

        getPostAdapter getDataAdapter1 =  getDataAdapter.get(position);

        holder.unique_idTextView.setText(getDataAdapter1.getUnique_id());

        holder.IdTextView.setText(String.valueOf(getDataAdapter1.getId()));

        String _userimg = getDataAdapter1.getUserImage();

        if (!_userimg.isEmpty()) {
          //  holder.userimageTextView.setVisibility(View.VISIBLE);

            Picasso.with(context)
                    .load(_userimg)
                    .fit()
                    .centerInside()
                    .into(holder.userimageTextView);
        }
        else {
          //  holder.imageTextView.setVisibility(View.GONE);
        }

        String _img = getDataAdapter1.getImage();

        if (!_img.isEmpty()) {
            holder.imageTextView.setVisibility(View.VISIBLE);

            Picasso.with(context)
                    .load(_img)
                    .fit()
                    //.resize(600,6000)
                    .centerInside()
                    //.placeholder(R.drawable.agregar_imagen)
                    //.error(R.drawable.vlover)
                    //.networkPolicy(NetworkPolicy.NO_STORE)
                    //.memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(holder.imageTextView);
        }
        else {
            holder.imageTextView.setVisibility(View.GONE);
        }

        //holder.imageTextView.setText(getDataAdapter1.getImage());
        holder.contentTextView.setText(getDataAdapter1.getContent());
        holder.dateTextView.setText(getDataAdapter1.getDate());
        String descripcion = getDataAdapter1.getContent();
        if (!descripcion.isEmpty()) {
            holder.contentTextView.setVisibility(View.VISIBLE);

        } else {
            holder.contentTextView.setVisibility(View.GONE);
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
        public ImageView imageTextView;
        public TextView contentTextView;
        public TextView dateTextView;
        public ImageButton opciones;
        public SwipeRefreshLayout refreshLayout;

        public ViewHolder(View itemView) {

            super(itemView);

            IdTextView = (TextView) itemView.findViewById(R.id.textView2);

            IdTextView.setVisibility(View.GONE);
            unique_idTextView = (TextView) itemView.findViewById(R.id.textView4);

            String uid = unique_idTextView.getText().toString();
            Intent intent;
            intent = new Intent(context, UserActivity.class);
            intent.putExtra("unique_id", uid);
            userimageTextView = (ImageView) itemView.findViewById(R.id.post_user_image);
            imageTextView = (ImageView) itemView.findViewById(R.id.textView6);
            imageTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final String[] option = new String[]{"Eliminar", "Editar", "Descargar", "Reportar",
                    };
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                            android.R.layout.select_dialog_item, option);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    //  builder.setTitle("Selecciona una Opcion");
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            if (which == 0) {
                                String ide = IdTextView.getText().toString();
                                borrarPost(ide);
                                dialog.dismiss();
                                getDataAdapter.remove(getAdapterPosition());
                                notifyDataSetChanged();
                                notifyItemChanged(getAdapterPosition());
                                dialog.dismiss();
                                Toast.makeText(context, "Borrando...", Toast.LENGTH_SHORT).show();

                                //getDataAdapter.notify();
                            } else if (which == 1) {
                            } else if (which == 2) {

                                if (isStoragePermissionGranted()) {
                                    Toast.makeText(context, "Descargando", Toast.LENGTH_SHORT).show();
                                    getPostAdapter getDataAdapter1 = getDataAdapter.get(getAdapterPosition());
                                    String IMG = getDataAdapter1.getImage();

                                    file_download(IMG);
                                }

                            } else if (which == 3) {
                            }
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
            contentTextView = (TextView) itemView.findViewById(R.id.textView8);
            dateTextView = (TextView) itemView.findViewById(R.id.textView10);
            opciones = (ImageButton) itemView.findViewById(R.id.options);
            opciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String[] option = new String[]{"Eliminar", "Editar", "Reportar",
                    };
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                            android.R.layout.select_dialog_item, option);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    //  builder.setTitle("Selecciona una Opcion");
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            if (which == 0) {
                                String ide = IdTextView.getText().toString();
                                borrarPost(ide);
                                Toast.makeText(context, "Borrando...", Toast.LENGTH_SHORT).show();

                            } else if (which == 1) {
                            } else if (which == 2) {
                            }
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
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
                getPostAdapter getDataAdapter1 = getDataAdapter.get(getAdapterPosition());
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
        private void borrarPost(final String id) {
            // Tag used to cancel the request
            String tag_string_req = "req_login";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    "http://vlover.ruvnot.com/delete_post.php", new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");


                        if (!error) {
                            getDataAdapter.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            notifyItemChanged(getAdapterPosition());
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

    }


    public void editPost() {

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