<template>
  <form @submit.prevent="toSignIn" class="custom-form">
    <div class="mb-3">
      <label for="name" class="form-label">Name</label>
      <input type="text" class="form-control" id="name" v-model="users.name" required>
    </div>
    <div class="mb-3">
      <label for="email" class="form-label">Email</label>
      <input type="email" class="form-control" id="email" v-model="users.email" required>
      <p v-if="emailError" class="text-danger">{{ emailError }}</p>
    </div>
    <div class="mb-3">
      <label for="passwordInput" class="form-label">Password</label>
      <input type="password" class="form-control" id="passwordInput" v-model="users.password" required>
    </div>
    <div class="mb-3">
      <button type="submit" class="btn btn-dark">Submit</button>
      <button type="button" class="btn btn-dark" @click="$router.push('/signin')">Sign In</button>
    </div>
  </form>
</template>

<script>
import api from '@/utils/axios';
import { useToast } from 'vue-toastification';

export default {
  data() {
    return {
      users: {
        name: '',
        email: '',
        password: ''
      },
      emailError: '',
    };
  },
  methods: {
    async toSignIn() {
      this.emailError = '';

      // Optional: Email validation
      // if (!this.validateEmail(this.users.email)) {
      //   this.emailError = 'Email must end with @nitorinfotech.com';
      //   return; 
      // }

      const formData = new FormData();
      
      Object.keys(this.users).forEach(key => {
        formData.append(key, this.users[key]);
      });

      try {
        const response = await api.post('http://localhost:9090/signup/addUser', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });

        console.log('Response:', response.data);
        
        const toast = useToast();
        toast.success('Registration success!');

        this.$router.push('/signin');
      } catch (error) {
        console.error('Error adding user:', error.response?.data || error.message);
      }
    }
  }
};
</script>

<style scoped>
.custom-form {
  border: 1px solid #ddd;
  border-radius: 10px;
  max-width: 600px; 
  margin: 40px auto; /* Increased top margin to push the form lower */
  padding: 20px;
  background-color: #f9f9f9;
}

.custom-form .btn {
  margin-right: 10px;
}

@media (max-width: 576px) {
  .custom-form {
    max-width: 100%; 
    margin: 10px; 
  }
}

.text-danger {
  color: red;
}
</style>
