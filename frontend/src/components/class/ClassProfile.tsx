import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {ISchoolClass} from "../../interfaces/school_class/SchoolClassInterface.ts";
import classesService from "../../services/classes.ts";
import subjectsService from "../../services/subjects.ts";
import userService from "../../services/users.ts";
import {ISubject} from "../../interfaces/subject/SubjectInterface.ts";
import {ROLES} from "../../constants/roles.ts";
import RequireRole from "../wrapper/RequireRole.tsx";
import {Select} from "@mantine/core";
import {IUserBasic} from "../../interfaces/user/UserBasicInterface.ts";
import {ICreateSubject} from "../../interfaces/subject/CreateSubjectInterface.ts";
import {IconXboxX} from "@tabler/icons-react";

const ClassProfile = () => {
    const {id} = useParams();
    const axiosPrivate = useAxiosPrivate();
    const [schoolClass, setSchoolClass] = useState<ISchoolClass | null>(null);
    const [subjects, setSubjects] = useState<ISubject[]>([]);
    const [teachers, setTeachers] = useState<IUserBasic[]>([])
    const [isAddingSubject, setIsAddingSubject] = useState(false);
    const [selectedTeacherId, setSelectedTeacherId] = useState<number | null>(null);
    const [subjectName, setSubjectName] = useState<string>("");
    const [error, setError] = useState<string | null>(null);
    const [subjectsError, setSubjectsError] = useState<string | null>(null);
    const [teachersError, setTeachersError] = useState<string | null>(null);
    const [subjectNameError, setSubjectNameError] = useState<string | null>(null);
    const [subjectAddError, setSubjectAddError] = useState<string | null>(null);
    const [removeStudentError, setRemoveStudentError] = useState<string | null>(null);
    const [isAddingStudent, setIsAddingStudent] = useState(false);
    const [selectedStudentId, setSelectedStudentId] = useState<number | null>(null);
    const [students, setStudents] = useState<IUserBasic[]>([]);
    const [studentAddError, setStudentAddError] = useState<string | null>(null);

    useEffect(() => {
        if (id) {
            classesService.getById(axiosPrivate, Number(id))
                .then(data => {
                    setSchoolClass(data);
                    setError(null);
                })
                .catch(error => {
                    setError(error.message);
                });
        }
    }, [id, axiosPrivate]);

    useEffect(() => {
        if (id) {
            subjectsService.getAll(axiosPrivate, Number(id))
                .then(data => {
                    setSubjects(data);
                    setSubjectsError(null);
                })
                .catch(error => {
                    setSubjectsError(error.message);
                });
        }
    }, [id, axiosPrivate]);

    useEffect(() => {
        if (isAddingSubject && teachers.length === 0) {
            userService.getAll(axiosPrivate, ROLES.Teacher)
                .then(data => {
                    setTeachers(data);
                    setTeachersError(null);
                })
                .catch(error => {
                    setTeachersError(error.message);
                });
        }
    }, [isAddingSubject, axiosPrivate, teachers.length]);

    useEffect(() => {
        if (isAddingStudent && students.length === 0) {
            userService.getAll(axiosPrivate, ROLES.Student)
                .then(data => {
                    setStudents(data);
                    setStudentAddError(null);
                })
                .catch(error => {
                    setStudentAddError(error.message);
                });
        }
    }, [isAddingStudent, axiosPrivate, students.length]);


    const handleAddSubjectSection = () => {
        setSelectedTeacherId(null);
        setSubjectName("");
        setSubjectNameError(null);
        setIsAddingSubject(!isAddingSubject);
    };

    const handleAddSubject = () => {
        if (selectedTeacherId && subjectName && id) {
            const newSubject: ICreateSubject = {
                name: subjectName,
                classId: Number(id),
                teacherId: selectedTeacherId,
            };

            subjectsService.create(axiosPrivate, newSubject)
                .then((data) => {
                    setSubjects([...subjects, data]);
                    setSubjectAddError(null);
                    handleAddSubjectSection();
                })
                .catch(error => {
                    setSubjectAddError(error.message);
                });
        }
    };

    const handleRemoveStudent = (studentId: number) => {
        if (id) {
            classesService.removeStudent(axiosPrivate, Number(id), studentId)
                .then(() => {
                    if (schoolClass) {
                        setSchoolClass({
                            ...schoolClass,
                            students: schoolClass.students.filter(student => student.id !== studentId)
                        });
                    }
                    setRemoveStudentError(null);
                })
                .catch(error => {
                    setRemoveStudentError(error.message);
                });
        }
    };

    const handleAddStudentSection = () => {
        setIsAddingStudent(!isAddingStudent);
        setSelectedStudentId(null);
        setStudentAddError(null);
    };

    const handleAddStudent = () => {
        if (selectedStudentId && id) {
            classesService.addStudent(axiosPrivate, Number(id), selectedStudentId)
                .then(() => {
                    schoolClass?.students.push(students.find(student => student.id === selectedStudentId)!);
                    handleAddStudentSection();
                })
                .catch(error => {
                    setStudentAddError(error.message);
                });
        }
    };

    const getAvailableStudents = () => {
        if (schoolClass) {
            return students.filter(student =>
                !schoolClass.students.some(existingStudent => existingStudent.id === student.id)
            );
        }
        return students;
    };


    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-8 rounded-b shadow-2xl max-w-4xl w-full">
                {schoolClass ? (
                    <>
                        <div>
                            <h1 className="text-3xl font-bold mb-2 text-center">{schoolClass.name}</h1>
                            <p className="text-xl text-center">
                                Classroom: {schoolClass.classroom}
                            </p>
                            <div className="pr-8 mt-8">
                                <h2 className="text-lg font-semibold mb-2">Students</h2>
                                {schoolClass.students.length > 0 ? (
                                    <div>
                                        <ul>
                                            {schoolClass.students.map((student) => (
                                                <li key={student.id} className="border-t py-2 flex justify-between">
                                                    <div className="flex-1 flex items-center">
                                                        <span>{student.firstName} {student.lastName}</span>
                                                    </div>
                                                    <span>{student.email}</span>
                                                    <IconXboxX
                                                        className="text-red-500 cursor-pointer hover:text-red-700 transition duration-300 ml-4"
                                                        onClick={() => handleRemoveStudent(student.id)}
                                                    />
                                                </li>
                                            ))}
                                        </ul>
                                        {removeStudentError && (
                                            <div className="text-red-500">{removeStudentError}</div>)}
                                    </div>
                                ) : (
                                    <p>No students in this class.</p>
                                )}
                            </div>
                        </div>

                        {isAddingStudent ? (
                            <div className="mt-2 pr-8">
                                <div>
                                    <label className="block mb-1">Select Student:</label>
                                    <Select
                                        data={getAvailableStudents().map(student => ({
                                            value: String(student.id),
                                            label: `${student.firstName} ${student.lastName} [${student.email}]`
                                        }))}
                                        placeholder="Select a student"
                                        value={selectedStudentId ? String(selectedStudentId) : ''}
                                        onChange={(_value, option) => setSelectedStudentId(Number(option.value))}
                                    />
                                </div>

                                <div className="mt-4 flex space-x-4">
                                    <button
                                        onClick={handleAddStudent}
                                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 transition duration-300"
                                    >
                                        Add Student
                                    </button>
                                    <button
                                        onClick={handleAddStudentSection}
                                        className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition duration-300"
                                    >
                                        Cancel
                                    </button>
                                </div>
                                {studentAddError && (
                                    <div className="text-red-500 mt-2">{studentAddError}</div>
                                )}
                            </div>
                        ) : (
                            <RequireRole allowedRoles={[ROLES.Admin]}>
                                <button
                                    onClick={handleAddStudentSection}
                                    className="mt-2 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                                >
                                    Add Student
                                </button>
                            </RequireRole>
                        )}

                        <div className="pr-8 mt-8">
                            <h2 className="block font-bold mb-2">Assigned subjects</h2>
                            {subjects.length > 0 ? (
                                <ul>
                                    {subjects.map(subject => (
                                        <li key={subject.id}
                                            className="border-t py-2 flex justify-between">
                                            <span className="flex-1">{subject.name}</span>
                                            <span
                                                className="flex items-center">{subject.teacher.firstName} {subject.teacher.lastName}
                                            </span>
                                        </li>
                                    ))}
                                </ul>
                            ) : subjectsError ? (
                                <div className="text-red-500">{subjectsError}</div>
                            ) : (
                                <p>No subjects assigned.</p>
                            )}

                            {isAddingSubject ? (
                                <div className="mt-4">
                                    <div>
                                        <label className="block mb-1">Subject Name:</label>
                                        <input
                                            type="text"
                                            className="border border-gray-300 rounded px-3 py-2 w-full"
                                            value={subjectName}
                                            onChange={(e) => {
                                                setSubjectName(e.target.value);
                                                if (e.target.value.length >= 2) {
                                                    setSubjectNameError(null);
                                                } else {
                                                    setSubjectNameError("Subject name must be at least 2 characters long.");
                                                }
                                            }}
                                            placeholder="Enter subject name"
                                        />
                                        {subjectNameError && (
                                            <div className="text-red-500 mt-1">{subjectNameError}</div>
                                        )}
                                    </div>

                                    {teachersError ? (
                                        <div className="text-red-500">{teachersError}</div>
                                    ) : (
                                        <div>
                                            <label className="block mt-4 mb-1">Teacher:</label>
                                            <Select
                                                data={teachers.map(teacher => ({
                                                    value: String(teacher.id),
                                                    label: `${teacher.firstName} ${teacher.lastName}`
                                                }))}
                                                placeholder="Select a teacher"
                                                value={String(selectedTeacherId)}
                                                onChange={(_value, option) => setSelectedTeacherId(Number(option.value))}
                                            />
                                        </div>
                                    )}

                                    <div className="mt-4 flex space-x-4">
                                        <button
                                            onClick={handleAddSubject}
                                            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 transition duration-300"
                                        >
                                            Save
                                        </button>
                                        <button
                                            onClick={handleAddSubjectSection}
                                            className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition duration-300"
                                        >
                                            Cancel
                                        </button>
                                    </div>
                                    {subjectAddError && (
                                        <div className="text-red-500 mt-2">{subjectAddError}</div>
                                    )}
                                </div>
                            ) : (
                                <RequireRole allowedRoles={[ROLES.Admin]}>
                                    <button
                                        onClick={handleAddSubjectSection}
                                        className="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                                    >
                                        Add Subject
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