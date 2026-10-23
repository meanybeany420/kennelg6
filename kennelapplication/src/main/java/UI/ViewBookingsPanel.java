package UI;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Databaseinit;

public class ViewBookingsPanel extends JPanel {

    private JTable bookingsTable;
    private DefaultTableModel model;

    public ViewBookingsPanel(CardLayout cardLayout, JPanel mainPanel) {

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.setPreferredSize(new Dimension(800, 40));

        JButton homeButton = Homebutton.createHomeButton(cardLayout, mainPanel);
        topPanel.add(homeButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel viewBookingsLabel = new JLabel("Bookings");
        viewBookingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] columns = {
                "Booking ID",
                "Owner Name",
                "Animal Name",
                "Animal Type",
                "Kennel ID",
                "Start Date",
                "End Date"
        };

        model = new DefaultTableModel(columns, 0);
        bookingsTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);

        JButton refreshButton = new JButton("Refresh");
        JButton deleteButton = new JButton("Delete Booking");

        refreshButton.addActionListener(e -> loadBookings());

        deleteButton.addActionListener(e -> deleteSelectedBooking());

        contentPanel.add(viewBookingsLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(refreshButton);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        loadBookings();
    }

    private void loadBookings() {
        model.setRowCount(0);

        String sql = """
            SELECT 
                b.booking_id,
                o.owner_name,
                a.animal_name,
                a.animal_type,
                b.kennel_id,
                b.start_date,
                b.end_date
            FROM booking b
            JOIN animal a ON b.animal_id = a.animal_id
            JOIN owner o ON a.owner_id = o.owner_id
            ORDER BY b.start_date
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("booking_id"),
                        rs.getString("owner_name"),
                        rs.getString("animal_name"),
                        rs.getString("animal_type"),
                        rs.getInt("kennel_id"),
                        rs.getString("start_date"),
                        rs.getString("end_date")
                };

                model.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load bookings.");
        }
    }

    private void deleteSelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
            return;
        }

        int bookingId = (int) model.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this booking?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM booking WHERE booking_id = ?";

            try (Connection conn = Databaseinit.connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, bookingId);
                stmt.executeUpdate();

                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Booking deleted.");

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Could not delete booking.");
            }
        }
    }
}
