import React, { useState } from 'react';
import axios from 'axios';
import "./loginStyle.css"
import { Link } from 'react-router-dom';
import ErrorRegister from './errorRegister';

function Register(){
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [repeatPassword, setRepeatPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

  const handleUsernameChange = (event) => {
    setUsername(event.target.value);
  };

  const handlePasswordChange = (event) => {
    setPassword(event.target.value);
  };

  const handleRepeatPasswordChange = (event) => {
    setRepeatPassword(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault(); 

    const data = {
      username: username,
      password: password,
      repeatPassword: repeatPassword,
    };

    try {
      const response = await axios.post('http://10.0.40.161:8080/regestrieren', data);
      console.log(response.status); 
    } catch (error) {
      if (error.response) {
        console.log(error.response.status); 
        if (error.response.status === 409) {
          setErrorMessage("Dieser User ist vergeben"); 
        } else if (error.response.status === 400){
          setErrorMessage("Passwörter passen nicht überein")
        } else if(error.response.status === 403){
          setErrorMessage("Das Password muss mindestens eine Zahl ein Großbuchstabe und länger als 3 Zeichen sein")
        }
      } else if (error.request) {
        console.log(error.request);
      } else {
        console.log('Error', error.message);
      }
    }
    

    setUsername('');
    setPassword('');
    setRepeatPassword('');
  };

  const closeModal = () => {
    setErrorMessage('');
  };

  return (
    <div className="login-background">
       <p className="title" >VoidSpace</p>
      <form onSubmit={handleSubmit} className="login-form">
        <label htmlFor="username" className="form-label">Username:</label>
        <input
          type="text"
          id="username"
          className="form-input"
          value={username}
          onChange={handleUsernameChange}
          required
        />
        <label htmlFor="password" className="form-label">Password:</label>
        <input
          type="password"
          id="password"
          className="form-input"
          value={password}
          onChange={handlePasswordChange}
          required
        />
        <label htmlFor="repeatPassword" className="form-label">Repeat Password:</label>
        <input
          type="password"
          id="repeatPassword"
          className="form-input"
          value={repeatPassword}
          onChange={handleRepeatPasswordChange}
          required
        />
        <button type="submit" className="login-button">Register</button>
        
        <p className="clickable-text">
              <Link to="/login">Login</Link>
        </p>
      </form>
      {errorMessage && <ErrorRegister message={errorMessage} onClose={closeModal} />}
    </div>

    
  );
};

export default Register;