import React, { useEffect, useState } from 'react';
import './aimbot.css';

function RandomCircleTask({ onClose, onComplete, task }) {
  const [circles, setCircles] = useState([]);
  const [hits, setHits] = useState(0);
  const [attempts, setAttempts] = useState(0);
  const [timer, setTimer] = useState(10);

  const generateCircle = () => {
    const size = Math.random() * 50 + 30; 
    const circle = {
      id: Math.random(),
      x: Math.random() * (100 - size / 6),
      y: Math.random() * (100 - size / 6),
      size: size,
      dx: (Math.random() - 0.5) * 2, 
      dy: (Math.random() - 0.5) * 2
    };

    
    setTimeout(() => {
      setCircles(prevCircles => {
        const stillExists = prevCircles.some(c => c.id === circle.id);
        if (stillExists) {
          setAttempts(prevAttempts => prevAttempts + 1); 
        }
        return prevCircles.filter(c => c.id !== circle.id);
      });
    }, 3000);
    return circle;
  };

  useEffect(() => {
    const moveInterval = setInterval(() => {
      setCircles(prevCircles => prevCircles.map(circle => ({
        ...circle,
        x: Math.max(0, Math.min(100, circle.x + circle.dx)), 
        y: Math.max(0, Math.min(100, circle.y + circle.dy))
      })));
    }, 50); 

    return () => clearInterval(moveInterval);
  }, []);

  useEffect(() => {
    const interval = setInterval(() => {
      if (circles.length < 5) { 
        setCircles(prev => [...prev, generateCircle()]);
      }
    }, 1000); 

    const countdown = setInterval(() => {
      setTimer(prevTimer => {
        if (prevTimer === 1) {
          clearInterval(interval);
          clearInterval(countdown);
          return 0;
        }
        return prevTimer - 1;
      });
    }, 1000);

    return () => {
      clearInterval(interval);
      clearInterval(countdown);
    };
  }, [circles.length]);

  useEffect(() => {
    if (timer === 0) {
      const accuracy = hits / Math.max(attempts, 1);
      if (accuracy < 0.8) {
        onClose();  // Lose condition
      } else {
        onComplete(task.taskId);  // Win condition
      }
    }
  }, [timer, hits, attempts, onClose, onComplete]);

  const handleClick = (id, event) => {
    event.stopPropagation(); // Prevent event from bubbling up to the game area
    setAttempts(prev => prev + 1);
    setCircles(prev => prev.filter(circle => {
      if (circle.id === id) {
        setHits(prevHits => prevHits + 1);
        return false;
      }
      return true;
    }));
  };

  const handleMiss = () => {
    if (circles.length > 0) { // Only count misses if there are circles to click
      setAttempts(prev => prev + 1);
    }
  };

  return (
    <div className="game-area" onClick={handleMiss}>
      <div style={{ padding: '20px 0' }}>
        <h3>Click the Asteroids</h3>
      </div>
      <div className="timer">Time left: {timer}s</div>
      {circles.map(circle => (
        <button
          key={circle.id}
          className="circle"
          style={{
            top: `${circle.y}%`,
            left: `${circle.x}%`,
            width: `${circle.size}px`,
            height: `${circle.size}px`,
            transform: `translate(-50%, -50%)` // Center the circle based on its position
          }}
          onClick={(event) => handleClick(circle.id, event)}
        />
      ))}
      <div className="accuracy">
        Accuracy: {(hits / Math.max(attempts, 1) * 100).toFixed(2)}%
      </div>
    </div>
  );
}

export default RandomCircleTask;
