package UI;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import database.Databaseinit;

public class AddBookingPanel extends JPanel {

    private static final DateTimeFormatter END_DATE_PREFIX_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-");

    private JComboBox<AnimalOption> animalComboBox;
    private JComboBox<KennelOption> kennelComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private String autoFilledEndDatePrefix = "";

    public AddBookingPanel(CardLayout cardLayout, JPanel mainPanel) {

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

        JLabel addBookingLabel = new JLabel("Add Booking");

        animalComboBox = new JComboBox<>();
        kennelComboBox = new JComboBox<>();
        startDateField = new JTextField(15);
        endDateField = new JTextField(15);

        JButton submitButton = new JButton("Create Booking");
        addBookingFieldListeners();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(addBookingLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Animal:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(animalComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        contentPanel.add(startDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        contentPanel.add(endDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(new JLabel("Kennel:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(kennelComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(submitButton, gbc);

        submitButton.addActionListener(e -> createBooking());

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        loadAnimals();
        updateAvailableKennels();
    }

    private void addBookingFieldListeners() {
        animalComboBox.addActionListener(e -> updateAvailableKennels());

        startDateField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateEndDatePrefix();
                updateAvailableKennels();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateEndDatePrefix();
                updateAvailableKennels();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateEndDatePrefix();
                updateAvailableKennels();
            }
        });

        endDateField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAvailableKennels();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAvailableKennels();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateAvailableKennels();
            }
        });
    }

    private void updateEndDatePrefix() {
        String currentEndDate = endDateField.getText();
        boolean endDateStillAutoFilled = !autoFilledEndDatePrefix.isEmpty()
                && currentEndDate.equals(autoFilledEndDatePrefix);

        LocalDate startDate;
        try {
            startDate = LocalDate.parse(startDateField.getText().trim());
        } catch (Exception e) {
            if (endDateStillAutoFilled) {
                endDateField.setText("");
            }
            autoFilledEndDatePrefix = "";
            return;
        }

        LocalDate latestAllowedEndDate = startDate.plusDays(10);
        String newPrefix = "";

        if (startDate.getYear() == latestAllowedEndDate.getYear()
                && startDate.getMonth() == latestAllowedEndDate.getMonth()) {
            newPrefix = startDate.format(END_DATE_PREFIX_FORMATTER);
        }

        if (currentEndDate.isBlank() || endDateStillAutoFilled) {
            endDateField.setText(newPrefix);
            autoFilledEndDatePrefix = newPrefix;
        }
    }

