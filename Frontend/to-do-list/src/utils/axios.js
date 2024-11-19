import axios from 'axios';

// Create an Axios instance with the base URL and default headers
const api = axios.create({
  baseURL: 'http://localhost:9090',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add a request interceptor to include the token in every request
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

export default api;
