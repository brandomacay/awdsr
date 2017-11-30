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

import java.util.HashMap;

import vlover.android.ec.Login.IniciarSesion;
import vlover.android.ec.R;
import vlover.android.ec.editAccount;
import vlover.android.ec.services.SQLite;
import vlover.android.ec.services.Session;


public class    MiCuentaFragment extends Fragment {

    View mView;
    Button cerrar;
    ImageButton edit_account_ib;
    private TextView emailview,nameview;
    private SQLite dbsqlite;
    private Session session;

    public MiCuentaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_mi_cuenta, container, false);

        emailview = (TextView) mView.findViewById(R.id.emaildelusuario);
        nameview = (TextView) mView.findViewById(R.id.nombres);


        //mostrardatodeusuario();
        dbsqlite = new SQLite(getContext());

        // session manager
        session = new Session(getContext());

        mostrardatodeusuario();




        edit_account_ib = mView.findViewById(R.id.edit_account);
        edit_account_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), editAccount.class);
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


        return mView;
    }


    private void  mostrardatodeusuario() {

        HashMap<String, String> user = dbsqlite.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");
        nameview.setText(name);
        emailview.setText(email);
    }

    public void logout(){
        session.setLogin(false);

        dbsqlite.deleteUsers();
       salir();

    }

    private void salir(){
        Intent intent = new Intent(getContext(), IniciarSesion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void cerrar_sesion() {
        AlertDialog.Builder myBulid = new AlertDialog.Builder(getContext()).setCancelable(false);
        myBulid.setMessage(getString(R.string.enverdaddeceacerrarsesion));
        myBulid.setIcon(R.mipmap.ic_launcher);
        myBulid.setTitle(getString(R.string.cerrarsesion));
        myBulid.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        myBulid.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = myBulid.create();
        dialog.show();
    }


}
