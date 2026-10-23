package UI;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Databaseinit;

public class ViewAnimalsPanel extends JPanel {

    private JTable animalsTable;
    private DefaultTableModel model;

    public ViewAnimalsPanel(CardLayout cardLayout, JPanel mainPanel) {

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.setPreferredSize(new Dimension(800, 40));

        JButton homeButton = Homebutton.createHomeButton(cardLayout, mainPanel);
        topPanel.add(homeButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel viewAnimalLabel = new JLabel("View Animals");
        viewAnimalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] columns = {
                "Animal ID",
                "Animal Name",
                "Animal Type",
                "Animal Size",
                "Notes",
                "Owner Name"
        };

        model = new DefaultTableModel(columns, 0);
        animalsTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(animalsTable);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton deleteButton = new JButton("Delete Animal");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        refreshButton.addActionListener(e -> loadAnimals());
        deleteButton.addActionListener(e -> deleteSelectedAnimal());

        contentPanel.add(viewAnimalLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(refreshButton);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        loadAnimals();
    }

    private void loadAnimals() {
        model.setRowCount(0);

        String sql = """
            SELECT 
                a.animal_id,
                a.animal_name,
                a.animal_type,
                a.animal_size,
                a.animal_notes,
                o.owner_name
            FROM animal a
            LEFT JOIN owner o ON a.owner_id = o.owner_id
            ORDER BY a.animal_id
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("animal_id"),
                        rs.getString("animal_name"),
                        rs.getString("animal_type"),
                        rs.getString("animal_size"),
                        rs.getString("animal_notes"),
                        rs.getString("owner_name")
                };

                model.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load animals.");
        }
    }

    private void deleteSelectedAnimal() {
        int selectedRow = animalsTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an animal first.");
            return;
        }

        int animalId = (int) model.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this animal?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM animal WHERE animal_id = ?";

            try (Connection conn = Databaseinit.connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, animalId);
                stmt.executeUpdate();

                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Animal deleted.");

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Could not delete animal. This animal may have a booking linked to it."
                );
            }
        }
    }
}
