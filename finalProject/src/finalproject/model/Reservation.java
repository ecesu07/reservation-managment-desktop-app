/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.model;

/**
 *
 * @author ardaf
 */
public class Reservation {
    private String reservationId;
    private Voyage voyage;
    private Seat seat;
    private Passenger passenger;
    
    public Reservation(String reservationId, Voyage voyage, Seat seat, Passenger passenger) {
        this.reservationId = reservationId;
        this.voyage = voyage;
        this.seat = seat;
        this.passenger = passenger;
    }
    public String getReservationId() {
        return reservationId;
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public Seat getSeat() {
        return seat;
    }

    public Passenger getPassenger() {
        return passenger;
    }
    public void cancel() {
        seat.cancel(); // in the Seat class, isReserved = false and passenger = null
    }
    public String toString() {
        return "Rezervasyon: " + reservationId +
               " | Koltuk: " + seat.getSeatNumber() +
               " | Sefer: " + voyage.toString() +
               " | Yolcu: " + passenger.getName();
    }
}
