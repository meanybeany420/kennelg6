package UI;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Databaseinit;

public class ViewOwnersPanel extends JPanel {

    private JTable ownersTable;
    private DefaultTableModel model;

    public ViewOwnersPanel(CardLayout cardLayout, JPanel mainPanel) {

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.setPreferredSize(new Dimension(800, 40));

        JButton homeButton = Homebutton.createHomeButton(cardLayout, mainPanel);
        topPanel.add(homeButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel viewOwnersLabel = new JLabel("View Owners");
        viewOwnersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] columns = {
                "Owner ID",
                "First Name",
                "Surname",
                "Email",
                "Phone Number",
                "Address"
        };

        model = new DefaultTableModel(columns, 0);
        ownersTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(ownersTable);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        refreshButton.addActionListener(e -> loadOwners());

        contentPanel.add(viewOwnersLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        loadOwners();
    }

    private void loadOwners() {
        model.setRowCount(0);

        String sql = """
            SELECT owner_id, owner_name, owner_Sname, owner_email, owner_phone_number, owner_address
            FROM owner
            ORDER BY owner_id
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("owner_id"),
                        rs.getString("owner_name"),
                        rs.getString("owner_Sname"),
                        rs.getString("owner_email"),
                        rs.getString("owner_phone_number"),
                        rs.getString("owner_address")
                };

                model.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load owners.");
        }
    }
}