    private void createBooking() {
        AnimalOption selectedAnimal = (AnimalOption) animalComboBox.getSelectedItem();
        KennelOption selectedKennel = (KennelOption) kennelComboBox.getSelectedItem();
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();

        if (selectedAnimal == null) {
            JOptionPane.showMessageDialog(this, "Please add an animal before creating a booking.");
            return;
        }

        if (selectedKennel == null) {
            JOptionPane.showMessageDialog(this, "No kennels are available.");
            return;
        }

        String animalId = String.valueOf(selectedAnimal.getAnimalId());
        String kennelId = String.valueOf(selectedKennel.getKennelId());

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            long days = ChronoUnit.DAYS.between(start, end);

            if (days < 1 || days > 10) {
                JOptionPane.showMessageDialog(this, "Booking must be between 1 and 10 days.");
                return;
            }

            if (animalHasOverlappingBooking(animalId, startDate, endDate)) {
                JOptionPane.showMessageDialog(this, "This animal already has a booking during those dates.");
                return;
            }

            if (!kennelHasEnoughSpace(animalId, kennelId, startDate, endDate)) {
                JOptionPane.showMessageDialog(this, "That kennel does not have enough space for this animal.");
                return;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        String sql = """
            INSERT INTO booking (start_date, end_date, animal_id, kennel_id)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            stmt.setString(3, animalId);
            stmt.setString(4, kennelId);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Booking created.");

            startDateField.setText("");
            endDateField.setText("");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not create booking. Check the animal ID and kennel ID.");
        }
    }

    private void updateAvailableKennels() {
        kennelComboBox.removeAllItems();

        AnimalOption selectedAnimal = (AnimalOption) animalComboBox.getSelectedItem();
        LocalDate start;
        LocalDate end;

        try {
            start = LocalDate.parse(startDateField.getText().trim());
            end = LocalDate.parse(endDateField.getText().trim());
        } catch (Exception e) {
            loadKennelsForSelectedAnimal();
            return;
        }

        long days = ChronoUnit.DAYS.between(start, end);

        if (selectedAnimal == null) {
            return;
        }

        if (days < 1 || days > 10) {
            loadKennelsForSelectedAnimal();
            return;
        }

        String sql = """
            SELECT
                kennel.kennel_id,
                kennel.kennel_size,
                kennel_capacity.total_capacity,
                COALESCE(SUM(existing_animal_size.space_needed), 0) AS used_space,
                new_animal_size.space_needed AS new_animal_space
            FROM kennel
            JOIN kennel_capacity
                ON kennel.kennel_size = kennel_capacity.kennel_size
            JOIN animal new_animal
                ON new_animal.animal_id = ?
            JOIN animal_size new_animal_size
                ON new_animal.animal_size = new_animal_size.animal_size
                AND kennel.kennel_size = new_animal_size.kennel_size
            LEFT JOIN booking existing_booking
                ON existing_booking.kennel_id = kennel.kennel_id
                AND existing_booking.start_date < ?
                AND existing_booking.end_date > ?
            LEFT JOIN animal existing_animal
                ON existing_booking.animal_id = existing_animal.animal_id
            LEFT JOIN animal_size existing_animal_size
                ON existing_animal.animal_size = existing_animal_size.animal_size
            GROUP BY
                kennel.kennel_id,
                kennel.kennel_size,
                kennel_capacity.total_capacity,
                new_animal_size.space_needed
            HAVING COALESCE(SUM(existing_animal_size.space_needed), 0)
                + new_animal_size.space_needed <= kennel_capacity.total_capacity
            ORDER BY kennel.kennel_id
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, selectedAnimal.getAnimalId());
            stmt.setString(2, endDateField.getText().trim());
            stmt.setString(3, startDateField.getText().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    kennelComboBox.addItem(new KennelOption(
                            rs.getInt("kennel_id"),
                            rs.getString("kennel_size")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load available kennels.");
        }
    }

    private void loadAllKennels() {
        String sql = """
            SELECT kennel_id, kennel_size
            FROM kennel
            ORDER BY kennel_id
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    kennelComboBox.addItem(new KennelOption(
                            rs.getInt("kennel_id"),
                            rs.getString("kennel_size")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load kennels.");
        }
    }

    private void loadKennelsForSelectedAnimal() {
        kennelComboBox.removeAllItems();

        AnimalOption selectedAnimal = (AnimalOption) animalComboBox.getSelectedItem();

        if (selectedAnimal == null) {
            loadAllKennels();
            return;
        }

        String sql = """
            SELECT kennel.kennel_id, kennel.kennel_size
            FROM kennel
            JOIN animal_size
                ON kennel.kennel_size = animal_size.kennel_size
            WHERE animal_size.animal_size = ?
            ORDER BY kennel.kennel_id
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, selectedAnimal.getAnimalSize());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    kennelComboBox.addItem(new KennelOption(
                            rs.getInt("kennel_id"),
                            rs.getString("kennel_size")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load kennels for this animal.");
        }
    }

    private void loadAnimals() {
        animalComboBox.removeAllItems();

        String sql = """
            SELECT
                a.animal_id,
                a.animal_name,
                a.animal_type,
                a.animal_size,
                o.owner_name,
                o.owner_Sname
            FROM animal a
            LEFT JOIN owner o ON a.owner_id = o.owner_id
            ORDER BY a.animal_id
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    animalComboBox.addItem(new AnimalOption(
                            rs.getInt("animal_id"),
                            rs.getString("animal_name"),
                            rs.getString("animal_type"),
                            rs.getString("animal_size"),
                            rs.getString("owner_name"),
                            rs.getString("owner_Sname")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load animals.");
        }
    }

    private boolean kennelHasEnoughSpace(String animalId, String kennelId, String startDate, String endDate) {
        String sql = """
            SELECT
                new_animal_size.space_needed AS new_animal_space,
                kennel_capacity.total_capacity AS kennel_capacity,
                COALESCE(SUM(existing_animal_size.space_needed), 0) AS used_space
            FROM animal new_animal
            JOIN animal_size new_animal_size
                ON new_animal.animal_size = new_animal_size.animal_size
            JOIN kennel
                ON kennel.kennel_id = ?
                AND kennel.kennel_size = new_animal_size.kennel_size
            JOIN kennel_capacity
                ON kennel.kennel_size = kennel_capacity.kennel_size
            LEFT JOIN booking existing_booking
                ON existing_booking.kennel_id = kennel.kennel_id
                AND existing_booking.start_date < ?
                AND existing_booking.end_date > ?
            LEFT JOIN animal existing_animal
                ON existing_booking.animal_id = existing_animal.animal_id
            LEFT JOIN animal_size existing_animal_size
                ON existing_animal.animal_size = existing_animal_size.animal_size
            WHERE new_animal.animal_id = ?
            GROUP BY new_animal_size.space_needed, kennel_capacity.total_capacity
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, kennelId);
            stmt.setString(2, endDate);
            stmt.setString(3, startDate);
            stmt.setString(4, animalId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int newAnimalSpace = rs.getInt("new_animal_space");
                    int kennelCapacity = rs.getInt("kennel_capacity");
                    int usedSpace = rs.getInt("used_space");

                    return usedSpace + newAnimalSpace <= kennelCapacity;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean animalHasOverlappingBooking(String animalId, String startDate, String endDate) {
        String sql = """
            SELECT COUNT(*) AS overlap_count
            FROM booking
            WHERE animal_id = ?
                AND start_date < ?
                AND end_date > ?
            """;

        try (Connection conn = Databaseinit.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, animalId);
            stmt.setString(2, endDate);
            stmt.setString(3, startDate);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("overlap_count") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static class AnimalOption {
        private final int animalId;
        private final String animalName;
        private final String animalType;
        private final String animalSize;
        private final String ownerName;
        private final String ownerSurname;

        AnimalOption(int animalId, String animalName, String animalType, String animalSize, String ownerName, String ownerSurname) {
            this.animalId = animalId;
            this.animalName = animalName;
            this.animalType = animalType;
            this.animalSize = animalSize;
            this.ownerName = ownerName;
            this.ownerSurname = ownerSurname;
        }

        int getAnimalId() {
            return animalId;
        }

        String getAnimalSize() {
            return animalSize;
        }

        @Override
        public String toString() {
            if ("cat".equalsIgnoreCase(animalType)) {
                return animalName + " (" + animalType + "), " + ownerName + " " + ownerSurname;
            }

            return animalName + " (" + animalType + ", " + animalSize + "), " + ownerName + " " + ownerSurname;
        }
    }

    private static class KennelOption {
        private final int kennelId;
        private final String kennelSize;

        KennelOption(int kennelId, String kennelSize) {
            this.kennelId = kennelId;
            this.kennelSize = kennelSize;
        }

        int getKennelId() {
            return kennelId;
        }

        @Override
        public String toString() {
            return "Kennel " + kennelId + " (" + kennelSize + ")";
        }
    }
}
