import "./CSS/SettingsModal.css";

const SettingsModal = ({ toggleSettings, playMusic, setPlayMusic, volume, setVolume }) => {
    return (
        <div className="overlay">
            <div className="settings-modal">
                <div className="settings-interior">
                    <h2>Settings</h2>
                    <div className="settings-item">
                        <div className="label-and-control">
                            <div className="music-label">
                                <label htmlFor="musicToggle">Music</label>
                            </div>
                            <div className="checkbox-container">
                                <input
                                    type="checkbox"
                                    id="musicToggle"
                                    checked={playMusic}
                                    onChange={() => setPlayMusic(!playMusic)}
                                />
                            </div>
                        </div>
                    </div>
                    <div className="settings-item">
                        <div className="label-and-control">
                            <label htmlFor="volumeControl" className="volume-label">Volume:</label>
                            <input
                                type="range"
                                id="volumeControl"
                                className="settings-slider"
                                min="0"
                                max="100"
                                value={volume}
                                onChange={(e) => setVolume(e.target.value)}
                            />
                        </div>
                    </div>
                    <button onClick={toggleSettings} className="settings-close-btn">Close</button>
                </div>
            </div>
        </div>
    );
};

export default SettingsModal;
