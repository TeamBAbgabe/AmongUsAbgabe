import React, { useState } from 'react';

function RandomQuestionTask({ task }) {
  const [answer, setAnswer] = useState('');

  const handleSubmit = () => {
    if (answer.toLowerCase() === task.correctAnswer.toLowerCase()) {
      console.log("Correct answer!");
      // Implement completion logic here
    } else {
      console.log("Incorrect, try again!");
    }
  };

  return (
    <div>
      <h3>{task.description}</h3>
      <p>{task.question}</p>
      <input
        type="text"
        value={answer}
        onChange={(e) => setAnswer(e.target.value)}
        placeholder="Type your answer here"
      />
      <button onClick={handleSubmit}>Submit</button>
    </div>
  );
}

export default RandomQuestionTask;
