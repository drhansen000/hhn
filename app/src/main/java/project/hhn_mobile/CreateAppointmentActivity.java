package project.hhn_mobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateAppointmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    Context context;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    private TextView displayDate;
    private TextView displayTime;
    EditText editText6;
    Spinner spinner;

    List<Service> services = new ArrayList<>();
    List<String> nameList = new ArrayList<>();
    int appointmentListSize = 0;

    String description = "";
    String service = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        displayTime = (TextView) findViewById(R.id.timeView);
        displayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timeDialog = new TimePickerDialog(
                        CreateAppointmentActivity.this,
                        onTimeSetListener,
                        hour, minute,
                        false);
                timeDialog.show();
            }
        });
        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String appTime = hour + ":" + minute;
                displayTime.setText(appTime);
            }
        };

        displayDate = findViewById(R.id.dateView);
        displayDate.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year     = cal.get(Calendar.YEAR);
                int month    = cal.get(Calendar.MONTH);
                int day      = cal.get(Calendar.DAY_OF_MONTH);

                //set up DatePickerDialog context, theme, and information
                DatePickerDialog dateDialog = new DatePickerDialog(
                        CreateAppointmentActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        onDateSetListener,
                        year, month, day);

                //make background transparent
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dateDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //because they count January as 0
                month += 1;

                //set textview to date selected
                String date = year + "-" + month + "-" + day;
                displayDate.setText(date);
            }
        };

        // Get the list size that was sent from the FutureAppointmentActivity so that we know the number of
        //   appointments. This allows us to create an appointment in the next available spot in the database.
        Intent intent = getIntent();
        appointmentListSize = intent.getIntExtra(FutureAppointmentsActivity.LIST_SIZE_MESSAGE, 0);
        Log.d("Appointment list size", Long.toString(appointmentListSize));

//        datePicker = findViewById(R.id.datePicker);
//        textClock = findViewById(R.id.textClock);
        editText6 = findViewById(R.id.editText6);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        context = getApplicationContext();

        // Populate the spinner with the service options from the database.
        myRef = database.getReference();
        myRef.child("service").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Service nodes in the database are read as objects into a Service object and loaded into the correct arrays.
                    Service service = childSnapshot.getValue(Service.class);
                    services.add(service);
                    nameList.add(service.getFullService());

                    // Debug logs to make sure that everything is getting read from the database correctly.
                    Log.d("Service Name", service.getService());
                    Log.d("Service Cost", Long.toString(service.getCost()));
                    Log.d("Service Duration", Long.toString(service.getDuration()));
                    Log.d("Service ID", Long.toString(service.getID()));
                    Log.d("List size", Long.toString(services.size()));
                }

                // Connect the service name list array to the spinner so that the spinner is populated with the correct info.
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, nameList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Debug log in case something messed up while reading the database.
                Log.d("On Cancelled: ", "Something messed up");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        Context context = this;

        description = editText6.getText().toString();

        // Look at the URL below to learn more about the following variable instantiation
        // https://developer.android.com/reference/android/preference/PreferenceManager.html
        SharedPreferences.Editor preferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();

        // This adds a string attached to a key that I create
        preferencesEditor.putString("DESCRIPTION", description);
        preferencesEditor.apply(); //this applies the changes to the preferences
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Create the context (it's the activity that we're currently in)
        Context context = this;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Store the retrieved data into strings. If there is no data, use the default values
        // passed in the second parameter
        String storedDescription = prefs.getString("DESCRIPTION", "Description");

        editText6.setText(storedDescription);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // On selecting a spinner item
        String item = adapterView.getItemAtPosition(i).toString();
        service = item;

        // Showing selected spinner item
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Nothing is done here, don't really know what should go here.
    }

    public void confirmAppointment(View view) {
        // References are made to each leaf in an appointment node under the current user's UID.
        String size = Integer.toString(appointmentListSize);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myServiceRef = database.getReference("appointment/" + user.getUid() + "/" + size + "/service");
        DatabaseReference myDateRef = database.getReference("appointment/" + user.getUid() + "/" + size + "/date");
        DatabaseReference myTimeRef = database.getReference("appointment/" + user.getUid() + "/" + size + "/time");
        DatabaseReference myInfoRef = database.getReference("appointment/" + user.getUid() + "/" + size + "/info");
        DatabaseReference myCancelRef = database.getReference("appointment/" + user.getUid() + "/" + size + "/cancelled");

        // Each piece of data is written to the database, it has to be done separately to my knowledge.
        myServiceRef.setValue(service);
        myDateRef.setValue(displayDate.getText().toString());
        myTimeRef.setValue(displayTime.getText().toString());
        myInfoRef.setValue(editText6.getText().toString());
        myCancelRef.setValue("No");

        // After everything is written to the database the user is sent back to the FutureAppointmentActivity.
        Intent intent = new Intent(this, FutureAppointmentsActivity.class);
        startActivity(intent);
    }
}
