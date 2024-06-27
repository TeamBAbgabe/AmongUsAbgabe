import React from 'react';


function Gamestart({ gamestart}) {
  return (
    <div className="lobby-background">
      <h2 className="lobby-name">Lobby</h2>
      <div className="lobby-container">
        <ul className="user-list">
          {gamestart.map((user, index) => (
            <li key={user.userId} className="user-item">
              <img src={user.avatarUrl} alt={user.userId} className="user-avatar" />
              <span className="username" style={{ color: user.color }}>
                {user.userId}
              </span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default Gamestart;
