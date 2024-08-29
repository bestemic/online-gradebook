import {UNAVAILABLE} from "../constants/messages.ts";
import {AxiosInstance} from "axios";
import {IBadRequest} from "../interfaces/helper/BadRequestInterface.ts";
import {ICreateSubject} from "../interfaces/subject/CreateSubjectInterface.ts";

const SUBJECTS_URL = '/subjects';

const create = (axiosInstance: AxiosInstance, data: ICreateSubject) => {
    return axiosInstance.post(SUBJECTS_URL, data)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 400) {
                error.response.data.forEach((error: IBadRequest) => {
                    error.errors.forEach((errorMessage: string) => {
                        if (errorMessage.includes("class already")) {
                            throw new Error("Class already has a subject with this name taught by this teacher");
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

const getAll = (axiosInstance: AxiosInstance, classId = "") => {
    return axiosInstance.get(SUBJECTS_URL, {params: {classId}})
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

const getById = (axiosInstance: AxiosInstance, subjectId: string) => {
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
    getById
};