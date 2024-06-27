import React, { useState } from 'react';
import axios from 'axios';
import './CSS/chat.css';

const Chat = ({ sessionId, chatMessages }) => {
  const [message, setMessage] = useState('');

  const handleSendMessage = () => {
    const data = { sessionId, message };
    axios.post('http://10.0.40.161:8080/sendMessage', data)
      .then(response => {
        console.log('Message sent:', response.data);
        setMessage('');
      })
      .catch(error => console.error('Error sending message:', error));
  };

  return (
    <div className="chat-container">
      <div className="message-display-area">
        {chatMessages.map((msg, index) => (
          <div key={index} className="message">
            <strong style={{ color: msg.color }}>{msg.username}:</strong> {msg.message}
          </div>
        ))}
      </div>
      <div className="chat-input-container">
        <input
          className="chat-input"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="Type a message... (max 30 characters)"
          maxLength={30}
        />
        <button className="button" onClick={handleSendMessage}>Send</button>
      </div>
    </div>
  );
};

export default Chat;
