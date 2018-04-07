package project.hhn_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This activity is creates an account. It only utilizes the email and password inputs. It gets the
 * input from the login activity and populates said input fields. It will create an account and then
 * log the user in.
 */

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String email;
    private String password;
    private String passwordVer;

    private EditText editUserName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editPasswordVer;

    /**
     * This function creates the activity and populates the email and password inputs with the passed
     * in values.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Receive email and password if the user entered them in in the MainActivity.
        Intent intent = getIntent();
        email = intent.getStringExtra(MainActivity.EMAIL_MESSAGE);
        password = intent.getStringExtra(MainActivity.PASS_MESSAGE);

        editUserName = findViewById(R.id.userName);
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        editPasswordVer = findViewById(R.id.passwordVer);

        // Set the email and password fields to the passed in email and password values.
        editEmail.setText(email);
        editPassword.setText(password);

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * This function checks that the passwords match, if they do, then it will create an account
     * utilizing the Firebase createUser method.
     * @param view
     */
    public void createAccount(View view) {
        final Intent intent = new Intent(this, FutureAppointmentsActivity.class);
        final String TAG = "CreateAccount: ";
        final String userName = editUserName.getText().toString();

        // Get the email and password entered if none was entered in the MainActivity.
        email = editEmail.getText().toString();
        password = editPassword.getText().toString();
        passwordVer = editPasswordVer.getText().toString();

        // Make sure that both passwords match
        if (password.equals(passwordVer)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Display an error message if the passwords don't match
            Toast.makeText(CreateAccountActivity.this, "Passwords do not match.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
