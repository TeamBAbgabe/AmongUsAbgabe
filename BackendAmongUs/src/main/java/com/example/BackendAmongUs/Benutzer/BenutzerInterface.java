package com.example.BackendAmongUs.Benutzer;

import com.example.BackendAmongUs.Benutzer.Exception.*;

public interface BenutzerInterface {

    public void benutzerRegestrieren(String username, String password, String repeatPassword) throws PasswordMismatchException, PasswordNotValidException, UserTakenException;
    public void benutzerLogin(String username, String password) throws UserNotMatchingException, PasswordNotValidException;
    public boolean benutzerAbmelden(String username, String password);
    public void benutzerLöschen(String username, String password);
    public String benutzerDaten(String usernamen) throws UserDataCouldntBeFetched;


}
