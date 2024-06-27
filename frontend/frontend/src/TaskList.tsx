import './CSS/taskList.css'; // Adjust the path as necessary


const TaskList = ({ tasks }) => {
    return (
      <div className="task-list">
        <h3>Tasks</h3>
        {tasks.map(task => (
          <div key={task.taskId} style={{ color: task.completed ? 'green' : 'inherit' }}>
            <p>{task.description} - Coords: ({task.coordinates.x}, {task.coordinates.y})</p>
          </div>
        ))}
      </div>
    );
  };
  

  export default TaskList;