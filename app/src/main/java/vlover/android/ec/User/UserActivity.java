package vlover.android.ec.User;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import vlover.android.ec.R;

public class UserActivity extends AppCompatActivity {

    public TextView correo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        correo = (TextView) findViewById(R.id.email);

        Intent intent = getIntent();
        Bundle extraEmail = intent.getExtras();
        String getEmail = (String) extraEmail.get("Email");
        correo.setText(getEmail);
    }
}
