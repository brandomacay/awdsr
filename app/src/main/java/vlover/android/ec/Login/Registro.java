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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vlover.android.ec.MainActivity.MainActivity;
import vlover.android.ec.R;

public class Registro extends AppCompatActivity {
    //DECLARE FIELDS
    EditText userEmailCreateEditText, userPassWordCreateEditText;
    Button createAccountBtn;

    //FIREBASE AUTHENTICATION ID
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    //PROGRESS DIALOG
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        //ASSIGN ID'S
        userEmailCreateEditText = ( EditText ) findViewById(R.id.emailRegisterEditText);
        userPassWordCreateEditText = (EditText) findViewById(R.id.passwordRegisterEditText);

        createAccountBtn = ( Button) findViewById(R.id.registrar);

        //PROGRESS DIALOG INSTANCE
        mProgressDialog = new ProgressDialog(this);

        //FIREBASE INSTANCE
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //CHECK USER
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if( user != null )
                {

                    Intent moveToHome = new Intent(Registro.this, MainActivity.class);
                    moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity( moveToHome );
                    finish();

                }

            }
        };


        mAuth.addAuthStateListener(mAuthListener);

        //Crear Cuenta BTN OnClickListener
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setTitle("Creando cuenta");
                mProgressDialog.setMessage("Por favor espere...");
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);
                createUserAccount();

            }
        });

    }


    //LOGIC FOR CREATING THE USER ACCOUNT
    private void createUserAccount() {

        String emailUser, passUser;

        emailUser = userEmailCreateEditText.getText().toString().trim();
        passUser = userPassWordCreateEditText.getText().toString().trim();

        if( !TextUtils.isEmpty(emailUser) && !TextUtils.isEmpty(passUser))
        {

            mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if( task.isSuccessful() )
                    {

                        mProgressDialog.dismiss();
                        Toast.makeText(Registro.this, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show();
                        Intent moveToHome = new Intent(Registro.this, MainActivity.class);
                        moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity( moveToHome );

                    }else
                    {if (isNetworkConnected()) {
                        mProgressDialog.dismiss();
                        Toast.makeText(Registro.this, getString(R.string.error), Toast.LENGTH_LONG).show();
                    }else {
                        mProgressDialog.dismiss();
                        Toast.makeText(Registro.this,getString(R.string.error_internet),Toast.LENGTH_LONG).show();
                    }
                        mProgressDialog.dismiss();
                        Toast.makeText(Registro.this, "Error de registro", Toast.LENGTH_LONG).show();

                    }

                }
            });

        }else{
            mProgressDialog.dismiss();
            Toast.makeText(Registro.this, getString(R.string.completarcampos), Toast.LENGTH_LONG).show();
        }

    }
    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
