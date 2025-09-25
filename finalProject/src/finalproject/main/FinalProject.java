/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package finalproject.main;

import finalproject.view.MainFrame;
import javax.swing.UIManager;

/**
 *
 * @author ardaf
 */
public class FinalProject {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Attempt to set the UI look and feel to Nimbus (if available)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // If Nimbus is not supported, the default theme will be used
        }
        
        // Launch the main application window (MainFrame) on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
    
}
