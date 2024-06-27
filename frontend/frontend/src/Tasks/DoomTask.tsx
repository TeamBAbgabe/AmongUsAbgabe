import React, { useEffect, useRef, useState } from 'react';
import './doom.css';

const DoomTask = ({ onComplete, onClose, task }) => {
    const canvasRef = useRef(null);
    const [monsters, setMonsters] = useState([]);
    const [timeLeft, setTimeLeft] = useState(10);
    const monsterImage = new Image();
    monsterImage.src = "https://cdn-icons-png.freepik.com/512/3031/3031160.png"; // Replace this with your actual monster image URL

    useEffect(() => {
        const canvas = canvasRef.current;
        const canvasWidth = canvas.width;
        const canvasHeight = canvas.height;
        const centerX = canvasWidth / 2;
        const centerY = canvasHeight / 2;
        const radius = canvasWidth / 2 - 100; // Ensure some padding inside the circle

        const spawnInterval = setInterval(() => {
            if (monsters.length < 10) {
                const angle = Math.random() * 2 * Math.PI;
                const distance = Math.random() * radius;
                const x = centerX + distance * Math.cos(angle);
                const y = centerY + distance * Math.sin(angle);
                setMonsters(prevMonsters => [
                    ...prevMonsters,
                    { x, y, img: monsterImage, scale: 1 }
                ]);
            }
        }, 1000); // Faster spawning interval

        const scaleInterval = setInterval(() => {
            setMonsters(prevMonsters => prevMonsters.map(monster => ({
                ...monster,
                scale: monster.scale * 1.3 // Increased scaling factor for faster growth
            })));
        }, 200); // Faster scaling interval

        const gameInterval = setInterval(() => {
            setTimeLeft(prevTime => {
                if (prevTime <= 1) {
                    clearInterval(spawnInterval);
                    clearInterval(scaleInterval);
                    clearInterval(gameInterval);
                    onComplete(task.taskId);
                    return 0;
                }
                return prevTime - 1;
            });
        }, 1000);

        return () => {
            clearInterval(spawnInterval);
            clearInterval(scaleInterval);
            clearInterval(gameInterval);
        };
    }, [onComplete, monsters.length, task.taskId]);

    useEffect(() => {
        const renderLoop = setInterval(() => {
            const canvas = canvasRef.current;
            const ctx = canvas.getContext('2d');
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            monsters.forEach(monster => {
                const size = 60 * monster.scale;
                ctx.drawImage(monster.img, monster.x - size / 2, monster.y - size / 2, size, size);
                if (size >= 300) {
                    clearInterval(renderLoop);
                    onClose();
                }
            });
        }, 100);

        return () => clearInterval(renderLoop);
    }, [monsters, onClose]);

    const shoot = (x, y) => {
        setMonsters(monsters.filter(monster => {
            const size = 60 * monster.scale;
            const centerX = monster.x;
            const centerY = monster.y;
            return Math.hypot(centerX - x, centerY - y) > size / 2;
        }));
    };

    return (
        <div className="doom-task">
            <canvas ref={canvasRef} width={600} height={600} onClick={(e) => {
                const rect = canvasRef.current.getBoundingClientRect();
                const x = e.clientX - rect.left;
                const y = e.clientY - rect.top;
                shoot(x, y);
            }} />
            <p>Time left: {timeLeft} seconds</p>
        </div>
    );
};

export default DoomTask;
