package project.hhn_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static final String EMAIL_MESSAGE = "project.hhn_mobile.EMAIL";
    public static final String PASS_MESSAGE = "project.hhn_mobile.PASSWORD";

    private FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("appointment/test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, FutureAppointmentsActivity.class);
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);

        EditText editText = findViewById(R.id.editText);
        EditText editText2 = findViewById(R.id.editText2);

        String email = editText.getText().toString();
        String password = editText2.getText().toString();

        intent.putExtra(EMAIL_MESSAGE, email);
        intent.putExtra(PASS_MESSAGE, password);
        startActivity(intent);
    }

    public void testWriteDatabase(View view) {
        myRef.setValue("Working");
    }

    public void testReadDatabase(View view) {
        myRef.addValueEventListener(new ValueEventListener() {
            public static final String TAG = "TAG: ";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
