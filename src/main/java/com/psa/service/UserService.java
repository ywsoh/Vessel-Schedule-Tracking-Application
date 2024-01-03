package com.psa.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.psa.config.WebSecurityConfig;
import com.psa.entity.User;
import com.psa.entity.Vessel;
import com.psa.entity.user.MyUserDetails;
import com.psa.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VesselService vesselService;

    @Autowired
    private WebSecurityConfig securityConfig;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("Could not find user");
        }

        return new MyUserDetails(user);
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public void save(User user){
        userRepository.save(user);
        //account creation
    }

    public User get(int id){
        return userRepository.findById(id).get();
    }

    public void delete(int id){
        userRepository.deleteById(id);
    }

    public Set<Integer> getSubscribedVesselId(String username) {
        return userRepository.findAllSubscriptionByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    public void updateSubscriptions(String username, List<String> vesselIDs) {
        User user = getUserByUsername(username);
        Set<Vessel> subscription = new HashSet<>();
        for (String vesselId: vesselIDs) {
            subscription.add(vesselService.getOne(Integer.parseInt(vesselId)));
        }
        user.setSubscriptions(subscription);
        save(user);
    }

    public boolean checkIfUserExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword, String cfmPassword) {

        User user = userRepository.getUserByUsername(username);

        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        oldPassword = encoder.encode(oldPassword);

        if (oldPassword.equals(user.getPassword())) {
            if (newPassword.equals(cfmPassword)) {
                user.setPassword(encoder.encode(cfmPassword));
                System.out.println("Password successfully changed");
                return true;
                //send email
            }
        }
        System.out.println("Please key in the correct password");
        return false;
    }

    public boolean createNewUser(String username, String password, String cfmPassword, boolean isAdmin, String email, boolean isEnabled) {



        if (!(password.equals(cfmPassword)) || checkIfUserExists(username)) {
            return false;
        }

        String role = "USER";
        if (isAdmin) {
            role="ADMIN";
        }

        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        password = encoder.encode(password);
        User user = new User(username, password, email, isEnabled, role);
        save(user);
        return true;
    }
    //upon account creation, send email

    //upon forget password + admin change password
}
