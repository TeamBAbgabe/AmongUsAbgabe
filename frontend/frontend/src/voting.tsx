import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './CSS/voting.css';
import Chat from './chat'; 

const Voting = ({ showVoting, closeVoting, votingData, mysessionID, chatMessages }) => {
  const [timeLeft, setTimeLeft] = useState(29); 
  const [showChat, setShowChat] = useState(false);

  useEffect(() => {
    if (showVoting) {
      const timerId = setInterval(() => {
        setTimeLeft(prevTimeLeft => {
          if (prevTimeLeft <= 1) {
            clearInterval(timerId);
            closeVoting();
            return 29; 
          } else {
            return prevTimeLeft - 1;
          }
        });
      }, 1000);
      return () => clearInterval(timerId);
    } else {
      setTimeLeft(29); 
    }
  }, [showVoting]);

  const handleVote = (sessionId) => {
    const data = { sessionId: mysessionID, suspectId: sessionId };
    axios.post('http://10.0.40.161:8080/voting', data)
      .then(response => console.log('Vote recorded:', response.data))
      .catch(error => console.error('Error recording vote:', error));
  };

  if (!showVoting) return null;

  return (
    <div className="voting-overlay">
      <div className="voting-container">
        <h2 className='voting-title'>Voting in Progress</h2>
        <p>Time left: {timeLeft} seconds</p>
        <ul>
          {votingData.map(player => (
            <li key={player.sessionId} className="vote-item">
              <img src={player.avatar} alt={player.userName} className="vote-avatar" />
              <span
                className="username"
                onClick={() => handleVote(player.sessionId)}
                style={{ color: player.color }} 
              >
                {player.userName}
              </span>
            </li>
          ))}
        </ul>
        <button className="button" onClick={() => setShowChat(!showChat)}>
          {showChat ? 'Hide Chat' : 'Show Chat'}
        </button>
      </div>
      {showChat && <Chat sessionId={mysessionID} chatMessages={chatMessages} />}
    </div>
  );
};

export default Voting;
