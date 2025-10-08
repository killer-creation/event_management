package event_management;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ViewEmployees")
public class ViewEmployees extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private String jdbcURL = "jdbc:mysql://localhost:3306/event_management";
    private String jdbcUsername = "root";    // 🔹 Change if different
    private String jdbcPassword = "";        // 🔹 Add your MySQL password

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

            stmt = con.createStatement();
            String sql = "SELECT * FROM employe";
            rs = stmt.executeQuery(sql);

            // HTML Table
            out.println("<html><head><title>Employee Data</title>");
            out.println("<style>");
            out.println("table { border-collapse: collapse; width: 80%; margin: 20px auto; font-family: Arial, sans-serif; }");
            out.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            out.println("th { background-color: #f2f2f2; color: #333; }");
            out.println("h2 { text-align: center; color: #444; }");
            out.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            out.println("</style>");
            out.println("</head><body>");

            out.println("<h2> Employee Data</h2>");
            out.println("<table>");
            out.println("<tr><th>Employee ID</th><th>Employee Name</th><th>Password</th><th>Email ID</th><th>Mobile No</th><th>Address</th></tr>");

            while (rs.next()) {
                int empId = rs.getInt("emp_id");
                String empName = rs.getString("emp_name");
                String password = rs.getString("password");
                String emailId = rs.getString("email_id");
                String mobileNo = rs.getString("mobile_no");
                String address = rs.getString("address");

                out.println("<tr>");
                out.println("<td>" + empId + "</td>");
                out.println("<td>" + empName + "</td>");
                out.println("<td>" + password + "</td>");
                out.println("<td>" + emailId + "</td>");
                out.println("<td>" + mobileNo + "</td>");
                out.println("<td>" + address + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("<div style='text-align:center; margin-top:20px;'>");
            out.println("<a href='admin dashboard.html'> Back to admin dashboard</a>");
            out.println("</div>");
            out.println("</body></html>");

        } catch (Exception e) {
            out.println("<h2>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace(out);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace(out);
            }
        }
    }
}