package com.example.firstapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.journeyapps.barcodescanner.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Item_list_Activity extends AppCompatActivity {
    ImageButton back;
    Button scan_btn;
    RecyclerItemlistAdapter adapter;
    RecyclerView recycler_view_items;
    FloatingActionButton btn_open_dialog;
    private List<item_list_model> list_of_items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_list);

        //for back button at left top corner of page
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Item_list_Activity.this,HOME_PAGE.class));
            }
        });

        //QR scanner & storing it in add_btn_dialog's code
        scan_btn = findViewById(R.id.QR_button);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(Item_list_Activity.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan a QR code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });

        //this is for list recycler view
        recycler_view_items = findViewById(R.id.recycler_view_items);
        recycler_view_items.setLayoutManager(new LinearLayoutManager(this));

        fetchData();

        RecyclerItemlistAdapter adapter = new RecyclerItemlistAdapter(this,list_of_items);
        recycler_view_items.setAdapter(adapter);


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
        String url = "http://192.168.0.105:8082/app1/Itemlist";

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

                                list_of_items.clear();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject data = dataArray.getJSONObject(i);

                                    int id = data.getInt("id");
                                    String code = data.getString("code");
                                    String name = data.getString("name");

                                    list_of_items.add(new item_list_model(id, code, name));
                                }

                                if (adapter == null) {
                                    adapter = new RecyclerItemlistAdapter(Item_list_Activity.this, list_of_items);
                                    recycler_view_items.setAdapter(adapter);
                                } else {
                                    adapter.notifyDataSetChanged();
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
        Dialog dialog = new Dialog(Item_list_Activity.this);
        dialog.setContentView(R.layout.add_update_layout);

        EditText edtName = dialog.findViewById(R.id.item_name);
        EditText edtCode = dialog.findViewById(R.id.item_code);
        Button add_button = dialog.findViewById(R.id.add_button);

        // Prefill the scanned code into the EditText
        edtCode.setText(code);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String code = edtCode.getText().toString().trim();

                if (!name.isEmpty() && !code.isEmpty()) {
                    String url = "http://192.168.0.105:8082/app1/AddItemlist?name=" + name + "&code=" + code;

                    RequestQueue requestQueue = Volley.newRequestQueue(Item_list_Activity.this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("success")) {
                                            JSONArray dataArray = response.getJSONArray("data");

                                            list_of_items.clear();
                                            for (int i = 0; i < dataArray.length(); i++) {
                                                JSONObject data = dataArray.getJSONObject(i);
                                                int id = data.getInt("id");
                                                String code = data.getString("code");
                                                String name = data.getString("name");
                                                list_of_items.add(new item_list_model(id, code, name));
                                            }

                                            adapter.notifyDataSetChanged();
                                            fetchData();
                                            dialog.dismiss();
                                            Toast.makeText(Item_list_Activity.this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Item_list_Activity.this, "Failed to add item!", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Log.e("Volley", "JSON Error: " + e.getMessage());
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Volley", "Error: " + error.getMessage());
                                }
                            }
                    );

                    requestQueue.add(jsonObjectRequest);
                } else {
                    Toast.makeText(Item_list_Activity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
}