/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.view;

import finalproject.controller.SystemController;
import finalproject.model.Admin;
import finalproject.model.Passenger;
import finalproject.model.User;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *
 * @author ardaf
 */
public class LoginPanel extends JPanel {  //  LoginPanel is the first screen of the application, allowing users to log in or register.
 // It provides a UI for entering credentials, choosing user type (Passenger/Admin),
 // and dynamically resizes its components with a custom background and images.
    private RoundedTextField emailField, nameField;
    private RoundedPasswordField passwordField;
    private JButton submitButton;
    private JLabel nameLabel, messageLabel;

    private JRadioButton loginRadio, registerRadio;
    private JRadioButton passengerRadio, adminRadio;

    private ButtonGroup modeGroup, userTypeGroup;
    private JPanel userTypePanel;

    private final MainFrame mainFrame;
    private final SystemController controller;
    
    private BufferedImage ucakImage;
    private BufferedImage otobusImage;

    public LoginPanel(MainFrame mainFrame, SystemController controller) {
        this.mainFrame = mainFrame;
        this.controller = controller;

        setLayout(new GridBagLayout());
        setOpaque(false);  // transparent for background gradient

        initComponents();  // creates and lays out components
        
        // Resize components when panel size changes
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
        
        // Load background images
        try {
            ucakImage = ImageIO.read(getClass().getResource("/finalproject/main/assets/ucak.png"));
            otobusImage = ImageIO.read(getClass().getResource("/finalproject/main/assets/otobus.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    //Initializes and lays out all UI components using GridBagLayout.
    private void initComponents() {
    setBackground(new Color(245, 245, 245));
    setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10); 
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;

    // Login/Register mode selection
    loginRadio = new JRadioButton("Giriş", true);
    registerRadio = new JRadioButton("Kayıt Ol");
    modeGroup = new ButtonGroup();
    modeGroup.add(loginRadio);
    modeGroup.add(registerRadio);
    add(loginRadio, gbc);
    gbc.gridx++;
    add(registerRadio, gbc);

    // Email input
    gbc.gridx = 0;
    gbc.gridy++;
    add(new JLabel("Email:"), gbc);
    gbc.gridx = 1;
    emailField = new RoundedTextField(15);
    emailField.setBackground(Color.WHITE);
    add(emailField, gbc);

    // Password input
    gbc.gridx = 0;
    gbc.gridy++;
    add(new JLabel("Şifre:"), gbc);
    gbc.gridx = 1;
    passwordField = new RoundedPasswordField(15);
    passwordField.setBackground(Color.WHITE);
    add(passwordField, gbc);

    // Name field (only visible in registration mode)
    gbc.gridx = 0;
    gbc.gridy++;
    nameLabel = new JLabel("İsim:");
    nameField = new RoundedTextField(15);
    nameField.setBackground(Color.WHITE);
    add(nameLabel, gbc);
    gbc.gridx = 1;
    add(nameField, gbc);

    // User type selection (Passenger/Admin)
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    userTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
    userTypePanel.setOpaque(false); 
    passengerRadio = new JRadioButton("Yolcu", true);
    adminRadio = new JRadioButton("Admin");
    userTypeGroup = new ButtonGroup();
    userTypeGroup.add(passengerRadio);
    userTypeGroup.add(adminRadio);
    userTypePanel.add(passengerRadio);
    userTypePanel.add(adminRadio);
    add(userTypePanel, gbc);

    // Submit button
    gbc.gridy++;
    submitButton = new JButton("Devam Et");
    submitButton.setOpaque(false);
    submitButton.setContentAreaFilled(true);
    submitButton.setBorderPainted(true);
    submitButton.setFocusPainted(false);
    submitButton.setBackground(UIManager.getColor("Button.background"));
    submitButton.setForeground(UIManager.getColor("Button.foreground"));
    submitButton.setBorder(UIManager.getBorder("Button.border")); 
    add(submitButton, gbc);

    // Message label (for errors or status)
    gbc.gridy++;
    messageLabel = new JLabel(" ");
    messageLabel.setForeground(Color.RED);
    add(messageLabel, gbc);

    // Mode switch listener
    ActionListener modeListener = e -> updateMode();
    loginRadio.addActionListener(modeListener);
    registerRadio.addActionListener(modeListener);
    updateMode();

    // Submit button click handler
    submitButton.addActionListener(e -> handleSubmit());
}

    // Shows or hides fields depending on login/register mode.
    private void updateMode() {
        boolean isRegister = registerRadio.isSelected();
        nameLabel.setVisible(isRegister);
        nameField.setVisible(isRegister);
        userTypePanel.setVisible(isRegister);
    }
    
    // Handles login or registration when the user clicks the submit button.
    private void handleSubmit() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (registerRadio.isSelected()) {
            String name = nameField.getText();
            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                messageLabel.setText("Lütfen tüm alanları doldurun.");
                return;
            }
            String type = passengerRadio.isSelected() ? "passenger" : "admin";
            if ("admin".equals(type)) {
                if (!verifyAdminCode()) {              
                    messageLabel.setText("Geçersiz admin kodu!");
                    return;                            //stop register
                }
            }
            int id = (int) (Math.random() * 100000); 
            String phone = "05550000000"; 
            try {
                controller.registerUser(type, id, name, email, password, phone);
                User user = controller.login(email, password);
                if (user instanceof Passenger) {
                    mainFrame.showPassengerPanel();


                } else {
                    mainFrame.showAdminPanel();

                }
            } catch (IllegalArgumentException e) {
                messageLabel.setText(e.getMessage()); 
            }
        } else {
            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Lütfen tüm alanları doldurun.");
                return;
            }
            User user = controller.login(email, password);
            
            if (user == null) {
                messageLabel.setText("Giriş başarısız.");
            } else if (user instanceof Passenger) {
                mainFrame.showPassengerPanel();
            } else if (user instanceof Admin) {
                mainFrame.showAdminPanel();

            }
        }
    }
    // Dynamically resizes fonts and input field sizes based on panel dimensions.
    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();

