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

/**
 * This activity is the login page. The user may enter his/her credentials to login. If they don't
 * have an account they can click the create account button and be sent to the CreateAccountActivity.
 * If they successfully login, they will be sent to the FutureAppointmentsActivity, which displays
 * their future appointments
 */

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // These are two intent messages used to send the email and password strings to the CreateAccountActivity.
    public static final String EMAIL_MESSAGE = "project.hhn_mobile.EMAIL";
    public static final String PASS_MESSAGE = "project.hhn_mobile.PASSWORD";

    private EditText editEmail;
    private EditText editPassword;

    /**
     * This function creates the view. If the user has previously logged in, then they will be directed
     * towards the FutureAppointmentsActivity.
     * @param savedInstanceState
     */
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
            Log.d("Name", user.getDisplayName());
            Log.d("Email", user.getEmail());
            Log.d("UID", user.getUid());

            Intent intent = new Intent(this, FutureAppointmentsActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Fired when user clicks Signin button. It will take the credentials entered and attempt to
     * authenticate the user, utilizing the Firebase signin method.
     * @param view
     */
    public void signIn(View view) {
        final Intent intent = new Intent(this, FutureAppointmentsActivity.class);
        final String TAG = "SignIn: ";

        // Prepare email and password strings to attempt to log in via Firebase.
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        // Utilize the firebase signin Method
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

    /**
     * Fires if the user chose to create an account. It will take the credentials entered and pass
     * them to the CreateAccountActivity.
     * @param view
     */
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
