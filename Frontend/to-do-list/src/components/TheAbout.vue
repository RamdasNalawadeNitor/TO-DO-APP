<template>
    <div class="about-container">
      <h1>About This To-Do List App</h1>
      <p>
        Welcome to the To-Do List App! This application is designed to help you manage your tasks efficiently and effectively. 
        Whether you're a student, a professional, or anyone looking to keep track of your daily activities, this app is for you.
      </p>
      <h2>Features</h2>
      <ul>
        <li>✨ Add, edit, and delete tasks with ease.</li>
        <li>📅 Set due dates to stay on top of deadlines.</li>
        <li>✅ Mark tasks as completed to keep track of your progress.</li>
        <li>🔍 Search for tasks by description.</li>
        <li>📊 Sort and filter tasks based on due dates.</li>
        <li>📱 User-friendly interface for smooth navigation.</li>
      </ul>
      <h2>Technologies Used</h2>
      <p>
        This application is built using Vue.js, a progressive JavaScript framework for building user interfaces. 
        It leverages components and a state management system for optimal performance and a seamless user experience.
      </p>
      <h2>Getting Started</h2>
      <p>
        To get started, simply create an account, log in, and start adding your tasks. 
        Your tasks will be saved and accessible from any device.
      </p>
      <button class="cta-button" @click="navigateToTasks">Get Started</button>
      <h2>Contact</h2>
      <p>
        If you have any questions or feedback, feel free to reach out at <a href="mailto:support@example.com">support@example.com</a>.
      </p>
    </div>
  </template>
  
  <script>
  export default {
    methods: {
      navigateToTasks() {
        this.$router.push('/tasks');
      },
    },
  };
  </script>
  
  <style scoped>
  .about-container {
    padding: 20px;
    max-width: 600px;
    margin: 30px auto; 
    line-height: 1.6;
    background: #f9f9f9;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  h1 {
    text-align: center;
    color: #333;
  }
  
  ul {
    list-style-type: disc;
    margin-left: 20px;
  }
  
  .cta-button {
    padding: 10px 20px;
    background: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background 0.3s;
  }
  
  .cta-button:hover {
    background: #0056b3;
  }
  </style>
  