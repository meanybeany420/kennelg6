package UI;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new DashboardPanel(cardLayout, mainPanel), "dashboard");
        mainPanel.add(new AddBookingPanel(cardLayout, mainPanel), "addBooking");
        mainPanel.add(new AddAnimalPanel(cardLayout, mainPanel), "addAnimal");
        mainPanel.add(new AddOwnerPanel(cardLayout, mainPanel), "addOwner");
        mainPanel.add(new ViewOwnersPanel(cardLayout, mainPanel), "viewOwners");
        mainPanel.add(new ViewAnimalsPanel(cardLayout, mainPanel), "viewAnimals");
        mainPanel.add(new ViewBookingsPanel(cardLayout, mainPanel), "viewBookings");

        add(mainPanel);

        cardLayout.show(mainPanel, "dashboard");

        setTitle("Kennel Booking System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
