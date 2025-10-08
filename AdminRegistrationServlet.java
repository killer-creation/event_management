package event_management;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AdminRegistrationServlet")
public class AdminRegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private String jdbcURL = "jdbc:mysql://localhost:3306/event_management";
    private String jdbcUsername = "root";  // 🔹 Change if your MySQL username is different
    private String jdbcPassword = ""; // 🔹 Change to your MySQL password

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get form data
        String name = request.getParameter("name");
        String email_id = request.getParameter("email_id");
        String mobile_no = request.getParameter("mobile_no");
        String password = request.getParameter("password");

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            Connection con = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

            // Insert query
            String query = "INSERT INTO admin(name, email_id, mobile_no, password) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, name);
            ps.setString(2, email_id);
            ps.setString(3, mobile_no);   // 🔹 Stored as String (VARCHAR in DB)
            ps.setString(4, password);

            int result = ps.executeUpdate();

            if (result > 0) {
                out.println("<h2>Admin Registered Successfully!</h2>");
            	response.sendRedirect("admin dashboard.html");

            } else {
                out.println("<h2>Registration Failed. Please try again.</h2>");
            }

            ps.close();
            con.close();

        } catch (Exception e) {
            out.println("<h2>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace();
        }
    }
}