        int baseSize = Math.max(Math.min(width, height) / 40, 12); 

        Font font = new Font("Arial", Font.PLAIN, baseSize);

        for (Component comp : this.getComponents()) {
            comp.setFont(font);
            if (comp instanceof JButton || comp instanceof JTextField || comp instanceof JPasswordField) {
                comp.setPreferredSize(new Dimension(width / 3, baseSize * 2));
            }
        }

        for (Component comp : userTypePanel.getComponents()) {
            comp.setFont(font);
            comp.setPreferredSize(new Dimension(width / 6, baseSize * 2));  
        }
        
        

        revalidate();
        repaint();
    }
    
    // Custom background paint with vertical gradient and decorative images (plane & bus).
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        Color colorStart = new Color(200, 225, 255); 
        Color colorEnd = Color.WHITE;

        int width = getWidth();
        int height = getHeight();

        GradientPaint gp = new GradientPaint(0, 0, colorStart, 0, height, colorEnd);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
        
        if (ucakImage != null) {
            int iw = width / 4;     
            int ih = height / 4;
            g2d.drawImage(ucakImage, 10, 10, iw, ih, this);
        }

        if (otobusImage != null) {
            int iw = width / 3;     
            int ih = height / 3;
            int x = width - iw - 10;
            int y = height - ih - 10;
            g2d.drawImage(otobusImage, x, y, iw, ih, this);
        }

    }
    /**
 *Admin opens a verification dialog. Returns true if the code is correct, otherwise returns false.
 */
    private boolean verifyAdminCode() {
        JPasswordField pf = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(
            this,                         // parent component
            pf,                           // content (password box)
            "Admin Kodu Girin",           // title
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return false;                 // Cancel or window closed
        }
        String code = new String(pf.getPassword());
        return "3510".equals(code);
    }


}   
 