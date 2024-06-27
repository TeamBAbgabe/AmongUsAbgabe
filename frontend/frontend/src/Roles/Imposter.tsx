import React from 'react';

function Imposter({ killButton, tunnel, picture, tunnelButton }) {

  return (
    <>
      <img onClick={killButton} src={picture} style={{
        position: 'absolute',
        right: 150,
        bottom: 50,
        width: '100px',
        height: 'auto',
        cursor: 'pointer' 
      }} />
      <img onClick={tunnel} src={tunnelButton} style={{
        position: 'absolute',
        left: 'calc(80% - 60px)',
        bottom: 50,
        width: '100px',
        height: 'auto',
        cursor: 'pointer' 
      }} />
    </>
  );
}

export default Imposter;
