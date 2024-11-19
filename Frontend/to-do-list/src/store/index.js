import { reactive } from 'vue';
import { userId } from '../utils/auth';

const state = reactive({
  token: localStorage.getItem('token') || null,
  userId: userId(localStorage.getItem('token')) || null,
});

const setToken = (token) => {
  state.token = token;
  state.userId = userId(token);
  localStorage.setItem('token', token);
};

const clearToken = () => {
  state.token = null;
  state.userId = null;
  localStorage.removeItem('token');
};

export default {
  state,
  setToken,
  clearToken,
};
