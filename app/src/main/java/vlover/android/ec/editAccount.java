package vlover.android.ec;

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

import java.util.Calendar;
import java.util.List;



public class editAccount extends AppCompatActivity {

    EditText name_et;
   static TextView birthday_tv;
    //List<String> list;
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name_et = (EditText) findViewById(R.id.edit_account_name_et);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        ccp.detectLocaleCountry(true);

        //ccp.getSelectedCountryCode();
        //ccp.getSelectedCountryName();


        birthday_tv = (TextView) findViewById(R.id.edit_account_birthday_tv);
        // Cargar la fecha de nacimiento desde la web
        birthday_tv.setText(getString(R.string.fecha_nacimiento));
        birthday_tv.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {


                DialogFragment newfrag = new DatePickerFragment();

                //	newfrag.setArguments(null);
                newfrag.show(getFragmentManager(), "datePicker");
                //	newfrag.sh
            }
        });

    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int	month = c.get(Calendar.MONTH);
            int	day = c.get(Calendar.DAY_OF_MONTH);


            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet(DatePicker view, int year, int month, int day) {

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

}
