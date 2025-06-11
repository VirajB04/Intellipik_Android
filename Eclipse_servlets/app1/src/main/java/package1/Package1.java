package package1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
//added new ones
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.PrintWriter;


@WebServlet("/Package1")
public class Package1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String username =request.getParameter("name");
        String email = request.getParameter("email");
        String password =request.getParameter("password") ;
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/signupinfo?useSSL=false&serverTimezone=UTC","root","Viraj@9311");
            
            PreparedStatement preparestatement = con.prepareStatement("insert into users (name, email, password) VALUES (?, ?, ?)");
            preparestatement.setString(1,username);
            preparestatement.setString(2,email);
            preparestatement.setString(3,password);
            
            int i = preparestatement.executeUpdate();
            if(i>0){
                out.print("User registered successfully!");
            }else{
                out.print("Failed to register user!");
            }
            con.close();
            
        }catch(Exception e){
            out.print("Error: " + e.getMessage());
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
        return "Sign up page for user";
    }
}
