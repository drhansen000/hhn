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

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String email;
    private String password;

    EditText editText3;
    EditText editText9;
    EditText editText7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Receive email and password if the user entered them in in the MainActivity.
        Intent intent = getIntent();
        email = intent.getStringExtra(MainActivity.EMAIL_MESSAGE);
        password = intent.getStringExtra(MainActivity.PASS_MESSAGE);

        editText3 = findViewById(R.id.editText3);
        editText9 = findViewById(R.id.editText9);
        editText7 = findViewById(R.id.editText7);

        // Set the email and password fields to the passed in email and password values.
        editText9.setText(email);
        editText7.setText(password);

        mAuth = FirebaseAuth.getInstance();
    }

    public void createAccount(View view) {
        final Intent intent = new Intent(this, FutureAppointmentsActivity.class);
        final String TAG = "CreateAccount: ";
        final String userName = editText3.getText().toString();

        // Get the email and password entered if none was entered in the MainActivity.
        email = editText9.getText().toString();
        password = editText7.getText().toString();

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
    }
}
