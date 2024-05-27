import axios from "../api/axios";
import {UNAVAILABLE} from "../constants/messages.ts";
import {ICreateUser} from "../interfaces/CreateUserInterface.ts";
import {AxiosInstance} from "axios";

const LOGIN_URL = '/users/login';
const CREATE_URL = '/users';

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

const create = (axiosInstance: AxiosInstance, data: ICreateUser) => {
    const filteredData = Object.fromEntries(Object.entries(data).filter(([, v]) => v != ''));
    return axiosInstance.post(CREATE_URL, filteredData)
        .then(response => response.data.password)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 400) {
                const errorMessage = error.response.data.message || '';
                if (errorMessage.includes("already exists")) {
                    throw new Error('A user with the given email already exists');
                } else {
                    throw new Error('Invalid input data');
                }
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else {
                throw new Error('Failed to add a new user');
            }
        });
}

export default {
    login,
    create,
};