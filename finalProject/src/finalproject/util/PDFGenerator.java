/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import finalproject.model.Reservation;

import java.io.FileOutputStream;

/**
 *
 * @author cenov
 */
public class PDFGenerator {
    
    /// Generates a PDF file containing reservation details.
    public static void generateReservationPDF(String filename, Reservation res) {
        // Create a new PDF document
        Document document = new Document() {};

        try {
            // Initialize PDF writer to output file
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            
            // Define fonts for title and content
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font textFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            document.add(new Paragraph("Rezervasyon Bilgileri", titleFont));
            document.add(new Paragraph(" ")); 
            
            // Add reservation details
            document.add(new Paragraph("Rezervasyon ID: " + res.getReservationId(), textFont));
            document.add(new Paragraph("Koltuk No: " + res.getSeat().getSeatNumber(), textFont));
            document.add(new Paragraph("Sefer: " + res.getVoyage().getOrigin() + " → " + res.getVoyage().getDestination(), textFont));
            document.add(new Paragraph("Tarih: " + res.getVoyage().getLocalDate(), textFont));
            document.add(new Paragraph("Saat: " + res.getVoyage().getLocalTime(), textFont));
            document.add(new Paragraph("Ulaşım Tipi: " + res.getVoyage().getVoyageType(), textFont));
            document.add(new Paragraph("Yolcu: " + res.getPassenger().getName(), textFont));

            document.add(new Paragraph("\nİyi yolculuklar dileriz!", textFont));
        } catch (Exception e) {
            // Print any exceptions to console
            e.printStackTrace();
        } finally {
            // Ensure document is properly closed
            document.close();
        }
    }
}
