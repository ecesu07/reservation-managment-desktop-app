/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.factory;

import finalproject.model.Seat;
import finalproject.model.Voyage;
import finalproject.model.VoyageType;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author ardaf
 */
public class VoyageBuilder {
    private String voyageNumber;
    private String origin;
    private String destination;
    private VoyageType voyageType;
    private LocalDate date;
    private LocalTime time;
    private Seat[][] seatLayout;
    public VoyageBuilder setVoyageNumber(String voyageNumber) {
        this.voyageNumber = voyageNumber;
        return this;
    }

    public VoyageBuilder setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public VoyageBuilder setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public VoyageBuilder setVoyageType(VoyageType voyageType) {
        this.voyageType = voyageType;
        return this;
    }

    public VoyageBuilder setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public VoyageBuilder setTime(LocalTime time) {
        this.time = time;
        return this;
    }

    public VoyageBuilder setSeatLayout(Seat[][] seatLayout) {
        this.seatLayout = seatLayout;
        return this;
    }

    public Voyage build() { 
        if (seatLayout.length > 24) {                 // seatLayout[row][col]
            throw new IllegalStateException(
                "Seat layout cannot have more than 24 rows.");
        }// Builds and returns a Voyage object using the specified fields.
        return new Voyage(voyageNumber, origin, destination, voyageType, date, time, seatLayout);
    }
}
