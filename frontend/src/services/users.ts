import axios from "../api/axios";
import {UNAVAILABLE} from "../constants/messages.ts";
import {ICreateUser} from "../interfaces/user/CreateUserInterface.ts";
import {AxiosInstance} from "axios";
import {IBadRequest} from "../interfaces/helper/BadRequestInterface.ts";

const LOGIN_URL = '/users/login';
const USERS_URL = '/users';

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
    return axiosInstance.post(USERS_URL, filteredData)
        .then(response => response.data.password)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 400) {
                error.response.data.forEach((error: IBadRequest) => {
                    error.errors.forEach((errorMessage: string) => {
                        if (errorMessage.includes("already exists")) {
                            throw new Error("A user with the given email already exists");
                        }
                    });
                });
                throw new Error('Invalid input data');
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else {
                throw new Error('Failed to add a new user');
            }
        });
}

const changePassword = (axiosInstance: AxiosInstance, userId: number, currentPassword: string, newPassword: string) => {
    return axiosInstance.post(`${USERS_URL}/${userId}/password`, {
        currentPassword: currentPassword,
        newPassword: newPassword
    })
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 400) {
                error.response.data.forEach((error: IBadRequest) => {
                    error.errors.forEach((errorMessage: string) => {
                        if (errorMessage.includes("Current password don't match")) {
                            throw new Error(errorMessage);
                        }
                    });
                });
                throw new Error('Invalid input data');
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                throw new Error('User not found');
            } else {
                throw new Error('Failed to change password');
            }
        });
}

const getAll = (axiosInstance: AxiosInstance, roleName = "") => {
    return axiosInstance.get(USERS_URL, {params: {roleName}})
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else {
                throw new Error('Failed to fetch users list');
            }
        });
};

const get = (axiosInstance: AxiosInstance, userId: string) => {
    return axiosInstance.get(`${USERS_URL}/${userId}`)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                throw new Error('User not found');
            } else {
                throw new Error('Failed to fetch user');
            }
        });
}

const resetPassword = (axiosInstance: AxiosInstance, userId: number) => {
    return axiosInstance.post(`${USERS_URL}/${userId}/password/reset`)
        .then(response => response.data.password)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                throw new Error('User not found');
            } else {
                throw new Error('Failed to reset password');
            }
        });
};

const resetPasswords = (axiosInstance: AxiosInstance, userIds: number[]) => {
    return axiosInstance.post(`${USERS_URL}/password/reset/bulk`, {userIds}, {responseType: 'blob'})
        .then(response => {
            const header = response.headers['content-disposition'];
            const filename = header.split('filename=')[1];

            const url = window.URL.createObjectURL(new Blob([response.data], {type: 'application/pdf'}));
            const link = document.createElement('a');
            link.href = url;
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 400) {
                throw new Error('Invalid input data');
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                throw new Error('User not found');
            } else {
                throw new Error('Failed to reset passwords');
            }
        });
};

export default {
    login,
    create,
    changePassword,
    getAll,
    get,
    resetPassword,
    resetPasswords
};