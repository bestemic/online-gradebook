import {useEffect, useState} from "react";
import classesService from "../../services/classes.ts";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {IClassSubject} from "../../interfaces/ClassSubjectInterface.ts";
import useAuth from "../../hooks/useAuth.ts";
import {jwtDecode} from "jwt-decode";
import JwtInterface from "../../interfaces/JwtInterface.ts";

const TeacherSubjects = () => {
    const axiosPrivate = useAxiosPrivate();
    const {auth} = useAuth();
    const [taughtClasses, setTaughtClasses] = useState<IClassSubject[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        classesService.getAllSubjectsInClasses(axiosPrivate)
            .then((data: IClassSubject[]) => {
                if (auth?.token) {
                    const teacherId = (jwtDecode<JwtInterface>(auth.token))?.id;
                    const filteredData = data.filter((cs: IClassSubject) => cs.teacher.id === teacherId);
                    setTaughtClasses(filteredData);
                    setError(null);
                }
            })
            .catch(error => {
                setError(error.message);
            });
    }, [auth.token, axiosPrivate]);

    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-8 rounded-b shadow-2xl max-w-4xl w-full">
                {taughtClasses ? (
                    <div>
                        <h1 className="text-2xl font-bold mb-2 text-center">Assigned subjects to teaching</h1>
                        {taughtClasses.length > 0 ? (
                            <ul className="space-y-4 pt-8 mb-6 w-full">
                                <li className="border-b-2">
                                    <div className="flex items-center justify-between p-2 font-bold">
                                        <span className="w-1/3 text-center">Subject</span>
                                        <span className="w-1/3 text-center">Class</span>
                                        <span className="w-1/3 text-center">Classroom</span>
                                    </div>
                                </li>
                                {taughtClasses.map(classSubject => (
                                    <li key={classSubject.id} className="border rounded shadow cursor-pointer">
                                        <div className="flex items-center justify-between p-2">
                                            <span className="w-1/3 text-center">{classSubject.subject.name}</span>
                                            <span className="w-1/3 text-center">{classSubject.classGroup.name}</span>
                                            <span
                                                className="w-1/3 text-center">{classSubject.classGroup.classroom}</span>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>No subject to teach</p>
                        )}
                    </div>
                ) : !error && (
                    <div>Loading...</div>
                )}

                {error && (<div className="text-red-500 text-center">{error}</div>)}
            </div>
        </div>
    );
};

export default TeacherSubjects;