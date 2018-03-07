package project.hhn_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Intent intent = getIntent();
        email = intent.getStringExtra(MainActivity.EMAIL_MESSAGE);
        password = intent.getStringExtra(MainActivity.PASS_MESSAGE);

        EditText editText9 = findViewById(R.id.editText9);
        EditText editText7 = findViewById(R.id.editText7);

        editText9.setText(email);
        editText7.setText(password);

        mAuth = FirebaseAuth.getInstance();
    }

    public void createAccount(View view) {

    }
}
