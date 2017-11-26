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

import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.R;

public class IniciarSesion extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog cargando;
    private EditText correo,contra;
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

        String contra1 = contra.getText().toString();

        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.error_email);
        awesomeValidation.addValidation(this, R.id.clave, String.valueOf(contra1.length()>6), R.string.error_contraseña);


        cargando = new ProgressDialog(this);
        iniciarsesion = findViewById(R.id.iniciar);
        cambiarpass = findViewById(R.id.cambiar_clave);
        registrarse = findViewById(R.id.registrar);
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IniciarSesion.this,Registro.class));
            }
        });
        cambiarpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IniciarSesion.this,CambiarClave.class));
            }
        });
        cargando = new ProgressDialog(this);
        iniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                campos_completos();
                //startActivity(new Intent(IniciarSesion.this,MainActivity.class));
                //Intent i = new Intent(IniciarSesion.this,MainActivity.class) ;
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        });

    }

    private void verificarInicioSesion() {
    }
    private void campos_completos() {
        //primero valida si los campos estan correctos
        //si todo esta bien nos pasa a la siguiente activity
        if (awesomeValidation.validate()) {
            startActivity(new Intent(IniciarSesion.this,MainActivity.class));
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
