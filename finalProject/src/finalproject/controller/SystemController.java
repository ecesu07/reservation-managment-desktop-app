/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.controller;

import finalproject.factory.UserFactory;
import finalproject.factory.VoyageBuilder;
import finalproject.model.Admin;
import finalproject.model.Passenger;
import finalproject.model.Reservation;
import finalproject.model.Seat;
import finalproject.model.User;
import finalproject.model.Voyage;
import finalproject.model.VoyageType;
import finalproject.util.Database;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * @author ardaf
 */
public class SystemController {
    // Main controller class that manages application logic and coordinates interactions 
            //between the UI, model, and file-based database.
    private final Database database;
    private User sessionUser;  // // currently logged-in user (can be Admin or Passenger)
    public SystemController() {
        this.database = Database.getInstance();
        this.sessionUser = null;
    }
    public User getSessionUser() {
        return sessionUser;
    }
    public void setSessionUser(User user){
        this.sessionUser = user;
    }
    
    // Registers a new user (admin or passenger) after checking email uniqueness.
    public void registerUser(String type,int id,String name,String email,String password,String phoneNumber){
        if (database.isEmailRegistered(email)) {
            throw new IllegalArgumentException("Bu e-posta zaten kayıtlı: " + email);
        }
        User newUser;
        
        if(type.equalsIgnoreCase("passenger")){
            newUser = UserFactory.createUser("passenger", id, name, email, password,phoneNumber);
        }else if(type.equalsIgnoreCase("admin")){
            newUser = UserFactory.createUser("admin", id, name, email, password);
        }else{
            throw new IllegalArgumentException("Geçersiz kullanıcı tipi" + type);
        }
        database.addUser(newUser);
    }
    
    // Logs in a user by matching email and password in users.txt. If successful, sets sessionUser and returns it.
    public User login(String email,String password){
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String type = parts[0];
            int id = Integer.parseInt(parts[1]);
            String name = parts[2];
            String storedEmail = parts[3];
            String storedPassword = parts[4];

            if (storedEmail.equals(email) && storedPassword.equals(password)) {
                // Create the user with Userfactory
                if (type.equalsIgnoreCase("passenger")) {
                    String phone = parts[5];
                    sessionUser = UserFactory.createUser("passenger", id, name, email, password, phone);
                } else if (type.equalsIgnoreCase("admin")) {
                    sessionUser = UserFactory.createUser("admin", id, name, email, password);
                }
                return sessionUser;
            }
        }
        }catch (IOException e) {
            System.err.println("users.txt okunamadı: " + e.getMessage());
        }
        return null; // Login failed
    }
    
    // Logs out the current user by clearing the session
    public void logout(){
        sessionUser = null;
    }
    
    // Admin creates a new voyage using the builder pattern.
    public void addVoyageByAdmin(String voyageNumber, String origin, String destination,
                                VoyageType voyageType, LocalDate date, LocalTime time,
                                int rows, int cols) {
        if (rows > 24) {
            throw new IllegalArgumentException("Row count cannot exceed 24.");
        }
      if(database.isVoyageNumberRegister(voyageNumber)){
          throw new IllegalArgumentException("Bu voyage number zaten kayıtlı: " + voyageNumber);
      }
      VoyageBuilder builder = new VoyageBuilder();
      builder.setVoyageNumber(voyageNumber)
             .setOrigin(origin)
             .setDestination(destination)
             .setVoyageType(voyageType)
             .setDate(date)
             .setTime(time)
             .setSeatLayout(new Seat[rows][cols]);  // initialize empty seat matrix

      Voyage voyage = builder.build();
      database.addVoyage(voyage);
    }
    
    // Admin removes a voyage by number and also deletes related reservations.
    public void removeVoyageByAdmin(String voyageNumber) {
        database.removeVoyage(voyageNumber);   // remove voyage from file
        File inputFile = new File("reservations.txt");
        File tempFile = new File("reservations_temp.txt");
        
        // Remove all reservations related to the deleted voyage 
        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4 || !parts[1].equals(voyageNumber)) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Replace original file with filtered temp file
            if (!inputFile.delete()) {
                System.err.println("reservations.txt silinemedi.");
            }
            if (!tempFile.renameTo(inputFile)) {
                System.err.println("reservations_temp.txt yeniden adlandırılamadı.");
            }

        
    }
    
    // Passenger makes a reservation for a specific seat on a voyage.
    public Reservation makeReservation(Passenger passenger, Voyage voyage, String seatNumber, String reservationId) {
        if (passenger == null || voyage == null || seatNumber == null || reservationId == null) {
            System.err.println("Eksik rezervasyon bilgisi.");
            return null;
        }

        // Find the relevant seat
        Seat seat = voyage.getSeatByNumber(seatNumber);

        if (seat == null) {
            System.err.println("Koltuk bulunamadı: " + seatNumber);
            return null;
        }

        if (!seat.isAvailable()) {
            System.err.println("Koltuk zaten rezerve edilmiş.");
            return null;
        }

        // mark seat as reserved
        seat.reserve(passenger);

        // Create the reservation object
          Reservation reservation = new Reservation(reservationId, voyage, seat, passenger);

        // Writing to the database
        database.addReservation(reservation);

        return reservation;
    }
    
    // Cancels a reservation and frees up the seat.
    public void cancelReservation(Reservation reservation) {
        if (reservation == null) {
            System.err.println("İptal edilecek rezervasyon bulunamadı.");
            return;
        }

        database.removeReservation(reservation.getReservationId());

        reservation.getSeat().cancel(); // In Seat isReserved=false, passenger=null should be done, unmark reservation in memory
    }
    
    // Returns all available voyages.
    public List<Voyage> getAllVoyages() {
        return database.loadVoyages();
    }
    
    // Generates the next reservation ID in format R0001, R0002, ...
    public String generateNextReservationId() {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].startsWith("R")) {
                    try {
                        int num = Integer.parseInt(parts[0].substring(1)); // R0001 → 1
                        if (num > maxId) {
                            maxId = num;
                        }
                    } catch (NumberFormatException ignore) {}
                }
            }
        } catch (IOException e) {
            System.err.println("Rezervasyon ID okuma hatası: " + e.getMessage());
        }

        return String.format("R%04d", maxId + 1); // R0001, R0002, ...
    }
    
    // Returns all reservations made by the given passenger.
    public List<Reservation> getUserReservations(Passenger passenger) {
        return database.loadReservationsForPassenger(passenger.getId());
    }

}
