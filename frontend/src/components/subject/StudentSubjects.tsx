import subjectsService from "../../services/subjects.ts";
import userService from "../../services/users.ts";
import {IUser} from "../../interfaces/user/UserInterface.ts";
import GenericSubjects from "./GenericSubjectsList.tsx";
import {ISubject} from "../../interfaces/subject/SubjectInterface.ts";
import {NavigateFunction} from "react-router-dom";
import {AxiosInstance} from "axios";

const StudentSubjects = () => {
    const fetchSubjects = async (axiosPrivate: AxiosInstance, studentId: number) => {
        const data = await userService.getById(axiosPrivate, String(studentId)) as IUser;
        const classId = data.classId;
        if (classId) {
            return subjectsService.getAll(axiosPrivate, classId, null);
        }
        return [];
    };

    const handleItemClick = (navigate: NavigateFunction, id: number) => {
        navigate(`/subjects/${id}`);
    };

    const renderHeaders = () => (
        <div className="flex items-center justify-between p-2 font-bold">
            <span className="w-1/2 text-center">Subject Name</span>
            <span className="w-1/2 text-center">Teacher</span>
        </div>
    );

    const renderRow = (subject: ISubject) => (
        <div className="flex items-center justify-between p-2">
            <span className="w-1/2 text-center">{subject.name}</span>
            <span className="w-1/2 text-center">{subject.teacher?.firstName} {subject.teacher?.lastName}</span>
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

export default StudentSubjects;