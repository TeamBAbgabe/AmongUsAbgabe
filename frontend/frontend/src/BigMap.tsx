import React, { useEffect } from 'react';

function BigMap({ show, onClose, playerPosition, tasks, allCoords, role }) {
  const originalWidth = 32 * 64;
  const originalHeight = 32 * 64;
  const zoomFactor = 0.3;
  const tileSize = 32; // Each tile is 32 pixels
  console.log("my player", playerPosition);
  if (!show) return null;

  const vents = [
    { x: 44, y: 10 },
    { x: 1, y: 3 },
    { x: 17, y: 19 },
    { x: 10, y: 10 },
    { x: 27, y: 46 },
    { x: 20, y: 23 },
    { x: 52, y: 41 },
    { x: 31, y: 58 },
    { x: 46, y: 28 },
    { x: 56, y: 5 }
  ];

  const taskLocations = [
    { x: 55, y: 4 },
    { x: 3, y: 4 },
    { x: 33, y: 59 },
    { x: 16, y: 19 }
  ];

  console.log("All coordinates:");
  allCoords.forEach((coord, index) => {
    console.log(`Coord ${index}: x=${coord.x}, y=${coord.y}`);
  });

  // Event listener for ESC key
  useEffect(() => {
    const handleKeyDown = (event) => {
      if (event.keyCode === 27) { // 27 is the keyCode for Escape key
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [onClose]);

  return (
    <div style={{
      position: 'absolute',
      top: '50%',
      left: '50%',
      transform: 'translate(-50%, -50%)',
      width: `${originalWidth * zoomFactor}px`,
      height: `${originalHeight * zoomFactor}px`,
      border: '2px solid white',
      borderRadius: '10px',
      zIndex: 9000,
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      overflow: 'hidden',
      backgroundColor: 'black'
    }}>
      <div style={{
        width: '100%',
        height: '100%',
        position: 'relative',
        borderRadius: '10px',
        overflow: 'hidden'
      }}>
        <img
          src="https://i.imgur.com/ZVGXi61.png"
          alt="Big Map"
          style={{
            width: '100%',
            height: '100%',
            objectFit: 'cover'
          }}
        />
        {tasks.map((task, index) => (
          <div
            key={index}
            style={{
              position: 'absolute',
              top: `${task.coordinates.y * tileSize * zoomFactor}px`,
              left: `${task.coordinates.x * tileSize * zoomFactor}px`,
              color: 'yellow',
              fontSize: '15px',
              fontWeight: 'bold',
              transform: 'translate(-50%, -50%)',
              zIndex: 9001
            }}
          >
            {task.description}
          </div>
        ))}
        <div
          style={{
            position: 'absolute',
            top: `${playerPosition.y * zoomFactor}px`,
            left: `${playerPosition.x * zoomFactor}px`,
            color: 'red',
            fontSize: '20px',
            fontWeight: 'bold',
            transform: 'translate(-50%, -50%)',
            zIndex: 200000 // Ensure player marker is on top
          }}
        >
          O
        </div>
        {role === 'Imposter' && vents.map((vent, index) => (
          <div
            key={index}
            style={{
              position: 'absolute',
              top: `${vent.y * tileSize * zoomFactor}px`,
              left: `${vent.x * tileSize * zoomFactor}px`,
              width: '20px',
              height: '20px',
              backgroundColor: 'blue',
              borderRadius: '50%',
              transform: 'translate(-50%, -50%)',
              zIndex: 9001
            }}
          />
        ))}
        {role === 'Imposter' && taskLocations.map((task, index) => (
          <div
            key={index}
            style={{
              position: 'absolute',
              top: `${task.y * tileSize * zoomFactor}px`,
              left: `${task.x * tileSize * zoomFactor}px`,
              color: 'yellow',
              fontSize: '20px', // Increased font size
              fontWeight: 'bold',
              transform: 'translate(-50%, -50%)',
              zIndex: 9001
            }}
          >
            Task
          </div>
        ))}
        <button
          onClick={onClose}
          style={{
            position: 'absolute',
            top: '10px',
            right: '10px',
            backgroundColor: 'red',
            border: 'none',
            borderRadius: '5px',
            padding: '5px 10px',
            cursor: 'pointer',
            zIndex: 10000,
          }}
        >
          Close
        </button>
      </div>
    </div>
  );
}

export default BigMap;
