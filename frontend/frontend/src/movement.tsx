import React, { useState, useEffect } from 'react';
import './CSS/movement.css'; 

function Movement({ taskHandle, handleClick, blockMovement, close, isMinimapOpen }) {
  const [showEscapeBox, setShowEscapeBox] = useState(false);

  const escapeHandling = () => {
    setShowEscapeBox(prev => !prev);
  };

  useEffect(() => {
    const detectKeyDown = (e) => {
      if (e.key === "Escape") {
        if (!isMinimapOpen) {
          escapeHandling();
        }
        return; 
      }

      if (blockMovement) {
        return;
      }

      const movementKeys = ['w', 'a', 's', 'd', 'e', 'f', 'r'];
      if (movementKeys.includes(e.key)) {
        if (e.key === 'r') {
          taskHandle();
        } else {
          handleClick(e.key);
        }
      }
    };

    // Always listen to keydown events
    window.addEventListener("keydown", detectKeyDown, true);
    return () => window.removeEventListener("keydown", detectKeyDown, true);
  }, [handleClick, taskHandle, blockMovement, isMinimapOpen]);

  return (
    <div>
      {showEscapeBox && (
        <div className="escape-box">
          <button className="button" onClick={() => console.log("Help clicked")}>Help</button>
          <button className="button" onClick={() => console.log("Settings clicked")}>Settings</button>
          <button className="button" onClick={() => close()}>Quit</button>
        </div>
      )}
    </div>
  );
}

export default Movement;
