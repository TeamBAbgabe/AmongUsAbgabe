import React, { Suspense, useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './Login';
import Register from './Register';
import Movement from './movement';
import GameContainer from './GameComponent';
import Voting from './voting';
import Lobby from './lobby'; 
import axios from 'axios';
import "./CSS/player.css"
import LobbyButtons from './LobbyButton';
import GameEnd from './win';
import TaskComponent from './Tasks/TaskComponent';
import './CSS/style.css'; 
import TaskBar from './Tasks/Taskbar';
import TaskList from './TaskList';
import Modal from './Modal';
import Crewmate from './Roles/Crewmate';
import Imposter from './Roles/Imposter';
import Notification from './Notification';
import FullScreenVideo from './FullVideo';
import CoordsDisplay from './Coords';
//import BigMap from './oldBigmap';

const BigMap = React.lazy(() => import('./BigMap'));
const WS_URL = 'ws://10.0.40.161:8080/gs-guide-backendAmongUs'; 

function App() {
  const [videoEnded, setVideoEnded] = useState(false);

  const [clearGravesTrigger, setClearGravesTrigger] = useState(false);
  const [showGameUI, setShowGameUI] = useState(false); 
  const [isLoginSuccess, setIsLoginSuccess] = useState(false);
  const [connected, setConnected] = useState(false);
  var [players, setPlayers] = useState([]); 
  const clientRef = useRef(null);
  const [blockMovement, setBlockMovement] = useState(false);
  const[votingData, setVotingData] = useState({})
  const handleLoginSuccess = () => setIsLoginSuccess(true);
  const [mysessionID, setmysessionID] = useState(null);
  const [removablePlayerID, setremovablePlayerID] = useState(null);
  const [showGame, setShowGame] = useState(false); 
  const [lobbyUsers, setLobbyUsers] = useState([])
  const [username, setUsername] = useState('');
  const [chatMessages, setChatMessages] = useState([]);
  const [picture, setPicture] = useState("");
  const [myRole, setMyRole] = useState("");
  const [gameEndCondition, setGameEndCondition] = useState(null);
  const [lobbyShow, setLobbyShow] = useState(false);
  const [lobbyId, setLobbyId] = useState('');
  const [isInLobby, setIsInLobby] = useState(false); 
  const [showCreateLobbyForm, setShowCreateLobbyForm] = useState(false);
  const [LobbyCreator, setLobbyCreator] = useState(null);
  const [currentTask, setCurrentTask] = useState(null);
  const [taskPercentage, setTaskPercentage] = useState(0);
  const [tunnelButton, setTunnelButton] = useState("");
  const [task, setTasks] = useState([]);
  const [isErrorModalOpen, setIsErrorModalOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [doTask, setDoTask] = useState("");
  const [voting, setVoting] = useState(false);
  const [showVideo, setShowVideo] = useState(true); 
  const [getImposterAmount, setImposterAmount] = useState(0);
  const [lobbyType, setLobbyType] = useState("");
  const [showBigMinimap, setShowBigMinimap] = useState(false); 
  const [playerPosition, setPlayerPosition] = useState({ x: 0, y: 0 });
  const [realSessionId, setRealSessionId] = useState(null);
  const [deathPlayer , setdeathPlayer] = useState(null);
  const [showNotification, setShowNotification] = useState(false);
  const [playerNotification, setPlayerNotification] = useState(null);
  const [reportDeadPlayer, setReportDeadPlayer] = useState("");
  const [coords, setCoords] = useState({ x: null, y: null });
  const [allCoords, setAllCoords] = useState([]); 
  const [isGameUILoaded, setIsGameUILoaded] = useState(false);


  useEffect(() => {
    if (isLoginSuccess) {
      const client = new Client({
        brokerURL: WS_URL,
        reconnectDelay: 5000,
        debug: console.log,
      });
      
      client.onConnect = (frame) => {
        console.log('Connected to WebSocket server');
        setConnected(true);
        const sessionId = frame.headers['user-name']; 
        console.log(frame.headers)

        console.log('Session ID:', sessionId);
        
        setmysessionID(sessionId);
        console.log(mysessionID)

        setRealSessionId(() => {
          console.log('Setting realSessionId to:', sessionId);
          return sessionId;
        });
        function returnReal(){
          console.log('Setting realSessionId to:', sessionId);
          return sessionId;
        }

        client.subscribe('/user/queue/task', (message) => {
          try {
            setBlockMovement(true);
            const taskData = JSON.parse(message.body);
            console.log("Task received: ", taskData);
            setCurrentTask(taskData);
          } catch (error) {
            console.error('Failed to parse task data:', error);
          }
        });

        client.subscribe('/user/queue/ImposterCoords', (message) => {
          try {
            const newCoords = JSON.parse(message.body);
            console.log("this should be my Imposter coords", message.body);
            
            if (typeof newCoords === 'object' && !Array.isArray(newCoords)) {
              const coordsArray = Object.values(newCoords); 
              setAllCoords(coordsArray); 
            } else {
              console.error('Parsed data is not an object:', newCoords);
            }
          } catch (error) {
            console.error('Failed to parse task data:', error);
          }
        });
        

        client.subscribe('/user/queue/Coords', (message) => {
          try {
            const newCoords  = JSON.parse(message.body);
            console.log("this should be my coords", coords);
            setCoords(prevCoords => {
              if (prevCoords.x !== newCoords.x || prevCoords.y !== newCoords.y) {
                return newCoords;
              }
              return prevCoords;
            });
          } catch (error) {
            console.error('Failed to parse task data:', error);
          }
        });

        client.subscribe('/user/queue/taskButton', (message) => {
          try {
            setDoTask(message.body);
          } catch (error) {
            console.error('Failed to parse task data:', error);
          }
        });

        client.subscribe('/user/queue/taskList', (message) => {
          try {
            const taskData = JSON.parse(message.body);
            console.log("Task received: ", taskData);
            setTasks(taskData)
          } catch (error) {
            console.error('Failed to parse task data:', error);
          }
        });

        client.subscribe('/user/queue/tunnel', (message) => {
          try {
            const imageUrl = message.body;  
            console.log("Image URL received: ", imageUrl);
            setTunnelButton(imageUrl);
          } catch (error) {
            console.error('Failed to handle image URL:', error);
          }
        });
        
        client.subscribe('/user/queue/reportDeadPlayer', (message) => {
          try {
            const imageUrl = message.body;  
            console.log("Image URL received: ", imageUrl);
            setReportDeadPlayer(imageUrl);
          } catch (error) {
            console.error('Failed to handle image URL:', error);
          }
        });

        client.subscribe('/user/queue/taskBar', (message) => {
          try {
            const taskData = JSON.parse(message.body);
            setTaskPercentage(taskData);
            console.log("TaskBar received: ", taskData);
            setCurrentTask(taskData);
          } catch (error) {
            console.error('Failed to parse task data:', error);
          }
        });

        
        client.subscribe('/user/queue/joinCustomLobby', (message) => {
          console.log('Received message on /topic/lobby:', message.body);
          try {
              const lobbyUsers = JSON.parse(message.body);
              if (lobbyUsers.message === "LobbyId doesnt exist" || lobbyUsers.message === "This is a public LobbyID") {
                setErrorMessage(lobbyUsers.message)
                setIsErrorModalOpen(true);
              } else {
                console.log('Lobby update received:', lobbyUsers);
                const data = lobbyUsers.Object
                setLobbyUsers(lobbyUsers);
                setLobbyId(lobbyUsers[0].lobbyId)
                console.log("the logger is ", lobbyUsers[0].lobbyId)
                setLobbyShow(true);
            }
          } catch (error) {
              console.error('Failed to parse lobby data:', error);
          }
      });


      client.subscribe('/user/queue/Lobby', (message) => {
        console.log('Received message on /topic/lobby:', message.body);
        try {
            const lobbyUsers = JSON.parse(message.body);
            console.log('Lobby update received:', lobbyUsers);
            setLobbyUsers(lobbyUsers);
            setLobbyId(lobbyUsers[0].lobbyId)
            console.log("the logger is ", lobbyUsers[0].lobbyId)
            setLobbyShow(true);
        } catch (error) {
            console.error('Failed to parse lobby data:', error);
        }
    });

      client.subscribe('/user/queue/startGame', (message) => {
        try {
          console.log("My role is" , message.body)
          setClearGravesTrigger(true); 
          setMyRole(message.body)
          setShowGame(true);
          setShowVideo(true);
          setBlockMovement(true);
          MyUserDisplay();
        } catch (error) {
          console.error('Failed to parse lobby data:', error);
        }
      });

      client.subscribe('/user/queue/customLobby', (message) => {
        console.log('Received message on /topic/lobby:', message.body);
        try {
            const lobbyUsers = JSON.parse(message.body);
    
            if (lobbyUsers.message === "Lobby exists already") {
              setErrorMessage(lobbyUsers.message)
                setIsErrorModalOpen(true);
            } else {
                console.log('Lobby update received:', lobbyUsers);
                if (Array.isArray(lobbyUsers) && lobbyUsers.length > 0 && lobbyUsers[0].lobbyId) {
                    setLobbyUsers(lobbyUsers);
                    setLobbyId(lobbyUsers[0].lobbyId);
                    console.log("the logger is ", lobbyUsers[0].lobbyId);
                    setLobbyShow(true);
                } else {
                    console.error("Received data is not in the expected format or missing lobbyId");
                }
            }
        } catch (error) {
            console.error('Failed to parse lobby data:', error);
        }
    });
    
        
        client.subscribe('/user/queue/Endgame', (message) => {
          try {
            setShowBigMinimap(false);
            setCurrentTask(null);
            setGameEndCondition(message.body)
            console.log(message.body)
          } catch (error) {
            console.error('Failed to parse message data:', error);
          }
        });

        client.subscribe('/user/queue/Death', (message) => {
          try {
            const death = JSON.parse(message.body);
            console.log('The Player which is death has this id:', death);
            setdeathPlayer(death);
          } catch (error) {
            console.error('Failed to parse message data:', error);
          }
        });

        client.subscribe('/user/queue/Chat', (message) => {
          try {
            const newMessage = JSON.parse(message.body);
            setChatMessages(prevMessages => [...prevMessages, newMessage]);
          } catch (error) {
            console.error('Failed to parse message data:', error);
          }
        });

        client.subscribe('/user/queue/removePlayer', (message) => {
          try {
            const data = JSON.parse(message.body);
            setremovablePlayerID(data);
            console.log("my data is ", data)
            console.log("my username is " , data.username)
            const removablePlayer = data.kickedSessionID;
            console.log('The Player which is getting removed has this id:', removablePlayer);
            setClearGravesTrigger(prevState => !prevState);

            if(data.username !== undefined) {
              setTimeout(() => {
                setPlayerNotification({
                  playerName: data.username,
                  role: data.role
                });
                setShowNotification(true);

              setTimeout(() => {
                setShowNotification(false);
              }, 5000);
            }, 2000);
          } 

          } catch (error) {
            console.error('Failed to parse full update data:', error);
          }
        });

        client.subscribe('/user/queue/killButton', (message) => {
          try {
            console.log("this should be my picture ",message.body)
            setPicture(message.body)
          } catch (error) {
            console.error('Failed to parse full update data:', error);
          }
        });

        client.subscribe('/user/queue/voting', (message) => {
          try {
            const voting = JSON.parse(message.body);
            console.log("Voting data received: ", voting);
            clearChat(); 
            handleVotingStart(voting);
            setDoTask("");
            console.log("Voting has started.");
          } catch (error) {
            console.error('Failed to parse voting data:', error);
          }
        });
        

        client.subscribe('/user/queue/fullUpdate', (message) => {
          try {
            const updates = JSON.parse(message.body);
            console.log("my users are the following", updates)

            updates.forEach((update) => {
              if (update.sessionId === returnReal()) {
                setPlayerPosition(update);
              }
            });
        
            setPlayers(updates); 
  
          } catch (error) {
            console.error('Failed to parse map data:', error);
          }
        });

        client.subscribe('/user/queue/map', (message) => {
          console.log("the users: ", players);
          try {

              const update = JSON.parse(message.body);
              setPlayers(() => {
                console.log('Clearing players array');
                return []; 
               });

              setPlayers(prevPlayers => {
                if(update.sessionId === returnReal()){
                  setPlayerPosition(update)
                }
                console.log("prev player", update)
                  return [...prevPlayers, update];
              });


              console.log("my players ", update)
          } catch (error) {
              console.error('Failed to parse map data:', error);
          }
      });
      
      };

      client.activate();
      clientRef.current = client;

      return () => {
        client.deactivate();
      };
    }
  }, [isLoginSuccess]);

  const handleClick = (key) => {
    if (clientRef.current && connected) {
      clientRef.current.publish({
        destination: '/app/move',
        body: JSON.stringify({ input: key }),
      });
    }
  };
  useEffect(() => {

}, [showGame]); 

const clearChat = () => {
  setChatMessages([]);
};

const MyUserDisplay = () => {
  const user = lobbyUsers.find(user => user.sessionId === mysessionID);
  if(!user){
    return null;
  }

  return (
    <div className="fixedContainer">
      <div className="flexContainer">
        <img
          src={user.avatarUrl}
          className="userAvatar"
        />
        <p style={{color: user.color}} 
        className="userName">
          {user.userId}
        </p>
        <p className="userRole" style={{ color: user.isImposter ? 'blue' : 'red' }}>
          {myRole}
        </p>
      </div>
    </div>
  );
};

const handleTaskComplete = (taskID) => {
  setBlockMovement(false);
  if (clientRef.current && connected) {
    clientRef.current.publish({
      destination: '/app/checkTask',
      body: JSON.stringify({ taskId: taskID }),
    });
  }
  console.log("Task successfully completed!", taskID);

  setTasks(prevTasks => prevTasks.map(task => 
    task.taskId === taskID ? { ...task, completed: true } : task
  ));
  setTasks(prevTasks => prevTasks.filter(task => task.taskId !== taskID)); 
  setCurrentTask(null);
};



const handleTaskFail = () => {
  setBlockMovement(false);
  console.log("Task failed. Closing task.");
  setCurrentTask(null); 
};


const handleJoinLobby = () => {
  setLobbyType("Public");
  setShowCreateLobbyForm(false);
  setLobbyCreator(0);  
  setIsInLobby(true);
  clientRef.current.publish({
    destination: '/app/lobby',
    body: JSON.stringify({ userName: username}), 
  });
};

const handleCreateCustomLobby = (lobbyId) => {
  setLobbyType("Custom");
  setShowCreateLobbyForm(true);
  setIsInLobby(true);
  setLobbyCreator(mysessionID);  
  clientRef.current.publish({
    destination: '/app/create',
    body: JSON.stringify({ userName: username, lobbyId: lobbyId }),
  });
  console.log('Create Custom Lobby clicked');
};


const handleJoinCustomLobby = (lobbyId) => {
  setShowCreateLobbyForm(false);
  setIsInLobby(true);
  setLobbyCreator(null);  
  clientRef.current.publish({
    destination: '/app/join',
    body: JSON.stringify({ userName: username, lobbyId: lobbyId }), 
  });
  console.log('Join Custom Lobby clicked');
};

const handleStartClick = async (imposterCount) => {
  try {
    const data = { lobbyId: lobbyId, imposterAmount: imposterCount, lobbyType: lobbyType};
    setShowVideo(true); 
    setShowGameUI(false); 
    console.log("my data" , data)
    await axios.post('http://10.0.40.161:8080/gameStart', data);
     } catch (error) {
    console.error('Error starting game:', error);
  }
};

function reset() {
  setMyRole("");
  setGameEndCondition(false);
  setShowGame(false)
  setLobbyShow(false);
  setLobbyUsers([])
  setPlayers([])
  setTasks([])
  setVoting(false)
  setVotingData({})
  setCurrentTask(null);
  setTaskPercentage(0);
  setremovablePlayerID(null);
  setdeathPlayer(null);
}

const killButton = async () => {
  try {
    console.log("Kill button")
    console.log(mysessionID )
    const data = { killerId: mysessionID};
    await axios.post('http://10.0.40.161:8080/kill', data);
     } catch (error) {
    console.error('Error starting game:', error);
  }
};

const repeat = () => {
  clientRef.current.publish({
    destination: '/app/lobby',
    body: JSON.stringify({ userName: username }),
  });
  setIsLoginSuccess(true);
  reset();
}



const close = () => {
  if (clientRef.current && clientRef.current.active) {
    clientRef.current.deactivate().then(() => {
      console.log('Disconnected from WebSocket server on close');
    }).catch(error => console.error('Disconnection error:', error));
  }
  setIsLoginSuccess(false);
  reset();
}

const backToLobbys = () => {
  clientRef.current.publish({
    destination: '/app/back',
  });
  setIsLoginSuccess(true);
  reset();
}

const reportDeadBody = () => {
  clientRef.current.publish({
    destination: '/app/reportDeadPlayer',
  });
}

const taskHandle = () => {
  if (clientRef.current && connected) {
    clientRef.current.publish({
      destination: '/app/taskInitialize'
    });
  }
};

const tunnel = () => {
  if (clientRef.current && connected) {
    clientRef.current.publish({
      destination: '/app/move',
      body: JSON.stringify({ input: "f" }),
    });
  }
};

const handleVideoEnd = () => {
  setBlockMovement(false);
  setShowVideo(false); 
};

useEffect(() => {
  if (showGame) {
    setShowVideo(true);
    setShowGameUI(false);
    setBlockMovement(true);
    const timeoutId = setTimeout(() => {
      setShowVideo(false);
      setShowGameUI(true);
      setVideoEnded(true);
      setBlockMovement(false);
    }, 2850);

    return () => {
      clearTimeout(timeoutId);
      setBlockMovement(false);
    };
  }
}, [showGame]);


const changeCharacter = (char) => {
  clientRef.current.publish({
    destination: '/app/changeCharacter',
    body: JSON.stringify({ character: char, lobbyId: lobbyId }),
  });
}

const handleVotingStart = (votingData) => {
  setVoting(true);
  setCurrentTask(null); 
  setBlockMovement(true); 
  setVotingData(votingData); 
};



const toggleBigMinimap = () => {
  setShowBigMinimap(!showBigMinimap);
};

<button onClick={handleStartClick}>Start Game</button>
return (
  <Router>
    <Routes>
      {isLoginSuccess ? (
        <>
          <Route path="/voidspace" element={
            <div style={{ position: 'relative', height: '100vh' }}>
              {showNotification && (
                <Notification playerName={playerNotification.playerName} role={playerNotification.role} />
              )}
              {showGame ? (
                <div style={{ position: 'relative', width: '100%', height: '100%' }}>
                  {showVideo && <FullScreenVideo myrole={myRole} />}
                  {showGameUI && (
                    <>
                      <TaskBar percentage={taskPercentage} />
                      {gameEndCondition && <GameEnd close={close} repeat={repeat} gameEndCondition={gameEndCondition} />}
                      <MyUserDisplay />
                      {myRole === "Crewmate" && <TaskList tasks={task} />}
                      {currentTask && <TaskComponent handleTaskFail={handleTaskFail} handleTaskComplete={handleTaskComplete} task={currentTask} />}
                      <Voting chatMessages={chatMessages} mysessionID={mysessionID} showVoting={voting} closeVoting={() => { setVoting(false); setBlockMovement(false); }} votingData={votingData} />
                      <GameContainer killpicture={picture} triggerClearGraves={clearGravesTrigger} deathPlayer={deathPlayer} playerData={players} setPlayerData={setPlayers} removablePlayerID={removablePlayerID} mysessionID={mysessionID} />
                      <Movement isMinimapOpen={showBigMinimap} taskHandle={taskHandle} close={close} blockMovement={blockMovement} handleClick={handleClick} />
                      <div style={{ position: 'relative', width: '100%', height: '100%' }}>
                        {myRole === "Crewmate" && <Crewmate taskHandle={taskHandle} picture={doTask} reportPlayer={reportDeadPlayer} reportDeadBody={reportDeadBody} />}
                        {myRole === "Imposter" && <Imposter killButton={killButton} tunnel={tunnel} picture={picture} tunnelButton={tunnelButton} />}
                      </div>
                      <img
                        src="https://media.forgecdn.net/avatars/314/557/637412791166512939.png"
                        alt="Minimap Icon"
                        onClick={toggleBigMinimap}
                        style={{ position: 'absolute', bottom: '50px', right: '10px', width: '100px', height: 'auto', borderRadius: '50%', cursor: 'pointer' }}
                      />
                      <Suspense fallback={<div>Loading...</div>}>
                        <BigMap 
                          role={myRole}
                          allCoords={allCoords}
                          tasks={task} 
                          show={showBigMinimap} 
                          onClose={toggleBigMinimap} 
                          playerPosition={playerPosition}
                        />
                      </Suspense>
                      <CoordsDisplay coords={coords} />
                    </>
                  )}
                </div>
              ) : (
                <div>
                  {lobbyShow ? (
                    <Lobby
                      clientRef={clientRef}
                      lobbyUsers={lobbyUsers}
                      onStartClick={handleStartClick}
                      close={backToLobbys}
                      move={LobbyCreator}
                      showCreateLobbyForm={showCreateLobbyForm}
                      changeCharacter={changeCharacter}
                    />
                  ) : (
                    <LobbyButtons
                      close={close}
                      handleJoinLobby={handleJoinLobby}
                      handleCreateCustomLobby={handleCreateCustomLobby}
                      handleJoinCustomLobby={handleJoinCustomLobby}
                      userName={username}
                    />
                  )}
                </div>
              )}
              <Modal messageError={errorMessage} isOpen={isErrorModalOpen} onClose={() => setIsErrorModalOpen(false)} />
            </div>
          } />
          <Route path="*" element={<Navigate replace to="/voidspace" />} />
        </>
      ) : (
        <>
          <Route path="/login" element={<Login onLoginSuccess={handleLoginSuccess} setUsernameParent={setUsername} />} />
          <Route path="/register" element={<Register />} />
          <Route path="*" element={<Navigate replace to="/login" />} />
        </>
      )}
    </Routes>
  </Router>
);

}


export default App;
