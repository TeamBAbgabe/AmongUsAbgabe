import React from 'react';

function Crewmate({taskHandle, picture, reportPlayer, reportDeadBody}) {

  return (
    <>
      <img onClick={taskHandle} src={picture} style={{
        position: 'absolute',
        right: 150,
        bottom: 50,
        width: '100px',
        height: 'auto'
      }} /> 

      <img onClick={reportDeadBody} src={reportPlayer} style={{
        position: 'absolute',
        right: 250,
        bottom: 35,
        width: '140px',
        height: 'auto'
      }} /> 
    </>

    
  );
}

export default Crewmate;
