function TaskBar({ percentage }) {
    return (
      <div style={{
        height: '20px',
        width: '100%',  // To make it stretch across the top
        backgroundColor: 'grey',
        borderRadius: '5px',
        position: 'absolute',
        left: 0,  // Start from the far left
        top: 0,   // Position it at the very top
        zIndex: 1000  // Ensure it stays on top of other components
      }}>
        <div style={{
          height: '100%',
          width: `${percentage}%`,
          backgroundColor: percentage >= 100 ? 'green' : 'orange',
          borderRadius: '5px',
          textAlign: 'center',
          color: 'white',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}>
          {percentage}%
        </div>
      </div>
    );
  }
export default TaskBar;  