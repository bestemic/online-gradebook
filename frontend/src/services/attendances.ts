import {UNAVAILABLE} from "../constants/messages.ts";
import {AxiosInstance} from "axios";
import {ICreateAttendancesLesson} from "../interfaces/attendance/CreateAttendancesLessonInterface.ts";

const ATTENDANCES_URL = '/attendances';

const create = (axiosInstance: AxiosInstance, data: ICreateAttendancesLesson) => {
    return axiosInstance.post(ATTENDANCES_URL, data)
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
                throw new Error('Lesson not found');
            } else if (error.response.status === 409) {
                throw new Error('Attendance was already checked');
            } else {
                throw new Error('Failed to check attendance');
            }
        });
}

const getByLessonId = (axiosInstance: AxiosInstance, lessonId: number) => {
    return axiosInstance.get(`${ATTENDANCES_URL}/lesson/${lessonId}`)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                if (error.response.data.message.includes("Attendance not found")) {
                    throw new Error('Attendance not found');
                }
                throw new Error('Lesson not found');
            } else {
                throw new Error('Failed to get class attendance');
            }
        });
}

const getByLessonIdAndStudentId = (axiosInstance: AxiosInstance, lessonId: number, studentId: number) => {
    return axiosInstance.get(`${ATTENDANCES_URL}/lesson/${lessonId}/student/${studentId}`)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                if (error.response.data.message.includes("Attendance not found")) {
                    throw new Error('Attendance not found');
                }
                throw new Error('Lesson or student not found');
            } else {
                throw new Error('Failed to get attendance');
            }
        });
}

export default {
    create,
    getByLessonId,
    getByLessonIdAndStudentId
};