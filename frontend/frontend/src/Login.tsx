import React, { useState } from 'react';
import axios from 'axios';
import "./loginStyle.css";
import { Link } from 'react-router-dom';
import ErrorLogin from './errorLogin'; // Ensure the import matches the file name exactly

const Login = ({ onLoginSuccess, setUsernameParent }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const handleUsernameChange = (event) => {
    setUsername(event.target.value);
  };

  const handlePasswordChange = (event) => {
    setPassword(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const data = {
      username: username,
      password: password,
    };

    try {
      const response = await axios.post('http://10.0.40.161:8080/login', data);
      if (response.status === 200) {
        console.log('Login Success:', response.data);
        onLoginSuccess();
        setUsernameParent(username);
        setErrorMessage('');
      }
    } catch (error) {
      console.error('Login Error:', error.response ? error.response.data : error.message);
      if (error.response && (error.response.status === 409 || error.response.status === 403)) {
        setErrorMessage("Username or Password wrong.");
      } else {
        setErrorMessage("An unexpected error occurred.");
      }
    }

    setUsername('');
    setPassword('');
  };

  const closeModal = () => {
    setErrorMessage('');
  };

  return (
    <div className="login-background">
      <p className="title">VoidSpace</p>
      <form autoComplete="off" onSubmit={handleSubmit} className="login-form">
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
        <button type="submit" className="login-button">Login</button>
        <p className="clickable-text">
          <Link to="/register">Register</Link>
        </p>
      </form>
      <div className="browser-recommendation">
        For a better playing experience, we recommend using Chrome.
      </div>
      {errorMessage && <ErrorLogin message={errorMessage} onClose={closeModal} />}
    </div>
  );
};

export default Login;
