package UI;

import javax.swing.*;
import java.awt.*;

public class Homebutton {
    public static JButton createHomeButton(CardLayout cardLayout, JPanel mainPanel) {

        ImageIcon icon = new ImageIcon("kennelapplication/src/homeIcon.png");

        Image scaledImage = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);

        JButton homeButton = new JButton(new ImageIcon(scaledImage));

        homeButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "dashboard");
        });

        return homeButton;
    }
}
