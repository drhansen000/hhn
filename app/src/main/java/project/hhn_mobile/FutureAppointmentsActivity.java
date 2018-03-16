package project.hhn_mobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        TextView  textView11 = findViewById(R.id.textView11);
        textView11.setText("Welcome " + currentUser.getEmail());
        listView = findViewById(R.id.listView);

        context = getApplicationContext();

        myRef = database.getReference();
        myRef.child("appointment/" + currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = childSnapshot.getValue(Appointment.class);
                    appointments.add(appointment);
                    a.add(appointment.getService() + ": " + appointment.getDate() + ", "
                            + appointment.getTime() + ", " + appointment.getInfo());

                    Log.d("Service", appointment.getService());
                    Log.d("Date", appointment.getDate());
                    Log.d("Time", appointment.getTime());
                    Log.d("Info", appointment.getInfo());
                    Log.d("Cancelled", Long.toString(appointment.getCancelled()));
                }

                appointmentAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, a);
                listView.setAdapter(appointmentAdapter);
                Log.d("List size", Long.toString(appointments.size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("On Cancelled: ", "Something messed up");
            }
        });
    }

    public void createNewAppointment(View view) {
        Intent intent = new Intent(this, CreateAppointmentActivity.class);
        intent.putExtra(LIST_SIZE_MESSAGE, appointments.size());
        startActivity(intent);
    }

    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }
}
