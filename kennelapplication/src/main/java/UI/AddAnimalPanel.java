package UI;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Databaseinit;

public class AddAnimalPanel extends JPanel {

    private JComboBox<OwnerOption> ownerComboBox;
    private JTextField animalNameField;
    private JComboBox<String> animalTypeComboBox;
    private JComboBox<String> animalSizeComboBox;
    private JTextField animalNotesField;

    public AddAnimalPanel(CardLayout cardLayout, JPanel mainPanel) {

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.setPreferredSize(new Dimension(800, 40));

        JButton homeButton = Homebutton.createHomeButton(cardLayout, mainPanel);
        topPanel.add(homeButton);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        JLabel addAnimalLabel = new JLabel("Add Animal");

        ownerComboBox = new JComboBox<>();
        animalNameField = new JTextField(15);
        animalTypeComboBox = new JComboBox<>(new String[]{"cat", "dog"});
        animalSizeComboBox = new JComboBox<>(new String[]{"toy", "small", "medium", "large"});
        animalNotesField = new JTextField(15);

        JButton addAnimalButton = new JButton("Add Animal");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(addAnimalLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Owner:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(ownerComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Animal Name:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(animalNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(new JLabel("Animal Type:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(animalTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(new JLabel("Dog Size:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(animalSizeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        contentPanel.add(new JLabel("Animal Notes:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(animalNotesField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(addAnimalButton, gbc);

        addAnimalButton.addActionListener(e -> addAnimal());
        animalTypeComboBox.addActionListener(e -> updateSizeSelection());

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        loadOwners();
        updateSizeSelection();
    }

    private void updateSizeSelection() {
        String animalType = (String) animalTypeComboBox.getSelectedItem();
        boolean dogSelected = "dog".equals(animalType);

        animalSizeComboBox.setEnabled(dogSelected);

        if (!dogSelected) {
            animalSizeComboBox.setSelectedItem("toy");
        }
    }

    private void addAnimal() {
        OwnerOption selectedOwner = (OwnerOption) ownerComboBox.getSelectedItem();
        String animalName = animalNameField.getText().trim();
        String animalType = (String) animalTypeComboBox.getSelectedItem();
        String animalSize = (String) animalSizeComboBox.getSelectedItem();
        String animalNotes = animalNotesField.getText().trim();

        if (selectedOwner == null) {
            JOptionPane.showMessageDialog(this, "Please add an owner before adding an animal.");
            return;
        }

        if (animalName.isBlank()) {
            JOptionPane.showMessageDialog(this, "Animal name is required.");
            return;
        }

        if (animalType == null) {
            JOptionPane.showMessageDialog(this, "Please select an animal type.");
            return;
        }

        if (animalType.equals("cat")) {
            animalSize = "cat";
        } else if (animalSize == null) {
            JOptionPane.showMessageDialog(this, "Please select a dog size.");
            return;
        }

        String sql = """
            INSERT INTO animal (owner_id, animal_name, animal_type, animal_size, animal_notes)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, selectedOwner.getOwnerId());
            stmt.setString(2, animalName);
            stmt.setString(3, animalType);
            stmt.setString(4, animalSize);
            stmt.setString(5, animalNotes);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Animal added.");

            animalNameField.setText("");
            animalTypeComboBox.setSelectedItem("cat");
            animalSizeComboBox.setSelectedItem("toy");
            animalNotesField.setText("");
            updateSizeSelection();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not add animal.");
        }
    }

    private void loadOwners() {
        ownerComboBox.removeAllItems();

        String sql = """
            SELECT owner_id, owner_name, owner_Sname
            FROM owner
            ORDER BY owner_id
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ownerComboBox.addItem(new OwnerOption(
                            rs.getInt("owner_id"),
                            rs.getString("owner_name"),
                            rs.getString("owner_Sname")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load owners.");
        }
    }

    private static class OwnerOption {
        private final int ownerId;
        private final String firstName;
        private final String surname;

        OwnerOption(int ownerId, String firstName, String surname) {
            this.ownerId = ownerId;
            this.firstName = firstName;
            this.surname = surname;
        }

        int getOwnerId() {
            return ownerId;
        }

        @Override
        public String toString() {
            return ownerId + " - " + firstName + " " + surname;
        }
    }
}
