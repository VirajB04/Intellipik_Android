package package1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet("/Addpickstatus")
public class Addpickstatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String Input_code = request.getParameter("code");
        String date = request.getParameter("date");
        String name = request.getParameter("name");
        String Input_no_of_kits = request.getParameter("no_of_kits");
        String Input_no_of_kits_picked = request.getParameter("no_of_kits_picked");
        String status = request.getParameter("status");
        
        int no_of_kits = 0;
        int no_of_kits_picked = 0;
        int code= 0;

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC", 
                "root", "Viraj@9311")) {
        	
        	if ((Input_code != null && !Input_code.isEmpty())) {
                try {
                	code = Integer.parseInt(Input_code);
                } catch (NumberFormatException e) {
                    throw new ServletException("Invalid value for code. It must be an integer.");
                }
            }

        	if (Input_no_of_kits != null && !Input_no_of_kits.isEmpty()) {
        	    try {
        	        no_of_kits = Integer.parseInt(Input_no_of_kits);
        	    } catch (NumberFormatException e) {
        	        throw new ServletException("Invalid value for no_of_kits. It must be an integer.");
        	    }
        	} else {
        	    no_of_kits = 0; // Default to 0
        	}
        	
        	if (Input_no_of_kits_picked != null && !Input_no_of_kits_picked.isEmpty()) {
        	    try {
        	        no_of_kits_picked = Integer.parseInt(Input_no_of_kits_picked);
        	    } catch (NumberFormatException e) {
        	        throw new ServletException("Invalid value for no_of_kits_picked. It must be an integer.");
        	    }
        	} else {
        	    no_of_kits_picked = 0; // Default to 0
        	}
        	
            // Insert new item if nothing is empty & are provided
            if (name != null && date != null && status!= null && code !=0) {
                String insertQuery = "INSERT INTO pick_order (code,date,name,no_of_kits, no_of_kits_picked,status) VALUES (?, ?, ?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(insertQuery)) {
                    ps.setInt(1, code);
                    ps.setString(2, date);
                    ps.setString(3, name);
                    ps.setInt(4, no_of_kits);
                    ps.setInt(5, no_of_kits_picked);
                    ps.setString(6, status);
                    ps.executeUpdate();
                }
            }

            // Fetch updated pick_list of items
            String selectQuery = "SELECT code, date, name, no_of_kits, no_of_kits_picked, status FROM pick_order ORDER BY code ASC";
            try (PreparedStatement ps = con.prepareStatement(selectQuery);
                 ResultSet rs = ps.executeQuery()) {

                JSONArray jsonArray = new JSONArray();

                while (rs.next()) {
                    JSONObject item = new JSONObject();
                    item.put("code", rs.getInt("code"));
                    item.put("date", rs.getString("date"));
                    item.put("name", rs.getString("name"));
                    item.put("no_of_kits", rs.getInt("no_of_kits"));
                    item.put("no_of_kits_picked", rs.getInt("no_of_kits_picked"))  ; 
                    item.put("status", rs.getString("status"));
                    jsonArray.put(item);
                }

                JSONObject result = new JSONObject();
                result.put("program_status", "success");
                result.put("data", jsonArray);

                out.print(result.toString());
            }
        } catch (Exception e) {
            try {
                JSONObject error = new JSONObject();
                error.put("program_status", "error");
                error.put("message", e.getMessage());
                out.print(error.toString());
            } catch (JSONException jsonException) {
                // Handle JSONException if needed
                out.print("{\"program_status\":\"error\",\"message\":\"Failed to generate error response\"}");
            }
        }
    }
       
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
	
	@Override
    public String getServletInfo() {
        return "Adding an item to pick status list";
    }
}
