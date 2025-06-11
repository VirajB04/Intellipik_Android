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
import java.util.List;

public class RecyclerItemlistAdapter extends RecyclerView.Adapter<RecyclerItemlistAdapter.ViewHolder> {
    Context context;
    private List<item_list_model> list_of_items;
    int selectedPosition = -1;

    RecyclerItemlistAdapter(Context context, List<item_list_model> list_of_items){
        this.context = context;
        this.list_of_items = list_of_items;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_list_row,parent,false);
        ViewHolder view = new ViewHolder(v);
        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        item_list_model model = (item_list_model) list_of_items.get(position);
      holder.column_id.setText(String.valueOf(list_of_items.get(position).id));
        holder.column_code.setText(list_of_items.get(position).code);
        holder.column_name.setText(list_of_items.get(position).name);

        if (selectedPosition == position) {
            holder.row_table.setBackgroundColor(Color.parseColor("#289DCC"));
        } else {
            holder.row_table.setBackgroundColor(Color.parseColor("#DBF5FF"));
        }

        holder.row_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_update_layout);

                EditText edtName = dialog.findViewById(R.id.item_name);
                EditText edtCode = dialog.findViewById(R.id.item_code);
                Button add_button = dialog.findViewById(R.id.add_button);
                TextView welcome_head = dialog.findViewById(R.id.welcome_head);

                welcome_head.setText("Edit Item");

                add_button.setText("Update");

                edtName.setText((list_of_items.get(position)).name);
                edtCode.setText((list_of_items.get(position)).code);
                int itemId = list_of_items.get(position).id;

                add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String updatedName = edtName.getText().toString().trim();
                        String updatedCode = edtCode.getText().toString().trim();

                        if (!updatedName.isEmpty() && !updatedCode.isEmpty()) {
                            String updateUrl = "http://192.168.0.105:8082/app1/Updateitemlist";

                            Log.d("UpdateItem", "ID: " + itemId + ", Name: " + updatedName + ", Code: " + updatedCode);

                            JSONObject params = new JSONObject();
                            try {
                                params.put("id", itemId);
                                params.put("name", updatedName);
                                params.put("code", updatedCode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                    Request.Method.POST,
                                    updateUrl,
                                    params,
                                    response -> {
                                        try {
                                            if (response.getString("status").equals("success")) {
                                                // Update the item locally
                                                list_of_items.get(position).name = updatedName;
                                                list_of_items.get(position).code = updatedCode;

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
                                    error -> Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
                            );

                            Volley.newRequestQueue(context).add(jsonObjectRequest);
                        } else {
                            Toast.makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.setOnDismissListener(d -> {
                    selectedPosition = -1; // Reset selection
                    notifyDataSetChanged();
                });

                dialog.show();
            }

        });
        holder.row_table.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Delete Item")
                        .setMessage("Are you sure, you want to delete item?")
                        .setIcon(R.drawable.delete_24)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int itemId = list_of_items.get(position).id;
                                String deleteUrl = "http://192.168.0.105:8082/app1/Deleteitemlist";

                                JSONObject params = new JSONObject();
                                try {
                                    params.put("id", itemId);
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
                                                    list_of_items.remove(position);

                                                    for (int i = 0; i < list_of_items.size(); i++) {
                                                        list_of_items.get(i).id = i + 1; // Assign new IDs
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
                                        error -> Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
                                );

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
        return list_of_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        TextView column_name, column_code, column_id;
        LinearLayout row_table;
        public ViewHolder(View itemView){
            super(itemView);

            column_name = itemView.findViewById(R.id. column_name);
            column_code = itemView.findViewById(R.id. column_code);
            column_id = itemView.findViewById(R.id.column_id );
            row_table = itemView.findViewById(R.id.row_table);
        }
    }
}
