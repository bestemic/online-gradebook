import useAuth from "../../hooks/useAuth.ts";
import useSubject from "../../hooks/useSubject.ts";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import React, {useEffect, useState} from "react";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {jwtDecode} from "jwt-decode";
import RequireRole from "../wrapper/RequireRole.tsx";
import {ROLES} from "../../constants/roles.ts";
import {ICreateGrade} from "../../interfaces/grade/CreateGradeInterface.ts";
import {ICreateGrades} from "../../interfaces/grade/CreateGradesInterface.ts";
import gradesService from "../../services/grades.ts";
import {IGrades} from "../../interfaces/grade/GradesInferface.ts";
import {IGradeStudent} from "../../interfaces/grade/GradeStudentInterface.ts";
import {IGrade} from "../../interfaces/grade/GradeInterface.ts";
import {IconSquareRoundedChevronDown} from "@tabler/icons-react";

const GradesTab = () => {
    const {auth} = useAuth();
    const {subject, schoolClass} = useSubject();
    const axiosPrivate = useAxiosPrivate();
    const [error, setError] = useState<string | null>(null);
    const [addError, setAddError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isAddingGrade, setIsAddingGrade] = useState(false);
    const [classGrades, setClassGrades] = useState<IGrades[]>([]);
    const [studentGrades, setStudentGrades] = useState<IGradeStudent[]>([]);
    const [gradesCreate, setGradesCreate] = useState<ICreateGrade[]>([]);
    const [gradeName, setGradeName] = useState<string>("");
    const [expandedGradeIds, setExpandedGradeIds] = useState<string[]>([]);

    const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
    const currentUserId: number = decoded?.id || 0;

    useEffect(() => {
        if (subject && currentUserId === subject.teacher.id) {
            gradesService.getBySubjectId(axiosPrivate, subject.id)
                .then((data: IGrades[]) => {
                    const sortedData = data.sort((a, b) => new Date(b.assignedTime).getTime() - new Date(a.assignedTime).getTime());
                    setClassGrades(sortedData);
                    setError(null);
                })
                .catch(error => {
                    if (error.message.includes("Grades not found")) {
                        setClassGrades([]);
                        setError(null);
                    } else {
                        setError(error.message);
                    }
                })
        }
    }, [axiosPrivate, currentUserId, subject]);

    useEffect(() => {
        if (schoolClass && schoolClass.students.some(student => student.id === currentUserId) && subject) {
            gradesService.getBySubjectIdAndStudentId(axiosPrivate, subject.id, currentUserId)
                .then((data: IGradeStudent[]) => {
                    const sortedData = data.sort((a, b) => new Date(b.assignedTime).getTime() - new Date(a.assignedTime).getTime());
                    setStudentGrades(sortedData);
                    setError(null);
                })
                .catch(error => {
                    if (error.message.includes("Grades not found")) {
                        setStudentGrades([]);
                        setError(null);
                    } else {
                        setError(error.message);
                    }
                });
        }
    }, [axiosPrivate, currentUserId, schoolClass, subject]);

    const handleAddGradeSection = (event: React.FormEvent) => {
        event.preventDefault();

        if (currentUserId === subject?.teacher.id && schoolClass && schoolClass.students) {
            const initialGrades = schoolClass.students.map((student) => ({
                studentId: student.id,
                grade: null
            }));
            setGradesCreate(initialGrades);
        }
        setGradeName("");
        setAddError(null);
        setIsAddingGrade(!isAddingGrade);
    }

    const handleGradeChange = (studentId: number, value: string) => {
        setGradesCreate((prevGrades) =>
            prevGrades.map((grade) =>
                grade.studentId === studentId ? {...grade, grade: value == "" ? null : value} : grade
            )
        );
    };

    const handleSubmitAdd = (event: React.FormEvent) => {
        event.preventDefault();

        if (!gradeName) {
            setAddError("Grade name is required.");
            return;
        }

        if (gradeName.length < 2) {
            setAddError("Grade name must be at least 2 characters long.");
            return;
        }

        const gradeData: ICreateGrades = {
            subjectId: subject?.id || 0,
            name: gradeName,
            grades: gradesCreate
        };

        setIsSubmitting(true);

        gradesService.create(axiosPrivate, gradeData)
            .then((data: IGrades) => {
                const extendedGrades = [...classGrades, data];
                const sortedGrades = extendedGrades.sort((a, b) => new Date(b.assignedTime).getTime() - new Date(a.assignedTime).getTime());
                setClassGrades(sortedGrades);
                setAddError(null);
                setIsAddingGrade(false);
            })
            .catch((error) => {
                setAddError(error.message);
            })
            .finally(() => setIsSubmitting(false));
    };

    const handleToggleGrade = (id: string) => {
        setExpandedGradeIds((prevIds) =>
            prevIds.includes(id) ? prevIds.filter((expandedId) => expandedId !== id) : [...prevIds, id]
        );
    };

    return (
        <div className="h-full flex flex-col items-center justify-center">
            <div className="w-full max-w-3xl mt-6">
                {isAddingGrade ? (
                    <form onSubmit={handleSubmitAdd}>
                        <div className="mb-4">
                            <label htmlFor="name" className="block mb-1">
                                Grade Title:
                            </label>
                            <input
                                type="text"
                                id="title"
                                placeholder="Grade name"
                                autoComplete="off"
                                value={gradeName}
                                onChange={(e) => setGradeName(e.target.value)}
                                className="border border-gray-300 rounded px-3 py-2 w-full"
                            />
                        </div>

                        <ul className="space-y-2 mb-4">
                            <h1 className="mb-1 p-1 border-b-2 border-b-gray-500">Students:</h1>
                            {schoolClass && schoolClass.students.map((student) => (
                                <li key={student.id} className="flex items-center justify-between p-1 border-b">
                                    <span>{`${student.firstName} ${student.lastName}`}</span>
                                    <div className="flex items-center space-x-4">
                                        <select
                                            value={gradesCreate.find((g) => g.studentId === student.id)?.grade || ""}
                                            onChange={(e) => handleGradeChange(student.id, e.target.value)}
                                            className="border rounded py-1.5 pl-1 pr-1 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                        >
                                            <option value="">No grade</option>
                                            {[1, 2, 3, 4, 5, 6].map((grade) => (
                                                <option key={grade} value={grade}>
                                                    {grade}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                </li>
                            ))}
                        </ul>

                        <div className="mt-4 flex space-x-4">
                            <button
                                type="submit"
                                disabled={isSubmitting}
                                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 transition duration-300"
                            >
                                {isSubmitting ? "Submitting..." : "Submit Grades"}
                            </button>
                            <button
                                onClick={handleAddGradeSection}
                                className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition duration-300"
                            >
                                Cancel
                            </button>
                        </div>

                        {addError && (<div className="text-lg text-red-500 mt-2">{addError}</div>)}
                    </form>
                ) : (
                    <RequireRole allowedRoles={[ROLES.Teacher]}>
                        {currentUserId === subject?.teacher.id && (
                            <button
                                onClick={handleAddGradeSection}
                                className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                            >
                                Add grade
                            </button>
                        )}
                    </RequireRole>
                )}

                {currentUserId === subject?.teacher.id && (
                    <div className="py-2">
                        <h1 className="text-lg font-bold py-2">Students grades</h1>
                        <ul className="space-y-3">
                            {classGrades.length > 0 ? (
                                classGrades.map((grade, index) => {
                                    const uniqueId = `${grade.name}-${index}`;
                                    return (
                                        <li key={uniqueId} className="border rounded shadow">
                                            <div
                                                onClick={() => handleToggleGrade(uniqueId)}
                                                className="flex items-center justify-between p-2 cursor-pointer hover:bg-gray-100"
                                            >
                                                <div className="flex items-center space-x-5">
                                                    <span
                                                        className={expandedGradeIds.includes(uniqueId) ? "font-bold" : ""}>
                                                        {grade.name}
                                                    </span>
                                                    <span className="text-sm text-gray-500">
                                                        {new Date(grade.assignedTime).toLocaleString(undefined, {
                                                            dateStyle: "short",
                                                            timeStyle: "short"
                                                        })}
                                                    </span>
                                                </div>
                                                <IconSquareRoundedChevronDown
                                                    size={24}
                                                    className={`transition-transform ${expandedGradeIds.includes(uniqueId) ? "rotate-180" : ""}`}
                                                />
                                            </div>

                                            {expandedGradeIds.includes(uniqueId) && (
                                                <ul className="pl-8 pr-10">
                                                    {grade.grades.map((studentGrade: IGrade) => (
                                                        <li key={studentGrade.id}
                                                            className="flex items-center border-t py-1.5">
                                                            <div className="flex-1 flex items-center">
                                                                <span>
                                                                  {studentGrade.student.firstName} {studentGrade.student.lastName}
                                                                </span>
                                                            </div>
                                                            <span>{studentGrade.grade ? studentGrade.grade : "—"}</span>
                                                        </li>
                                                    ))}
                                                </ul>
                                            )}
                                        </li>
                                    );
                                })
                            ) : (
                                <h1 className="text-xl font-bold text-center">No grades available</h1>
                            )}
                        </ul>
                    </div>
                )}

                {schoolClass && schoolClass.students.some(student => student.id === currentUserId) && (
                    <div>
                        <h1 className="text-lg font-bold">Your grades</h1>
                        <ul className="space-y-2 mb-4">
                            {studentGrades.length > 0 ? (
                                studentGrades.map((grade) => (
                                    <li key={grade.id}
                                        className="flex items-center justify-between py-1 pr-2 border-b border-gray-300">
                                        <div>
                                            <span className="font-semibold">{grade.name}</span>
                                            <span className="block text-sm text-gray-500">
                                                {new Date(grade.assignedTime).toLocaleString(undefined, {
                                                    dateStyle: "short",
                                                    timeStyle: "short"
                                                })}
                                            </span>
                                        </div>
                                        <div>
                                            <span className="text-lg">{grade.grade ? grade.grade : "—"}</span>
                                        </div>
                                    </li>
                                ))
                            ) : (
                                <div className="text-gray-500">No grades available</div>
                            )}
                        </ul>
                    </div>
                )}

                {error && (<div className="text-lg text-red-500 mt-2">{error}</div>)}
            </div>
        </div>
    );
};

export default GradesTab;