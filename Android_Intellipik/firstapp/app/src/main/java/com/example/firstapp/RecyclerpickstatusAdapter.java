package com.example.firstapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RecyclerpickstatusAdapter extends RecyclerView.Adapter<RecyclerpickstatusAdapter.ViewHolder> {
    Context context;
    List<pick_status_model> pick_status_list;
    int selectedPosition = -1;

    RecyclerpickstatusAdapter(Context context, List<pick_status_model>pick_status_list) {
        this.context = context;
        this.pick_status_list = pick_status_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.pick_status_row, parent,false);
        ViewHolder view = new ViewHolder(v);
        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        pick_status_model model = (pick_status_model)pick_status_list.get(position);
        holder.code_pick_status.setText(String.valueOf(pick_status_list.get(position).code));
        holder.date_pick_status.setText(pick_status_list.get(position).date);
        holder.name_pick_status.setText(pick_status_list.get(position).name);
        holder.number_kits_pick_status.setText(String.valueOf(pick_status_list.get(position).number_of_kits));
        holder.number_kits_picked_pick_status.setText(String.valueOf(pick_status_list.get(position).kits_picked));
        holder.pick_status.setText(pick_status_list.get(position).status);


        if (selectedPosition == position) {
            holder.row_table_pick_status.setBackgroundColor(Color.parseColor("#289DCC"));
        } else {
            holder.row_table_pick_status.setBackgroundColor(Color.parseColor("#DBF5FF"));
        }

        holder.row_table_pick_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_update_pick_status);

                EditText edtCode = dialog.findViewById(R.id.pickstatus_code);
                EditText edtDate  = dialog.findViewById(R.id.pickstatus_date);
                EditText edtName = dialog.findViewById(R.id.pickstatus_name);
                EditText edtNumber_kits = dialog.findViewById(R.id.pickstatus_number_kits);
                EditText edtNumber_kits_picked = dialog.findViewById(R.id.pickstatus_number_kits_picked);
                EditText edtStatus = dialog.findViewById(R.id.pickstatus_status);
                Button add_button = dialog.findViewById(R.id.add_button);
                TextView welcome_head = dialog.findViewById(R.id.welcome_head);

                welcome_head.setText("Edit Pick status of an Item");

                add_button.setText("Update");

                edtCode.setText(String.valueOf(pick_status_list.get(position).code));
                edtDate.setText((pick_status_list.get(position)).date);
                edtName.setText((pick_status_list.get(position)).name);
                edtNumber_kits.setText(String.valueOf(pick_status_list.get(position).number_of_kits));
                edtNumber_kits_picked.setText(String.valueOf(pick_status_list.get(position).kits_picked));
                edtStatus.setText((pick_status_list.get(position)).status);

                add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String updated_date = edtDate.getText().toString().trim();
                        String updated_codeInput = edtCode.getText().toString().trim();
                        String updated_name = edtName.getText().toString().trim();
                        String updated_noOfkitspicked = edtNumber_kits_picked.getText().toString().trim();
                        String updated_noOfkits = edtNumber_kits.getText().toString().trim();
                        String updated_status = edtStatus.getText().toString().trim();

                        if (updated_name.isEmpty() || updated_codeInput.isEmpty() || updated_date.isEmpty() || updated_status.isEmpty()) {
                            Toast.makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int Code;
                        try {
                            Code = Integer.parseInt(updated_codeInput);
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Invalid code!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int no_of_kits;
                        try {
                            no_of_kits = Integer.parseInt(updated_noOfkits);
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Invalid number of kits!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int no_of_kits_picked;
                        try {
                            no_of_kits_picked = Integer.parseInt(updated_noOfkitspicked);
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Invalid number of kits picked!", Toast.LENGTH_SHORT).show();
                            return;

                        }
                        int id = pick_status_list.get(position).id;

                        String url = "http://192.168.0.105:8082/app1/Updatepickstatus";

                        JSONObject params = new JSONObject();
                        try {
                            params.put("id", id);
                            params.put("code", Code);
                            params.put("date", updated_date);
                            params.put("name", updated_name);
                            params.put("no_of_kits", no_of_kits);
                            params.put("no_of_kits_picked", no_of_kits_picked);
                            params.put("status", updated_status);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                                response -> {
                                    Log.d("Response", response.toString());
                                    try {
                                        String statusResponse = response.getString("program_status");
                                        String message = response.getString("message");

                                        if ("success".equals(statusResponse)) {
                                            pick_status_list.get(position).name = updated_name;
                                            pick_status_list.get(position).code = Code;
                                            pick_status_list.get(position).date = updated_date;
                                            pick_status_list.get(position).number_of_kits = no_of_kits;
                                            pick_status_list.get(position).kits_picked = no_of_kits_picked;
                                            pick_status_list.get(position).status = updated_status;

                                            notifyItemChanged(position);
                                            dialog.dismiss();
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Error: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "Response parsing error", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                error -> {
                                    error.printStackTrace();
                                    Toast.makeText(context, "Failed to update pick order", Toast.LENGTH_SHORT).show();
                                });

                        Volley.newRequestQueue(context).add(request);
                    }
                });
                dialog.setOnDismissListener(d -> {
                    selectedPosition = -1; // Reset selection
                    notifyDataSetChanged();
                });

                dialog.show();
            }
        });
        holder.row_table_pick_status.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Delete Picklist item")
                        .setMessage("Are you sure, you want to delete item?")
                        .setIcon(R.drawable.delete_24)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int id = pick_status_list.get(position).id;
                                String deleteUrl = "http://192.168.0.105:8082/app1/Deletepickstatus";

                                JSONObject params = new JSONObject();
                                try {
                                    params.put("id", id);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Send DELETE request
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                        Request.Method.POST,
                                        deleteUrl,
                                        params,
                                        response -> {
                                            try {
                                                if (response.getString("program_status").equals("success")) {
                                                    // Remove the item locally
                                                    pick_status_list.remove(position);

                                                    for (int i = 0; i < pick_status_list.size(); i++) {
                                                        pick_status_list.get(i).id = i + 1;
                                                    }
                                                    selectedPosition = -1;
                                                    notifyDataSetChanged();
                                                    //notifyItemRemoved(position);
                                                    Toast.makeText(context, "Item deleted successfully!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (Exception e) {
                                                Toast.makeText(context, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        },
                                        error -> {
                                            Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                ) {
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String, String> headers = new HashMap<>();
                                        headers.put("Content-Type", "application/json"); // Set JSON Content-Type
                                        return headers;
                                    }
                                };

                                // Add the request to the queue
                                Volley.newRequestQueue(context).add(jsonObjectRequest);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedPosition = -1;
                                notifyDataSetChanged();

                            }
                        });
                builder.setOnDismissListener(d -> {
                    selectedPosition = -1; // Reset selection
                    notifyDataSetChanged();
                });

                builder.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return pick_status_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView code_pick_status, date_pick_status,name_pick_status;
        TextView number_kits_pick_status,number_kits_picked_pick_status,pick_status;
        LinearLayout row_table_pick_status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            code_pick_status = itemView.findViewById(R.id.code_pick_status);
            date_pick_status = itemView.findViewById(R.id.date_pick_status);
            name_pick_status = itemView.findViewById(R.id.name_pick_status);
            number_kits_pick_status = itemView.findViewById(R.id.number_kits_pick_status);
            number_kits_picked_pick_status = itemView.findViewById(R.id.number_kits_picked_pick_status);
            pick_status = itemView.findViewById(R.id.pick_status);
            row_table_pick_status = itemView.findViewById(R.id.row_table_pick_status);
        }
    }
}
