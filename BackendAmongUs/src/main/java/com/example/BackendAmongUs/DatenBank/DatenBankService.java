package com.example.BackendAmongUs.DatenBank;

import com.example.BackendAmongUs.DatenBank.Pojo.User;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DatenBankService extends DatenBankConnection {

    @Override
    public Connection getConnection() throws SQLException {
        // Here, you can add any service-specific logic before or after getting the connection.
        // If you're just calling the superclass method, this override is not necessary.
        return super.getConnection();
    }

    // Consider adding a method to close the connection if needed
    public void closeServiceConnection() throws SQLException {
        super.closeConnection();
    }

    public boolean register(String username, String hashedPassword) {
        String sql = "INSERT INTO users (username, hashed_password) VALUES (?, ?)";
        if (!checkUsername(username)) {
            try (Connection conn = this.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("User added successfully.");
                    return true;
                } else {
                    System.out.println("Adding user failed, no rows affected.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to add user.");
            }
        } else return false;

        return false;
    }


    public User login(String username, String hashedPassword) {
        String sql = "Select * from users Where username = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()) {

                    User user = new User();
                    user.setUsername(rs.getString("username"));
                    user.setHashedpassword(rs.getString("hashed_password"));
                    return user;
                }
                else{
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add user.");
        }
        return null;
    }
    public boolean checkUsername(String username) {
        String sql = "Select * from users Where username = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add user.");
        }
        return false;
    }

    public void updateWins(Set<String> usernames) {
        String checkColumnSql = "SELECT column_name FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'wins'";
        String addColumnSql = "ALTER TABLE users ADD COLUMN wins INTEGER DEFAULT 0";
        String checkSql = "SELECT wins FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, wins) VALUES (?, 1)";
        String updateSql = "UPDATE users SET wins = wins + 1 WHERE username = ?";

        try (Connection conn = this.getConnection()) {
            // Check if the wins column exists
            boolean columnExists = false;
            try (PreparedStatement checkColumnStmt = conn.prepareStatement(checkColumnSql);
                 ResultSet rs = checkColumnStmt.executeQuery()) {
                if (rs.next()) {
                    columnExists = true;
                }
            }

            // If the wins column does not exist, add it
            if (!columnExists) {
                try (PreparedStatement addColumnStmt = conn.prepareStatement(addColumnSql)) {
                    addColumnStmt.executeUpdate();
                }
            }

            // Loop through usernames and update wins
            for (String username : usernames) {
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, username);

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            // User exists, update wins
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                updateStmt.setString(1, username);
                                updateStmt.executeUpdate();
                            }
                        } else {
                            // User does not exist, insert with wins = 1
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setString(1, username);
                                insertStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update wins for users.");
        }
    }

    public Map<String, Integer> getAllWins() {
        String sql = "SELECT username, Wins FROM users";
        Map<String, Integer> winsMap = new HashMap<>();

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                int wins = rs.getInt("Wins");
                winsMap.put(username, wins);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve wins.");
        }

        return winsMap;
    }

    public void updateKills(String username) {
        String checkColumnSql = "SELECT column_name FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'kills'";
        String addColumnSql = "ALTER TABLE users ADD COLUMN kills INTEGER DEFAULT 0";
        String checkSql = "SELECT kills FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, kills) VALUES (?, 1)";
        String updateSql = "UPDATE users SET kills = kills + 1 WHERE username = ?";

        try (Connection conn = this.getConnection()) {
            // Check if the kills column exists
            boolean columnExists = false;
            try (PreparedStatement checkColumnStmt = conn.prepareStatement(checkColumnSql);
                 ResultSet rs = checkColumnStmt.executeQuery()) {
                if (rs.next()) {
                    columnExists = true;
                }
            }

            // If the kills column does not exist, add it
            if (!columnExists) {
                try (PreparedStatement addColumnStmt = conn.prepareStatement(addColumnSql)) {
                    addColumnStmt.executeUpdate();
                }
            }

            // Check if the user exists and update or insert kills
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // User exists, update kills
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, username);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        // User does not exist, insert with kills = 1
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, username);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update kills for user: " + username);
        }
    }
    public void updateDeath(String username) {
        String checkColumnSql = "SELECT column_name FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'death'";
        String addColumnSql = "ALTER TABLE users ADD COLUMN death INTEGER DEFAULT 0";
        String checkSql = "SELECT death FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, death) VALUES (?, 1)";
        String updateSql = "UPDATE users SET death = death + 1 WHERE username = ?";

        try (Connection conn = this.getConnection()) {
            // Check if the kills column exists
            boolean columnExists = false;
            try (PreparedStatement checkColumnStmt = conn.prepareStatement(checkColumnSql);
                 ResultSet rs = checkColumnStmt.executeQuery()) {
                if (rs.next()) {
                    columnExists = true;
                }
            }

            // If the kills column does not exist, add it
            if (!columnExists) {
                try (PreparedStatement addColumnStmt = conn.prepareStatement(addColumnSql)) {
                    addColumnStmt.executeUpdate();
                }
            }
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, username);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, username);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update Deaths for user: " + username);
        }
    }

    public void updateWinLossStats(Map<String, Integer> winnerMap) {
        String checkColumnSql = "SELECT column_name FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'win_loss_stats'";
        String addColumnSql = "ALTER TABLE users ADD COLUMN win_loss_stats VARCHAR DEFAULT ''";
        String checkSql = "SELECT win_loss_stats FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, win_loss_stats) VALUES (?, ?)";
        String updateSql = "UPDATE users SET win_loss_stats = ? WHERE username = ?";

        try (Connection conn = this.getConnection()) {
            // Check if the win_loss_stats column exists
            boolean columnExists = false;
            try (PreparedStatement checkColumnStmt = conn.prepareStatement(checkColumnSql);
                 ResultSet rs = checkColumnStmt.executeQuery()) {
                if (rs.next()) {
                    columnExists = true;
                }
            }

            // If the win_loss_stats column does not exist, add it
            if (!columnExists) {
                try (PreparedStatement addColumnStmt = conn.prepareStatement(addColumnSql)) {
                    addColumnStmt.executeUpdate();
                }
            }

            // Loop through winnerMap and update win_loss_stats
            for (Map.Entry<String, Integer> entry : winnerMap.entrySet()) {
                String username = entry.getKey();
                int result = entry.getValue();

                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, username);

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            // User exists, update win_loss_stats
                            String currentStats = rs.getString("win_loss_stats");
                            String newStats = currentStats + result;
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                updateStmt.setString(1, newStats);
                                updateStmt.setString(2, username);
                                updateStmt.executeUpdate();
                            }
                        } else {
                            // User does not exist, insert with win_loss_stats = result
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setString(1, username);
                                insertStmt.setString(2, String.valueOf(result));
                                insertStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update win/loss stats for users.");
        }
    }

    public Map<String, Object> getUserStats(String username) {
        String querySql = "SELECT wins, kills, death, win_loss_stats FROM users WHERE username = ?";
        Map<String, Object> userStats = new HashMap<>();

        try (Connection conn = this.getConnection();
             PreparedStatement queryStmt = conn.prepareStatement(querySql)) {

            queryStmt.setString(1, username);

            try (ResultSet rs = queryStmt.executeQuery()) {
                if (rs.next()) {
                    userStats.put("wins", rs.getInt("wins"));
                    userStats.put("kills", rs.getInt("kills"));
                    userStats.put("death", rs.getInt("death"));
                    userStats.put("win_loss_stats", rs.getString("win_loss_stats"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve user stats.");
        }

        return userStats;
    }

}
