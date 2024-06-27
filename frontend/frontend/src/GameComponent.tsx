import React, { useEffect, useRef, useState } from 'react';
import Phaser from 'phaser';
import Player from './Player';

function GameContainer({ playerData, setPlayerData, removablePlayerID, mysessionID, deathPlayer, triggerClearGraves, killpicture }) {
  const gameRef = useRef(null);
  const playersRef = useRef({});
  const [gameReady, setGameReady] = useState(false);

  useEffect(() => {
    const config = {
      type: Phaser.AUTO,
      parent: 'game-container',
      width: window.innerWidth,
      height: window.innerHeight,
      physics: {
        default: 'arcade',
        arcade: {
          gravity: { x: 0, y: 0 },
          debug: false,
        },
      },
      scene: {
        preload: preload,
        create: create,
        update: update,
      },
      fps: {
        target: 400,
        forceSetTimeOut: true,
      },
    };

    gameRef.current = new Phaser.Game(config);

    function preload() {
      this.load.image('tiles', 'src/assets/tileset x1.png');
      this.load.image('grave', 'src/picture/grave/grave.jpg');
      this.load.image('walk0', 'src/picture/r3/hr1-removebg-preview.png');
      this.load.image('walk1', 'src/picture/r3/hr2-removebg-preview.png');
      this.load.image('walk2', 'src/picture/r3/hr3-removebg-preview.png');
      this.load.image('walk3', 'src/picture/r3/hr4-removebg-preview.png');

      this.load.image('Mwalk0', 'src/picture/r3/hr1-mirrored.png');
      this.load.image('Mwalk1', 'src/picture/r3/hr2-mirrored.png');
      this.load.image('Mwalk2', 'src/picture/r3/hr3-mirrored.png');
      this.load.image('Mwalk3', 'src/picture/r3/hr4-mirrored.png');

      this.load.image('walkF1', 'src/picture/g3/hg1-removebg-preview.png');
      this.load.image('walkF2', 'src/picture/g3/hg2-removebg-preview.png');
      this.load.image('walkF3', 'src/picture/g3/hg3-removebg-preview.png');
      this.load.image('walkF4', 'src/picture/g3/hg4-removebg-preview.png');

      this.load.image('MwalkF1', 'src/picture/g3/hg1-mirrored.png');
      this.load.image('MwalkF2', 'src/picture/g3/hg2-mirrored.png');
      this.load.image('MwalkF3', 'src/picture/g3/hg3-mirrored.png');
      this.load.image('MwalkF4', 'src/picture/g3/hg4-mirrored.png');

      /*
      this.load.image('walkG1', 'src/picture/grey3/hgg1-removebg-preview.png');
      this.load.image('walkG2', 'src/picture/grey3/hgg2-removebg-preview.png');
      this.load.image('walkG3', 'src/picture/grey3/hgg3-removebg-preview.png');
      this.load.image('walkG4', 'src/picture/grey3/hgg4-removebg-preview.png');*/
      this.load.image('tiles1', 'src/assets/tileset x2.png');
      this.load.image('tiles3', 'src/assets/tileset x3.png');
      this.load.image('tiles32', 'src/assets/props and items x3.png');
      this.load.tilemapTiledJSON('map', 'src/assets/updatedMap.json');
    }
    function create() {
      const map = this.make.tilemap({ key: 'map' });
      const tileset = map.addTilesetImage('tileset x1', 'tiles');
      const tileset2 = map.addTilesetImage('tileset x3', 'tiles3');
      const props = map.addTilesetImage('props and items x3', 'tiles32');
      const props2 = map.addTilesetImage('props and items x3', 'tiles32');

      map.createLayer('Kachelebene 1', tileset, 0, 0);
      map.createLayer('Kachelebene 2', tileset2, 0, 0);
      map.createLayer('Kachelebene 2', props, 0, 0);
      map.createLayer('Kachelebene 3', props2, 0, 0);



      this.anims.create({
        key: 'runningM1',
        frames: [
          { key: 'walk0' },
          { key: 'walk1' },
          { key: 'walk2' },
          { key: 'walk3' },
        ],
        frameRate: 9,
        repeat: 0,
      });

      this.anims.create({
        key: 'MrunningM1',
        frames: [
          { key: 'Mwalk0' },
          { key: 'Mwalk1' },
          { key: 'Mwalk2' },
          { key: 'Mwalk3' },
        ],
        frameRate: 9,
        repeat: 0,
      });

      this.anims.create({
        key: 'running2F2',
        frames: [
          { key: 'walkF1' },
          { key: 'walkF2' },
          { key: 'walkF3' },
          { key: 'walkF4' },
        ],
        frameRate: 9,
        repeat: 0,
      });

      this.anims.create({
        key: 'Mrunning2F2',
        frames: [
          { key: 'MwalkF1' },
          { key: 'MwalkF2' },
          { key: 'MwalkF3' },
          { key: 'MwalkF4' },
        ],
        frameRate: 9,
        repeat: 0,
      });

      this.cameras.main.setZoom(3);
      this.physics.world.setBounds(0, 0, map.widthInPixels, map.heightInPixels);

      setGameReady(true);
    }

    function update() {}

    return () => {
      if (gameRef.current) {
        gameRef.current.destroy(true);
      }
    };
  }, []);

  useEffect(() => {
    if (gameReady && gameRef.current && playerData) {
      const scene = gameRef.current.scene.scenes[0];
      playerData.forEach((player) => {
        const { sessionId, x, y, username, color, characterType, Role } = player;
        if (!playersRef.current[sessionId]) {
          playersRef.current[sessionId] = new Player(scene, sessionId, x, y, username, color, characterType, Role);
          scene.add.existing(playersRef.current[sessionId].sprite);
          if (sessionId === mysessionID) {
            scene.cameras.main.startFollow(playersRef.current[sessionId].sprite, true);
          }
        } else {
          playersRef.current[sessionId].updatePosition(x, y);
        }
      });
    }
  }, [gameReady, playerData, mysessionID]);

  useEffect(() => {
    if (deathPlayer != null && gameReady && gameRef.current) {
      const { x, y, sessionId, username, color } = deathPlayer;
      Player.createGrave(gameRef.current.scene.scenes[0], x, y, username, color);
      removePlayer(sessionId);
    }
  }, [deathPlayer]);



  useEffect(() => {
    if (gameReady && removablePlayerID) {
      console.log("gimme the", removablePlayerID)
      removePlayer(removablePlayerID.kickedSessionID);
    }
  }, [removablePlayerID]);

  const removePlayer = (sessionId) => {
    const scene = gameRef.current.scene.scenes[0];
    if (sessionId !== mysessionID && playersRef.current[sessionId]) {
      playersRef.current[sessionId].sprite.destroy();
      playersRef.current[sessionId].usernameText.destroy();
      playersRef.current[sessionId].roleText.destroy();
      playersRef.current[sessionId].circle.destroy(); 
      delete playersRef.current[sessionId];
    }
  };

  useEffect(() => {
    console.log(killpicture)
    if(killpicture.includes("sKkg1T8")) {
      
    }
  }, [killpicture]);


  useEffect(() => {
    if (gameReady && triggerClearGraves || !triggerClearGraves) {
      console.log("testingwfae")
      Player.deleteAllGraves();
    }
  }, [triggerClearGraves]);

  return <div id="game-container" style={{ width: '100vw', height: '100vh', position: 'fixed', top: 0, left: 0 }} />;
}

export default GameContainer;
