package com.example.BackendAmongUs.Benutzer;


import com.example.BackendAmongUs.Benutzer.Exception.*;
import com.example.BackendAmongUs.DatenBank.DatenBankService;
import com.example.BackendAmongUs.DatenBank.Pojo.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.authenticator.DigestAuthenticator;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class BenutzerService implements BenutzerInterface {


    @Autowired
    private DatenBankService service;
    public void benutzerRegestrieren(String username, String password, String repeatPassword)
            throws PasswordMismatchException, PasswordNotValidException, UserTakenException {
        System.out.println(password + " und " + repeatPassword);
        if (!checkpassword(password, repeatPassword)) {
            throw new PasswordMismatchException("Passwords do not match.");
        }
        if (!validPassword(password)) {
            throw new PasswordNotValidException("Password is not valid.");
        }
        String sha256hex = DigestUtils.sha256Hex(password);
        if (!service.register(username, sha256hex)) {
            throw new UserTakenException("User is already taken.");
        }
    }



    @Override
    public void benutzerLogin(String username, String password) throws UserNotMatchingException, PasswordNotValidException {
        User user = service.login(username, password);
        String hashedPassword = DigestUtils.sha256Hex(password);


        if(user == null){
            throw new UserNotMatchingException("The provided User doesnt exist");
        } else if(!hashedPassword.equals(user.getHashedpassword())){
            throw new PasswordNotValidException("The provided Password doesnt match the Password in the Database");
        }
    }

    @Override
    public boolean benutzerAbmelden(String username, String password) {
        return false;
    }

    @Override
    public void benutzerLöschen(String username, String password) {

    }

    private boolean checkpassword(String password, String repeatPassword){
        if(password.equals(repeatPassword)){
            return true;
        } else return false;
    }

    private boolean validPassword(String password) {
        if (password == null) {
            return false;
        }
        boolean hasUpperCase = false;
        boolean hasDigit = false;

        if (password.length() > 3) {
            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    hasUpperCase = true;
                }
                if (Character.isDigit(c)) {
                    hasDigit = true;
                }
                if (hasUpperCase && hasDigit) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String benutzerDaten(String username) throws UserDataCouldntBeFetched {
        Map<String, Object> userStats = service.getUserStats(username);

        if (userStats.isEmpty()) {
            throw new UserDataCouldntBeFetched("User Data couldnt be fetched: " + username);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(userStats);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to convert user stats to JSON.\"}";
        }
    }
}
