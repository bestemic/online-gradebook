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
                if (error.response.data.message.includes("Please try again!")) {
                    throw new Error(error.response.data.message);
                }
                throw new Error('Failed to add a new lesson');
            }
        });
}

const getBySubjectId = (axiosInstance: AxiosInstance, subjectId: number) => {
    return axiosInstance.get(`${MATERIALS_URL}/subject/${subjectId}`)
        .then(response => response.data)
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                throw new Error('Subject not found');
            } else {
                throw new Error('Failed to get subject materials');
            }
        });
}

const downloadFile = (axiosInstance: AxiosInstance, materialId: number) => {
    return axiosInstance.get(`${MATERIALS_URL}/${materialId}/file`, {responseType: 'blob'})
        .then(response => {
            const header = response.headers['content-disposition'];
            const filename = header.split('filename=')[1];
            const contentType = response.headers['content-type'] || 'application/octet-stream';

            const url = window.URL.createObjectURL(new Blob([response.data], {type: contentType}));
            const link = document.createElement('a');
            link.href = url;
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            if (!error.response) {
                throw new Error(UNAVAILABLE);
            } else if (error.response.status === 401) {
                throw new Error('Must be logged in');
            } else if (error.response.status === 403) {
                throw new Error('You do not have permission to perform this operation');
            } else if (error.response.status === 404) {
                throw new Error('Material not found');
            } else {
                if (error.response.data.message.includes("Please try again!")) {
                    throw new Error(error.response.data.message);
                }
                throw new Error('Failed to download material');
            }
        });
}

export default {
    create,
    getBySubjectId,
    downloadFile
};