package vlover.android.ec;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * Created by JUNED on 6/16/2016.
 */
public class recyclerViewPostAdapter extends RecyclerView.Adapter<recyclerViewPostAdapter.ViewHolder> {

    Context context;

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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        getPostAdapter getDataAdapter1 =  getDataAdapter.get(position);

        holder.unique_idTextView.setText(getDataAdapter1.getUnique_id());

        holder.IdTextView.setText(String.valueOf(getDataAdapter1.getId()));

        holder.imageTextView.setText(getDataAdapter1.getImage());

        holder.contentTextView.setText(getDataAdapter1.getContent());

    }

    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView IdTextView;
        public TextView unique_idTextView;
        public TextView imageTextView;
        public TextView contentTextView;


        public ViewHolder(View itemView) {

            super(itemView);

            IdTextView = (TextView) itemView.findViewById(R.id.textView2) ;
            unique_idTextView = (TextView) itemView.findViewById(R.id.textView4) ;
            imageTextView = (TextView) itemView.findViewById(R.id.textView6) ;
            contentTextView = (TextView) itemView.findViewById(R.id.textView8) ;


        }
    }
}
