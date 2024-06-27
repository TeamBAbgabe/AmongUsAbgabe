import React from 'react';

const MyUserDisplay = ({ lobbyUsers, myRole, mysessionID }) => {
  const user = lobbyUsers.find(user => user.sessionId === mysessionID);
  if (!user) {
    return null;
  }

  return (
    <div className="fixedContainer">
      <div className="flexContainer">
        <img
          src={user.avatarUrl}
          className="userAvatar"
          alt="User Avatar"
        />
        <p className="userName">
          {user.userId}
        </p>
        <p className="userRole" style={{ color: user.isImposter ? 'blue' : 'red' }}>
          {myRole}
        </p>
      </div>
    </div>
  );
};

export default MyUserDisplay;
