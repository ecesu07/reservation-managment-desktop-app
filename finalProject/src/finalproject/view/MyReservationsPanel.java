package finalproject.view;

import finalproject.controller.SystemController;
import finalproject.model.Passenger;
import finalproject.model.Reservation;
import finalproject.model.Seat;
import finalproject.util.PDFGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class MyReservationsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final SystemController controller;

    private RoundedList<String> reservationList;
    private DefaultListModel<String> reservationListModel;
    private List<Reservation> userReservations;
    private RoundedScrollPane scrollPane;
    private JLabel title;
    private JButton cancelButton, backButton;
    JButton downloadPDFButton = new JButton("PDF Olarak İndir");

    public MyReservationsPanel(MainFrame mainFrame, SystemController controller) {
        this.mainFrame = mainFrame;
        this.controller = controller;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Title
        title = new JLabel("Rezervasyonlarım", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Center Panel (List + Buttons)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        add(centerPanel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weighty = 1.0;

        // Left Panel: Reservation List
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);

        reservationListModel = new DefaultListModel<>();
        reservationList = new RoundedList<>(reservationListModel);
        reservationList.setCellRenderer(createRoundedRenderer());
 
        reservationList.setFont(new Font("Arial", Font.PLAIN, 16));
        reservationList.setSelectionBackground(new Color(180, 200, 255)); 
        reservationList.setSelectionForeground(Color.BLACK);

        scrollPane = new RoundedScrollPane(reservationList);
        scrollPane.setMinimumSize(new Dimension(250, 100)); 

        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.setMinimumSize(new Dimension(300, 100)); 


        gbc.gridx = 0;
        gbc.weightx = 0.55; 
        centerPanel.add(leftPanel, gbc);

        // Right Panel: Buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        cancelButton = new JButton("Rezervasyonu İptal Et");
        backButton = new JButton("Geri");

        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(cancelButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(downloadPDFButton); 
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(backButton);
        rightPanel.add(Box.createVerticalGlue());
        
        // PDF download action
        downloadPDFButton.addActionListener(e -> {
            int selected = reservationList.getSelectedIndex();
            if (selected >= 0) {
                Reservation r = userReservations.get(selected);
                String fileName = "rezervasyon_" + r.getReservationId() + ".pdf";
                PDFGenerator.generateReservationPDF(fileName, r);
                JOptionPane.showMessageDialog(this, "PDF oluşturuldu: " + fileName);
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir rezervasyon seçin.");
            }
        });


        gbc.gridx = 1;
        gbc.weightx = 0.65;
        centerPanel.add(rightPanel, gbc);

        // // Load reservations on screen
        loadUserReservations();

        // Cancel reservation action
        cancelButton.addActionListener(e -> {
            int selected = reservationList.getSelectedIndex();
            if (selected >= 0) {
                Reservation r = userReservations.get(selected);
                controller.cancelReservation(r);
                loadUserReservations();
                JOptionPane.showMessageDialog(this, "Rezervasyon iptal edildi.");
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir rezervasyon seçin.");
            }
        });
        
        // Back to passenger panel
        backButton.addActionListener(e -> mainFrame.showPassengerPanel());

        // Adjust component sizes on resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }
    // Custom renderer for rounded list items
    private ListCellRenderer<? super String> createRoundedRenderer() {
        return (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value);
            label.setOpaque(false);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            label.setForeground(Color.DARK_GRAY);

            JPanel wrapper = new RoundedPanel(20); 
            wrapper.setLayout(new BorderLayout());
            wrapper.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            wrapper.add(label, BorderLayout.CENTER);

            if (isSelected) {
                wrapper.setBackground(new Color(200, 210, 255)); 
            } else {
                wrapper.setBackground(Color.WHITE);
            }

            return wrapper;
        };
    }


    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();

        int titleSize = Math.max(18, width / 50);
        int listFontSize = Math.max(14, width / 65);
        int buttonFontSize = Math.max(14, width / 60);
        int buttonWidth = Math.max(180, width / 4); 
        int buttonHeight = Math.max(40, height / 15);

        title.setFont(new Font("Arial", Font.BOLD, titleSize));
        reservationList.setFont(new Font("Arial", Font.PLAIN, listFontSize));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, buttonFontSize));
        backButton.setFont(new Font("Arial", Font.PLAIN, buttonFontSize));
        

        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);
        cancelButton.setMaximumSize(buttonSize);
        backButton.setMaximumSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);
        downloadPDFButton.setFont(new Font("Arial", Font.PLAIN, buttonFontSize));
        downloadPDFButton.setMaximumSize(buttonSize);
        downloadPDFButton.setPreferredSize(buttonSize);
    }
    
    // Load and display user's reservations
    private void loadUserReservations() {
        Passenger user = (Passenger) controller.getSessionUser();
        userReservations = controller.getUserReservations(user);
        reservationListModel.clear();
        for (Reservation r : userReservations) {
            reservationListModel.addElement(
                "Nereden: " + r.getVoyage().getOrigin() +
                " | Nereye: " + r.getVoyage().getDestination() +
                " | Tarih: " + r.getVoyage().getLocalDate() +
                " " + r.getVoyage().getLocalTime() +
                " | Koltuk: " + r.getSeat().getSeatNumber() +
                " | Ulaşım: " + r.getVoyage().getVoyageType()
            );
        }
    }

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
    }
}
