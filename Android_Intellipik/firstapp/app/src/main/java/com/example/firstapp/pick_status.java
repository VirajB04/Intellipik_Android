package com.example.firstapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class pick_status extends AppCompatActivity {
    ImageButton back;
    Button scan_btn;
    RecyclerpickstatusAdapter adapter_pick_status;
    RecyclerView recycler_view_pick_status;
    FloatingActionButton btn_open_dialog;
    List<pick_status_model> pick_status_list = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pick_status);

        back = findViewById(R.id.back_button_pick_status);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(pick_status.this,HOME_PAGE.class));
            }
        });

        //QR scanner & storing it in add_btn_dialog's code
        scan_btn = findViewById(R.id.QR_button);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(pick_status.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan a QR code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });

        //this is for pick status list recycler view
        recycler_view_pick_status = findViewById(R.id.recycler_view_pick_status);
        recycler_view_pick_status.setLayoutManager(new LinearLayoutManager(this));

        fetchData();

        adapter_pick_status = new RecyclerpickstatusAdapter(this,pick_status_list);
        recycler_view_pick_status.setAdapter(adapter_pick_status);

        //this is for add dialog button/ add items
        btn_open_dialog = findViewById((R.id.btn_open_dialog));
        btn_open_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogWithCode(null);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //added bcoz of setting the text to code in add btn
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult!= null){
            String scannedResult = intentResult.getContents();
            if(scannedResult == null || scannedResult.trim().isEmpty()){
                Toast.makeText(this, "QR is empty.Please scan a valid code!", Toast.LENGTH_SHORT).show();
            }else {
                openDialogWithCode(scannedResult);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void fetchData(){
        String url = "http://192.168.0.105:8082/app1/Pickstatus";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Volley", "Response: " + response.toString());

                            if (response.getString("program_status").equals("success")) {
                                JSONArray dataArray = response.getJSONArray("data");

                                pick_status_list.clear();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject data = dataArray.getJSONObject(i);

                                    int id = data.getInt("id");
                                    int code = data.getInt("code");
                                    String date = data.getString("date");
                                    String name = data.getString("name");
                                    int no_of_kits = data.getInt("no_of_kits");
                                    int no_of_kits_picked = data.getInt("no_of_kits_picked");
                                    String status = data.getString("status");

                                    pick_status_list.add(new pick_status_model(id,code,date,name,no_of_kits,no_of_kits_picked,status));
                                }

                                if (adapter_pick_status == null) {
                                    adapter_pick_status = new RecyclerpickstatusAdapter(pick_status.this,pick_status_list );
                                    recycler_view_pick_status.setAdapter(adapter_pick_status);
                                } else {
                                    adapter_pick_status.notifyDataSetChanged();
                                }
                            } else {
                                String message = response.getString("message");
                                Log.e("Volley", "Error: " + message);
                                Toast.makeText(pick_status.this, "Failed to fetch data: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("Volley", "JSON Parsing Error: " + e.getMessage());
                            Toast.makeText(pick_status.this, "Parsing error occurred!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error Response: " + error.getMessage());
                        Toast.makeText(pick_status.this, "Network error! Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void openDialogWithCode(String code) {
        Dialog dialog= new Dialog(pick_status.this);
        dialog.setContentView(R.layout.add_update_pick_status);

        EditText edtCode = dialog.findViewById(R.id.pickstatus_code);
        EditText edtDate  = dialog.findViewById(R.id.pickstatus_date);
        EditText edtName = dialog.findViewById(R.id.pickstatus_name);
        EditText edtNumber_kits = dialog.findViewById(R.id.pickstatus_number_kits);
        EditText edtNumber_kits_picked = dialog.findViewById(R.id.pickstatus_number_kits_picked);
        EditText edtStatus = dialog.findViewById(R.id.pickstatus_status);
        Button add_button = dialog.findViewById(R.id.add_button);

        if (edtName == null || edtCode == null || edtDate == null || add_button == null ||  edtStatus == null) {
            Log.e("Dialog", "One or more views in the dialog are null. Check IDs in add_update_picklist.xml.");
            Toast.makeText(this, "Dialog setup error. Check layout IDs.", Toast.LENGTH_LONG).show();
            return;
        }

        // Prefill the scanned code (if available)
        if (code != null) {
            edtCode.setText(code);
        }

        add_button.setOnClickListener(v -> {
            String date = edtDate.getText().toString().trim();
            String codeInput = edtCode.getText().toString().trim();
            String name = edtName.getText().toString().trim();
            String noOfkitspicked = edtNumber_kits_picked.getText().toString().trim();
            String noOfkits = edtNumber_kits.getText().toString().trim();
            String status = edtStatus.getText().toString().trim();


            if (name.isEmpty() || codeInput.isEmpty() || date.isEmpty() || status.isEmpty()) {
                Toast.makeText(pick_status.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            int Code;
            try {
                Code = Integer.parseInt(codeInput);
            } catch (NumberFormatException e) {
                Toast.makeText(pick_status.this, "Invalid code!", Toast.LENGTH_SHORT).show();
                return;
            }

            int no_of_kits = noOfkits.isEmpty() ? 0 : Integer.parseInt(noOfkits);
            int no_of_kits_picked = noOfkitspicked.isEmpty() ? 0 : Integer.parseInt(noOfkitspicked);

            // URL
            String url = "http://192.168.0.105:8082/app1/Addpickstatus?name=" + name + "&date=" + date + "&code=" + codeInput +
                    "&no_of_kits=" + no_of_kits +"&no_of_kits_picked=" + no_of_kits_picked + "&status=" + status;

            // Log the constructed URL
            Log.d("URL", "Constructed URL: " + url);

            RequestQueue requestQueue = Volley.newRequestQueue(pick_status.this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    null,
                    response -> {
                        try {
                            Log.d("Response", "Server Response: " + response.toString());

                            if (response.getString("program_status").equals("success")) {
                                // Update RecyclerView
                                runOnUiThread(() -> {
                                    fetchData();
                                    dialog.dismiss();
                                    Toast.makeText(pick_status.this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                Toast.makeText(pick_status.this, "Failed to add item: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("Volley", "JSON Error: " + e.getMessage());
                            Toast.makeText(pick_status.this, "Unexpected response from server!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("Volley", "Error Response: " + error.getMessage());
                        Toast.makeText(pick_status.this, "Network error, please try again!", Toast.LENGTH_SHORT).show();
                    }
            );

            try {
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                Log.e("Volley", "Request Error: " + e.getMessage());
                Toast.makeText(pick_status.this, "Unexpected error occurred!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

}