import React, { useEffect, useState } from 'react';
import './noClickButtonTask.css';

function NoClickButtonTask({ task, onClose, onComplete }) {
  const [timer, setTimer] = useState(10);
  const [isTaskFailed, setIsTaskFailed] = useState(false);

  useEffect(() => {
    const timerId = setInterval(() => {
      setTimer((prev) => {
        if (prev === 1) {
          clearInterval(timerId);
          if (!isTaskFailed) {
            onComplete(task.taskId);
          }
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timerId);
  }, [isTaskFailed, onComplete, task.taskId]);

  const handleButtonClick = () => {
    setIsTaskFailed(true);
    onClose();
  };

  return (
    <div className="no-click-button-task-container">
      <div className="button-base">
        <button className="big-red-button" onClick={handleButtonClick} />
      </div>
    </div>
  );
}

export default NoClickButtonTask;
