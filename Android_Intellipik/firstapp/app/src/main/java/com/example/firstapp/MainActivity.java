package com.example.firstapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText edUsername, edPass;
    Button loginButton;
    TextView tv, forgotpassword;
    public static final String SHARED_PREFS = "sharedPrefs";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        CheckBox();

        edUsername = findViewById(R.id.username);
        edPass = findViewById(R.id.password);
        loginButton= findViewById(R.id.login_button);
        tv= findViewById(R.id.Sign_in);
        forgotpassword = findViewById(R.id.forgot_pass);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Activityforgotpassword.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Used for storing entered details and everytime no need to login.
    private void CheckBox() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String check = sharedPreferences.getString("name","");
        if(check.equals("true")){
            Toast.makeText(MainActivity.this,"Welcome back!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,HOME_PAGE.class));
            finish();
    }
    }

    private void checkCredentials(){
        String username = edUsername.getText().toString();
        String password = edPass.getText().toString();

        if(username.isEmpty() || username.length() < 4){
            showError(edUsername,"Your username is not valid!");
        }else if(password.isEmpty() || password.length()<4){
            showError(edPass,"Password must be 4 or more characters");
        }else{
            getfromServer(username,password);
        }
    }
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
    private void getfromServer(String username, String password){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Checking credentials...");
        progressDialog.show();

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                "http://192.168.0.103:8082/app1/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                       if(response.equals("Login successful")){
                           //Used for storing entered details , stores true and everytime no need to login.
                           SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                           SharedPreferences.Editor editor = sharedPreferences.edit();
                           editor.putString("name","true");
                           editor.apply();

                           Toast.makeText(MainActivity.this, "Welcome "+username, Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(MainActivity.this,HOME_PAGE.class));
                           finish();
                       }else {
                           Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                       }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hm= new HashMap<>();
                hm.put("name",username);
                hm.put("password",password);

                return hm;
            }
        };
        requestQueue.add(sr);

    }

    }