<template>
  <!-- <div class="background"></div> -->
    <div class="homepage">
      <header class="homepage-header">
        <h1>Welcome to Your Todo List</h1>
        <p>Stay organized and boost your productivity with our intuitive task management application.</p>
      </header>
      <main class="homepage-content">
        <h2>Features:</h2>
        <ul>
          <li>✔ Add, edit, and delete tasks effortlessly.</li>
          <li>✔ Set due dates to keep track of deadlines.</li>
          <li>✔ View your tasks in a clean and organized list.</li>
          <li>✔ Mark tasks as completed to stay on track.</li>
        </ul>
        <div class="cta">
          <button class="cta-button" @click="navigateToTasks">Get Started</button>
        </div>
      </main>
      <footer class="homepage-footer">
        <p>&copy; 2024 Todo List App</p>
      </footer>
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
  .homepage {
    max-width: 600px;
    margin: 30px auto;
    padding: 20px;
    text-align: center;
    background: #f9f9f9;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  .homepage-header h1 {
    font-size: 2.5rem;
    color: #333;
  }
  
  .homepage-header p {
    font-size: 1.2rem;
    color: #555;
  }
  
  .homepage-content h2 {
    margin-top: 20px;
  }
  
  .homepage-content ul {
    list-style-type: none;
    padding: 0;
  }
  
  .homepage-content li {
    margin: 10px 0;
    color: #666;
  }
  
  .cta {
    margin-top: 20px;
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
  
  .homepage-footer {
    margin-top: 30px;
    font-size: 0.9rem;
    color: #777;
  }

  .background {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: url('../assets/9.png');
    background-size: cover;
    background-position: center;
    z-index: 0;
}
  </style>
  