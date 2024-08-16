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

const getClassAssignedSubjects = (axiosInstance: AxiosInstance, id: number) => {
    return axiosInstance.get(`${CLASSES_URL}/${id}/subjects`)
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
                throw new Error('Failed to fetch class subjects');
            }
        });
}

const assignSubjectAndTeacher = (axiosInstance: AxiosInstance, classId: number, subjectId: number, teacherId: number) => {
    return axiosInstance.post(`${CLASSES_URL}/${classId}/assign-subject-teacher`, {subjectId, teacherId})
        .then(response => response.data)
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
                error.response.data.forEach((error: IBadRequest) => {
                    error.errors.forEach((errorMessage: string) => {
                        if (errorMessage.includes("Class")) {
                            throw new Error("Class not found");
                        }
                        if (errorMessage.includes("Subject")) {
                            throw new Error("Subject not found");
                        }
                        if (errorMessage.includes("Teacher")) {
                            throw new Error("Teacher not found");
                        }
                    });
                });
            } else if (error.response.status === 409) {
                throw new Error('Subject is already assigned to this class');
            } else {
                throw new Error('Failed to set subject to class');
            }
        });
}

export default {
    create,
    getAll,
    getById,
    getClassAssignedSubjects,
    assignSubjectAndTeacher
};