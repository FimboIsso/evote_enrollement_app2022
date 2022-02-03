package issonet.com.vote_enronlement_2019_new;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class login extends AppCompatActivity {

    Spinner spn_username;
    EditText ed_password;
    String password = "admin";

    Button btn_login;

    SharedPreferences shared;
    SharedPreferences.Editor editorr;
    String adresseIP,val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spn_username = (Spinner)findViewById(R.id.spn_username);
        ed_password = (EditText) findViewById(R.id.ed_password);

        btn_login = (Button)findViewById(R.id.btn_login);

        setTitle("Login Enrollement Vote");

        shared = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        editorr = shared.edit();
        adresseIP = shared.getString("adresseServeur","");

        if(adresseIP.length() == 0){
            editorr.putString("adresseServeur", "192.168.43.150");
            editorr.apply();
            editorr.commit();
        }

        askForPermission(Manifest.permission.NFC,2);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.equals(ed_password.getText().toString())){
                    startActivity(new Intent(login.this,MainActivity.class));
                    finish();
                }else{
                    ed_password.setError("Incorrect password. Please contact adminstrator ");
                }
            }
        });
    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(login.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(login.this, permission)) {

                ActivityCompat.requestPermissions(login.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(login.this, new String[]{permission}, requestCode);
            }
        } else {
            // Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}
