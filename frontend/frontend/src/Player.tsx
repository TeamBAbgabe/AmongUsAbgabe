export default class Player {
    scene: Phaser.Scene;
    sessionId: string;
    sprite: Phaser.GameObjects.Sprite;
    usernameText: Phaser.GameObjects.Text;
    graveText: Phaser.GameObjects.Text;
    roleText: Phaser.GameObjects.Text;
    lastX: number;
    lastY: number;
    characterType: string;
    movementTimer: ReturnType<typeof setTimeout> | null;
    static graves: Phaser.GameObjects.Sprite[] = [];
    static graveTexts: Phaser.GameObjects.Text[] = []; // New static array for grave texts
    circle: Phaser.GameObjects.Graphics; // Add this line

  
    constructor(scene, sessionId, x, y, username, color, characterType, Role) {
      this.scene = scene;
      this.sessionId = sessionId;
      this.characterType = characterType;
      this.movementTimer = null;
  
      const initialTexture = this.getIdleTexture(characterType);
      this.sprite = scene.physics.add.sprite(x, y, initialTexture);
  
      this.lastX = x;
      this.lastY = y;
  
      this.sprite.setScale(0.5, 0.5);
      this.sprite.setOrigin(0.5, 0.5);
  
      this.usernameText = scene.add.text(x, y + 15, username, { font: '10px Arial', fill: color });
      this.roleText = scene.add.text(x, y + 25, Role, { font: '10px Arial', fill: "red" });
      this.roleText.setOrigin(0.5, 0.0);
      this.usernameText.setOrigin(0.5, 0.0);


      this.circle = scene.add.graphics();
      this.circle.setVisible(false);  // Initially hidden
    }
  
    getIdleTexture(characterType) {
      switch (characterType) {
        case 'M1':
          return 'walk3';
        case 'F2':
          return 'walkF4';
        default:
          return 'walk0';
      }
    }
  
    updatePosition(x, y) {
      this.sprite.setPosition(x, y);
      this.usernameText.setPosition(x, y + 15);
      this.roleText.setPosition(x, y + 25);
      
      let animationKey;
    
      if (x < this.lastX) {  
        animationKey = this.characterType === 'M1' ? 'MrunningM1' : 'Mrunning2F2';
      } else {
        animationKey = this.characterType === 'M1' ? 'runningM1' : 'running2F2';
      }
    
      this.sprite.anims.play(animationKey, true);
    
      this.lastX = x;
      this.lastY = y;

      this.circle.clear();
      if (this.circle.visible) {
        this.circle.lineStyle(2, 0xffffff, 1);
        this.circle.strokeCircle(x, y, 45);  // Draw a circle with a 45-pixel radius
      }
    }

    
    showCircle() {
      this.circle.setVisible(true);
    }
  
    hideCircle() {
      this.circle.setVisible(false);
    }
  
    static createGrave(scene, x, y, username, color) {
      const graveSprite = scene.add.sprite(x, y, 'grave');
      graveSprite.setScale(0.5, 0.5);
      graveSprite.setOrigin(0.5, 0.5);
      graveSprite.setDisplaySize(64, 32);
      graveSprite.setDepth(0);
      this.graves.push(graveSprite);
  
      const usernameText = scene.add.text(x, y + 10, username, { font: '10px Arial', fill: color });
      usernameText.setOrigin(0.5, 0.0);
      this.graveTexts.push(usernameText);
  
      console.log(this.graves);
      return { graveSprite, usernameText };
    }
  
    static deleteAllGraves() {
      console.log("Deleting all graves", this.graves);
      this.graves.forEach((grave) => grave.destroy());
      this.graves = [];
  
      console.log("Deleting all grave texts", this.graveTexts);
      this.graveTexts.forEach((text) => text.destroy());
      this.graveTexts = [];
    }
  
    destroy() {
      this.sprite.destroy();
      this.usernameText.destroy();
      this.roleText.destroy();
    }
  }
  