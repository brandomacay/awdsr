package vlover.android.ec.Adapters;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.List;
import java.util.Map;

import vlover.android.ec.R;
import vlover.android.ec.Service.Controller;
import vlover.android.ec.Service.SQLite;

/**
 * Created by Vlover on 12/01/2018.
 */

public class recyclerViewFriendsAdapter extends RecyclerView.Adapter<recyclerViewFriendsAdapter.ViewHolder> {
    private Context context;
    List<getFriendsAdapter> getDataAdapter;


    public recyclerViewFriendsAdapter(List<getFriendsAdapter> getDataAdapter, Context context) {
        super();

        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_friends_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void addAll(List<getFriendsAdapter> post) {
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
    public void onBindViewHolder(recyclerViewFriendsAdapter.ViewHolder holder, int position) {

        getFriendsAdapter getDataAdapter1 = getDataAdapter.get(position);

        holder.unique_idTextView.setText(getDataAdapter1.getUnique_id());

        //holder.IdTextView.setText(String.valueOf(getDataAdapter1.getUserSend()));

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
        } else {
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

                }
            });

            opciones = (FloatingActionButton) itemView.findViewById(R.id.options);
            opciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }

        public void itemClean() {
            getDataAdapter.remove(getAdapterPosition());
            // notifyDataSetChanged();
            notifyItemChanged(getAdapterPosition());
        }


    }

}
