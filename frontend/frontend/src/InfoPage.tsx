import React, { useEffect } from 'react';
import './CSS/InfoPage.css';

const InfoPage = ({ closeInfo }) => {
    useEffect(() => {
        const handleKeyDown = (event) => {
            if (event.key === 'Escape') {
                closeInfo();
            }
        };

        document.addEventListener('keydown', handleKeyDown);
        return () => {
            document.removeEventListener('keydown', handleKeyDown);
        };
    }, [closeInfo]);

    return (
        <div className="info-page">
            <img
                src="https://www.pngall.com/wp-content/uploads/5/Delete-Red-X-Button-PNG-Image.png"
                alt="Close"
                className="close-info-button"
                onClick={closeInfo}
            />
            <div className="info-content">
                <h1>Game Information</h1>
                <p>When you start the game, you get assigned a role. You can see your role in the top left corner for Killers and top right corner for Crewmates.</p>
                <p>This is the MiniMap Icon. Click on it to view the whole map. You can see your current location, and as a Crewmate, you can also see where your tasks are located:</p>
                <img
                    src="https://media.forgecdn.net/avatars/314/557/637412791166512939.png"
                    alt="MiniMap Icon"
                    className="game-info-image"
                />
                <p></p>
                <p>In the middle of the room is the Voting Panel. Once you are close to it, press 'E' to activate the voting. When voting is initiated, all players are teleported to the main room, and the voting begins:</p>
                <img
                    src="https://i.imgur.com/e3gdhiF.png"  // Replace this with the actual image link when available
                    alt="Voting Panel Placeholder"
                    className="game-info-image"
                />
                
                <h2>Killer Role</h2>
                <p>As a Killer, your main objective is to discreetly eliminate crewmates without being caught. Approach a crewmate and activate your kill ability when the kill button turns red:</p>
                <img
                    src="https://i.imgur.com/sKkg1T8.png"
                    alt="Kill Button"
                    className="game-info-image"
                />
                <p>If you see such a Symbol this is called a Vent and it will show you the Symbol below to use it. 
                On the minimap, vents are represented as blue points. As an Impostor, you can use these vents to navigate the map quickly and discreetly.
                </p>
                <img
                    src="https://i.imgur.com/rqLwAni.png"
                    alt="Vent"
                    className="game-info-image"
                />
                <p>Utilize vents to navigate the map quickly and evade suspicion. Click on the symbol near a vent to transport to another connected vent:</p>
                <img
                    src="https://i.imgur.com/R9pwwP3.png"
                    alt="Vent"
                    className="game-info-image"
                />
                <p>Your stealth and cunning are key to achieving victory. Be strategic about when and where you strike to remain undetected and fulfill your mission.</p>

                <h2>Crewmate Role</h2>
                <p>As a Crewmate, your main objective is to complete tasks which are indicated in your top right corner along with their coordinates. Once near a task, the 'Use' button will appear allowing you to interact with it:</p>
                <img
                    src="https://i.imgur.com/LFcxEOq.png"
                    alt="Use Button"
                    className="game-info-image"
                />
                <p>Complete tasks to progress the task bar. Once a task is completed, it will disappear from your list.</p>
                <p>If you encounter a grave during your exploration, approach it and this button will appear:</p>
                <img
                    src="https://i.imgur.com/ryDkw9I.png"
                    alt="Report Button"
                    className="game-info-image"
                />
                <p>Click it to call a meeting where all players are teleported to the main group to discuss any suspicious activity or findings. This is crucial for identifying and eliminating any Killers among you.</p>
            </div>
        </div>
    );
};

export default InfoPage;
