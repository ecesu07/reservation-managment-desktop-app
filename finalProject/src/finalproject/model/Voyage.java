/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ardaf
 */
public class Voyage {
    private String voyageNumber;
    private String origin;
    private String destination;
    private VoyageType voyageType;
    private LocalTime localTime;
    private LocalDate localDate;
    private Seat[][] seats;

   public Voyage(String voyageNumber, String origin, String destination,
                  VoyageType voyageType, LocalDate date, LocalTime time, Seat[][] seats) {
        this.voyageNumber = voyageNumber;
        this.origin = origin;
        this.destination = destination;
        this.voyageType = voyageType;
        this.localDate = date;
        this.localTime = time;
        this.seats = seats;
    }
    
    public List<Seat> getAvailableSeats() {     // Returns a list of all seats that are currently not reserved
        List<Seat> result = new ArrayList<>();
        for (Seat[] row : seats) {
            for (Seat s : row) {
                if (!s.isReserved()) {
                    result.add(s);
                }
            }
        }
        return result;
    }
    public Seat getSeat(String seatNo) {     // Finds a seat by seat number (case-insensitive)
        for (Seat[] row : seats) {
            for (Seat s : row) {
                if (s.getSeatNumber().equalsIgnoreCase(seatNo)) {
                    return s;
                }
            }
        }
        return null;
    }
    
    public String getVoyageNumber(){
        return voyageNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public VoyageType getVoyageType() {
        return voyageType;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public Seat[][] getSeats() {
        return seats;
    }

    @Override
    public String toString() {
        return "Voyage{" + "voyageNumber=" + voyageNumber + ", origin=" + origin + ", destination=" + destination + ", voyageType=" + voyageType + ", localTime=" + localTime + ", localDate=" + localDate + ", seats=" + seats + '}';
    }
    public Seat getSeatByNumber(String seatNumber) {   // Returns a seat by exact seat number (trimmed comparison)
        for (Seat[] row : seats) {
            for (Seat seat : row) {
                if (seat.getSeatNumber().trim().equals(seatNumber.trim())) {
                    return seat;
                }
            }
        }
        return null; // if there is no matching seat
    }
    public SeatLayoutType getSeatLayout() {   // Infers the seat layout type based on voyage type and column count
        int cols = (seats != null && seats.length > 0) ? seats[0].length : 0;

        if (voyageType == VoyageType.BUS) {
            return (cols == 3) ? SeatLayoutType.BUS_2_1
                               : SeatLayoutType.BUS_2_2;      // 4 column
        } else { 
            return (cols == 6) ? SeatLayoutType.PLANE_3_3
                               : SeatLayoutType.PLANE_2_4_2;   // 8 column
        }
    }
}
