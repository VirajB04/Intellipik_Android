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
import org.json.JSONObject;

@WebServlet("/Pickstatus")
public class Pickstatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true","root","Viraj@9311");
            
            PreparedStatement preparestatement = con.prepareStatement("SELECT * FROM pick_order ORDER BY code ASC");
            
            ResultSet rs = preparestatement.executeQuery();
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            
            while (rs.next()){
            	JSONObject item = new JSONObject();
            	item.put("id", rs.getInt("id"));
            	item.put("code", rs.getInt("code"));
            	item.put("date", rs.getString("date"));
            	item.put("name", rs.getString("name"));
            	item.put("no_of_kits", rs.getInt("no_of_kits"));
            	item.put("no_of_kits_picked", rs.getInt("no_of_kits_picked"));
            	item.put("status", rs.getString("status"));
                jsonArray.put(item);
            }
            jsonResponse.put("program_status", "success");
            jsonResponse.put("data", jsonArray);
            
            out.print(jsonResponse.toString());
            con.close();
            
        }catch(Exception e){
        	try {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", "Database error: " + e.getMessage());
                out.print(error.toString());
            } catch (Exception jsonEx) {
            	out.print("{\"status\":\"error\",\"message\":\"Unexpected error\"}");
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
        return "Servlet to provide pick status data in JSON format";
    }

}
