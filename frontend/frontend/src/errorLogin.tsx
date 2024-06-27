import React from 'react';
import "./CSS/errorLogin.css"; // Ensure the correct import path

const ErrorLogin = ({ message, onClose }) => {
  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <p>{message}</p>
        <button onClick={onClose} className="close-button">Close</button>
      </div>
    </div>
  );
};

export default ErrorLogin; // Ensure the export name matches the component name
