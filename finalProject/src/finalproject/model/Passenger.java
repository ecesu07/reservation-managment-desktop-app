/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.model;

/**
 *
 * @author ardaf
 */
public class Passenger extends User {
    private String phoneNumber;

    public Passenger(int id, String name, String email, String password, String phoneNumber) {
        super(id, name, email, password, UserType.PASSENGER);
        this.phoneNumber = phoneNumber;
    } 

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return super.toString() + " - Tel: " + phoneNumber;
    }
    
        @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Passenger other = (Passenger) o;

        // For uniqueness id is enough
        return this.getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}

