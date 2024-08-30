import subjectsService from "../../services/subjects.ts";
import GenericSubjects from "./GenericSubjectsList.tsx";
import {ISubject} from "../../interfaces/subject/SubjectInterface.ts";
import {AxiosInstance} from "axios";
import {NavigateFunction} from "react-router-dom";

const AdminSubjects = () => {
    const fetchSubjects = (axiosPrivate: AxiosInstance) => {
        return subjectsService.getAll(axiosPrivate);
    };

    const handleItemClick = (navigate: NavigateFunction, id: number) => {
        navigate(`/classes/${id}`);
    };

    const renderHeaders = () => (
        <div className="flex items-center justify-between p-2 font-bold">
            <span className="w-2/5 text-center">Subject Name</span>
            <span className="w-1/5 text-center">Class Name</span>
            <span className="w-2/5 text-center">Teacher</span>
        </div>
    );

    const renderRow = (subject: ISubject) => (
        <div className="flex items-center justify-between p-2">
            <span className="w-2/5 text-center">{subject.name}</span>
            <span className="w-1/5 text-center">{subject.schoolClass.name}</span>
            <span className="w-2/5 text-center">{subject.teacher?.firstName} {subject.teacher?.lastName}</span>
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

export default AdminSubjects;