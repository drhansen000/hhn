package project.hhn_mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CreateAppointmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    EditText editText6;
    Spinner spinner;
    String description = "";

    List<Service> services = new ArrayList<>();
    List<String> nameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        editText6 = findViewById(R.id.editText6);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        final Context context = getApplicationContext();

        myRef = database.getReference();
        myRef.child("service").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Service service = new Service();
                    service = childSnapshot.getValue(Service.class);
                    services.add(service);
                    nameList.add(service.getFullService());

                    Log.d("Service Name", service.getService());
                    Log.d("Service Cost", Long.toString(service.getCost()));
                    Log.d("Service Duration", Long.toString(service.getDuration()));
                    Log.d("Service ID", Long.toString(service.getID()));
                    Log.d("List size", Long.toString(services.size()));
                }
                Log.d("Debug", "Out of the for loop");

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, nameList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("On Cancelled: ", "Something messed up");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        Context context = this;

        description = editText6.getText().toString();

        //look at the URL below to learn more about the following variable instantiation
        // https://developer.android.com/reference/android/preference/PreferenceManager.html
        SharedPreferences.Editor preferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();

        //This adds a string attached to a key that I create
        preferencesEditor.putString("DESCRIPTION", description);
        preferencesEditor.apply(); //this applies the changes to the preferences
    }

    @Override
    protected void onResume() {
        super.onResume();
        //create the context (it's the activity that we're currently in)
        Context context = this;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        //store the retrieved data into strings. If there is no data, use the default values
        // passed in the second parameter
        String storedDescription = prefs.getString("DESCRIPTION", "Description");

        editText6.setText(storedDescription);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // On selecting a spinner item
        String item = adapterView.getItemAtPosition(i).toString();

        // Showing selected spinner item
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
