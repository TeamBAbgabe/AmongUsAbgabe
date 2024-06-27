import React from 'react';
import './CSS/video.css'; // Importing the CSS file with the styles

const FullScreenVideo = ({ myrole }) => {
  const imposterGif = "https://64.media.tumblr.com/91d6844d857474df2ead53d3e04e8261/e6dcf11edac0d332-8b/s1280x1920/955d831b0f8276b4a94ff91d521bf2b235ae64c1.gif";
  const crewmateGif = "https://cdn.dribbble.com/users/2180988/screenshots/4393602/redplanet_dribbble.gif";

  return (
    <div className="result-overlay">
      <img src={myrole === 'Imposter' ? imposterGif : crewmateGif} alt={myrole === 'Imposter' ? "Imposter" : "Crewmate"} className="result-image" />
      <div className="result-message">
        {myrole === 'Imposter' ? <p>You are the Imposter. Take your revenge!</p> : <p>You are a Crewmate. Save your planet!</p>}
      </div>
    </div>
  );
};

export default FullScreenVideo;
