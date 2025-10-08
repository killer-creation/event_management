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
import java.time.LocalDateTime; // Use modern date/time API
import java.time.format.DateTimeFormatter;

@WebServlet("/PaymentServlet1")
public class PaymentServlet1 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials (update with yours)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/event_management";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get parameters from the form submission
        String machineName = request.getParameter("machine_name");
        String quantityStr = request.getParameter("quantity");
        String totalAmountStr = request.getParameter("total_amount");
        String datetime = request.getParameter("date_time");
        String mobileNumber = request.getParameter("mobile_no");
        String addressDetails = request.getParameter("address");
        String paymentMethod = request.getParameter("payment_method");
        
        // Generate a simple Invoice ID for the bill
        String invoiceId = "INV-" + System.currentTimeMillis(); 
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Connection con = null;
        PreparedStatement psEventBooking = null;
        PreparedStatement psPayment = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            con.setAutoCommit(false); // Start transaction

            // --- Database Operations (Same as before) ---
            if (machineName == null || machineName.trim().isEmpty() || quantityStr == null || quantityStr.trim().isEmpty() || totalAmountStr == null || totalAmountStr.trim().isEmpty() || datetime == null || datetime.trim().isEmpty()) {
                throw new IllegalArgumentException("Missing one or more required fields.");
            }
            
            int quantity = Integer.parseInt(quantityStr);
            double totalAmount = Double.parseDouble(totalAmountStr);

            // 1. Insert into event_item_booking table
            String sqlEventBooking = "INSERT INTO event_item_booking (machine_name, quantity, total_amount, date_time, mobile_no, address) VALUES (?, ?, ?, ?, ?, ?)";
            psEventBooking = con.prepareStatement(sqlEventBooking);
            psEventBooking.setString(1, machineName);
            psEventBooking.setInt(2, quantity);
            psEventBooking.setDouble(3, totalAmount);
            psEventBooking.setString(4, datetime);
            psEventBooking.setString(5, mobileNumber);
            psEventBooking.setString(6, addressDetails);
            psEventBooking.executeUpdate();

            // 2. Insert into payments table
            String sqlPayment = "INSERT INTO payments (item_details, mobile_number, address_details, payment_method) VALUES (?, ?, ?, ?)";
            psPayment = con.prepareStatement(sqlPayment);
            String combinedDetails = "Machine: " + machineName + ", Qty: " + quantity + ", Total: " + totalAmount;
            psPayment.setString(1, combinedDetails);
            psPayment.setString(2, mobileNumber);
            psPayment.setString(3, addressDetails);
            psPayment.setString(4, paymentMethod);
            psPayment.executeUpdate();

            con.commit(); // Commit all operations if successful

            // --- HTML Bill Generation (New part) ---
            
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Booking Confirmation - VIP Traders</title>");
            // Inline CSS for the Bill
            out.println("<style>");
            out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 20px; }");
            out.println(".container { max-width: 700px; margin: 20px auto; background: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); border-top: 5px solid #ff6600; }");
            out.println("h2 { color: #ff6600; text-align: center; margin-bottom: 25px; border-bottom: 2px solid #eee; padding-bottom: 10px; }");
            out.println("h1.logo { text-align: center; color: #333; margin-bottom: 30px; font-size: 2.5em; }");
            out.println(".header-info p { margin: 5px 0; font-size: 0.9em; }");
            out.println(".bill-details, .customer-info { margin-bottom: 25px; padding: 15px; border: 1px solid #ddd; border-radius: 8px; }");
            out.println(".bill-details h3, .customer-info h3 { color: #555; border-bottom: 1px dashed #ddd; padding-bottom: 5px; margin-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
            out.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            out.println("th { background-color: #f0f0f0; color: #333; }");
            out.println("tfoot td { font-weight: bold; background-color: #ffe0b2; }");
            out.println(".total-row td { font-size: 1.2em; color: #ff6600; }");
            out.println(".print-btn { display: block; width: 250px; margin: 30px auto 10px auto; padding: 12px 20px; background-color: #4CAF50; color: white; text-align: center; border: none; border-radius: 5px; cursor: pointer; font-size: 1.1em; transition: background-color 0.3s; }");
            out.println(".print-btn:hover { background-color: #45a049; }");
            out.println("@media print { .print-btn { display: none; } body { background-color: #fff; } .container { box-shadow: none; border: none; } }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            out.println("<div class='container'>");
            out.println("<h1 class='logo'>VIP Traders</h1>");
            out.println("<h2>Booking Confirmation & Bill</h2>");
            
            // Header Info
            out.println("<div class='header-info'>");
            out.println("<p><strong>Invoice ID:</strong> " + invoiceId + "</p>");
            out.println("<p><strong>Date:</strong> " + currentDate + "</p>");
            out.println("</div>");

            // Customer Info
            out.println("<div class='customer-info'>");
            out.println("<h3>Customer Details</h3>");
            out.println("<p><strong>Mobile No:</strong> " + mobileNumber + "</p>");
            out.println("<p><strong>Address:</strong> " + addressDetails + "</p>");
            out.println("<p><strong>Event Date/Time:</strong> " + datetime + "</p>");
            out.println("</div>");
            
            // Bill Details Table
            out.println("<div class='bill-details'>");
            out.println("<h3>Order Summary</h3>");
            out.println("<table>");
            out.println("<thead><tr><th>Description</th><th>Unit Price </th><th>Quantity</th><th>Subtotal </th></tr></thead>");
            out.println("<tbody>");
            out.println("<tr>");
            out.println("<td>" + machineName + "</td>");
            out.println("<td>" + (totalAmount / quantity) + "</td>");
            out.println("<td>" + quantity + "</td>");
            out.println("<td>" + totalAmount + "</td>");
            out.println("</tr>");
            out.println("</tbody>");
            out.println("<tfoot>");
            out.println("<tr class='total-row'><td colspan='3'><strong>TOTAL AMOUNT DUE</strong></td><td><strong>" + totalAmount + "</strong></td></tr>");
            out.println("<tr><td colspan='4'><strong>Payment Method:</strong> " + paymentMethod + "</td></tr>");
            out.println("</tfoot>");
            out.println("</table>");
            out.println("</div>"); // .bill-details
            
            out.println("<button class='print-btn' onclick='window.print()'> Print / Save as PDF</button>");
            out.println("</div>"); // .container
            
            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                // If rollback fails, just log it.
                ex.printStackTrace();
            }
            e.printStackTrace();
            out.println("<!DOCTYPE html><html><body><h2>❌ Error processing your request: " + e.getMessage() + "</h2><p>Please go back and try again.</p></body></html>");
        } finally {
            try { if (psEventBooking != null) psEventBooking.close(); } catch (Exception e) {}
            try { if (psPayment != null) psPayment.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
}