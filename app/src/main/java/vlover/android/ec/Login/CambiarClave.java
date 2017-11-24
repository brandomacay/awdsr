package vlover.android.ec.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import vlover.android.ec.R;

public class CambiarClave extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_clave);
        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        mProgressDialog = new ProgressDialog(this);


        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Ingresa tu correo electronico!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgressDialog.setMessage("Cargando....");
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CambiarClave.this,"Hemos enviado instrucciones a su correo para restablecer su contraseña!",
                                            Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                } else {
                                    if(isNetworkConnected()){

                                        Toast.makeText(CambiarClave.this, "Este correo no se encuentra disponible!",
                                                Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();
                                    }else{
                                        Toast.makeText(CambiarClave.this,getString(R.string.error_internet),Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();

                                    }

                                }

                                mProgressDialog.dismiss();
                            }
                        });
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
