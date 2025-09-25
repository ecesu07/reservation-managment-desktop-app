package finalproject.view;

import finalproject.controller.SystemController;
import finalproject.model.Voyage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PassengerPanel extends JPanel {
    private final MainFrame mainFrame;
    private final SystemController controller;

    private DefaultListModel<String> voyageListModel;
    private RoundedList<String> voyageList;
    private JTextField searchField;
    private JButton reserveButton, myReservationsButton, logoutButton;

    public PassengerPanel(MainFrame mainFrame, SystemController controller) {
        this.mainFrame = mainFrame;
        this.controller = controller;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Left Panel: Search field and voyage list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setPreferredSize(new Dimension(180, 0));  
        
        // Search bar at the top
        searchField = new HintTextField("Nereden-Nereye", 30);
        searchField.setPreferredSize(new Dimension(0, 35));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        searchPanel.add(searchField, BorderLayout.CENTER);
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        
        // List of voyages
        voyageListModel = new DefaultListModel<>();
        voyageList = new RoundedList<>(voyageListModel);
        voyageList.setCellRenderer(createRoundedRenderer());

        JScrollPane listScrollPane = new RoundedScrollPane(voyageList);
        listScrollPane.setPreferredSize(new Dimension(160, 0));  
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.CENTER);


        // Right Panel: Action buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        rightPanel.setPreferredSize(new Dimension(240, 0));

        Dimension buttonSize = new Dimension(250, 60); 
        Font buttonFont = new Font("Arial", Font.PLAIN, 15); 


        reserveButton = new JButton("Rezervasyon Yap");
        reserveButton.setFont(buttonFont);
        reserveButton.setMaximumSize(buttonSize);
        reserveButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        myReservationsButton = new JButton("Rezervasyonlarım");
        myReservationsButton.setFont(buttonFont);
        myReservationsButton.setMaximumSize(buttonSize);
        myReservationsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoutButton = new JButton("Çıkış Yap");
        logoutButton.setFont(buttonFont);
        logoutButton.setMaximumSize(buttonSize);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(reserveButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(myReservationsButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(logoutButton);
        rightPanel.add(Box.createVerticalGlue());

        add(rightPanel, BorderLayout.EAST);

        // Load voyage list from controller
        loadVoyages();
        
        // Search filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterVoyages(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterVoyages(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterVoyages(); }
        });

        // Handle "Make Reservation" button
        reserveButton.addActionListener((ActionEvent e) -> {
            int selectedIndex = voyageList.getSelectedIndex();
            if (selectedIndex >= 0) {
                Voyage selected = controller.getAllVoyages().get(selectedIndex);
                mainFrame.showSeatSelectionPanel(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir sefer seçin.");
            }
        });
        
        // Dynamic resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });


         // Handle "My Reservations" button
        myReservationsButton.addActionListener(e -> mainFrame.showMyReservationsPanel());

        // Handle "Logout" button
        logoutButton.addActionListener(e -> {
            controller.setSessionUser(null);
            mainFrame.showLoginPanel();
        });
    }
    // Custom cell renderer to style each item in the voyage list.
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
    
    // Loads all voyages from the controller and displays them in the list.
    private void loadVoyages() {
        voyageListModel.clear();
        List<Voyage> voyages = controller.getAllVoyages();
        for (Voyage v : voyages) {
            String item = v.getOrigin() + " → " + v.getDestination() + " | " +
                          v.getLocalDate() + " | " + v.getVoyageType();
            voyageListModel.addElement(item);
        }
    }
    // Filters voyages in the list based on the search field. 
    private void filterVoyages() {
        String query = searchField.getText().trim().toLowerCase();
        voyageListModel.clear();
        for (Voyage v : controller.getAllVoyages()) {
            String route = v.getOrigin().toLowerCase() + "-" + v.getDestination().toLowerCase();
            if (route.contains(query)) {
                String item = v.getOrigin() + " → " + v.getDestination() + " | " +
                              v.getLocalDate() + " | " + v.getVoyageType();
                voyageListModel.addElement(item);
            }
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
    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();

        int half = width / 2;
        int leftWidth = Math.max(half, 300);
        int rightWidth = width - leftWidth;

        Component leftPanel = getComponent(0);
        Component rightPanel = getComponent(1);

        leftPanel.setPreferredSize(new Dimension(leftWidth, height));
        rightPanel.setPreferredSize(new Dimension(rightWidth, height));

        int buttonFontSize = Math.max(14, width / 60);
        int listFontSize = Math.max(13, width / 70);
        int buttonWidth = Math.max(160, width / 4);
        int buttonHeight = Math.max(40, height / 15);

        Font buttonFont = new Font("Arial", Font.PLAIN, buttonFontSize);
        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);

        if (reserveButton != null) {
            reserveButton.setFont(buttonFont);
            reserveButton.setPreferredSize(buttonSize);
            reserveButton.setMaximumSize(buttonSize);
        }

        if (myReservationsButton != null) {
            myReservationsButton.setFont(buttonFont);
            myReservationsButton.setPreferredSize(buttonSize);
            myReservationsButton.setMaximumSize(buttonSize);
        }

        if (logoutButton != null) {
            logoutButton.setFont(buttonFont);
            logoutButton.setPreferredSize(buttonSize);
            logoutButton.setMaximumSize(buttonSize);
        }

        if (voyageList != null) {
            voyageList.setFont(new Font("Arial", Font.PLAIN, listFontSize));
        }

        revalidate();
        repaint();
    }



}

