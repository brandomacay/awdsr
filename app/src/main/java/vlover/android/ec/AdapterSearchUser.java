package vlover.android.ec;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSearchUser extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<DataSearchUser> data= Collections.emptyList();
    DataSearchUser current;
    int poss;

    // create constructor to initialize context and data sent from MainActivity
    public AdapterSearchUser(Context context, List<DataSearchUser> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.container_search_user, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        DataSearchUser current=data.get(position);
        myHolder.textUserName.setText(current.userName);

        myHolder.textUserEmail.setText(current.userEmail);


       // myHolder.imageUserAvatar.setpic(current.userAvatar);
        Picasso.with(this.context)
                .load(current.userAvatar)
                .resize(200, 200)
                .centerCrop()
                .into(myHolder.imageUserAvatar );

        //myHolder.textPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        //poss = position;

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }




    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textUserName;

        TextView textUserEmail;

        CircleImageView imageUserAvatar;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textUserName = (TextView) itemView.findViewById(R.id.search_user_name);

            textUserEmail = (TextView) itemView.findViewById(R.id.search_user_email);

            imageUserAvatar = (CircleImageView) itemView.findViewById(R.id.circleImageView_search_user);

            itemView.setOnClickListener(this);
        }

        // Click event for all items
        @Override
        public void onClick(View v) {
            String s;

            s = textUserEmail.getText().toString();

            Toast.makeText(context, "email: " + s , Toast.LENGTH_SHORT).show();
            data.clear();



          //  MainActivity mActivity= new MainActivity();
           // mActivity.clean_response();

           // MainActivity.clean_results();
           // data.notifyAll();




        }

    }

}
