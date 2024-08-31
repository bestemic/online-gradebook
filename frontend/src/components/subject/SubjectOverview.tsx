import {useParams} from "react-router-dom";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {useEffect, useState} from "react";
import subjectsService from "../../services/subjects.ts";
import classesService from "../../services/classes.ts";
import RequireRole from "../wrapper/RequireRole.tsx";
import {ROLES} from "../../constants/roles.ts";
import useSubject from "../../hooks/useSubject.ts";

const SubjectOverview = () => {
    const {id} = useParams();
    const axiosPrivate = useAxiosPrivate();
    const {subject, setSubject, schoolClass, setSchoolClass} = useSubject();
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (id) {
            subjectsService.getById(axiosPrivate, id)
                .then(data => {
                    setSubject(data);
                    setError(null);

                    classesService.getById(axiosPrivate, data.schoolClass.id)
                        .then(classData => {
                            setSchoolClass(classData);
                            setError(null);
                        })
                        .catch(error => {
                            setError(error.message);
                        });

                })
                .catch(error => {
                    setError(error.message);
                });
        }
    }, [id, axiosPrivate, setSubject, setSchoolClass]);

    return (
        <div className="h-full flex items-center justify-center">
            <div className="max-w-xl w-full">

                {subject ? (
                    <div>
                        <h1 className="text-3xl font-bold mb-2 text-center">{subject.name}</h1>
                        <RequireRole allowedRoles={[ROLES.Teacher]}>
                            <p className="text-xl text-center">
                                Class: {subject.schoolClass.name}
                            </p>
                        </RequireRole>
                        <RequireRole allowedRoles={[ROLES.Student]}>
                            <p className="text-xl text-center">
                                Classroom: {subject.schoolClass.classroom}
                            </p>
                            <p className="text-xl text-center">
                                Teacher: {subject.teacher.firstName} {subject.teacher.lastName}
                            </p>
                        </RequireRole>

                        <div className="pt-20">
                            <h2 className="text-lg font-semibold mb-2 text-center">Students</h2>

                            {schoolClass && schoolClass.students.length > 0 ? (
                                <div>
                                    <ul>
                                        {schoolClass.students.map((student) => (
                                            <li key={student.id} className="border-t py-2 text-center">
                                                <span>{student.firstName} {student.lastName}</span>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            ) : (
                                <p className="text-center">No students assigned to this subject.</p>
                            )}
                        </div>
                    </div>
                ) : !error && (
                    <div>Loading...</div>
                )}

                {error && (<div className="text-red-500">{error}</div>)}
            </div>
        </div>
    );
};

export default SubjectOverview;