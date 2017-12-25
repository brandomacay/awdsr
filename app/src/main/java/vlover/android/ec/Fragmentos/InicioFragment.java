package vlover.android.ec.Fragmentos;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vlover.android.ec.Edition.Account;
import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.Post.PostActivity;
import vlover.android.ec.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InicioFragment extends Fragment {

    View mView;
    FloatingActionButton fab;

    public InicioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_inicio, container, false);
        fab= mView.findViewById(R.id.post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()){
                    Intent i = new Intent(getContext(), PostActivity.class);
                    startActivity(i);
                }else{
                    Snackbar.make(view, "Accion denegada. Sin Internet!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null)
                            .show();         }

            }
        });
        return mView;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
