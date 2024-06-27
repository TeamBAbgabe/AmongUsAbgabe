import React from 'react';
import "./CSS/errorLogin.css"; // Ensure the correct import path

const ErrorRegister = ({ message, onClose }) => {
  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <p>{message}</p>
        <button onClick={onClose} className="close-button">Close</button>
      </div>
    </div>
  );
};

export default ErrorRegister; 