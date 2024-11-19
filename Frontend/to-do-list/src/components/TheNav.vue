<template>
    <nav class="navbar">
      <div class="container-fluid">
        <button class="navbar-toggler" @click="toggleNavbar">
          <div class="hamburger-menu" :class="{ 'open': isNavbarOpen }">
            <div class="line line1"></div>
            <div class="line line2"></div>
            <div class="line line3"></div>
          </div>
        </button>
        <div class="navbar-collapse" :class="{ 'show': isNavbarOpen }">
          <h1 class="navbar-brand">ToDoList</h1>
          <ul class="navbar-nav">
            <li class="nav-item">
              <a class="nav-link" aria-current="page" href="#" @click="toHome">Home</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#" @click="toAbout">About</a>
            </li>
          </ul>
          <button v-if="!isNavbarOpen" class="btn-signout" @click="signOut">Sign Out</button>
          <a v-else class="nav-link signout-link" @click="signOut">Sign Out</a>
        </div>
      </div>
    </nav>
  </template>
  
  <script>
  export default {
    name: 'TheNav',
    data() {
      return {
        isNavbarOpen: false
      }
    },
    methods: {
      toggleNavbar() {
        this.isNavbarOpen = !this.isNavbarOpen;
      },
      signOut() {
        localStorage.removeItem('token');
        this.$router.push('/signin').then(() => {
          window.location.reload();
        });
      },
      toHome()
      {
        this.$router.push('/home');
      },
      toAbout()
      {
        this.$router.push('/about');
      }
    }
  }
  </script>
  
  <style scoped>
  .navbar {
    display: flex;
    align-items: center;
    background-color: #f8f9fa;
    padding: 0 1rem;
    margin-bottom: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .container-fluid {
    display: flex;
    align-items: center;
    width: 100%;
    justify-content: space-between; /* Ensure space between brand and button */
  }
  
  .navbar-toggler {
    background: none;
    border: none;
    cursor: pointer;
    display: none; /* Hide on large screens */
  }
  
  .hamburger-menu {
    width: 30px;
    height: 20px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    transition: 0.3s ease;
  }
  
  .line {
    width: 100%;
    height: 3px;
    background-color: #007bff;
    transition: 0.3s ease;
  }
  
  .hamburger-menu.open .line1 {
    transform: rotate(45deg);
    background-color: #fff;
  }
  
  .hamburger-menu.open .line2 {
    opacity: 0;
  }
  
  .hamburger-menu.open .line3 {
    transform: rotate(-45deg);
    background-color: #fff;
  }
  
  .navbar-collapse {
    display: flex;
    align-items: center;
    width: 100%;
    justify-content: space-between; /* Space between brand, links, and button */
    transition: max-height 0.3s ease;
    overflow: hidden;
  }
  
  .navbar-collapse.show {
    display: flex;
    flex-direction: column;
  }
  
  .navbar-brand {
    font-size: 1.5rem;
    font-weight: bold;
  }
  
  .navbar-collapse ul {
    list-style: none;
    padding-left: 0;
    margin: 0;
    display: flex;
    flex-grow: 1;
    align-items: center;
    justify-content: flex-start; /* Align items to the start */
  }
  
  .navbar-nav {
    display: flex;
    margin: 0;
    padding: 0;
  }
  
  .nav-item {
    margin-right: 1rem;
  }
  
  .nav-link {
    padding: 0.5rem 1rem;
    text-decoration: none;
    color: #333;
  }
  
  .nav-link.active {
    color: #007bff;
  }
  
  .signout-link {
    color: #dc3545;
    cursor: pointer;
  }
  
  .signout-link:hover {
    color: #c82333;
  }
  
  .btn-signout {
    border-radius: 20px;
    padding: 0.5rem 1rem;
    border: 1px solid #dc3545;
    background-color: transparent;
    color: #dc3545;
    cursor: pointer;
    transition: background-color 0.3s, color 0.3s;
  }
  
  .btn-signout:hover {
    background-color: #dc3545;
    color: #fff;
  }
  
  /* Mobile Styles */
  @media (max-width: 767px) {
    .navbar-toggler {
      display: block; /* Show hamburger menu on small screens */
    }
  
    .navbar-collapse {
      flex-direction: column;
      max-height: 0;
      width: 100%;
      margin-top: 0;
    }
  
    .navbar-collapse.show {
      max-height: 500px; /* Adjust as needed */
    }
  
    .navbar-brand {
      margin: 0; /* Remove margin for better alignment */
    }
  
    .navbar-nav {
      flex-direction: column;
      width: 100%;
    }
  
    .nav-item {
      margin-bottom: 0.5rem;
    }
  
    .btn-signout {
      display: none; /* Hide button on mobile */
    }
  
    .signout-link {
      display: block; /* Show sign out as a link on mobile */
      margin-top: 1rem;
    }
  }
  
  @media (min-width: 768px) {
    .navbar-toggler {
      display: none; /* Hide hamburger menu on larger screens */
    }
  
    .navbar-collapse {
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
    }
  
    .navbar-nav {
      flex-direction: row;
    }
  
    .btn-signout {
      display: inline-block; /* Show button on larger screens */
    }
  
    .signout-link {
      display: none; /* Hide sign out link on larger screens */
    }
  }
  </style>
  