package vlover.android.ec.Fragmentos


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import vlover.android.ec.R


/**
 * A simple [Fragment] subclass.
 */
class ErrorInternetFragment : Fragment() {



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_error_internet, container, false)
    }

}// Required empty public constructor
