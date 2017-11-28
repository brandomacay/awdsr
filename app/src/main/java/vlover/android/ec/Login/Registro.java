package vlover.android.ec.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.R;

public class Registro extends AppCompatActivity {
    //DECLARE FIELDS
    EditText emailDeUsuario, claveDeUsuario, nombresDeUsuario;

    Button crearCuenta;

    //FIREBASE AUTHENTICATION ID
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    //PROGRESS DIALOG
    ProgressDialog mProgressDialog;

    private AwesomeValidation awesomeValidation;
    List<String> list;
    ArrayAdapter<String> spinner_adapter_genre;
    Spinner genre_spin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        emailDeUsuario = findViewById(R.id.emailRegisterEditText);
        claveDeUsuario = findViewById(R.id.passwordRegisterEditText);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.emailRegisterEditText, Patterns.EMAIL_ADDRESS, R.string.error_email);

        crearCuenta = findViewById(R.id.registrar);

        //PROGRESS DIALOG INSTANCE
        mProgressDialog = new ProgressDialog(this);

        //FIREBASE INSTANCE
        mAuth = FirebaseAuth.getInstance();

        genre_spin = findViewById(R.id.edit_account_genre_spin);
        load_spin_genre();

        //Crear Cuenta BTN OnClickListener
        crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarSiLosCamposEstanCompletados();
                /*mProgressDialog.setTitle("Creando cuenta");
                mProgressDialog.setMessage("Por favor espere...");
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);*/

            }
        });

        crearCuenta.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(Registro.this, "HolaMundo!", Toast.LENGTH_LONG).show();
                return true;
            }
        });

    }

    private void verificarSiLosCamposEstanCompletados() {
        //primero valida si los campos estan correctos
        //si todo esta bien nos pasa a la siguiente activity
        String emailvacio = emailDeUsuario.getText().toString();
        if (emailvacio.equalsIgnoreCase("")) {
            emailDeUsuario.setError(getString(R.string.ingresatucorreo));
        } else if (awesomeValidation.validate()) {
            String campoclave = claveDeUsuario.getText().toString();
            if (campoclave.equalsIgnoreCase("")) {
                claveDeUsuario.setError(getString(R.string.ingresatuclave));
            } else if (isNetworkConnected()) {
                startActivity(new Intent(Registro.this, MainActivity.class));
            } else {
                Toast.makeText(this, getString(R.string.error_internet), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void load_spin_genre() {


        list = new ArrayList<String>();
        list.add(0, getString(R.string.male));
        list.add(1, getString(R.string.female));
        spinner_adapter_genre = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);

        spinner_adapter_genre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genre_spin.setAdapter(spinner_adapter_genre);
        genre_spin.setWillNotDraw(false);

    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
