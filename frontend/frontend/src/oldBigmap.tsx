import React, { useEffect, useRef } from 'react';
import Phaser from 'phaser';

function BigMap({ show, onClose, playerPosition }) {
  const bigMapRef = useRef(null);
  const phaserInstance = useRef(null);

  const originalWidth = 64 * 32;
  const originalHeight = 64 * 32;

  const zoomFactor = 0.3;

  useEffect(() => {
    if (show) {
      const config = {
        type: Phaser.AUTO,
        parent: bigMapRef.current,
        width: originalWidth * zoomFactor,
        height: originalHeight * zoomFactor,
        physics: {
          default: 'arcade',
          arcade: {
            gravity: { x: 0, y: 0 },
            debug: false
          }
        },
        scene: {
          preload: preload,
          create: create,
          update: update
        }
      };

      const bigMapGame = new Phaser.Game(config);
      phaserInstance.current = bigMapGame;

      function preload() {
        this.load.image('tiles', 'src/assets/tileset x1.png');
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



        this.cameras.main.setZoom(zoomFactor);
        this.cameras.main.centerOn(map.widthInPixels / 2, map.heightInPixels / 2);
        this.physics.world.setBounds(0, 0, map.widthInPixels, map.heightInPixels);
      }

      function update() {
        if (this.playerDot) {
        }
      }

      return () => {
        bigMapGame.destroy(true);
      };
    }
  }, [show]);


  if (!show) return null;

  return (
    <div style={{
      position: 'absolute', 
      top: '50%', 
      left: '50%', 
      transform: 'translate(-50%, -50%)', 
      width: `${originalWidth * zoomFactor}px`,
      height: `${originalHeight * zoomFactor}px`,
      border: '2px solid white', 
      borderRadius: '10px', 
      zIndex: 9000,
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      overflow: 'hidden'
    }}>
      
      <div style={{
        width: `${originalWidth * zoomFactor}px`,
        height: `${originalHeight * zoomFactor}px`,
        borderRadius: '10px', 
        overflow: 'hidden'
      }}>
        <div ref={bigMapRef} style={{ width: '100%', height: '100%' }}></div>
      </div>
    </div>
  );
}

export default BigMap;
