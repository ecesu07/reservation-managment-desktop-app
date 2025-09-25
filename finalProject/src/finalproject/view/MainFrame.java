/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.view;

import finalproject.controller.SystemController;
import finalproject.model.Voyage;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author ardaf
 */
public class MainFrame extends JFrame{
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private LoginPanel loginPanel;
    private PassengerPanel passengerPanel;
    private AdminPanel adminPanel;

    private final SystemController controller;

    public MainFrame() {
        IconCustomizer.applyCustomRadioIcons();   // set custom radio-button icons
        controller = new SystemController();      // central application controller
        
        // CardLayout container for the three main panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Login panel
        loginPanel = new LoginPanel(this, controller);
        mainPanel.add(loginPanel, "login");
        
        // Passenger panel
        passengerPanel = new PassengerPanel(this,controller);
        mainPanel.add(passengerPanel, "passenger");
        
        // Admin panel
        adminPanel = new AdminPanel(this,controller);
        mainPanel.add(adminPanel, "admin");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");   // show login screen first
        
        // Window setup
        setTitle("Online Rezervasyon Sistemi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // Show seat-selection screen for a chosen voyage
    public void showSeatSelectionPanel(Voyage voyage) {
    setContentPane(new SeatSelectionPanel(this, controller, voyage));
    revalidate();
    repaint();
    }
    
    // Show admin dashboard
    public void showAdminPanel() {
        setContentPane(new AdminPanel(this, controller));
        revalidate();
        repaint();
    }
    
    // Show “My Reservations” screen for current passenger
    public void showMyReservationsPanel() {
        setContentPane(new MyReservationsPanel(this, controller));
        revalidate();
        repaint();
    }
    
    // Return to login screen 
    public void showLoginPanel() {
        setContentPane(new LoginPanel(this, controller));
        revalidate();
        repaint();
    }
    
    // Show passenger dashboard
    public void showPassengerPanel() {
        setContentPane(new PassengerPanel(this, controller));
        revalidate();
        repaint();
    }
}
