/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.util;

import finalproject.model.Admin;
import finalproject.model.Passenger;
import finalproject.model.Reservation;
import finalproject.model.Seat;
import finalproject.model.User;
import finalproject.model.Voyage;
import finalproject.model.VoyageType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ardaf
 */
public class Database {
    // File paths
    private static final String USERS_FILE = "users.txt";
    private static final String RESERVATIONS_FILE = "reservations.txt";
    private static final String VOYAGES_FILE = "voyages.txt";
    
    private static Database instance;
    
    // Singleton pattern
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    // Appends a User (Passenger or Admin) to users.txt
    public void addUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            if (user instanceof Passenger p) {
                writer.write("passenger," + p.getId() + "," + p.getName() + "," + p.getEmail() + "," + p.getPassword() + "," + p.getPhoneNumber());
            } else if (user instanceof Admin a) {
                writer.write("admin," + a.getId() + "," + a.getName() + "," + a.getEmail() + "," + a.getPassword());
            }
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Kullanıcı yazılamadı: " + e.getMessage());
        }
    }
    // Appends a Voyage header line (no seats) to voyages.txt
    public void addVoyage(Voyage voyage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VOYAGES_FILE, true))) {
            writer.write(voyage.getVoyageNumber() + "," +
                    voyage.getOrigin() + "," +
                    voyage.getDestination() + "," +
                    voyage.getVoyageType() + "," +
                    voyage.getLocalDate().toString() + "," +
                    voyage.getLocalTime().toString() + "," +
                    voyage.getSeats().length + "," +
                    voyage.getSeats()[0].length);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Sefer yazılamadı: " + e.getMessage());
        }
    }
    
    // Appends a reservation line to reservations.txt
    public void addReservation(Reservation r) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESERVATIONS_FILE, true))) {
            writer.write(r.getReservationId() + "," +
                    r.getVoyage().getVoyageNumber() + "," +
                    r.getSeat().getSeatNumber() + "," +
                    r.getPassenger().getId());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Rezervasyon yazılamadı: " + e.getMessage());
        }
    }
    // Deletes a voyage record by voyageNumber
    public void removeVoyage(String voyageNumber) {
        File inputFile = new File(VOYAGES_FILE);
        File tempFile = new File("voyages_temp.txt");

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(voyageNumber + ",")) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Sefer silme hatası: " + e.getMessage());
            return;
        }

        if (inputFile.delete()) {
            if (!tempFile.renameTo(inputFile)) {
                System.err.println("Geçici dosya yeniden adlandırılamadı.");
            }
        } else {
            System.err.println("Orijinal sefer dosyası silinemedi.");
        }
    }
    
    // Deletes a reservation record by reservationId
    public void removeReservation(String reservationId) {
        File inputFile = new File(RESERVATIONS_FILE);
        File tempFile = new File("reservations_temp.txt");

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(reservationId + ",")) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Rezervasyon silme hatası: " + e.getMessage());
            return;
        }

        if (inputFile.delete()) {
            if (!tempFile.renameTo(inputFile)) {
                System.err.println("Geçici dosya yeniden adlandırılamadı.");
            }
        } else {
            System.err.println("Orijinal rezervasyon dosyası silinemedi.");
        }
    }
    public boolean isEmailRegistered(String email) {
        try (var lines = Files.lines(Paths.get("users.txt"))) {
            return lines.anyMatch(line -> {
                String[] parts = line.split(",");
                return parts.length >= 4 && parts[3].equalsIgnoreCase(email); 
            });
        } catch (IOException e) {
            System.err.println("E-posta kontrolü sırasında hata: " + e.getMessage());
            return false;
        }
    }
    public boolean isVoyageNumberRegister(String voyageNumber){
        try(var lines = Files.lines(Paths.get("voyages.txt"))){
            return lines.anyMatch(line -> {
                String[] parts = line.split(",");
                return parts.length >=7 && parts[0].equalsIgnoreCase(voyageNumber);
            });
        } catch(IOException e){
            System.err.println("Voyage Number kontrolü sırasında hata:" + e.getMessage());
            return false;
        }
    }
    
    // Loads all voyages from voyages.txt and populates Seat objects. Afterwards marks reserved seats using reservations.txt
    public List<Voyage> loadVoyages() {
        List<Voyage> voyages = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(VOYAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 8) continue;

                String voyageNumber = parts[0];
                String origin = parts[1];
                String destination = parts[2];
                VoyageType type = VoyageType.valueOf(parts[3]);
                LocalDate date = LocalDate.parse(parts[4]);
                LocalTime time = LocalTime.parse(parts[5]);
                int rows = Integer.parseInt(parts[6]);
                int cols = Integer.parseInt(parts[7]);
                
                // Create empty seat matrix
                Seat[][] seats = new Seat[rows][cols];
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        String seatNumber = (r + 1) + String.valueOf((char) ('A' + c));
                        seats[r][c] = new Seat(seatNumber, false,null, r,c); // by default all seats are empty
                    }
                }

                Voyage voyage = new Voyage(voyageNumber, origin, destination, type, date, time, seats);
                voyages.add(voyage);
            }
            // Mark seats that are already reserved
            markReservedSeats(voyages);
        } catch (IOException e) {
            System.err.println("Seferler yüklenemedi: " + e.getMessage());
        }
        


        return voyages;
    }
    
    // Marks seats as reserved according to reservations.txt
    private void markReservedSeats(List<Voyage> voyages) {
        try (BufferedReader reader = new BufferedReader(new FileReader(RESERVATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String voyageNumber = parts[1];
                    String seatNumber = parts[2];
                    int passengerId = Integer.parseInt(parts[3]);

                    for (Voyage v : voyages) {
                        if (v.getVoyageNumber().equals(voyageNumber)) {
                            Seat seat = v.getSeatByNumber(seatNumber);
                            Passenger passenger = getPassengerById(passengerId);
                            if (seat != null && passenger != null) {
                                seat.reserve(passenger);  // assign the passenger to the seat and mark
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Rezervasyonlar işaretlenemedi: " + e.getMessage());
        }
    }
    
    // Retrieves a Passenger object by its ID (null if not found)
    public Passenger getPassengerById(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].equals("passenger")) {
                    int uid = Integer.parseInt(parts[1]);
                    if (uid == id) {
                        String name = parts[2];
                        String email = parts[3];
                        String password = parts[4];
                        String phone = parts[5];
                        return new Passenger(uid, name, email, password, phone);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Yolcu okunamadı: " + e.getMessage());
        }
        return null;
    }
    
    // Loads all reservations belonging to a specific passenger
    public List<Reservation> loadReservationsForPassenger(int passengerId) {
        List<Reservation> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(RESERVATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && Integer.parseInt(parts[3]) == passengerId) {
                    String resId = parts[0];
                    String voyageNo = parts[1];
                    String seatNo = parts[2];

                    Voyage voyage = findVoyageByNumber(voyageNo);
                    if (voyage == null) continue;
                    Seat seat = voyage.getSeatByNumber(seatNo);
                     if (seat == null) continue;
                    Passenger passenger = getPassengerById(passengerId);
                     if (passenger == null) continue;

                    Reservation r = new Reservation(resId, voyage, seat, passenger);
                    list.add(r);
                }
            }
        } catch (IOException e) {
            System.err.println("Rezervasyonlar yüklenemedi: " + e.getMessage());
        }
        return list;
    }
    
    // Finds a single voyage by number (fresh load each call)
    public Voyage findVoyageByNumber(String voyageNumber) {
        List<Voyage> voyages = loadVoyages();  // read all voyages
        for (Voyage v : voyages) {
            if (v.getVoyageNumber().equalsIgnoreCase(voyageNumber)) {
                return v;
            }
        }
        return null; // if there is no match, null is returned.
    }
}
