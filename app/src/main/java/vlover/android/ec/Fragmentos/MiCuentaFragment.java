package vlover.android.ec.Fragmentos;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vlover.android.ec.Login.IniciarSesion;
import vlover.android.ec.R;
import vlover.android.ec.editAccount;

/**
 * A simple {@link Fragment} subclass.
 */
public class MiCuentaFragment extends Fragment {

    View mView;
    Button cerrar;
    ImageButton edit_account_ib;
    TextView emailview,userid;
    public MiCuentaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_mi_cuenta, container, false);

        edit_account_ib = mView.findViewById(R.id.edit_account);
        edit_account_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), editAccount.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        });

        cerrar = mView.findViewById(R.id.cerrar_sesion);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            cerrar_sesion();
            }
        });
        emailview = mView.findViewById(R.id.emaildelusuario);
        userid = mView.findViewById(R.id.uid);
        mostrardatodeusuario();
        return mView;
    }


    private void  mostrardatodeusuario() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            emailview.setText(firebaseUser.getEmail());
            userid.setText(firebaseUser.getUid());
        }
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        salir();
    }

    private void salir(){
        Intent intent = new Intent(getContext(), IniciarSesion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void cerrar_sesion() {
        AlertDialog.Builder myBulid = new AlertDialog.Builder(getContext()).setCancelable(false);
        myBulid.setMessage("¿En verdad deseas cerrar sesion?");
        myBulid.setIcon(R.mipmap.ic_launcher);
        myBulid.setTitle("Cerrar sesion");
        myBulid.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //mProgressDialog.setMessage("Cerrando sesion...");
                //logout
                logout();
            }
        });
        myBulid.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = myBulid.create();
        dialog.show();
    }


}
