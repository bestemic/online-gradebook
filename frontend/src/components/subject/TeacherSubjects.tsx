import subjectsService from "../../services/subjects.ts";
import GenericSubjects from "./GenericSubjectsList.tsx";
import {ISubject} from "../../interfaces/subject/SubjectInterface.ts";
import {NavigateFunction} from "react-router-dom";
import {AxiosInstance} from "axios";

const TeacherSubjects = () => {
    const fetchSubjects = (axiosPrivate: AxiosInstance, teacherId: number) => {
        return subjectsService.getAll(axiosPrivate, null, teacherId);
    };

    const handleItemClick = (navigate: NavigateFunction, id: number) => {
        navigate(`/subjects/${id}`);
    };

    const renderHeaders = () => (
        <div className="flex items-center justify-between p-2 font-bold">
            <span className="w-1/2 text-center">Subject Name</span>
            <span className="w-1/2 text-center">Class Name</span>
        </div>
    );

    const renderRow = (subject: ISubject) => (
        <div className="flex items-center justify-between p-2">
            <span className="w-1/2 text-center">{subject.name}</span>
            <span className="w-1/2 text-center">{subject.schoolClass.name}</span>
        </div>
    );

    return (
        <GenericSubjects
            fetchSubjects={fetchSubjects}
            handleItemClick={handleItemClick}
            renderHeaders={renderHeaders}
            renderRow={renderRow}
        />
    );
};

export default TeacherSubjects;