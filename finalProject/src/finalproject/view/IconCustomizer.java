/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.view;

import javax.swing.*;
import java.awt.*;

public class IconCustomizer {
    public static void applyCustomRadioIcons() {
        UIManager.put("RadioButton.icon", new Icon() {
            private final int size = 16;

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.GRAY);
                g2.drawRect(x, y, size - 1, size - 1);

                AbstractButton button = (AbstractButton) c;
                if (button.isSelected()) {
                    g2.setFont(new Font("Dialog", Font.BOLD, 12));
                    g2.setColor(new Color(30, 144, 255));
                    g2.drawString("✓", x + 2, y + size - 3);
                }

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        });
    }
}

