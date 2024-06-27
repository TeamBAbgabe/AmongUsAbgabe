import React, { useState, useEffect } from 'react';
import "./CSS/lobby.css";

function Lobby({ lobbyUsers, onStartClick, close, clientRef, move, showCreateLobbyForm, changeCharacter }) {
  let showStart = true;
  if (move == null) {
    showStart = false;
  }

  const defaultPicture = "./src/picture/r3/hr4-removebg-preview.png";
  const [imposterCount, setImposterCount] = useState(1);
  const [selectedPicture, setSelectedPicture] = useState(defaultPicture);
  const [pictureMenuVisible, setPictureMenuVisible] = useState(false);
  const [preloadedPictures, setPreloadedPictures] = useState([]);

  const bot = () => {
    if (clientRef.current) {
      clientRef.current.publish({
        destination: '/app/bot',
      });
    }
  };

  const incrementImposters = () => {
    setImposterCount(prev => prev + 1);
  };

  const decrementImposters = () => {
    if (imposterCount > 1) {
      setImposterCount(prev => prev - 1);
    }
  };

  const pictures = [
    { src: './src/picture/g3/hg4-removebg-preview.png', name: 'F2' },
    { src: './src/picture/r3/hr4-removebg-preview.png', name: 'M1' }
  ];

  useEffect(() => {
    const loadImage = (picture) => {
      return new Promise((resolve) => {
        const img = new Image();
        img.src = picture.src;
        img.onload = () => resolve(img);
      });
    };

    const preloadImages = async () => {
      const images = await Promise.all(pictures.map(picture => loadImage(picture)));
      setPreloadedPictures(images);
    };

    preloadImages();
  }, [pictures]);

  const handlePictureClick = () => {
    setPictureMenuVisible(!pictureMenuVisible);
  };

  const handlePictureSelect = (picture) => {
    changeCharacter(picture.name);
    setSelectedPicture(picture.src);
    setPictureMenuVisible(false);
  };

  return (
    <div className="lobby-background">
      <div className="lobby-header">
        <h2 className="lobby-name">Lobby</h2>
        <img src='https://www.pngall.com/wp-content/uploads/5/Delete-Red-X-Button-PNG-Image.png' alt="Close" className="close-icon" onClick={close} />
      </div>
      <div className="lobby-main">
        <div className="lobby-content">
          <div className="lobby-containers">
            <ul className="user-list">
              {lobbyUsers.map((user) => (
                <li key={user.sessionId} className="user-item">
                  <img src={user.avatarUrl} alt={user.userId} className="user-avatar" />
                  <span className="username" style={{ color: user.color }}>
                    {user.userId}
                  </span>
                </li>
              ))}
            </ul>
            {showStart && (
              <>
                <button onClick={() => onStartClick(imposterCount)} className="start-button">Start Game</button>
                {!showCreateLobbyForm && (
                  <img src='https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/Red_plus_sign.svg/480px-Red_plus_sign.svg.png' className="plus-icon" onClick={bot} />
                )}
              </>
            )}
          </div>
          <div className="picture-input-container">
            <img
              src={selectedPicture}
              alt="Selected"
              onClick={handlePictureClick}
              className="selected-picture"
            />
            {pictureMenuVisible && (
              <div className="picture-menu">
                {preloadedPictures.map((img, index) => (
                  <img
                    key={index}
                    src={img.src}
                    alt={`Option ${index}`}
                    onClick={() => handlePictureSelect(pictures[index])}
                    className="picture-option"
                  />
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
      {showCreateLobbyForm && (
        <div className="imposter-controls">
          <button onClick={decrementImposters} className="imposter-count-button">-</button>
          <span className="imposter-count">Imposters: {imposterCount}</span>
          <button onClick={incrementImposters} className="imposter-count-button">+</button>
        </div>
      )}
    </div>
  );
};

export default Lobby;
