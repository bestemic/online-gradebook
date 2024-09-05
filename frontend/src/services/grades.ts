import {UNAVAILABLE} from "../constants/messages.ts";
import {AxiosInstance} from "axios";
import {ICreateGrades} from "../interfaces/grade/CreateGradesInterface.ts";

const GRADES_URL = '/grades';

const create = (axiosInstance: AxiosInstance, data: ICreateGrades) => {
    return axiosInstance.post(GRADES_URL, data)
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
                throw new Error('Subject not found');
            } else if (error.response.status === 409) {
                throw new Error('Grade with this name already exists in this subject');
            } else {
                throw new Error('Failed to add grade');
            }
        });
}

const getBySubjectId = (axiosInstance: AxiosInstance, subjectId: number) => {
    return axiosInstance.get(`${GRADES_URL}/subject/${subjectId}`)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                if (error.response.data.message.includes("Grades not found")) {
                    throw new Error('Grades not found');
                }
                throw new Error('Subject not found');
            } else {
                throw new Error('Failed to get class grades from subject');
            }
        });
}

const getBySubjectIdAndStudentId = (axiosInstance: AxiosInstance, subjectId: number, studentId: number) => {
    return axiosInstance.get(`${GRADES_URL}/subject/${subjectId}/student/${studentId}`)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                if (error.response.data.message.includes("Grades not found")) {
                    throw new Error('Grades not found');
                }
                throw new Error('Subject or student not found');
            } else {
                throw new Error('Failed to get student grades');
            }
        });
}

export default {
    create,
    getBySubjectId,
    getBySubjectIdAndStudentId
};