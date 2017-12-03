package vlover.android.ec;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.app.DatePickerDialog;
//import android.content.DialogInterface;
import android.app.Dialog;
import android.app.DialogFragment;
import android.widget.DatePicker;

import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
//import java.util.Set;
//import java.util.HashSet;
import java.util.ArrayList;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import vlover.android.ec.services.SQLite;
import vlover.android.ec.services.Session;


public class editAccount extends AppCompatActivity {

    EditText name_et;
    public TextView birthday_tv;
    //List<String> list;
    CountryCodePicker ccp;
    Spinner genre_spin;
    ArrayAdapter<String> spinner_adapter_genre;
    List<String> list;
    ImageView user_image;
    //CropImageView user_image;


    private SQLite dbsqlite;
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        name_et = (EditText) findViewById(R.id.edit_account_name_et);
        genre_spin = (Spinner)findViewById(R.id.edit_account_genre_spin);


        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        ccp.detectLocaleCountry(true);

        user_image = (ImageView) findViewById(R.id.edit_account_user_image);
        user_image.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(editAccount.this);
            }
        });

        // user_image = (CropImageView) findViewById(R.id.edit_account_cropImageView);
        // user_image.setImageResource(R.drawable.user);

        //ccp.getSelectedCountryCode();
        //ccp.getSelectedCountryName();
        dbsqlite = new SQLite(this);

        // session manager
        session = new Session(this);

        HashMap<String, String> user = dbsqlite.getUserDetails();
        String name = user.get("name");
        name_et.setText(name);

        birthday_tv = (TextView) findViewById(R.id.edit_account_birthday_tv);
        // Cargar la fecha de nacimiento desde la web
        birthday_tv.setText(getString(R.string.birthday));
        birthday_tv.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {


                DialogFragment newfrag = new DatePickerFragment();

                //	newfrag.setArguments(null);
                newfrag.show(getFragmentManager(), "datePicker");

            }
        });

        load_spin_genre();

    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        //birthday_tv.this =

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int	month = c.get(Calendar.MONTH);
            int	day = c.get(Calendar.DAY_OF_MONTH);


            return new DatePickerDialog(editAccount.this, this, year, month, day);
        }
        public  void onDateSet(DatePicker view, int year, int month, int day) {

            //	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            //	String today = formatter.format( ""+day+"/"+(month+1)+"/"+year );

            if (month < 9 ) {
                if (day < 9)
                    birthday_tv.setText(  ""+year+"-"+"0"+(month+1)+"-"+"0"+day );
                else
                    birthday_tv.setText(  ""+year+"-"+"0"+(month+1)+"-"+day );
            }
            else {
                if (day < 10)
                    birthday_tv.setText(  ""+year+"-"+(month+1)+"-"+"0"+day );
                else
                    birthday_tv.setText(  ""+year+"-"+(month+1)+"-"+day );
            }


        }
    }

    private void load_spin_genre () {


        list = new ArrayList<String>();

        list.add(0, getString(R.string.male));
        list.add(1, getString(R.string.female));

        spinner_adapter_genre = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);

        spinner_adapter_genre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genre_spin.setAdapter(spinner_adapter_genre);
        genre_spin.setWillNotDraw(false);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                user_image.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_account_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        //   menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
       // if (mDrawerToggle.onOptionsItemSelected(item)) {
         //   return true;
        //}
        // Handle action buttons
        switch(item.getItemId()) {

        case R.id.edit_account_menu_save:

            Toast.makeText(this, "Guardando...", Toast.LENGTH_SHORT).show();

            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
