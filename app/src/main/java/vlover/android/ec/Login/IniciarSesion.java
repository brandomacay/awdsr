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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
        correo = findViewById(R.id.email);
        contra = findViewById(R.id.clave);
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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        cargando = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        iniciarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargando.setMessage(getString(R.string.cargando));
                cargando.show();
                cargando.setCancelable(false);
                loginUser();
            }
        });

    }


    private void loginUser() {

        String Email,Password;

        Email = correo.getText().toString().trim();
        Password = contra.getText().toString().trim();

        if( !TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Password))
        {

            mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if( task.isSuccessful())
                    {
                        finish();
                        cargando.dismiss();
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if(firebaseUser != null) {
                            Toast.makeText(IniciarSesion.this, "Bienvenido " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                        }
                        Intent moveToHome = new Intent( IniciarSesion.this, MainActivity.class);
                        moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(moveToHome);

                    }else
                    {if (isNetworkConnected()) {
                        cargando.dismiss();
                        Toast.makeText(IniciarSesion.this, getString(R.string.error), Toast.LENGTH_LONG).show();
                    }else {
                        cargando.dismiss();
                        Toast.makeText(IniciarSesion.this,getString(R.string.error_internet),Toast.LENGTH_LONG).show();
                    }

                    }

                }
            });

        }else
        {
            cargando.dismiss();
            Toast.makeText(IniciarSesion.this, getString(R.string.completarcampos), Toast.LENGTH_LONG).show();

        }

    }
    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


}
