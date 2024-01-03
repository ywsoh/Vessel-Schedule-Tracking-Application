package group6.psa;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    
    public static void main(String []args){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin";
        System.out.print(encoder.encode(password));
    }
}
