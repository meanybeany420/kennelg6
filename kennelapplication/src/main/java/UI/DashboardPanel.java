package UI;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private static final Dimension BUTTON_SIZE = new Dimension(180, 35);

    public DashboardPanel(CardLayout cardLayout, JPanel mainPanel) {

        setLayout(new GridBagLayout());

        JLabel title = new JLabel("Kennel Booking System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addAnimalButton = new JButton("Add Animal");
        JButton addOwnerButton = new JButton("Add Owner");
        JButton addBookingButton = new JButton("Add Booking");
        JButton viewOwnersButton = new JButton("View Owners");
        JButton viewBookingsButton = new JButton("View Bookings");
        JButton viewAnimalsButton = new JButton("View Animals");

        JButton[] buttons = {
                addOwnerButton,
                addAnimalButton,
                addBookingButton,
                viewBookingsButton,
                viewAnimalsButton,
                viewOwnersButton
        };

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        optionsPanel.add(title);
        optionsPanel.add(Box.createVerticalStrut(25));

        for (JButton button : buttons) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(BUTTON_SIZE);
            button.setMaximumSize(BUTTON_SIZE);
            optionsPanel.add(button);
            optionsPanel.add(Box.createVerticalStrut(10));
        }

        add(optionsPanel, new GridBagConstraints());

        addBookingButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "addBooking");
        });
        addAnimalButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "addAnimal");
        });
        addOwnerButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "addOwner");
        });
        viewBookingsButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "viewBookings");
        });
        viewAnimalsButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "viewAnimals");
        });
        viewOwnersButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "viewOwners");
        });
    }
}
