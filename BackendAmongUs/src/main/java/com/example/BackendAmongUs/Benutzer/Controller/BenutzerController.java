package com.example.BackendAmongUs.Benutzer.Controller;
import com.example.BackendAmongUs.Benutzer.BenutzerService;
import com.example.BackendAmongUs.Benutzer.Exception.PasswordMismatchException;
import com.example.BackendAmongUs.Benutzer.Exception.PasswordNotValidException;
import com.example.BackendAmongUs.Benutzer.Exception.UserNotMatchingException;
import com.example.BackendAmongUs.Benutzer.Exception.UserTakenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class BenutzerController {

    @Autowired
    private BenutzerService service;

    @PostMapping("/login")
    public ResponseEntity benutzerLogin(@RequestBody Map<String, Object> userData) throws UserNotMatchingException, PasswordNotValidException {
        String username = (String) userData.get("username");
        String password = (String) userData.get("password");
        try {
            service.benutzerLogin(username, password);
            return ResponseEntity.ok().build();
        } catch (UserNotMatchingException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is not matching.");
        } catch (PasswordNotValidException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Password not valid.");
        }
    }


    @PostMapping("/abmelden")
    public ResponseEntity benutzerAbmelden() {
        return ResponseEntity.badRequest().build();
    }


    @PostMapping("/löschen")
    public ResponseEntity benutzerLöschen(@RequestBody Map<String, Object> userData) {
        String username = (String) userData.get("username");
        String password = (String) userData.get("password");

        return ResponseEntity.badRequest().build();
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/regestrieren")
    public ResponseEntity<?> benutzerRegestrieren(@RequestBody Map<String, Object> userData) {
        String username = (String) userData.get("username");
        String password = (String) userData.get("password");
        String repeatPassword = (String) userData.get("repeatPassword");

        try {
            service.benutzerRegestrieren(username, password, repeatPassword);
            return ResponseEntity.ok().build();
        } catch (PasswordMismatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password mismatch.");
        } catch (PasswordNotValidException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Password not valid.");
        } catch (UserTakenException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/benutzerDaten")
    public String benutzerRegestrieren(@RequestParam String username) {
        try {
            return service.benutzerDaten(username);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"An error occurred while processing the request.\"}";
        }
    }
}


