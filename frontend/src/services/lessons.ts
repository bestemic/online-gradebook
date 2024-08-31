import {UNAVAILABLE} from "../constants/messages.ts";
import {AxiosInstance} from "axios";
import {ICreateLesson} from "../interfaces/lesson/CreateLessonInterface.ts";

const LESSONS_URL = '/lessons';

const create = (axiosInstance: AxiosInstance, data: ICreateLesson) => {
    return axiosInstance.post(LESSONS_URL, data)
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
            } else {
                throw new Error('Failed to add a new lesson');
            }
        });
}

const getAll = (axiosInstance: AxiosInstance, subjectId: number | null = null,) => {
    return axiosInstance.get(LESSONS_URL, {
        params: {
            subjectId: subjectId ?? undefined,
        }
    })
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else {
                throw new Error('Failed to fetch lessons list');
            }
        });
};

const getById = (axiosInstance: AxiosInstance, lessonId: string) => {
    return axiosInstance.get(`${LESSONS_URL}/${lessonId}`)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                throw new Error('Lesson not found');
            } else {
                throw new Error('Failed to fetch lesson');
            }
        });
}

export default {
    create,
    getAll,
    getById
};