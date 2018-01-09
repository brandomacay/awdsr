package vlover.android.ec;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import vlover.android.ec.Login.IniciarSesion;

/**
 * Created by Vlover on 09/01/2018.
 */

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, IniciarSesion.class);
        startActivity(intent);
        finish();

    }
}
