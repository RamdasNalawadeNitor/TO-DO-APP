import { createApp } from 'vue'
import App from './App.vue'
import { createRouter, createWebHistory} from 'vue-router'
import ToDos from './components/ToDos.vue';
import SignIn from './components/SignIn.vue';
import Toast from 'vue-toastification';
import 'vue-toastification/dist/index.css';
import store from './store';
import TheRegister from './components/TheRegister.vue';
import TheHome from './components/TheHome.vue';
import 'bootstrap-icons/font/bootstrap-icons.css'
import TheAbout from './components/TheAbout.vue';
import 'bootstrap/dist/css/bootstrap.min.css';

const routes = [
    {   
        path: '/',
        component: SignIn
    },
    {   
        path: '/tasks',
        component: ToDos
    },
    {   
        path: '/signin',
        component: SignIn
    },
    {
        path: '/register',
        component: TheRegister
    },
    {
        path: '/home',
        component: TheHome
    },
    {
        path: '/about',
        component: TheAbout
    }
            ]

const router = createRouter({
            history: createWebHistory(),
            routes,
        });



const app = createApp(App);

app.config.globalProperties.$store = store;
        app.use(Toast, {
          position: 'top-right',      
          timeout: 2000,             
          closeButton: true,          
          pauseOnHover: true,       
          draggable: true        
        });
        app.use(router);

app.mount('#app');