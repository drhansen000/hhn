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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FutureAppointmentsActivity extends AppCompatActivity {

    public static final String LIST_SIZE_MESSAGE = "project.hhn_mobile.LIST_SIZE";
    public static final String APPOINTMENT_POSITION = "project.hhn_mobile.APPOINTMENT";
    public static final String FUNCTION_NUMBER = "project.hhn_mobile.FUNCTION_NUMBER";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    Context context;

    List<String> a = new ArrayList<>();

    //get the user's appointment array size
    Integer dbLength = 0;

    //To get the date at the equivalent ListView position
    List<String> appDates = new ArrayList<>();

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
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Reading from the database can put nodes of the tree into objects.
                    // Each appointment is put in an Appointment object and then added to the correct arrays.
                    Appointment appointment = childSnapshot.getValue(Appointment.class);
                    if (appointment.getCancelled().equals("No")) {
                        Log.d("If fired", appointment.getCancelled().getClass().getName());
                    } else {
                        Log.d("else Fired", appointment.getCancelled().getClass().getName());
                    }
                    Log.d("Today's date", date);


                    dbLength++;
                    //check if the appointment's cancelled or scheduled before the current date
                    if (appointment.getCancelled().equals("No") && appointment.getDate().compareTo(date) >= 0) {
                        a.add(appointment.getService() + "\nDate: " + appointment.getDate() + " Time: "
                                + appointment.getTime() + "\nAdditional Info:\n " + appointment.getInfo());

                        //To get the date from the listView at a position (used to query the db for a child with the same date)
                        appDates.add(appointment.getDate());

                        // Debug logs to make sure that everything in the database is getting read correctly.
                        Log.d("Service", appointment.getService());
                        Log.d("Date", appointment.getDate());
                        Log.d("Time", appointment.getTime());
                        Log.d("Info", appointment.getInfo());
                        Log.d("Cancelled", appointment.getCancelled());
                    }
                }
                // After the appointments are read and stored properly, it is connected to the list view adapter to display it.
                appointmentAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, a);
                listView.setAdapter(appointmentAdapter);
                Log.d("List size", dbLength.toString());

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
//                Appointment date = (Appointment) o;
//                Log.d("Appointment Date", date.getDate());
                String str = (String) o; //As you are using Default String Adapter
                AlertDialog.Builder builder = new AlertDialog.Builder(FutureAppointmentsActivity.this);
                builder.setMessage(str)
                        .setTitle("Change Appointment");

                builder.setPositiveButton("Cancel Appointment", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked cancelAppointment option
                        final DatabaseReference myChangeRef = database.getReference("appointment/" + currentUser.getUid());
                        Query query = myChangeRef.orderByChild("date").equalTo(appDates.get(position));

                        //this should only fire if it found a match
                        query.addChildEventListener(
                                new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        //myChangeReg would refer to path                  : ApprovedEvents
                                        //adding the key as a child would make it : A/Record1
                                        myChangeRef.child(dataSnapshot.getKey()).child("cancelled").setValue("Yes");
                                    }

                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    }
                                });

                        //refresh the activity
                        finish();
                        startActivity(getIntent());
                    }
                });

                builder.setNegativeButton("Edit Appointment", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //User clicked Edit Appointment option
                        editAppointment(position);

                        //refresh the activity
                        finish();
                        startActivity(getIntent());
                    }
                });

                builder.setNeutralButton("Nevermind", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Nevermind option
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
        intent.putExtra(LIST_SIZE_MESSAGE, dbLength);
        intent.putExtra(FUNCTION_NUMBER, 1);
        startActivity(intent);
    }

    public void editAppointment(int position) {
        Intent intent = new Intent(this, CreateAppointmentActivity.class);
        intent.putExtra(APPOINTMENT_POSITION, position);
        intent.putExtra(FUNCTION_NUMBER, 2);
        startActivity(intent);
    }

    public void logOut(View view) {
        // Allows a user to log out and is sent back to the MainActivity.
        Intent intent = new Intent(this, MainActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }
}
