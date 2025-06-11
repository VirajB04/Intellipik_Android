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
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;


@WebServlet("/Itemlist")
public class Itemlist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true","root","Viraj@9311");
            
            PreparedStatement preparestatement = con.prepareStatement("SELECT * FROM item_list");
            
            ResultSet rs = preparestatement.executeQuery();
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            
            while (rs.next()){
            	JSONObject item = new JSONObject();
            	item.put("id", rs.getInt("id"));
            	item.put("code", rs.getString("code"));
            	item.put("name", rs.getString("item_name"));
                jsonArray.put(item);
            }
            jsonResponse.put("status", "success");
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
        return "Servlet to provide item list data in JSON format";
    }
}
