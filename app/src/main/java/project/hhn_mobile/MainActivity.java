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

    public static final String EMAIL_MESSAGE = "project.hhn_mobile.EMAIL";
    public static final String PASS_MESSAGE = "project.hhn_mobile.PASSWORD";

    private FirebaseAuth mAuth;

    EditText editText;
    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

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

        String email = editText.getText().toString();
        String password = editText2.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);

        String email = editText.getText().toString();
        String password = editText2.getText().toString();

        intent.putExtra(EMAIL_MESSAGE, email);
        intent.putExtra(PASS_MESSAGE, password);
        startActivity(intent);
    }
}
