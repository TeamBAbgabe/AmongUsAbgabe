import React from 'react';

const CoordsDisplay = ({ coords }) => {
  if (!coords || coords.x === null || coords.y === null) {
    return null;
  }
  console.log("Coords: ", coords)
  return (
    <div style={{
      position: 'absolute',
      top: '30px', // Lower the position
      left: '50%',
      transform: 'translateX(-50%)',
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      color: 'white',
      padding: '10px 20px', // Increase padding for larger size
      borderRadius: '5px',
      fontFamily: 'Arial, sans-serif',
      fontSize: '24px', // Increase font size
      zIndex: 10000, // Ensure it's on top of other elements
    }}>
      [{coords.x}, {coords.y}]
    </div>
  );
};

export default CoordsDisplay;
