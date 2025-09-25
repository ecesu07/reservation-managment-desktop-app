package finalproject.view;

import finalproject.model.Seat;

import javax.swing.*;
import java.awt.*;

public class SeatButton extends JButton {
    private int currentSize = 48; 
    private final ImageIcon availableIcon;
    private final ImageIcon selectedIcon;
    private final ImageIcon occupiedIcon;
    private boolean selected = false;

    public SeatButton(Seat seat, ImageIcon availableIcon, ImageIcon selectedIcon, ImageIcon occupiedIcon) {
        this.availableIcon = availableIcon;
        this.selectedIcon = selectedIcon;
        this.occupiedIcon = occupiedIcon;

        setPreferredSize(new Dimension(48, 48));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);

        if (seat.isReserved()) {
            setIcon(occupiedIcon);
            setDisabledIcon(occupiedIcon); 
            setEnabled(false);
        } else {
            setIcon(availableIcon);
        }
        scaleIcons(48);
    }

    public void setSelectedState(boolean selected) {
        this.selected = selected;
         scaleIcons(currentSize);   
        
    }

    public boolean isSelectedState() {
        return selected;
    }
    public void scaleIcons(int size) {
         currentSize = size;     
        ImageIcon baseAvail = new ImageIcon(
                availableIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        ImageIcon baseSel   = new ImageIcon(
                selectedIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        ImageIcon baseOcc   = new ImageIcon(
                occupiedIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));

        setPreferredSize(new Dimension(size, size));
        setDisabledIcon(baseOcc);

        if (!isEnabled()) {
            setIcon(baseOcc);
        } else if (selected) {
            setIcon(baseSel);
        } else {
            setIcon(baseAvail);
        }
    }
}
