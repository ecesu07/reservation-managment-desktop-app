/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.view;

import javax.swing.*;
import java.awt.*;

public class HintTextField extends JTextField {
    private final String hint;

    public HintTextField(String hint, int columns) {
        super(columns);
        this.hint = hint;
        setOpaque(false); 
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground() != null ? getBackground() : Color.WHITE);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); 

        super.paintComponent(g); 

        if (getText().isEmpty() && !isFocusOwner()) {
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.setColor(Color.GRAY);
            Insets insets = getInsets();
            g2.drawString(hint, insets.left + 2, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 4);
        }

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.LIGHT_GRAY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g2.dispose();
    }
}
