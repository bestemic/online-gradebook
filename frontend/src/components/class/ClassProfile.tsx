import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {IClassGroup} from "../../interfaces/ClassGroupInterface.ts";
import classesService from "../../services/classes.ts";
import subjectsService from "../../services/subjects.ts";
import RequireRole from "../wrapper/RequireRole.tsx";
import {ROLES} from "../../constants/roles.ts";
import {IClassSubject} from "../../interfaces/ClassSubjectInterface.ts";
import {ISubject} from "../../interfaces/SubjectInterface.ts";
import {Select} from "@mantine/core";

const ClassProfile = () => {
    const {id} = useParams();
    const axiosPrivate = useAxiosPrivate();
    const [classGroup, setClassGroup] = useState<IClassGroup | null>(null);
    const [classSubjects, setClassSubjects] = useState<IClassSubject[]>([]);
    const [availableSubjects, setAvailableSubjects] = useState<ISubject[]>([]);
    const [isAssigningSubjects, setIsAssigningSubjects] = useState(false);
    const [selectedSubjectId, setSelectedSubjectId] = useState<number | null>(null);
    const [selectedTeacherId, setSelectedTeacherId] = useState<number | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [subjectsError, setSubjectsError] = useState<string | null>(null);

    useEffect(() => {
        if (id) {
            classesService.getById(axiosPrivate, Number(id))
                .then(data => {
                    setClassGroup(data);
                    setError(null);
                })
                .catch(error => {
                    setError(error.message);
                });
        }
    }, [id, axiosPrivate]);

    useEffect(() => {
        if (id) {
            classesService.getClassAssignedSubjects(axiosPrivate, Number(id))
                .then(data => {
                    setClassSubjects(data);
                    setSubjectsError(null);
                })
                .catch(error => {
                    setSubjectsError(error.message);
                });

            subjectsService.getAll(axiosPrivate)
                .then(data => {
                    setAvailableSubjects(data);
                })
                .catch(error => {
                    setSubjectsError(error.message);
                });
        }
    }, [id, axiosPrivate]);

    const handleAssignSubjects = () => {
        setSelectedSubjectId(null);
        setSelectedTeacherId(null);
        setIsAssigningSubjects(!isAssigningSubjects);
    };

    const handleSaveSubject = () => {
        if (selectedSubjectId && selectedTeacherId) {
            classesService.assignSubjectAndTeacher(axiosPrivate, Number(id), selectedSubjectId, selectedTeacherId)
                .then((data: IClassSubject) => {
                    setClassSubjects([...classSubjects, data])
                    setIsAssigningSubjects(false);
                    setSelectedSubjectId(null);
                    setSelectedTeacherId(null);
                })
                .catch(error => {
                    setSubjectsError(error.message);
                });
        }
    };

    const filteredSubjects = availableSubjects.filter(
        (subject) => !classSubjects.some((classSubject) => classSubject.subject.id === subject.id)
    );

    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-8 rounded-b shadow-2xl max-w-4xl w-full">
                {classGroup ? (
                    <>
                        <div>
                            <h1 className="text-3xl font-bold mb-2 text-center">{classGroup.name}</h1>
                            <p className="text-xl text-center">
                                Classroom: {classGroup.classroom}
                            </p>
                            <div className="pr-8 mt-8">
                                <h2 className="text-lg font-semibold mb-2">Students</h2>
                                {classGroup.students.length > 0 ? (
                                    <ul>
                                        {classGroup.students.map((student) => (
                                            <li key={student.id} className="border-t py-2 flex justify-between">
                                                <div className="flex-1 flex items-center">
                                                    <span>{student.firstName} {student.lastName}</span>
                                                </div>
                                                <span>{student.email}</span>
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p>No students in this class.</p>
                                )}
                            </div>
                        </div>

                        <div className="pr-8 mt-8">
                            <h2 className="block font-bold mb-2">Assigned subjects</h2>
                            {classSubjects.length > 0 ? (
                                <ul>
                                    {classSubjects.map(classSubject => (
                                        <li key={classSubject.id}
                                            className="border-t py-2 flex justify-between">
                                            <span className="flex-1">{classSubject.subject.name}</span>
                                            <span
                                                className="flex items-center">{classSubject.teacher.firstName} {classSubject.teacher.lastName}
                                            </span>
                                        </li>
                                    ))}
                                </ul>
                            ) : subjectsError ? (
                                <div className="text-red-500">{subjectsError}</div>
                            ) : (
                                <p>No subjects assigned.</p>
                            )}

                            {isAssigningSubjects ? (
                                <div className="mt-4">
                                    <Select
                                        data={filteredSubjects.map(subject => ({
                                            value: String(subject.id),
                                            label: subject.name
                                        }))}
                                        placeholder="Select a subject"
                                        value={String(selectedSubjectId)}
                                        onChange={(_value, option) => setSelectedSubjectId(Number(option.value))}
                                    />

                                    {selectedSubjectId && (
                                        <Select
                                            className="mt-4"
                                            data={availableSubjects.find(subject => subject.id == selectedSubjectId)?.teachers.map(teacher => ({
                                                value: String(teacher.id),
                                                label: `${teacher.firstName} ${teacher.lastName}`
                                            }))}
                                            placeholder="Select a teacher"
                                            value={String(selectedTeacherId)}
                                            onChange={(_value, option) => setSelectedTeacherId(Number(option.value))}
                                        />
                                    )}

                                    <div className="mt-4 flex space-x-4">
                                        <button
                                            onClick={handleSaveSubject}
                                            className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 transition duration-300"
                                        >
                                            Save
                                        </button>
                                        <button
                                            onClick={handleAssignSubjects}
                                            className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition duration-300"
                                        >
                                            Cancel
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                <RequireRole allowedRoles={[ROLES.Admin]}>
                                    <button
                                        onClick={handleAssignSubjects}
                                        className="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                                    >
                                        Assign Subject
                                    </button>
                                </RequireRole>
                            )}
                        </div>
                    </>
                ) : !error && (
                    <div>Loading...</div>
                )}

                {error && (<div className="text-red-500">{error}</div>)}
            </div>
        </div>
    );
};

export default ClassProfile;