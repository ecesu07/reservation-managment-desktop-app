package finalproject.view;

import finalproject.controller.SystemController;
import finalproject.model.SeatLayoutType;
import finalproject.model.Voyage;
import finalproject.model.VoyageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;

public class AdminPanel extends JPanel {
    private final MainFrame mainFrame;
    private final SystemController controller;

    private DefaultListModel<String> voyageListModel;
    private JList<String> voyageList;

    private RoundedTextField voyageNumberField, originField, destinationField;
    private JRadioButton busRadio, planeRadio;
    private RoundedTextField dateField, timeField, rowField, colField;
    private JComboBox<SeatLayoutType> layoutCombo;

    private JButton addVoyageButton, deleteVoyageButton, logoutButton;

    public AdminPanel(MainFrame mainFrame, SystemController controller) {
        this.mainFrame = mainFrame;
        this.controller = controller;

        setOpaque(false); 
        setLayout(new BorderLayout());
        
        // Re-layout fonts & sizes when window is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });

        // Left panel: Voyage list
        voyageListModel = new DefaultListModel<>();
        voyageList = new RoundedList<>(voyageListModel);
        RoundedScrollPane scrollPane = new RoundedScrollPane(voyageList);
        scrollPane.setMinimumSize(new Dimension(250, 200)); 
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20)); 
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // Right panel: Create / delete voyage form
        JPanel rightPanel = new JPanel(new GridLayout(10, 2, 5, 5));
        rightPanel.setOpaque(false);

        voyageNumberField = new RoundedTextField(15);
        originField = new RoundedTextField(15);
        destinationField = new RoundedTextField(15);
        dateField = new RoundedTextField(15);
        timeField = new RoundedTextField(15);
        rowField = new RoundedTextField(15);
        colField = new RoundedTextField(15);
        colField.setEditable(false);         
        colField.setBackground(new Color(230,230,230));
        
        // Add labels & fields to the grid
        rightPanel.add(new JLabel("Sefer Numarası:")); rightPanel.add(voyageNumberField);
        rightPanel.add(new JLabel("Nereden:")); rightPanel.add(originField);
        rightPanel.add(new JLabel("Nereye:")); rightPanel.add(destinationField);
        rightPanel.add(new JLabel("Tarih (YYYY-MM-DD):")); rightPanel.add(dateField);
        rightPanel.add(new JLabel("Saat (HH:MM):")); rightPanel.add(timeField);
        rightPanel.add(new JLabel("Satır Sayısı:")); rightPanel.add(rowField);
        layoutCombo = new JComboBox<>();
        rightPanel.add(new JLabel("Düzen:"));       rightPanel.add(layoutCombo);
        rightPanel.add(new JLabel("Sütun Sayısı:")); rightPanel.add(colField); 
        
        // Transport type radio buttons
        rightPanel.add(new JLabel("Ulaşım Türü:"));
        JPanel typePanel = new JPanel(new GridLayout(0, 1));
        typePanel.setOpaque(false);
        busRadio = new JRadioButton("Otobüs");
        planeRadio = new JRadioButton("Uçak", true);
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(busRadio);
        typeGroup.add(planeRadio);
        typePanel.add(busRadio);
        typePanel.add(planeRadio);
        rightPanel.add(typePanel);
        busRadio.addActionListener(e -> refreshLayoutOptions());
        planeRadio.addActionListener(e -> refreshLayoutOptions());
        layoutCombo.addActionListener(e -> updateColField());
        
        // Form buttons
        addVoyageButton = new JButton("Sefer Ekle");
        deleteVoyageButton = new JButton("Seferi Sil");
        rightPanel.add(addVoyageButton);
        rightPanel.add(deleteVoyageButton);
        
        // Wrap form with padding
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        rightWrapper.add(rightPanel, BorderLayout.CENTER);
        add(rightWrapper, BorderLayout.CENTER);

        // Bottom: Logout
        logoutButton = new JButton("Çıkış Yap");
        logoutButton.addActionListener(e -> {
            controller.setSessionUser(null);
            mainFrame.showLoginPanel();
        });
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottomPanel.add(logoutButton, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        //Populate initial data & bind handlers
        loadVoyagesToList();
        
        // Add voyage handler
        addVoyageButton.addActionListener(e -> {
            try {
                String voyageNumber = voyageNumberField.getText();
                String origin = originField.getText();
                String destination = destinationField.getText();
                VoyageType type = planeRadio.isSelected() ? VoyageType.PLANE : VoyageType.BUS;
                LocalDate date = LocalDate.parse(dateField.getText());
                LocalTime time = LocalTime.parse(timeField.getText());
                int rows = Integer.parseInt(rowField.getText());
                 if (rows > 24) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Satır sayısı en fazla 24 olabilir.",
                        "Uyarı",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                 }
                SeatLayoutType layout = (SeatLayoutType) layoutCombo.getSelectedItem();
                int cols = layout.getColumns();

                controller.addVoyageByAdmin(voyageNumber, origin, destination, type, date, time, rows, cols);
                loadVoyagesToList();
                JOptionPane.showMessageDialog(this, "Sefer başarıyla eklendi.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });
        // Delete voyage handler
        deleteVoyageButton.addActionListener(e -> {
            int index = voyageList.getSelectedIndex();
            if (index != -1) {
                String selected = voyageListModel.get(index);
                String voyageNumber = extractVoyageNumber(selected);
                controller.removeVoyageByAdmin(voyageNumber);
                loadVoyagesToList();
            }
        });
    }
    // Responsive font & size handling
    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();
        int baseSize = Math.max(Math.min(width, height) / 40, 12);
        Font font = new Font("Segoe UI", Font.PLAIN, baseSize);

        for (Component comp : getComponents()) {
            applyFontRecursively(comp, font, width);
        }

        revalidate();
        repaint();
    }

    private void applyFontRecursively(Component comp, Font font, int width) {
        comp.setFont(font);

        if (comp instanceof JTextField || comp instanceof JButton || comp instanceof JRadioButton) {
            comp.setPreferredSize(new Dimension(width / 4, font.getSize() * 2));
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                applyFontRecursively(child, font, width);
            }
        }
    }

    private String extractVoyageNumber(String line) {
        // Simple prompt; in a real app we might parse the line automatically
        return JOptionPane.showInputDialog("Silinecek sefer numarasını girin:");
    }

    private void loadVoyagesToList() {
        voyageListModel.clear();
        for (Voyage voyage : controller.getAllVoyages()) {
            String item = voyage.getVoyageNumber() + " | " +
                          voyage.getOrigin() + " → " + voyage.getDestination() + " | " +
                          voyage.getLocalDate() + " | " +
                          voyage.getVoyageType();
            voyageListModel.addElement(item);
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
    private void refreshLayoutOptions() {
    layoutCombo.removeAllItems();
    if (planeRadio.isSelected()) {
        layoutCombo.addItem(SeatLayoutType.PLANE_2_4_2);
        layoutCombo.addItem(SeatLayoutType.PLANE_3_3);
    } else {
        layoutCombo.addItem(SeatLayoutType.BUS_2_1);
        layoutCombo.addItem(SeatLayoutType.BUS_2_2);
    }
    layoutCombo.setSelectedIndex(0);
    updateColField();
    }
    private void updateColField() {
        SeatLayoutType layout = (SeatLayoutType) layoutCombo.getSelectedItem();
        if (layout != null) colField.setText(String.valueOf(layout.getColumns()));
    }
    

}
