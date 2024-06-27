import React from 'react';
import './CSS/gameEnd.css'; // Ensure the path is correct

function GameEnd({ gameEndCondition, repeat, close }) {
    console.log(gameEndCondition); // Debugging log

    let content;
    if (gameEndCondition.includes("Defeat: Imposters have taken ")) {
        content = {
            gif: "https://qph.cf2.quoracdn.net/main-qimg-83bd55bd1ed64d9c0398d5b7b45fada7",
            message: gameEndCondition
        };
        
    } 
    else if (gameEndCondition.includes("Tasks")) {
        content = {
            gif: "https://i.gifer.com/MEH1.gif",
            message: gameEndCondition
        };
    }
    else if (gameEndCondition.includes("tasks")) {
        content = {
            gif: "https://i.gifer.com/MEH1.gif",
            message: gameEndCondition
        };
    }
    else if (gameEndCondition.includes("Victory: You have successfully")) {
        content = {
            gif: "https://64.media.tumblr.com/ff96154f2fe5fe44d1d112908574a432/tumblr_psp0e8iI2A1u9werno1_540.gif",
            message: gameEndCondition
        };
    } 
    else if (gameEndCondition.includes("Victory: All imposters ")) {
        content = {
            gif: "https://j.gifs.com/mG25aZ.gif",
            message: gameEndCondition
        };
    }     
    else {
        content = {
            gif: null,
            message: "Game Ended"
        };
    }

    return (
        <div className="gameEndOverlay">
            {content.gif && (
                <div className="gameEndGifContainer">
                    <img src={content.gif} alt={content.message} className="gameEndGif" />
                    <div className="gameEndMessage">
                        <h1>{content.message}</h1>
                    </div>
                    <div className="svgContainer" onClick={repeat}>
                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-repeat">
                            <polyline points="17 1 21 5 17 9"></polyline>
                            <path d="M3 11V9a4 4 0 0 1 4-4h14"></path>
                            <polyline points="7 23 3 19 7 15"></polyline>
                            <path d="M21 13v2a4 4 0 0 1-4 4H3"></path>
                        </svg>
                        <div className="repeatLabel">Repeat the game</div>
                    </div>
                    <div className="quitButton" onClick={close}>
                        <svg className="quitIcon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                        </svg>
                        <span className="quitLabel">Quit</span>
                    </div>
                </div>
            )}
        </div>
    );
}

export default GameEnd;
