package finalproject.view;

import finalproject.controller.SystemController;
import finalproject.model.Passenger;
import finalproject.model.Reservation;
import finalproject.model.Seat;
import finalproject.model.Voyage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SeatSelectionPanel extends JPanel {
    private final MainFrame mainFrame;
    private final SystemController controller;
    private final Voyage voyage;

    private JButton[][] seatButtons;
    private JButton selectedButton;
    private JLabel title;
    private JButton confirmButton;
    private JButton backButton;


    public SeatSelectionPanel(MainFrame mainFrame, SystemController controller, Voyage voyage) {
        this.mainFrame = mainFrame;
        this.controller = controller;
        this.voyage = voyage;

        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Top title label
        title = new JLabel("Koltuk Seçimi - " + voyage.getOrigin() + " → " + voyage.getDestination());
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        int rows = voyage.getSeats().length;
        int cols = voyage.getSeats()[0].length;
        
        // Panel for seat layout
        JPanel seatPanel = new JPanel();
        seatPanel.setOpaque(false);
        seatPanel.setLayout(new BoxLayout(seatPanel, BoxLayout.Y_AXIS));
        seatPanel.setOpaque(false);
        seatButtons = new JButton[rows][cols];

        int[] blocks = voyage.getSeatLayout().getBlocks(); 

        for (int i = 0; i < rows; i++) {
           
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
            rowPanel.setOpaque(false);

            int col = 0;
            for (int b = 0; b < blocks.length; b++) {
                int blockSize = blocks[b];

                JPanel blockPanel = new JPanel(new GridLayout(1, blockSize, 5, 5));
                blockPanel.setOpaque(false);

                for (int k = 0; k < blockSize; k++) {
                    Seat seat = voyage.getSeats()[i][col];
                    String seatNumber = seat.getSeatNumber();
                    // Create a SeatButton with images for different states
                    SeatButton seatButton = new SeatButton(
                        seat,
                        new ImageIcon(getClass().getResource("/finalproject/main/assets/seat-available.png")),
                        new ImageIcon(getClass().getResource("/finalproject/main/assets/seat-selected.png")),
                        new ImageIcon(getClass().getResource("/finalproject/main/assets/seat-occupied.png"))
                    );
                    
                    // Scale the icons according to panel width
                    seatButton.scaleIcons(Math.max(30, Math.min(getWidth() / 14, 70)));
                    seatButton.setToolTipText(seatNumber);
                    seatButton.putClientProperty("seatNo", seatNumber);  
                    
                    // Disable button if seat is not available
                    if (!seat.isAvailable()) {
                        seatButton.setEnabled(false);
                    } else {
                        seatButton.addActionListener(e -> {
                            // Remove the selection if it is clicked on the same seat again
                            if (selectedButton == seatButton) {
                                ((SeatButton) seatButton).setSelectedState(false);
                                selectedButton = null;
                                return;
                            }

                            // Turn off the previous choice
                            if (selectedButton != null) {
                                ((SeatButton) selectedButton).setSelectedState(false);
                            }

                            // Activate the new selection
                            selectedButton = seatButton;
                            ((SeatButton) seatButton).setSelectedState(true);
                        });
                    }

                    blockPanel.add(seatButton);
                    seatButtons[i][col] = seatButton;
                    col++;
                }

                rowPanel.add(blockPanel);

                if (b < blocks.length - 1) {
                    rowPanel.add(Box.createHorizontalStrut(20));  // aisle spacing
                     
                }
            }

            seatPanel.add(rowPanel);
            seatPanel.add(Box.createVerticalStrut(10)); 
        }
        // Scroll pane to make seat panel scrollable
        JScrollPane seatScroll = new JScrollPane(
                seatPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        seatScroll.setBorder(null);                  
        seatScroll.getViewport().setOpaque(false);   
        add(seatScroll, BorderLayout.CENTER);
        
        // Bottom buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        confirmButton = new JButton("Onayla");
        backButton = new JButton("Geri");
        
        // Reservation logic
        confirmButton.addActionListener(e -> {
            if (selectedButton != null) {
                String selectedSeat = (String) selectedButton.getClientProperty("seatNo");
                Passenger currentUser = (Passenger) controller.getSessionUser();
                String reservationId = controller.generateNextReservationId();

                Reservation res = controller.makeReservation(currentUser, voyage, selectedSeat, reservationId);

                if (res != null) {
                    SeatButton sb = (SeatButton) selectedButton;
                    sb.setSelectedState(false);         
                    sb.setEnabled(false);                

                    sb.scaleIcons(Math.max(30, Math.min(getWidth() / 14, 70))); 

                    JOptionPane.showMessageDialog(this,
                        "Rezervasyon yapıldı: " + selectedSeat);
                    mainFrame.showPassengerPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "Rezervasyon başarısız.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir koltuk seçin.");
            }
        });
        
        // Back button returns to passenger panel
        backButton.addActionListener(e -> mainFrame.showPassengerPanel());

        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
        
        // Responsive resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });

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

        if (width > 900) {
            title.setFont(new Font("Arial", Font.BOLD, 26));
            confirmButton.setFont(new Font("Arial", Font.PLAIN, 18));
            backButton.setFont(new Font("Arial", Font.PLAIN, 18));
            confirmButton.setPreferredSize(new Dimension(160, 50));
            backButton.setPreferredSize(new Dimension(160, 50));
        } else {
            title.setFont(new Font("Arial", Font.BOLD, 18));
            confirmButton.setFont(new Font("Arial", Font.PLAIN, 14));
            backButton.setFont(new Font("Arial", Font.PLAIN, 14));
            confirmButton.setPreferredSize(new Dimension(120, 40));
            backButton.setPreferredSize(new Dimension(120, 40));
        }

        int seatSize = Math.max(30, Math.min(width / 14, 70)); 
        for (JButton[] row : seatButtons) {
            for (JButton btn : row) {
                if(btn instanceof SeatButton sb){
                    sb.scaleIcons(seatSize);
                }else {
                    btn.setPreferredSize(new Dimension(seatSize, seatSize));
                }
            }
        }

        revalidate();
        repaint();
    }
    

}