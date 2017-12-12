package vlover.android.ec.Fragmentos;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vlover.android.ec.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AvisosFragment extends Fragment {

    View mView;

    public AvisosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_avisos, container, false);

        return mView;
    }

}
