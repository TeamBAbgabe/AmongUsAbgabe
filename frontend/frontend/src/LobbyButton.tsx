import React, { useState, useEffect, useRef } from 'react';
import './CSS/LobbyButtons.css';
import SettingsModal from './settings';
import Leaderboard from './LeaderBoard';
import AccountOverlay from './AccountOverlay';
import InfoPage from './InfoPage';  // Import the InfoPage component

const LobbyButtons = ({ handleJoinLobby, handleCreateCustomLobby, handleJoinCustomLobby, close, userName }) => {
    const [showModal, setShowModal] = useState(false);
    const [lobbyId, setLobbyId] = useState('');
    const [showJoin, setShowJoin] = useState(false);
    const [isSettingsOpen, setIsSettingsOpen] = useState(false);
    const [playMusic, setPlayMusic] = useState(false);
    const [volume, setVolume] = useState(50);
    const [showLeaderboard, setShowLeaderboard] = useState(false);
    const [showInfoPage, setShowInfoPage] = useState(false);  // State for showing InfoPage
    const audioRef = useRef(null);

    useEffect(() => {
        if (audioRef.current) {
            audioRef.current.volume = volume / 100;
        }
    }, [volume]);

    useEffect(() => {
        const handleEscapeKey = (event) => {
            if (event.key === 'Escape') {
                setShowModal(false);
                setShowJoin(false);
                setIsSettingsOpen(false);
                setShowLeaderboard(false);
                setShowInfoPage(false);
            }
        };

        document.addEventListener('keydown', handleEscapeKey);

        return () => {
            document.removeEventListener('keydown', handleEscapeKey);
        };
    }, []);

    const toggleSettings = () => {
        setIsSettingsOpen(!isSettingsOpen);
    };

    const handleLeaderboardClick = () => {
        setShowLeaderboard(true);
    };

    const closeLeaderboard = () => {
        setShowLeaderboard(false);
    };

    const openInfoPage = () => {
        setShowInfoPage(true);
    };

    const closeInfoPage = () => {
        setShowInfoPage(false);
    };

    return (
        <div className="lobby-container">
            {playMusic && (
                <audio ref={audioRef} loop autoPlay>
                    <source src='./src/assets/Mick Gordon - Meathook (DOOM Eternal - Gamerip) [REUPLOAD].mp3' />
                    Your browser does not support the audio tag.
                </audio>
            )}

            <AccountOverlay userName={userName} />

            <div className="lobby-buttons-container">
                <button onClick={handleJoinLobby} className="lobby-button">Join Lobby</button>
                <button onClick={() => setShowModal(true)} className="lobby-button">Create Custom Lobby</button>
                <button onClick={() => setShowJoin(true)} className="lobby-button">Join Custom Lobby</button>
                <button onClick={handleLeaderboardClick} className="lobby-button leaderboard-button">Leaderboard</button>
                <img src='https://www.pngall.com/wp-content/uploads/5/Delete-Red-X-Button-PNG-Image.png' alt="Close" className="close-icon-main" onClick={close} />

                {showModal && (
                    <div className="modal">
                        <div className="modal-content">
                            <div className="modal-title">Create your Lobby</div>
                            <div className="input-group">
                                <label htmlFor="lobbyId">LobbyId:</label>
                                <input type="text" id="lobbyId" value={lobbyId} onChange={(e) => setLobbyId(e.target.value)} />
                            </div>
                            <div>
                                <button onClick={() => { handleCreateCustomLobby(lobbyId); setShowModal(false); setLobbyId(''); }} className="modal-button">Enter</button>
                                <button onClick={() => { setShowModal(false); setLobbyId(""); }} className="modal-button">Close</button>
                            </div>
                        </div>
                    </div>
                )}
                {showJoin && (
                    <div className="modal">
                        <div className="modal-content">
                            <div className="input-group">
                                <label htmlFor="lobbyId">LobbyId:</label>
                                <input type="text" id="lobbyId" value={lobbyId} onChange={(e) => setLobbyId(e.target.value)} />
                            </div>
                            <div>
                                <button onClick={() => { handleJoinCustomLobby(lobbyId); setShowJoin(false); setLobbyId(''); }} className="modal-button">Enter</button>
                                <button onClick={() => { setShowJoin(false); setLobbyId(""); }} className="modal-button">Close</button>
                            </div>
                        </div>
                    </div>
                )}
                {showLeaderboard && (
                    <Leaderboard closeLeaderboard={closeLeaderboard} />
                )}
                <img
                    src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGCzw-X1Jp-ngMahgT5bf9eyA3HbvPc_0X_A&s" 
                    alt="Settings"
                    className="settings-icon"
                    onClick={toggleSettings} 
                />
                {isSettingsOpen && (
                    <SettingsModal
                        toggleSettings={toggleSettings}
                        playMusic={playMusic}
                        setPlayMusic={setPlayMusic}
                        volume={volume}
                        setVolume={setVolume}
                    />
                )}
            </div>

            <img 
                src='https://i.imgur.com/byuSLvx.png' 
                alt="Information Icon" 
                className="info-icon"
                onClick={openInfoPage} 
            />

            {showInfoPage && <InfoPage closeInfo={closeInfoPage} />}
        </div>
    );
};

export default LobbyButtons;
