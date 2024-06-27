package com.example.BackendAmongUs.Leaderboard;

import com.example.BackendAmongUs.Benutzer.Exception.PasswordNotValidException;
import com.example.BackendAmongUs.Benutzer.Exception.UserNotMatchingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
@Controller
public class LeaderBoardController {
    @Autowired
    private LeaderBoardService leaderBoardService;

    @CrossOrigin(origins = "*")
    @GetMapping("/leaderboard")
    public ResponseEntity<String> benutzerLogin() throws UserNotMatchingException, PasswordNotValidException {

        String json = leaderBoardService.getLeaderboard();
        System.out.println(json);
        return new ResponseEntity<>(json, HttpStatus.CREATED);

    }
}
