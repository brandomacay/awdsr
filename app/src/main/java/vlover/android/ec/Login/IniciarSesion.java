package vlover.android.ec.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.R;

public class IniciarSesion extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog cargando;
    private EditText correo, contra;
    private TextView registrarse;
    private CardView iniciarsesion;
    private FirebaseAnalytics mFirebaseAnalytics;
    TextView cambiarpass;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        correo = findViewById(R.id.email);
        contra = findViewById(R.id.clave);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.error_email);
        //awesomeValidation.addValidation(this, R.id.clave, Pattern.compile("Brandon"), R.string.error_contraseña);


        cargando = new ProgressDialog(this);
        iniciarsesion = findViewById(R.id.iniciar);
        cambiarpass = findViewById(R.id.cambiar_clave);
        registrarse = findViewById(R.id.registrar);
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IniciarSesion.this, Registro.class));
            }
        });
        cambiarpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IniciarSesion.this, CambiarClave.class));
            }
        });
        cargando = new ProgressDialog(this);
        iniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verificarSiLosCamposEstanCompletados();
            }
        });

    }

    private void verificarInicioSesion() {
    }

    private void verificarSiLosCamposEstanCompletados() {
        //primero valida si los campos estan correctos
        //si todo esta bien nos pasa a la siguiente activity
        String emailvacio = correo.getText().toString();
        if (emailvacio.equalsIgnoreCase("")) {
            correo.setError(getString(R.string.ingresatucorreo));
        } else if (awesomeValidation.validate()) {
            String campoclave = contra.getText().toString();
            if (campoclave.equalsIgnoreCase("")) {
                contra.setError(getString(R.string.ingresatuclave));
            } else if (isNetworkConnected()) {
                startActivity(new Intent(IniciarSesion.this, MainActivity.class));
            } else {
                Toast.makeText(this, "Sin coneccion a Internet", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
