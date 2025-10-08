package event_management;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PaymentServlet2")
public class PaymentServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // --- Database Configuration ---
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/event_management";
    private static final String JDBC_USER = "root";   
    private static final String JDBC_PASS = "";       

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get form data from the request
        String eventDetails = request.getParameter("event_details");
        String datetimeDetails = request.getParameter("datetime_details");
        String persons = request.getParameter("persons_details");
        String rent = request.getParameter("rent");
        String paymentMethod = request.getParameter("payment_method"); // <-- NEW: Retrieve payment method
       
        // --- Data Parsing ---
        String[] dtSplit = datetimeDetails.split(" ");
        String date = dtSplit[0];
        String time = dtSplit.length > 1 ? dtSplit[1] : "00:00:00";
        String eventName = eventDetails.replace("Event:", "").trim();
        
        // Generate bill-specific details
        String invoiceId = "HALL-" + System.currentTimeMillis(); 
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // SQL statements for the two tables (Keep original SQL)
        String sqlBooking = "INSERT INTO hall_booking(event_name, date, time, total_persons, rent) VALUES (?,?,?,?,?)";
        String sqlPayment = "INSERT INTO hall_payment(event_name, date, time, total_persons, rent) VALUES (?,?,?,?,?)";
        
        Connection con = null;
        PreparedStatement psBooking = null;
        PreparedStatement psPayment = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
            con.setAutoCommit(false); // Start transaction for atomicity
            
            int rentAmount = Integer.parseInt(rent);
            int totalPersons = Integer.parseInt(persons);

            // Set parameters for the hall_booking statement
            psBooking = con.prepareStatement(sqlBooking);
            psBooking.setString(1, eventName);
            psBooking.setString(2, date);
            psBooking.setString(3, time);
            psBooking.setInt(4, totalPersons);
            psBooking.setInt(5, rentAmount);

            // Set parameters for the hall_payment statement
            psPayment = con.prepareStatement(sqlPayment);
            psPayment.setString(1, eventName);
            psPayment.setString(2, date);
            psPayment.setString(3, time);
            psPayment.setInt(4, totalPersons);
            psPayment.setInt(5, rentAmount);
          
            // Execute both updates
            int rowBooking = psBooking.executeUpdate();
            int rowPayment = psPayment.executeUpdate();

            if (rowBooking > 0 && rowPayment > 0) {
                con.commit(); // Commit all operations if successful

                // ------------------------------------------------------------------
                // --- NEW: HTML Bill Generation ---
                // ------------------------------------------------------------------
                
                out.println("<!DOCTYPE html>");
                out.println("<html lang='en'>");
                out.println("<head>");
                out.println("<meta charset='UTF-8'>");
                out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
                out.println("<title>Hall Booking Invoice</title>");
                
                // Inline CSS for Bill Presentation and Print Handling
                out.println("<style>");
                out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 20px; }");
                out.println(".container { max-width: 700px; margin: 20px auto; background: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); border-top: 5px solid #6f42c1; }");
                out.println("h1.logo { text-align: center; color: #333; margin-bottom: 5px; font-size: 2.5em; }");
                out.println("h2 { color: #6f42c1; text-align: center; margin-bottom: 25px; border-bottom: 2px solid #ddd; padding-bottom: 10px; }");
                out.println(".info-section { margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 8px; }");
                out.println(".info-section p { margin: 5px 0; font-size: 1em; }");
                out.println("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
                out.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
                out.println("th { background-color: #f0f0f0; color: #333; }");
                out.println("tfoot td { font-weight: bold; background-color: #e6e6fa; }");
                out.println(".total-row td { font-size: 1.2em; color: #6f42c1; }");
                out.println(".print-btn, .home-btn { display: block; width: 250px; margin: 15px auto 10px auto; padding: 12px 20px; color: white; text-align: center; border: none; border-radius: 5px; cursor: pointer; font-size: 1.1em; text-decoration: none; transition: background-color 0.3s; }");
                out.println(".print-btn { background-color: #007bff; }");
                out.println(".print-btn:hover { background-color: #0056b3; }");
                out.println(".home-btn { background-color: #6f42c1; }");
                out.println(".home-btn:hover { background-color: #5a32a3; }");
                out.println("@media print { .print-btn, .home-btn { display: none; } .container { box-shadow: none; border: none; } }");
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");
                
                out.println("<div class='container'>");
                out.println("<h1 class='logo'>Hall Booking</h1>");
                out.println("<h2>Invoice & Confirmation</h2>");
                
                // Header Info
                out.println("<div class='info-section'>");
                out.println("<p><strong>Invoice ID:</strong> " + invoiceId + "</p>");
                out.println("<p><strong>Date Generated:</strong> " + currentDate + "</p>");
                out.println("<p><strong>Event Date & Time:</strong> " + date + " at " + time + "</p>");
                out.println("</div>");

                // Event & Payment Info
                out.println("<div class='info-section'>");
                out.println("<h3>Event & Payment Details</h3>");
                out.println("<p><strong>Event Name:</strong> " + eventName + "</p>");
                out.println("<p><strong>Total Persons:</strong> " + persons + "</p>");
                out.println("<p><strong>Payment Method:</strong> " + paymentMethod + "</p>");
                out.println("<p><strong>Payment Status:</strong> Successful</p>");
                out.println("</div>");
                
                // Bill Details Table
                out.println("<h3>Summary of Charges</h3>");
                out.println("<table>");
                out.println("<thead><tr><th>Description</th><th>Unit Price </th><th>Quantity</th><th>Amount </th></tr></thead>");
                out.println("<tbody>");
                out.println("<tr>");
                out.println("<td>Hall Rental - " + eventName + "</td>");
                out.println("<td>" + rentAmount + "</td>");
                out.println("<td>1</td>");
                out.println("<td>" + rentAmount + "</td>");
                out.println("</tr>");
                out.println("</tbody>");
                out.println("<tfoot>");
                out.println("<tr class='total-row'><td colspan='3'><strong>TOTAL AMOUNT PAID</strong></td><td><strong>" + rentAmount + "</strong></td></tr>");
                out.println("</tfoot>");
                out.println("</table>");
                
                // Print Button (Allows Save as PDF)
                out.println("<button class='print-btn' onclick='window.print()'> Print / Save as PDF</button>");
                
                // Button to return to the hall booking page
                out.println("<a href='hall_booking_form.html' class='home-btn'> New Hall Booking</a>");

                out.println("</div>"); // .container
                out.println("</body>");
                out.println("</html>");

            } else {
                 con.rollback(); // Rollback if not all rows were inserted
                 out.println("<h2 style='color:red;'>Failed to save booking or payment. Please try again.</h2>");
            }
        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            // Log the error and show a user-friendly message
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(out); }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<!DOCTYPE html><html><body><div class='container' style='background:#f9e8e8; border-top: 5px solid #cc0000;'><h2>❌ Error Processing Booking</h2><p>An error occurred while saving your booking. Please try again.</p></div></body></html>");
            e.printStackTrace(out); // Print detailed error for debugging
        } finally {
            // Close resources
            try { if (psBooking != null) psBooking.close(); } catch (Exception e) {}
            try { if (psPayment != null) psPayment.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
}