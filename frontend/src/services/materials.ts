import {UNAVAILABLE} from "../constants/messages.ts";
import {AxiosInstance} from "axios";
import {ICreateMaterial} from "../interfaces/material/CreateMaterialInterface.ts";

const MATERIALS_URL = '/materials';

const create = (axiosInstance: AxiosInstance, data: ICreateMaterial) => {
    const formData = new FormData();
    formData.append("name", data.name);
    formData.append("description", data.description);
    formData.append("subjectId", data.subjectId.toString());
    formData.append("file", data.file);

    return axiosInstance.post(MATERIALS_URL, data, {headers: {'Content-Type': 'multipart/form-data'}})
        .then(response => response.data)
        .catch(error => {
            console.log(error.response);
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

export default {
    create
};