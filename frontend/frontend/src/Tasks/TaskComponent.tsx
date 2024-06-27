import React from 'react';
import MultiButtonClickTask from './MultiButtonClickTask';
import RandomQuestionTask from './RandomQuestionTask';
import RandomCircleTask from './AimBot';
import DoomTask from './DoomTask';
import NoClickButtonTask from './NoClickButtonTask';

const TaskComponent = ({ task, handleTaskComplete, handleTaskFail }) => {
  console.log("", task.taskId);
  
  // Apply a custom class to each task based on the taskId
  const getClassForTask = (taskId) => {
    switch (taskId) {
      case '2':
        return 'multi-button-task';
      case '4':
        return 'random-question-task';
      case '3':
        return 'random-circle-task';
      case '5':
        return 'doom-task';
      case '6':
        return 'no-click-button-task';
      default:
        return '';
    }
  };

  return (
    <div className={getClassForTask(task.taskId)}>
      {task.taskId === '2' && <MultiButtonClickTask onComplete={handleTaskComplete} onClose={handleTaskFail} task={task} />}
      {task.taskId === '3' && <RandomCircleTask onComplete={handleTaskComplete} onClose={handleTaskFail} task={task} />}
      {task.taskId === '5' && <DoomTask onComplete={handleTaskComplete} onClose={handleTaskFail} task={task} />}
      {task.taskId === '6' && <NoClickButtonTask onComplete={handleTaskComplete} onClose={handleTaskFail} task={task} />}
    </div>
  );
};

export default TaskComponent;
