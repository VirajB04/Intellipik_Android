package com.example.firstapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.credentials.CredentialManager;

import androidx.annotation.Nullable;
import androidx.credentials.CredentialManagerCallback;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetPasswordOption;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText edUsername, edEmail, edPass,edConfirm;
    Button btn;
    TextView tv, google_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        edUsername = findViewById(R.id.new_reg_username);
        edEmail = findViewById(R.id.new_reg_email_id);
        edPass = findViewById(R.id.new_reg_password);
        edConfirm = findViewById(R.id.Confirm_reg_password);
        btn = findViewById(R.id.create_acc_button);
        tv= findViewById(R.id.login_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
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
        String email = edEmail.getText().toString();
        String password = edPass.getText().toString();
        String confirm = edConfirm.getText().toString();


        if(username.isEmpty() || username.length() < 4){
            showError(edUsername,"Your username is not valid!");
        }else if(!email.contains("@")){
            showError(edEmail,"email is not valid");
        }else if(password.isEmpty() || password.length()<4){
            showError(edPass,"Password must be 4 or more characters");
        }else if(confirm.isEmpty() || !confirm.equals(password)){
            showError(edConfirm,"Password is not matching");
        }else{
            sendToServer(username, email, password);
        }
    }

    private void sendToServer(String username, String email, String password){
        RequestQueue rq = Volley.newRequestQueue(this);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading..Please wait..");
        progressDialog.show();

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                "http://192.168.0.105:8082/app1/Package1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if(response.equals("User registered successfully!")){
                            Toast.makeText(SignupActivity.this, String.format("%s", response), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this,MainActivity.class));
                        }else {
                            Toast.makeText(SignupActivity.this, String.format("%s", response), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(SignupActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hm= new HashMap<>();
                hm.put("name",username);
                hm.put("email",email);
                hm.put("password",password);

                return hm;
            }
        };
        rq.add(sr);

    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}