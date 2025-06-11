package com.example.firstapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class pick_list_Activity extends AppCompatActivity {
    ImageButton back;
    RecyclerpicklistAdapter adapter_pick_list;
    Button scan_btn;
    RecyclerView recycler_view_pick_list;
    FloatingActionButton btn_open_dialog;
    private List<pick_list_model> pick_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pick_list);

        //for back button at left top corner of page
        back = findViewById(R.id.back_button_picklist);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(pick_list_Activity.this,HOME_PAGE.class));
            }
        });

        //QR scanner & storing it in add_btn_dialog's code
        scan_btn = findViewById(R.id.QR_button);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(pick_list_Activity.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan a QR code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });

        //this is for picklist recycler view
        recycler_view_pick_list = findViewById(R.id.recycler_view_pick_list);
        recycler_view_pick_list.setLayoutManager(new LinearLayoutManager(this));

        fetchData();

        RecyclerpicklistAdapter adapter_pick_list = new RecyclerpicklistAdapter(this,pick_list);
        recycler_view_pick_list.setAdapter(adapter_pick_list);

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
        String url = "http://192.168.0.105:8082/app1/Picklist";

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
                            if (response.getString("status").equals("success")) {
                                JSONArray dataArray = response.getJSONArray("data");

                                pick_list.clear();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject data = dataArray.getJSONObject(i);

                                    int id = data.getInt("id");
                                    String code = data.getString("code");
                                    String name = data.getString("name");
                                    int no_of_items = data.getInt("no_of_items");

                                    pick_list.add(new pick_list_model(id,code,name,no_of_items));
                                }

                                if (adapter_pick_list == null) {
                                    adapter_pick_list = new RecyclerpicklistAdapter(pick_list_Activity.this,pick_list );
                                    recycler_view_pick_list.setAdapter(adapter_pick_list);
                                } else {
                                    adapter_pick_list.notifyDataSetChanged();
                                }
                            } else {
                                Log.e("Volley", "Error: " + response.getString("message"));
                            }
                        } catch (Exception e) {
                            Log.e("Volley", "JSON Parsing Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error Response: " + error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void openDialogWithCode(String code) {
        Dialog dialog = new Dialog(pick_list_Activity.this);
        dialog.setContentView(R.layout.add_update_picklist);

        EditText edtName = dialog.findViewById(R.id.picklist_name);
        EditText edtCode = dialog.findViewById(R.id.picklist_code);
        EditText edtNumber = dialog.findViewById(R.id.picklist_number);
        Button add_button = dialog.findViewById(R.id.add_button);

        if (edtName == null || edtCode == null || edtNumber == null || add_button == null) {
            Log.e("Dialog", "One or more views in the dialog are null. Check IDs in add_update_picklist.xml.");
            Toast.makeText(this, "Dialog setup error. Check layout IDs.", Toast.LENGTH_LONG).show();
            return;
        }

        // Prefill the scanned code (if available)
        if (code != null) {
            edtCode.setText(code);
        }

        add_button.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String codeInput = edtCode.getText().toString().trim();
            String noOfItemsStr = edtNumber.getText().toString().trim();

            if (name.isEmpty() || codeInput.isEmpty() || noOfItemsStr.isEmpty()) {
                Toast.makeText(pick_list_Activity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            int no_of_items;
            try {
                no_of_items = Integer.parseInt(noOfItemsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(pick_list_Activity.this, "Invalid number of items!", Toast.LENGTH_SHORT).show();
                return;
            }

            // URL
            String url = "http://192.168.0.105:8082/app1/Addpicklist?name=" + name + "&code=" + codeInput +
                    "&no_of_items=" + no_of_items;

            // Log the constructed URL
            Log.d("URL", "Constructed URL: " + url);

            RequestQueue requestQueue = Volley.newRequestQueue(pick_list_Activity.this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    null,
                    response -> {
                        try {
                            Log.d("Response", "Server Response: " + response.toString());

                            if (response.getString("status").equals("success")) {
                                // Update RecyclerView
                                runOnUiThread(() -> {
                                    fetchData();
                                    dialog.dismiss();
                                    Toast.makeText(pick_list_Activity.this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                Toast.makeText(pick_list_Activity.this, "Failed to add item: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("Volley", "JSON Error: " + e.getMessage());
                            Toast.makeText(pick_list_Activity.this, "Unexpected response from server!", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("Volley", "Error Response: " + error.getMessage());
                        Toast.makeText(pick_list_Activity.this, "Network error, please try again!", Toast.LENGTH_SHORT).show();
                    }
            );

            try {
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                Log.e("Volley", "Request Error: " + e.getMessage());
                Toast.makeText(pick_list_Activity.this, "Unexpected error occurred!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }



}