/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.factory;

import finalproject.model.Admin;
import finalproject.model.Passenger;
import finalproject.model.User;

/**
 *
 * @author ardaf
 */
public class UserFactory {
    // For create passenger
    public static User createUser(String type, int id, String name, String email, String password, String phoneNumber) {
        if (type.equalsIgnoreCase("passenger")) {
            return new Passenger(id, name, email, password, phoneNumber);
        } else {
            throw new IllegalArgumentException("Passenger için geçerli kullanıcı tipi değil: " + type);
        }
    }

    // For create admin
    public static User createUser(String type, int id, String name, String email, String password) {
        if (type.equalsIgnoreCase("admin")) {
            return new Admin(id, name, email, password);
        } else {
            throw new IllegalArgumentException("Admin için geçerli kullanıcı tipi değil: " + type);
        }
    }
}

