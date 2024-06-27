import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './CSS/Leaderboard.css';

const Leaderboard = ({ closeLeaderboard }) => {
    const [leaderboardData, setLeaderboardData] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchLeaderboardData = async () => {
            try {
                const response = await axios.get('http://10.0.40.161:8080/leaderboard');
                const data = response.data;

                // Transform the response object into an array of objects
                const transformedData = Object.keys(data).map(key => ({
                    name: key,
                    wins: data[key]
                }));

                setLeaderboardData(transformedData);
            } catch (error) {
                console.error('Error fetching leaderboard data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchLeaderboardData();

        // Event listener to close leaderboard on Escape key press
        const handleKeyDown = (event) => {
            if (event.key === 'Escape') {
                closeLeaderboard();
            }
        };

        window.addEventListener('keydown', handleKeyDown);

        // Cleanup the event listener on component unmount
        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, [closeLeaderboard]);

    if (loading) {
        return <div>Loading...</div>; // Optional: Add a loading state if needed
    }

    return (
        <div className="leaderboard-modal">
            <div className="leaderboard-content">
                <div className="leaderboard-title">Leaderboard</div>
                <div className="leaderboard-list">
                    {leaderboardData.map((entry, index) => (
                        <div key={index} className="leaderboard-item">
                            <span className="leaderboard-name">{entry.name}</span>
                            <span className="leaderboard-wins">{entry.wins}</span>
                        </div>
                    ))}
                </div>
                <button onClick={closeLeaderboard} className="modal-button">Close</button>
            </div>
        </div>
    );
};

export default Leaderboard;
