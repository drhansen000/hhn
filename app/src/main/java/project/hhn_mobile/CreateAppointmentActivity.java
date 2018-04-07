package project.hhn_mobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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

/**
 * This Activity creates or edits an appointment. If it creates an appointment, it will add it to
 * the end of the user's appointment table. Or else, it will overwrite the passed in index.
 */
public class CreateAppointmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Context context;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    private TextView displayDate;
    private TextView displayTime;
    private EditText additionalInfo;
    private Spinner serviceInfo;

    private List<Service> services = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private int appointmentPosition = 0;

    private String service = null;

    /**
     * This function creates the activity. It contains date and time pickers, a spinner that
     * dynamically displays the information of a selected service, and an input field for additional
     * information.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        TextView title = findViewById(R.id.title);

        displayTime = findViewById(R.id.timeView);
        displayTime.setOnClickListener(new View.OnClickListener() {
            // When the view is clicked, open the clock view
            @Override
            public void onClick(View view) {
                // Get current time
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                // Create Clock View, populating input with current time
                TimePickerDialog timeDialog = new TimePickerDialog(
                        CreateAppointmentActivity.this,
                        onTimeSetListener,
                        hour, minute,
                        false);
                timeDialog.show();
            }
        });
        // Set the textView to the time selected
        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String appTime = hour + ":" + minute;
                displayTime.setText(appTime);
            }
        };

        displayDate = findViewById(R.id.dateView);
        displayDate.setOnClickListener(new View.OnClickListener () {
            // When the view is clicked, open the calendar view
            @Override
            public void onClick(View view) {
                // Get the current date
                Calendar cal = Calendar.getInstance();
                int year     = cal.get(Calendar.YEAR);
                int month    = cal.get(Calendar.MONTH);
                int day      = cal.get(Calendar.DAY_OF_MONTH);

                // Set up DateDialog and populate input with current date
                DatePickerDialog dateDialog = new DatePickerDialog(
                        CreateAppointmentActivity.this,
                        onDateSetListener,
                        year, month, day);
                dateDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            // When the date is chosen populate the TextView
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String sMonth;
                String sDay;
                // Because they count January as 0
                month += 1;

                // Add a 0 to the string if it's less than ten
                if (month < 10) {
                    sMonth = "0" + month;
                } else {
                    sMonth = "" + month;
                }
                if (day < 10) {
                    sDay = "0" + day;
                } else {
                    sDay = "" + day;
                }

                // Set textview to date selected
                String date = year + "-" + sMonth + "-" + sDay;
                displayDate.setText(date);
            }
        };

        // Get the list size that was sent from the FutureAppointmentActivity so that we know the number of
        //   appointments. This allows us to create an appointment in the next available spot in the database.
        Intent intent = getIntent();
        int n = intent.getIntExtra(FutureAppointmentsActivity.FUNCTION_NUMBER, 0);
        Log.d("Function Number", Long.toString(n));
        if (n == 1) {
            appointmentPosition = intent.getIntExtra(FutureAppointmentsActivity.LIST_SIZE_MESSAGE, 0);
            Log.d("Appointment list size", Long.toString(appointmentPosition));
            title.setText("Create an Appointment");
        } else {
            appointmentPosition = intent.getIntExtra(FutureAppointmentsActivity.APPOINTMENT_POSITION, 0);
            Log.d("Appointment Position", Long.toString(appointmentPosition));
            title.setText("Edit Appointment");
        }

        additionalInfo = findViewById(R.id.additionalInfo);

        serviceInfo = findViewById(R.id.serviceInfo);
        serviceInfo.setOnItemSelectedListener(this);

        context = getApplicationContext();

        // Populate the spinner with the service options from the database.
        DatabaseReference dbRef = database.getReference();
        dbRef.child("service").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Service nodes in the database are read as objects into a Service object and loaded into the correct arrays.
                    Service service = childSnapshot.getValue(Service.class);
                    services.add(service);
                    assert service != null;
                    nameList.add(service.getFullService());

                    // Debug logs to make sure that everything is getting read from the database correctly.
                    Log.d("Service Name", service.getService());
                    Log.d("Service Cost", Long.toString(service.getCost()));
                    Log.d("Service Duration", Long.toString(service.getDuration()));
                    Log.d("Service ID", Long.toString(service.getID()));
                    Log.d("List size", Long.toString(services.size()));
                }

                // Connect the service name list array to the spinner so that the spinner is populated with the correct info.
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.spinner_layout, nameList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                serviceInfo.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Debug log in case something messed up while reading the database.
                Log.d("On Cancelled: ", "Something messed up");
            }
        });

        Log.d("Display date", displayDate.getText().toString());
        Log.d("Display time", displayTime.getText().toString());
    }

    /**
     * This function stores the additional info that the user inputted (it might be a lot to retype!)
     */
    @Override
    protected void onPause() {
        super.onPause();
        Context context = this;

        String description = additionalInfo.getText().toString();

        // Look at the URL below to learn more about the following variable instantiation
        // https://developer.android.com/reference/android/preference/PreferenceManager.html
        SharedPreferences.Editor preferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();

        // This adds a string attached to a key that I create
        preferencesEditor.putString("DESCRIPTION", description);
        preferencesEditor.apply(); //this applies the changes to the preferences
    }

    /**
     * This function refills the additional information input field.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Create the context (it's the activity that we're currently in)
        Context context = this;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Store the retrieved data into strings. If there is no data, use the default values
        // passed in the second parameter
        String storedDescription = prefs.getString("DESCRIPTION", "Description");

        additionalInfo.setText(storedDescription);
    }

    /**
     * This fires if a spinner item was selected. It displays a toast containing the service
     * information.
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // On selecting a spinner item
        String item = adapterView.getItemAtPosition(i).toString();
        service = item;

        // Showing selected spinner item
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    /**
     * Override all methods of AdapterView interface.
     * @param adapterView
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /**
     * This fires if the user clicks the confirm appointment button. It checks that all of the input
     * fields are selected. If so, then it inserts it into the user's appointment table. If not, it
     * displays an error message.
     * @param view
     */
    public void confirmAppointment(View view) {
        // References are made to each leaf in an appointment node under the current user's UID.
        String size = Integer.toString(appointmentPosition);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DatabaseReference myServiceRef = database.getReference("appointment/" + user.getUid() + "/" + size + "/service");
        DatabaseReference myDateRef = database.getReference("appointment/" + user.getUid() + "/" + size + "/date");
        DatabaseReference myTimeRef = database.getReference("appointment/" + user.getUid() + "/" + size + "/time");
        DatabaseReference myInfoRef = database.getReference("appointment/" + user.getUid() + "/" + size + "/info");
        DatabaseReference myCancelRef = database.getReference("appointment/" + user.getUid() + "/" + size + "/cancelled");

        // Check it all input fields are filled in
        if ((displayDate.getText().toString().equals("Select Date")) || (displayTime.getText().toString().equals("Select Time"))) {
            // If not, display error message and don't submit to database
            Toast.makeText(this, "Please fill in the required fields!", Toast.LENGTH_LONG).show();
        } else {
            // Each piece of data is written to the database, it has to be done separately to my knowledge.
            myServiceRef.setValue(service);
            myDateRef.setValue(displayDate.getText().toString());
            myTimeRef.setValue(displayTime.getText().toString());
            myInfoRef.setValue(additionalInfo.getText().toString());
            myCancelRef.setValue("No");

            // After everything is written to the database the user is sent back to the FutureAppointmentActivity.
            Intent intent = new Intent(this, FutureAppointmentsActivity.class);
            startActivity(intent);
        }
    }
}
