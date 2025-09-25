/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.model;

/**
 *
 * @author ardaf
 */
public class Admin extends User {

    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password, UserType.ADMIN);
    }

    @Override
    public String toString() {
        return "[Admin] " + super.toString();
    }
}

