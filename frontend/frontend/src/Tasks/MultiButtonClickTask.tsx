import React, { useEffect, useState } from 'react';
import "./mutlitpleButton.css"

function MultiButtonClickTask({ task, onClose, onComplete }) {
  const [stage, setStage] = useState(0);
  const [buttons, setButtons] = useState(Array(9).fill(false));
  const [activeButtons, setActiveButtons] = useState(Array(9).fill(false));
  const [currentStage, setCurrentStage] = useState(0);
  const [amount, setAmount] = useState(0);
  const [timer, setTimer] = useState(10);

  // Utility to generate random indices
  const getRandomIndices = (count, total) => {
    const indices = Array.from({ length: total }, (_, index) => index);
    for (let i = indices.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [indices[i], indices[j]] = [indices[j], indices[i]]; // ES6 array destructuring swap
    }
    return indices.slice(0, count);
  };

  useEffect(() => {
    const timerId = setInterval(() => {
      setTimer(prev => {
        if (prev === 1) {
          clearInterval(timerId);
          onClose();
          return 0;
        }
        return prev - 1;
      });
    }, 500);

    return () => clearInterval(timerId);
  }, [stage]);

  useEffect(() => {
    setButtons(Array(9).fill(false));
    setAmount(0);
    setTimer(5);  // Reset timer with the start of a new stage

    if (stage < task.stages.length) {
      const newActiveButtons = Array(9).fill(false);
      const currentIndex = task.stages[stage];
      
      if (typeof currentIndex === 'number') {
        const randomIndices = getRandomIndices(currentIndex, 9);
        randomIndices.forEach(index => {
          newActiveButtons[index] = true;
        });
        setCurrentStage(currentIndex);
      } else {
        console.error('Invalid stage data:', currentIndex);
        onClose();
      }

      setActiveButtons(newActiveButtons);
    } else {
      onComplete(task.taskId);
    }
  }, [stage, task.stages, onClose]);

  const handleButtonClick = (index) => {
    if (activeButtons[index]) {
      const updatedButtons = [...buttons];
      updatedButtons[index] = true;
      setButtons(updatedButtons);
      const newAmount = amount + 1;
      setAmount(newAmount);

      if (newAmount === currentStage) {
        if (stage < task.stages.length - 1) {
          setStage(stage + 1);
        } else {
          onComplete(task.taskId);
        }
      }
    } else {
      onClose();
    }
  };

  return (
    <div className="multi-button-container">
      <div className="header">
        <h3>Button Task</h3>
        <div className="timer">Time left: {timer}s</div>
      </div>
      <div className="grid">
        {buttons.map((clicked, index) => (
          <button
            key={index}
            className={`button ${clicked ? 'disabled' : ''} ${activeButtons[index] ? 'active' : ''}`}
            disabled={clicked}
            onClick={() => handleButtonClick(index)}
          >
            {clicked ? 'Done' : 'Click'}
          </button>
        ))}
      </div>
    </div>
  );
}

export default MultiButtonClickTask;
