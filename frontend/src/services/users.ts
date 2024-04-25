import axios from "../api/axios";
import {UNAVAILABLE} from "../constants/messages.ts";

const LOGIN_URL = '/users/login';

const login = (email: string, password: string) => {
    return axios.post(LOGIN_URL, {email, password})
        .then(response => response.data.token)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 400) {
                throw new Error('Invalid email or password');
            } else if (error.response.status === 401) {
                throw new Error('Invalid credentials');
            } else {
                throw new Error('Login failed');
            }
        });
};

export {
    login,
};