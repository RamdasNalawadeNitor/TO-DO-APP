<template>
    <div class="background"></div>
    <br>
    <form @submit.prevent class="custom-form">
      <div class="mb-3">
        <label for="exampleInputEmail" class="form-label">Email</label>
        <input type="email" class="form-control" id="exampleInputEmail" v-model="email" required>
      </div>
      <div class="mb-3">
        <label for="exampleInputPassword" class="form-label">Password</label>
        <input type="password" class="form-control" id="exampleInputPassword" v-model="password" required>
      </div>
      <div class="button-container">
        <button type="submit" class="btn btn-dark" @click="handleSubmit">SignIn</button>
      </div>
      <span class="form-message">Not a user yet? <a href="#" @click="toRegister">Register here</a></span>
    </form>
    <br>
</template>

<script>
import api from '@/utils/axios';
import store from '@/store';
import { useToast } from 'vue-toastification';

export default {
    data() {
      return {
        email: '',
        password: ''
      };
    },
    methods: {
        async handleSubmit() {
            try {
                const response = await api.post('http://localhost:9090/user/login', {
                    email: this.email,
                    password: this.password,
                });
                console.log(response.data);
                console.log("Login success!");
                const token = response.data.jwtToken;
                console.log(token);
                store.setToken(token);

                const toast = useToast();
                toast.success('Welcome!');

                this.$router.push('/tasks');
            } catch (error) {
                const toast = useToast();
                toast.error('Invalid credentials!');
                console.log(error.data, "1");
            }
        },
        toRegister() {
            this.$router.push('/register');
        }
    }
};
</script>

<style scoped>
.custom-form {
    border: 1px solid #ddd;
    border-radius: 10px;
    max-width: 400px; 
    margin: auto; 
    padding: 20px;
    background-color: rgba(249, 249, 249, 0.8);
    position: relative;
    z-index: 1;
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

.custom-form .mb-3 {
    margin-bottom: 1rem; 
}

.button-container {
    display: flex;
    justify-content: center; 
    margin-top: 1rem; 
}

.form-message {
    font-size: 0.875rem; 
    color: #666;
    display: block;
    text-align: center;
    margin-top: 1rem; 
}

.form-message a {
    color: #007bff; 
    text-decoration: none; 
}

.form-message a:hover {
    text-decoration: underline; 
}

@media (max-width: 576px) {
    .custom-form {
        max-width: 100%; 
        margin-left: 10px; 
        margin-right: 10px;
    }
}
</style>
