package UI;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

import database.Databaseinit;

public class AddOwnerPanel extends JPanel {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\-\\s]+$");

    private JTextField ownerNameField;
    private JTextField ownerSurnameField;
    private JTextField ownerEmailField;
    private JTextField ownerPhoneField;
    private JTextField ownerAddressField;

    public AddOwnerPanel(CardLayout cardLayout, JPanel mainPanel) {

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

        JLabel addOwnerLabel = new JLabel("Add Owner");

        ownerNameField = new JTextField(15);
        ownerSurnameField = new JTextField(15);
        ownerEmailField = new JTextField(15);
        ownerPhoneField = new JTextField(15);
        ownerAddressField = new JTextField(15);

        JButton addOwnerButton = new JButton("Add Owner");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(addOwnerLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Owner First Name:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(ownerNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Owner Surname:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(ownerSurnameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(new JLabel("Owner Email:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(ownerEmailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(new JLabel("Owner Phone Number:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(ownerPhoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        contentPanel.add(new JLabel("Owner Address:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(ownerAddressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(addOwnerButton, gbc);

        addOwnerButton.addActionListener(e -> addOwner());

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addOwner() {
        String ownerName = ownerNameField.getText().trim();
        String ownerSurname = ownerSurnameField.getText().trim();
        String ownerEmail = ownerEmailField.getText().trim();
        String ownerPhone = ownerPhoneField.getText().trim();
        String ownerAddress = ownerAddressField.getText().trim();

        if (ownerName.isBlank()
                || ownerSurname.isBlank()
                || ownerEmail.isBlank()
                || ownerPhone.isBlank()
                || ownerAddress.isBlank()) {

            JOptionPane.showMessageDialog(this, "Please fill in all owner details.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(ownerEmail).matches()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return;
        }

        String phoneDigits = ownerPhone.replaceAll("\\D", "");

        if (!PHONE_PATTERN.matcher(ownerPhone).matches() || phoneDigits.length() < 7) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number.");
            return;
        }

        String sql = """
            INSERT INTO owner (owner_name, owner_Sname, owner_email, owner_phone_number, owner_address)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ownerName);
            stmt.setString(2, ownerSurname);
            stmt.setString(3, ownerEmail);
            stmt.setString(4, ownerPhone);
            stmt.setString(5, ownerAddress);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Owner added.");

            ownerNameField.setText("");
            ownerSurnameField.setText("");
            ownerEmailField.setText("");
            ownerPhoneField.setText("");
            ownerAddressField.setText("");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not add owner.");
        }
    }
}
