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
        public ViewHolder(View itemView) {

            super(itemView);

            IdTextView = (TextView) itemView.findViewById(R.id.textView2);

            IdTextView.setVisibility(View.GONE);
            unique_idTextView = (TextView) itemView.findViewById(R.id.textView4);

            userimageTextView = (ImageView) itemView.findViewById(R.id.post_user_image);
            imageTextView = (ImageView) itemView.findViewById(R.id.textView6);
            contentTextView = (TextView) itemView.findViewById(R.id.textView8);

        }

        public void itemClean() {
            getDataAdapter.remove(getAdapterPosition());
            // notifyDataSetChanged();
            notifyItemChanged(getAdapterPosition());
        }



    }


    public void editPost() {

    }
}