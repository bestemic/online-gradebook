import {UNAVAILABLE} from "../constants/messages.ts";
import {AxiosInstance} from "axios";
import {IBadRequest} from "../interfaces/BadRequestInterface.ts";
import {ICreateSubject} from "../interfaces/CreateSubjectInterface.ts";

const SUBJECTS_URL = '/subjects';

const create = (axiosInstance: AxiosInstance, data: ICreateSubject) => {
    return axiosInstance.post(SUBJECTS_URL, data)
        .then(response => response.data.password)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 400) {
                error.response.data.forEach((error: IBadRequest) => {
                    error.errors.forEach((errorMessage: string) => {
                        if (errorMessage.includes("already exists")) {
                            throw new Error("A subject with the given name already exists");
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
    return axiosInstance.get(SUBJECTS_URL)
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

const get = (axiosInstance: AxiosInstance, subjectId: string) => {
    return axiosInstance.get(`${SUBJECTS_URL}/${subjectId}`)
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
                throw new Error('Failed to fetch subject');
            }
        });
}

export default {
    create,
    getAll,
    get
};