package com.example.firstapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class password_reset extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    EditText edUsername, edOldPass, edNewPass, edConfirm;
    Button ResetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);

        edUsername = findViewById(R.id.username_reset);
        edOldPass =  findViewById(R.id.old_password_reset);
        edNewPass = findViewById(R.id.new_password_reset);
        edConfirm = findViewById(R.id.confirm_new_password_reset);
        ResetButton = findViewById(R.id.reset_password_button);

        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void checkCredentials(){
        String username = edUsername.getText().toString();
        String old_password = edOldPass.getText().toString();
        String password = edNewPass.getText().toString();
        String confirm = edConfirm.getText().toString();


        if(username.isEmpty() || username.length() < 4){
            showError(edUsername,"Your username is not valid!");
        }else if(old_password.isEmpty() || old_password.length()<4){
            showError(edOldPass,"old password must be 4 or more characters");
        }else if(password.isEmpty() || password.length()<4){
            showError(edNewPass,"Password must be 4 or more characters");
        }else if(confirm.isEmpty() || !confirm.equals(password)){
            showError(edConfirm,"Password is not matching");
        }else{

            sendToServer(username,old_password,password, confirm);
        }
    }
    private void sendToServer(String username, String old_password, String password, String confirm){
        String url = "http://192.168.0.105:8082/app1/Passwordreset";

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resetting password...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    // Handle successful response
                    if (response.contains("Password updated successfully!")) {
                        Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name","");
                        editor.apply();

                        startActivity(new Intent(password_reset.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    // Handle error response
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("old_password", old_password);
                params.put("new_password", password);
                return params;
            }
        };

        queue.add(stringRequest);

    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}