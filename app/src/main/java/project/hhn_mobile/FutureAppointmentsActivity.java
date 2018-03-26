package project.hhn_mobile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FutureAppointmentsActivity extends AppCompatActivity {

    public static final String LIST_SIZE_MESSAGE = "project.hhn_mobile.LIST_SIZE";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    Context context;

    List<Appointment> appointments = new ArrayList<>();
    List<String> a = new ArrayList<>();

    ListView listView;
    ArrayAdapter<String> appointmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_appointments);

        // Get the user email and display it so that we can tell who is logged in.
        TextView  textView11 = findViewById(R.id.textView11);
        textView11.setText("Welcome " + currentUser.getEmail());

        listView = findViewById(R.id.listView);
        context = getApplicationContext();

        //create the ListView, which then connects an alert to each ListView item
        createListView();
    }

    public void createListView() {
        // A reference is made under the appointment branch in the database to the user's UID.
        // All of the user's appointments are under their UID under the appointment branch.
        myRef = database.getReference();
        myRef.child("appointment/" + currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Reading from the database can put nodes of the tree into objects.
                    // Each appointment is put in an Appointment object and then added to the correct arrays.
                    Appointment appointment = childSnapshot.getValue(Appointment.class);
                    appointments.add(appointment);
                    a.add(appointment.getService() + "\nDate : " + appointment.getDate() + " Time: "
                            + appointment.getTime() + "\nAdditional Info:\n " + appointment.getInfo());

                    // Debug logs to make sure that everything in the database is getting read correctly.
                    Log.d("Service", appointment.getService());
                    Log.d("Date", appointment.getDate());
                    Log.d("Time", appointment.getTime());
                    Log.d("Info", appointment.getInfo());
                    Log.d("Cancelled", appointment.getCancelled());
                }
                // After the appointments are read and stored properly, it is connected to the list view adapter to display it.
                appointmentAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, a);
                listView.setAdapter(appointmentAdapter);
                Log.d("List size", Long.toString(appointments.size()));

                //attach an alert dialog to each listView item
                cancelEditAppointment();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Debug log in case something went wrong.
                Log.d("On Cancelled: ", "Something messed up");
            }
        });
    }

    public void cancelEditAppointment() {
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                Object o = listView.getItemAtPosition(position);
                String str = (String) o; //As you are using Default String Adapter
                AlertDialog.Builder builder = new AlertDialog.Builder(FutureAppointmentsActivity.this);
                builder.setMessage(str)
                        .setTitle("Change Appointment");

                builder.setPositiveButton("Cancel Appointment", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Log.d("Positive", "The positive button was clicked");
                        Log.d("Progress", "Position: " + position);
                        Log.d("Removed", "Removed appointment at position " + position);
                        DatabaseReference myChangeRef = database.getReference("appointment/" + currentUser.getUid() + "/" + position + "/cancelled");
                        myChangeRef.setValue("Yes");
                    }
                });

                builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void createNewAppointment(View view) {
        // Go to the CreateAppointmentActivity.
        Intent intent = new Intent(this, CreateAppointmentActivity.class);
        intent.putExtra(LIST_SIZE_MESSAGE, appointments.size());
        startActivity(intent);
    }

    public void logOut(View view) {
        // Allows a user to log out and is sent back to the MainActivity.
        Intent intent = new Intent(this, MainActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }
}
