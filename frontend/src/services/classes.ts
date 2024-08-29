import {UNAVAILABLE} from "../constants/messages.ts";
import {AxiosInstance} from "axios";
import {IBadRequest} from "../interfaces/helper/BadRequestInterface.ts";
import {ICreateSchoolClass} from "../interfaces/school_class/CreateSchoolClassInterface.ts";

const CLASSES_URL = '/classes';

const create = (axiosInstance: AxiosInstance, data: ICreateSchoolClass) => {
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
                throw new Error('Failed to add a new class');
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
                throw new Error('Failed to fetch classes list');
            }
        });
};

const getById = (axiosInstance: AxiosInstance, id: number) => {
    return axiosInstance.get(`${CLASSES_URL}/${id}`)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                throw new Error('Class not found');
            } else {
                throw new Error('Failed to fetch class details');
            }
        });
}

const removeStudent = (axiosInstance: AxiosInstance, classId: number, studentId: number) => {
    return axiosInstance.delete(`${CLASSES_URL}/${classId}/students/${studentId}`)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                throw new Error('Student not found');
            } else if (error.response.status === 409) {
                throw new Error('Student is not in the class');
            } else {
                throw new Error('Failed to remove student from class');
            }
        });
}

const addStudent = (axiosInstance: AxiosInstance, classId: number, studentId: number) => {
    return axiosInstance.post(`${CLASSES_URL}/${classId}/students/${studentId}`)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                throw new Error('Student not found');
            } else if (error.response.status === 409) {
                throw new Error('User is not a student');
            } else {
                throw new Error('Failed to add student to class');
            }
        })
}

export default {
    create,
    getAll,
    getById,
    removeStudent,
    addStudent
};