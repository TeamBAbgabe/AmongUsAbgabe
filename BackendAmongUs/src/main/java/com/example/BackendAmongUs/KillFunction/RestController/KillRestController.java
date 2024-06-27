package com.example.BackendAmongUs.KillFunction.RestController;

import com.example.BackendAmongUs.GameSession.Exceptions.GameIdNotFound;
import com.example.BackendAmongUs.GameSession.Exceptions.NoSessionIDs;
import com.example.BackendAmongUs.KillFunction.Helper.KillHelper;
import com.example.BackendAmongUs.KillFunction.KillFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class KillRestController {
    @Autowired
    private KillFunction killFunction;
    @CrossOrigin(origins = "*")
    @PostMapping("/kill")
    public ResponseEntity<?> killAction(@RequestBody Map<String, String> payload) {
        try {
            String sessionId = payload.get("killerId");
            killFunction.findNearestKillablePlayer(sessionId);
            return ResponseEntity.ok("Kill action executed successfully");
        } catch (NoSessionIDs ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No session IDs found: " + ex.getMessage());
        } catch (GameIdNotFound ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game ID not found: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

}
