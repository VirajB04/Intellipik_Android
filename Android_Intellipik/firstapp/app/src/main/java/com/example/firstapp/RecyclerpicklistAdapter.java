package com.example.firstapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class RecyclerpicklistAdapter extends RecyclerView.Adapter<RecyclerpicklistAdapter.ViewHolder> {
    Context context;
    private List<pick_list_model> pick_list;
    int selectedPosition = -1;

    RecyclerpicklistAdapter(Context context, List<pick_list_model> pick_list) {
        this.context = context;
        this.pick_list = pick_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.pick_list_row, parent,false);
        ViewHolder view = new ViewHolder(v);
        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        pick_list_model model = (pick_list_model)pick_list.get(position);
        holder.id_pick_list.setText(String.valueOf(pick_list.get(position).id));
        holder.name_pick_list.setText((pick_list.get(position).name));
        holder.code_pick_list.setText((pick_list.get(position)).code);
        holder.number_pick_list.setText(String.valueOf(pick_list.get(position).number));

        if (selectedPosition == position) {
            holder.row_table_pick_list.setBackgroundColor(Color.parseColor("#289DCC"));
        } else {
            holder.row_table_pick_list.setBackgroundColor(Color.parseColor("#DBF5FF"));
        }

        holder.row_table_pick_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_update_picklist);

                EditText edtName = dialog.findViewById(R.id.picklist_name);
                EditText edtCode = dialog.findViewById(R.id.picklist_code);
                EditText edtNumber = dialog.findViewById((R.id.picklist_number));
                Button add_button = dialog.findViewById(R.id.add_button);
                TextView welcome_head = dialog.findViewById(R.id.welcome_head);

                welcome_head.setText("Edit Pick list Item");

                add_button.setText("Update");

                edtName.setText((pick_list.get(position)).name);
                edtCode.setText((pick_list.get(position)).code);
                edtNumber.setText(String.valueOf(pick_list.get(position).number));
                int itemId = pick_list.get(position).id;

                add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Updated_name = edtName.getText().toString().trim();
                        String Updated_codeInput = edtCode.getText().toString().trim();
                        String Updated_noOfItemsStr = edtNumber.getText().toString().trim();

                        if (Updated_name.isEmpty() || Updated_codeInput.isEmpty() || Updated_noOfItemsStr.isEmpty()) {
                            Toast.makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int no_of_items;
                        try {
                            no_of_items = Integer.parseInt(Updated_noOfItemsStr);
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Invalid number of items!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String url = "http://192.168.0.105:8082/app1/Updatepicklist";

                        Log.d("UpdateItem", "ID: " + itemId + ", Name: " + Updated_name + ", Code: " + Updated_codeInput + ", no_of_items: " + no_of_items);

                        JSONObject params = new JSONObject();
                        try {
                            params.put("id", itemId);
                            params.put("name", Updated_name);
                            params.put("code", Updated_codeInput);
                            params.put("no_of_items", no_of_items);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                params,
                                response -> {
                                    try {
                                        if (response.getString("status").equals("success")) {
                                            // Update the item locally
                                            pick_list.get(position).name = Updated_name;
                                            pick_list.get(position).code = Updated_codeInput;
                                            pick_list.get(position).number = no_of_items;

                                            notifyItemChanged(position);
                                            dialog.dismiss();
                                            Toast.makeText(context, "Item updated successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(context, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                },
                                error -> {
                                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("VolleyError", error.toString());
                                }
                        );

                        Volley.newRequestQueue(context).add(jsonObjectRequest);
                    }
                });
                dialog.setOnDismissListener(d -> {
                    selectedPosition = -1; // Reset selection
                    notifyDataSetChanged();
                });
                dialog.show();
            }
        });
        holder.row_table_pick_list.setOnLongClickListener(new View.OnLongClickListener() {
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
                                int id = pick_list.get(position).id;
                                String deleteUrl = "http://192.168.0.105:8082/app1/Deletepicklist";

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
                                                if (response.getString("status").equals("success")) {
                                                    // Remove the item locally
                                                    pick_list.remove(position);

                                                    for (int i = 0; i < pick_list.size(); i++) {
                                                        pick_list.get(i).id = i + 1;
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
                                selectedPosition = -1; // Reset selection
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
        return pick_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_pick_list, code_pick_list, id_pick_list, number_pick_list;
        LinearLayout row_table_pick_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name_pick_list = itemView.findViewById(R.id.name_pick_list);
            code_pick_list = itemView.findViewById(R.id. code_pick_list);
            id_pick_list = itemView.findViewById(R.id.id_pick_list );
            number_pick_list = itemView.findViewById(R.id.number_items_pick_list);
            row_table_pick_list = itemView.findViewById(R.id.row_table_pick_list);
        }
    }
}
