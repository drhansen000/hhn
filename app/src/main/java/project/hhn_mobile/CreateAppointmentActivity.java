package project.hhn_mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateAppointmentActivity extends AppCompatActivity {
    EditText editText6;
    String description = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        editText6 = findViewById(R.id.editText6);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Context context = this;

        description = editText6.getText().toString();

        //look at the URL below to learn more about the following variable instantiation
        // https://developer.android.com/reference/android/preference/PreferenceManager.html
        SharedPreferences.Editor preferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();

        //This adds a string attached to a key that I create
        preferencesEditor.putString("DESCRIPTION", description);
        preferencesEditor.apply(); //this applies the changes to the preferences
    }

    @Override
    protected void onResume() {
        super.onResume();
        //create the context (it's the activity that we're currently in)
        Context context = this;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        //store the retrieved data into strings. If there is no data, use the default values
        // passed in the second parameter
        String storedDescription = prefs.getString("DESCRIPTION", "description");

        editText6.setText(storedDescription);
    }
}
