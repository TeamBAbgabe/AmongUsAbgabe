import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import 'chart.js/auto';
import './CSS/AccountOverlay.css';

const AccountOverlay = ({ userName }) => {
  const [isVisible, setIsVisible] = useState(false);
  const [stats, setStats] = useState(null);

  useEffect(() => {
    const handleKeyDown = (event) => {
      if (event.key === 'Escape') {
        setIsVisible(false);
      }
    };

    if (isVisible) {
      document.addEventListener('keydown', handleKeyDown);
    } else {
      document.removeEventListener('keydown', handleKeyDown);
    }

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [isVisible]);

  const handleIconClick = async () => {
    setIsVisible(true);
    try {
      const response = await fetch(`http://10.0.40.161:8080/benutzerDaten`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({ username: userName }),
      });
      const data = await response.json();
      setStats(data);
    } catch (error) {
      console.error('Error fetching user stats:', error);
    }
  };

  const handleCloseClick = () => {
    setIsVisible(false);
  };

  const generateChartData = (winLossStats) => {
    if (typeof winLossStats === 'string') {
      winLossStats = winLossStats.split('');
    }

    if (!Array.isArray(winLossStats)) {
      console.error('winLossStats is not an array:', winLossStats);
      return { datasets: [] };
    }

    let dataPoints = [{ x: 0, y: 0 }];
    let currentValue = 0;

    winLossStats.forEach((result, index) => {
      if (result === '1') {
        currentValue++;
      } else if (result === '0') {
        currentValue--;
      }
      dataPoints.push({ x: index + 1, y: currentValue });
    });

    return {
      labels: dataPoints.map(point => point.x),
      datasets: [
        {
          label: 'Win/Loss Stats',
          data: dataPoints.map(point => point.y),
          borderColor: 'rgba(255, 69, 0, 1)',
          backgroundColor: 'rgba(255, 69, 0, 1)',
          fill: false,
          tension: 0.1,
        },
      ],
    };
  };

  return (
    <div className="account-overlay-container">
      <img 
        src="https://static.vecteezy.com/system/resources/previews/008/506/404/original/contact-person-red-icon-free-png.png" 
        alt="Account Icon"
        className="account-icon"
        onClick={handleIconClick}
      />
      {isVisible && (
        <div className="account-overlay">
          <img 
            src="https://www.pngall.com/wp-content/uploads/5/Delete-Red-X-Button-PNG-Image.png"
            alt="Close"
            className="close-icon"
            onClick={handleCloseClick}
          />
          <div className="account-details">
            <h2>{userName}</h2>
            {stats ? (
              <>
                <p>Wins: {stats.wins}</p>
                <p>Kills: {stats.kills}</p>
                <p>Deaths: {stats.death}</p>
                <p>Games Played: {stats.gamesPlayed}</p>
                {stats.win_loss_stats && (
                  <div className="chart-container">
                    <Line data={generateChartData(stats.win_loss_stats)} />
                  </div>
                )}
              </>
            ) : (
              <p>Loading...</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default AccountOverlay;
