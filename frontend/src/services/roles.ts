import {UNAVAILABLE} from "../constants/messages.ts";
import {AxiosInstance} from "axios";

const GET_URL = '/roles';

const getAll = (axiosInstance: AxiosInstance) => {
    return axiosInstance.get(GET_URL)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in to get roles');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to get roles');
            } else {
                throw new Error('Failed to get roles');
            }
        });
};

export default {
    getAll
};