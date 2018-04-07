package project.hhn_mobile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // These are two intent messages used to send the email and password strings to the CreateAccountActivity.
    public static final String EMAIL_MESSAGE = "project.hhn_mobile.EMAIL";
    public static final String PASS_MESSAGE = "project.hhn_mobile.PASSWORD";

    private FirebaseAuth mAuth;

    EditText editEmail;
    EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // EditTexts for the email and password to use in the signIn and signUp functions.
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);

        // Get an instance of the currentUser
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // If the current user is not null (i.e. someone has logged in previously) then immediately log them in.
        if (user != null) {
            // Souts are for debug purposes right now
            System.out.println("Name: " + user.getDisplayName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("UID: " + user.getUid());

            Intent intent = new Intent(this, FutureAppointmentsActivity.class);
            startActivity(intent);
        }
    }

    public void signIn(View view) {
        final Intent intent = new Intent(this, FutureAppointmentsActivity.class);
        final String TAG = "SignIn: ";

        // Prepare email and password strings to attempt to log in via Firebase.
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        //utilize the firebase signin Method
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, send an intent to the FutureAppointmentActivity
                            Log.d(TAG, "signInWithEmail:success");
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);

        // If a user has entered an email and password then prepare to send it to the CreateAccountActivity.
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        // Send email and password entered to fill the email and password boxes in the next activity.
        intent.putExtra(EMAIL_MESSAGE, email);
        intent.putExtra(PASS_MESSAGE, password);
        startActivity(intent);
    }
}
