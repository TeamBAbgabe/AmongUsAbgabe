import "./CSS/notification.css";

const Notification = ({ playerName, role }) => {
    console.log(playerName); // Debugging log
    return (
      <div className="notification">
        <p>{playerName} was removed from the game.</p>
        <p>Role: {role}</p>
      </div>
    );
};

export default Notification;