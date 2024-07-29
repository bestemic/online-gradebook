import {UNAVAILABLE} from "../constants/messages.ts";
import {AxiosInstance} from "axios";
import {IBadRequest} from "../interfaces/BadRequestInterface.ts";
import {ICreateClassGroup} from "../interfaces/CreateClassGroupInterface.ts";

const CLASSES_URL = '/classes';

const create = (axiosInstance: AxiosInstance, data: ICreateClassGroup) => {
    return axiosInstance.post(CLASSES_URL, data)
        .then(response => response.data.password)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 400) {
                error.response.data.forEach((error: IBadRequest) => {
                    error.errors.forEach((errorMessage: string) => {
                        if (errorMessage.includes("already exists")) {
                            throw new Error("A class with the given name already exists");
                        }
                    });
                });
                throw new Error('Invalid input data');
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else {
                throw new Error('Failed to add a new subject');
            }
        });
}

const getAll = (axiosInstance: AxiosInstance) => {
    return axiosInstance.get(CLASSES_URL)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else {
                throw new Error('Failed to fetch subjects list');
            }
        });
};

export default {
    create,
    getAll,
};