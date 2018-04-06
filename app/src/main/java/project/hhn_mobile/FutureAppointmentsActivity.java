package project.hhn_mobile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

    //create Firebase properties
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = database.getReference();

    private List<String> appointmentDates = new ArrayList<>();
    private ListView appointmentListView;
    private Integer dbLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_appointments);

        // Get the user email and display it so that we can tell who is logged in.
        TextView  textView11 = findViewById(R.id.textView11);
        textView11.setText("Welcome " + currentUser.getEmail());

        appointmentListView = findViewById(R.id.listView);

        //create the ListView, which then connects an alert to each ListView item
        createListView();
    }

    public void createListView() {

        // A reference is made under the appointment branch in the database to the user's UID.
        // All of the user's appointments are under their UID under the appointment branch.
        dbRef.child("appointment/" + currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get today's date and format it like the dates in the database
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                List<String> filteredAppointments = new ArrayList<>();
                ArrayAdapter<String> appointmentAdapter;


                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Reading from the database can put nodes of the tree into objects.
                    // Each appointment is put in an Appointment object and then added to the correct arrays.
                    Appointment appointment = childSnapshot.getValue(Appointment.class);

                    //verify toady's date is correct
                    Log.d("Today's date", date);
                    //verify index in list for each element
                    Log.d("Child key", childSnapshot.getKey());

                    //increment the user's appointment list, to be able to correctly add and cancel appointment
                    //at correct index
                    dbLength++;

                    //check if the appointment's cancelled or scheduled before the current date
                    assert appointment != null;
                    if (appointment.getCancelled().equals("No") && appointment.getDate().compareTo(date) >= 0) {
                        filteredAppointments.add(appointment.getService() + "\nDate: " + appointment.getDate() + " Time: "
                                + appointment.getTime() + "\nAdditional Info:\n " + appointment.getInfo());

                        //To get the date from the listView at a position (used to query the db for a child with the same date)
                        appointmentDates.add(appointment.getDate());

                        // Debug logs to make sure that everything in the database is getting read correctly.
                        Log.d("Service", appointment.getService());
                        Log.d("Date", appointment.getDate());
                        Log.d("Time", appointment.getTime());
                        Log.d("Info", appointment.getInfo());
                        Log.d("Cancelled", appointment.getCancelled());
                    }
                }

                // After the appointments are read and stored properly, it is connected to the list view adapter to display it.
                appointmentAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_custom, filteredAppointments);
                appointmentListView.setAdapter(appointmentAdapter);

                //verify the list size is the same as db's list size
                Log.d("List size", dbLength.toString());

                //attach an alert dialog to each listView item
                cancelEditAppointment();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Display what went wrong
                Log.d("On Cancelled: ", databaseError.getMessage());
            }
        });
    }

    public void cancelEditAppointment() {
        appointmentListView.setClickable(true);
        appointmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //when the item is clicked create an AlertDialog
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                //get the user's appointment table from the database
                final DatabaseReference userAppointments = database.getReference("appointment/" + currentUser.getUid());
                //create an object for the ListView item selected
                Object itemObject = appointmentListView.getItemAtPosition(position);
                //create AlertDialog using the ListView's item's content and make its title "Change Appointment"
                String str = (String) itemObject;
                AlertDialog.Builder builder = new AlertDialog.Builder(FutureAppointmentsActivity.this);
                builder.setMessage(str)
                        .setTitle("Change Appointment");

                //its positive button cancels an appointment
                builder.setPositiveButton("Cancel Appointment", new DialogInterface.OnClickListener() {
                    // User clicked cancelAppointment option
                    public void onClick(DialogInterface dialog, int id) {
                        //find the user's appointment from the database by the date
                        Query query = userAppointments.orderByChild("date").equalTo(appointmentDates.get(position));
                        //this should only fire if it found a match
                        query.addChildEventListener(
                                new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        //set the appointment's cancelled value to Yes
                                        userAppointments.child(dataSnapshot.getKey()).child("cancelled").setValue("Yes");
                                    }

                                    //implement all of the interface's methods
                                    @Override
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

                //it's negative button edits the appointment
                builder.setNegativeButton("Edit Appointment", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //find the user's appointment in the database by the date
                        Query query = userAppointments.orderByChild("date").equalTo(appointmentDates.get(position));
                        //this should only fire if it found a match
                        query.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                //get the appointment's index
                                String sKey = dataSnapshot.getKey();
                                Log.d("Appointment Index", sKey);
                                int key = Integer.parseInt(sKey);
                                editAppointment(key);
                            }

                            //implement all of the interface's methods
                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                //the neutral button is 'Nevermind', which returns the user to the activity
                builder.setNeutralButton("Nevermind", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //returns the user to the activity
                    }
                });
                //display the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    //this function creates an appointment at the end of the list
    public void createNewAppointment(View view) {
        // Go to the CreateAppointmentActivity.
        Intent intent = new Intent(this, CreateAppointmentActivity.class);
        //give the end of the appointment list
        intent.putExtra(LIST_SIZE_MESSAGE, dbLength);
        intent.putExtra(FUNCTION_NUMBER, 1);
        startActivity(intent);
    }

    //this function overwrites the specified index with the new information
    public void editAppointment(int position) {
        //go to the CreateAppointmentActivity, it will overwrite the appointment's current index
        Intent intent = new Intent(this, CreateAppointmentActivity.class);
        //give the appointment's index in the list
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
