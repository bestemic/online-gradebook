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

export default {
    create
};