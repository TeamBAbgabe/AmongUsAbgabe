import React, { useEffect } from 'react';
import './CSS/FullLobby.css';  // CSS import for overall styling

const Modal = ({ isOpen, onClose, messageError }) => {
    useEffect(() => {
        const handleEscapeKey = (event) => {
            if (event.key === 'Escape') {
                onClose();
            }
        };

        if (isOpen) {
            document.addEventListener('keydown', handleEscapeKey);
        }

        return () => {
            document.removeEventListener('keydown', handleEscapeKey);
        };
    }, [isOpen, onClose]);

    if (!isOpen) return null;

    return (
        <div className="overlay">
            <div className="content">
                <h1>{messageError}</h1>
                <img src="https://media.tenor.com/xV827E-f84MAAAAi/fo76-vault-boy.gif" alt="Loading..." className="loading-gif" />
                <button onClick={onClose} className="close-button">Close</button>
            </div>
        </div>
    );
};

export default Modal;
