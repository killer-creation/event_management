package event_management;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/EmployeeLoginServlet")
public class EmployeeLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String empName = request.getParameter("emp_name");
        String password = request.getParameter("emp_password");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/event_management", "root", "");
            
            String sql = "SELECT * FROM employe WHERE emp_name=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, empName);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                out.println("<h2>Employee Login Successful</h2>");
            	    response.sendRedirect("user dashboard.html");

            } else {
                out.println("<h2>Employee Login Failed. Invalid credentials.</h2>");
            }
            
            con.close();
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}