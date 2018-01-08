package vlover.android.ec.Fragmentos;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import vlover.android.ec.Adapters.getPostAdapter;
import vlover.android.ec.R;
import vlover.android.ec.Service.SQLite;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificacionesFragment extends Fragment {

    View nfView;

    private SQLite dbsqlite;
    String unique_id;

    ProgressDialog cargando;

    List<getPostAdapter> GetDataAdapter1;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;

    RecyclerView.Adapter recyclerViewadapter;


    public NotificacionesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        nfView = inflater.inflate(R.layout.fragment_notificaciones, container, false);

                dbsqlite = new SQLite(getContext());
        cargando = new ProgressDialog(getContext());
        HashMap<String, String> user = dbsqlite.getUserDetails();
        // Inflate the layout for this fragment
        return nfView;


    }

}
