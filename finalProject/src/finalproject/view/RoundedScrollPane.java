/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

/**
 *
 * @author cenov
 */
public class RoundedScrollPane extends JScrollPane{
    private int arc = 20;

    public RoundedScrollPane(Component view) {
        super(view);
        setOpaque(false);
        getViewport().setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        getVerticalScrollBar().setOpaque(false);
        getHorizontalScrollBar().setOpaque(false);
        setViewportBorder(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(getBackground());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2d.dispose();
    }
}
