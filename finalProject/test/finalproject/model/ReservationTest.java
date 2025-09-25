package finalproject.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * JUnit 4 test class for the Reservation class.
 */
public class ReservationTest {

    /**
     * Tests whether the Reservation constructor correctly sets all fields.
     */
    @Test
    public void constructorShouldSetAllFields() {
        Passenger passenger = new Passenger(
                1,                       // id
                "Arda",                  // name
                "arda@example.com",      // email
                "1234",                  // password
                "555-1234");             // phone

        Seat seat = new Seat("3B",       // seatNumber
                             false,      // isReserved
                             null,       // passenger
                             0, 1);      // row, column

        Seat[][] seats = { { seat } };

        Voyage voyage = new Voyage(
                "V001",
                "İzmir",
                "Ankara",
                VoyageType.BUS,          
                LocalDate.of(2025, 6, 18),
                LocalTime.of(12, 0),
                seats);

        Reservation res = new Reservation("R001", voyage, seat, passenger);

        assertEquals("R001", res.getReservationId());
        assertSame(voyage,   res.getVoyage());
        assertSame(seat,     res.getSeat());
        assertSame(passenger, res.getPassenger());
    }

    /**
     * Tests whether calling cancel() frees the seat and clears the passenger.
     */
    @Test
    public void cancelShouldFreeSeat() {
        Passenger p = new Passenger(2, "Ayşe", "ayse@ex.com", "abcd", "555-4321");

        Seat seat = new Seat("4A", true, p, 0, 0);   
        Seat[][] seats = { { seat } };

        Voyage voyage = new Voyage("V002", "A", "B",
                                   VoyageType.PLANE,
                                   LocalDate.now(),
                                   LocalTime.now(),
                                   seats);

        Reservation r = new Reservation("R002", voyage, seat, p);

        // precondition
        assertTrue("Önce dolu olmalı", seat.isReserved());

        r.cancel();   // should reset seat's reserved status and passenger

        assertFalse("İptal sonrası boş olmalı", seat.isReserved());
        assertNull("Yolcu temizlenmeli", seat.getPassenger());
    }

    /**
     * Tests whether toString() contains key elements like ID, passenger, and voyage details.
     */
    @Test
    public void toStringShouldContainKeyParts() {
        Passenger passenger = new Passenger(3, "Ece",
                                            "ece@ex.com", "qwerty", "555-9876");

        Seat seat = new Seat("1C", false, null, 0, 2);
        Seat[][] seats = { { seat } };

        Voyage voyage = new Voyage("V003", "İstanbul", "Bursa",
                                   VoyageType.BUS,
                                   LocalDate.of(2025, 6, 20),
                                   LocalTime.of(14, 30),
                                   seats);

        Reservation res = new Reservation("R003", voyage, seat, passenger);

        String str = res.toString();

        assertTrue(str.contains("R003"));
        assertTrue(str.contains("1C"));
        assertTrue(str.contains("İstanbul"));
        assertTrue(str.contains("Bursa"));
        assertTrue(str.contains("Ece"));
    }
    
    /**
     * Tests that an already reserved seat does not allow being reserved again.
     */
    @Test
    public void cannotReserveAlreadyReservedSeat() {
        Passenger p1 = new Passenger(1, "Ali", "ali@ex.com", "123", "555");
        Passenger p2 = new Passenger(2, "Zeynep", "zeynep@ex.com", "abc", "444");

        Seat seat = new Seat("1A", false, null, 0, 0);
        seat.reserve(p1);

        assertTrue(seat.isReserved());
        assertEquals(p1, seat.getPassenger());

        // Try to reserve with another passenger (if the Seat class blocks this)
        seat.reserve(p2);

        // Should still belong to the first passenger
        assertEquals(p1, seat.getPassenger());
    }
    
    /**
     * Tests that reserving a seat correctly assigns the passenger and updates status.
     */
    @Test
    public void reserveShouldSetPassengerAndIsReserved() {
        Passenger passenger = new Passenger(10, "Can", "can@ex.com", "pw", "111");
        Seat seat = new Seat("2B", false, null, 1, 1);

        seat.reserve(passenger);

        assertTrue(seat.isReserved());
        assertEquals(passenger, seat.getPassenger());
    }
    
    /**
     * Tests if a seat can be reused for another reservation after being cancelled.
     */
    @Test
    public void seatShouldBeReusableAfterCancel() {
        Passenger p1 = new Passenger(1, "Fatma", "f@e.com", "123", "999");
        Seat seat = new Seat("4C", true, p1, 2, 2);
        Seat[][] seats = { { seat } };
        Voyage v = new Voyage("V200", "X", "Y", VoyageType.BUS, LocalDate.now(), LocalTime.now(), seats);

        Reservation r = new Reservation("R999", v, seat, p1);
        r.cancel(); // cancel the reservation

        assertFalse(seat.isReserved());
        // New reservation attempt
        Passenger p2 = new Passenger(2, "Mert", "m@e.com", "456", "888");
        seat.reserve(p2);

        assertTrue(seat.isReserved());
        assertEquals(p2, seat.getPassenger());
    }
    
    /**
     * Tests that Voyage correctly stores LocalDate and LocalTime fields.
     */
    @Test
    public void voyageShouldStoreCorrectDateAndTime() {
        LocalDate date = LocalDate.of(2025, 12, 24);
        LocalTime time = LocalTime.of(18, 45);

        Seat[][] seats = new Seat[1][1];
        seats[0][0] = new Seat("1A", false, null, 0, 0);

        Voyage v = new Voyage("V500", "Manisa", "Muğla", VoyageType.PLANE, date, time, seats);

        assertEquals(date, v.getLocalDate());
        assertEquals(time, v.getLocalTime());
        assertEquals("Muğla", v.getDestination());
    }
}
