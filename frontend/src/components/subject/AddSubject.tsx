import {useEffect, useState} from "react";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import classesService from "../../services/classes.ts";
import userService from "../../services/users.ts";
import subjectsService from "../../services/subjects.ts";
import {ISchoolClass} from "../../interfaces/school_class/SchoolClassInterface.ts";
import {IUserBasic} from "../../interfaces/user/UserBasicInterface.ts";
import {ROLES} from "../../constants/roles.ts";
import {ICreateSubject} from "../../interfaces/subject/CreateSubjectInterface.ts";
import {ISubject} from "../../interfaces/subject/SubjectInterface.ts";
import {Select} from "@mantine/core";

const AddSubject = () => {
    const axiosPrivate = useAxiosPrivate();
    const [classes, setClasses] = useState<ISchoolClass[]>([]);
    const [teachers, setTeachers] = useState<IUserBasic[]>([]);
    const [selectedClassId, setSelectedClassId] = useState<number | null>(null);
    const [selectedTeacherId, setSelectedTeacherId] = useState<number | null>(null);
    const [subjectName, setSubjectName] = useState<string>("");
    const [subjectNameError, setSubjectNameError] = useState<string | null>(null);
    const [formError, setFormError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    useEffect(() => {
        classesService.getAll(axiosPrivate)
            .then(data => {
                setClasses(data);
            })
            .catch(error => {
                setFormError(error.message);
            });

        userService.getAll(axiosPrivate, ROLES.Teacher)
            .then(data => {
                setTeachers(data);
            })
            .catch(error => {
                setFormError(error.message);
            });
    }, [axiosPrivate]);

    const handleAddSubject = () => {
        if (!selectedClassId || !selectedTeacherId || subjectName.length < 2) {
            setFormError("Please fill all fields correctly.");
            return;
        }

        const newSubject: ICreateSubject = {
            name: subjectName,
            classId: selectedClassId,
            teacherId: selectedTeacherId,
        };

        subjectsService.create(axiosPrivate, newSubject)
            .then((data: ISubject) => {
                setSuccessMessage(`Successfully created subject with name: ${data.name}`);
                setFormError(null);
                setSelectedClassId(null);
                setSelectedTeacherId(null);
                setSubjectName("");
            })
            .catch(error => {
                setSuccessMessage(null);
                setFormError(error.message);
            });
    };

    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-8 rounded-b shadow-2xl max-w-4xl w-full">
                <h1 className="text-3xl font-bold mb-4 text-center">Add New Subject</h1>

                <div className="mb-4">
                    <label className="block font-bold mb-1">Subject Name:</label>
                    <input
                        type="text"
                        value={subjectName}
                        onChange={(e) => {
                            setSubjectName(e.target.value);
                            if (e.target.value.length >= 2) {
                                setSubjectNameError(null);
                            } else {
                                setSubjectNameError("Subject name must be at least 2 characters long.");
                            }
                        }}
                        className="border border-gray-300 rounded px-3 py-2 w-full"
                        placeholder="Enter subject name"
                    />
                    {subjectNameError && <div className="text-red-500 mt-1">{subjectNameError}</div>}
                </div>

                <div className="mb-4">
                    <label className="block font-bold mb-1">Class:</label>
                    <Select
                        data={classes.map(schoolClass => ({
                            value: String(schoolClass.id),
                            label: schoolClass.name,
                        }))}
                        placeholder="Select class"
                        value={selectedClassId ? String(selectedClassId) : null}
                        onChange={(value) => setSelectedClassId(Number(value))}
                    />
                </div>

                <div className="mb-4">
                    <label className="block font-bold mb-1">Teacher:</label>
                    <Select
                        data={teachers.map(teacher => ({
                            value: String(teacher.id),
                            label: `${teacher.firstName} ${teacher.lastName}`,
                        }))}
                        placeholder="Select teacher"
                        value={selectedTeacherId ? String(selectedTeacherId) : null}
                        onChange={(value) => setSelectedTeacherId(Number(value))}
                    />
                </div>

                <div className="flex space-x-4">
                    <button
                        onClick={handleAddSubject}
                        className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 transition duration-300"
                    >
                        Add Subject
                    </button>
                </div>
                {formError && <div className="text-red-500 mt-2">{formError}</div>}
                {successMessage && <div className="text-green-500 mt-2">{successMessage}</div>}
            </div>
        </div>
    );
};

export default AddSubject;