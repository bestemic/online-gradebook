import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {useEffect, useState} from "react";
import subjectsService from "../../services/subjects.ts";
import {ISubject} from "../../interfaces/subject/SubjectInterface.ts";
import {useNavigate} from "react-router-dom";
import useAuth from "../../hooks/useAuth.ts";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {jwtDecode} from "jwt-decode";

const TeacherSubjects = () => {
    const axiosPrivate = useAxiosPrivate();
    const {auth} = useAuth();
    const [subjects, setSubjects] = useState<ISubject[]>([]);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
        const teacherId: string = String(decoded?.id) || "";

        subjectsService.getAll(axiosPrivate, "", teacherId)
            .then((data: ISubject[]) => {
                const sortedSubjects = data.sort((a, b) => a.name.localeCompare(b.name));
                setSubjects(sortedSubjects);
                setError(null);
            })
            .catch(error => {
                setError(error.message);
            });
    }, [auth.token, axiosPrivate]);

    const handleSubjectClick = (id: number) => {
        navigate(`/subjects/${id}`);
    };

    return (
        <>
            {error ? (
                <div className="h-full flex items-center justify-center">
                    <div className="text-center">
                        <h2 className="text-3xl font-semibold text-red-500">{error}</h2>
                    </div>
                </div>
            ) : (
                <div className="h-full flex items-center justify-center">
                    {subjects.length > 0 ? (
                        <ul className="space-y-4 mt-6 mb-6 max-w-4xl w-full">
                            <li className="border-b-2">
                                <div className="flex items-center justify-between p-2 font-bold">
                                    <span className="w-1/2 text-center">Subject Name</span>
                                    <span className="w-1/2 text-center">Class Name</span>
                                </div>
                            </li>
                            {subjects.map(subject => (
                                <li key={subject.id} className="border rounded shadow cursor-pointer"
                                    onClick={() => handleSubjectClick(subject.schoolClass.id)}>
                                    <div className="flex items-center justify-between p-2">
                                        <span className="w-1/2 text-center">{subject.name}</span>
                                        <span className="w-1/2 text-center">{subject.schoolClass.name}</span>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <h1 className="text-xl font-bold text-center">No subjects available</h1>
                    )}
                </div>
            )}
        </>
    );
};

export default TeacherSubjects;