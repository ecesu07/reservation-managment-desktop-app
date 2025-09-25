/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.model;

/**
 *
 * @author ardaf
 */
public class Seat {
    private String seatNumber;
    private boolean isReserved;
    private Passenger passenger;
    private int row;
    private int column;

    public Seat(String seatNumber, boolean isReserved, Passenger passenger, int row, int column) {
        this.seatNumber = seatNumber;
        this.isReserved = isReserved;
        this.passenger = passenger;
        this.row = row;
        this.column = column;
    }
    
    public boolean reserve(Passenger passenger){
            if (isReserved) {           
            return false;           // fail, seat does not change
        }
        this.passenger = passenger;
        this.isReserved = true;
        return true;
    }
    public void cancel(){     // cancels the reservation of the seat
        this.isReserved = false;
        this.passenger = null;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
    public boolean isAvailable() {
        return !this.isReserved;
    }
}
