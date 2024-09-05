import useAuth from "../../hooks/useAuth.ts";
import useSubject from "../../hooks/useSubject.ts";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import React, {useState} from "react";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {jwtDecode} from "jwt-decode";
import RequireRole from "../wrapper/RequireRole.tsx";
import {ROLES} from "../../constants/roles.ts";
import {ICreateGrade} from "../../interfaces/grade/CreateGradeInterface.ts";
import {ICreateGrades} from "../../interfaces/grade/CreateGradesInterface.ts";
import gradesService from "../../services/grades.ts";

const GradesTab = () => {
    const {auth} = useAuth();
    const {subject, schoolClass} = useSubject();
    const axiosPrivate = useAxiosPrivate();
    const [error, setError] = useState<string | null>(null);
    const [addError, setAddError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isAddingGrade, setIsAddingGrade] = useState(false);
    const [gradesCreate, setGradesCreate] = useState<ICreateGrade[]>([]);
    const [gradeName, setGradeName] = useState<string>("");

    const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
    const currentUserId: number = decoded?.id || 0;

    const handleAddGradeSection = () => {
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
            .then(() => {
                setAddError(null);
                setIsAddingGrade(false);
            })
            .catch((error) => {
                setAddError(error.message);
            })
            .finally(() => setIsSubmitting(false));
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

                {error && (<div className="text-lg text-red-500 mt-2">{error}</div>)}
            </div>
        </div>
    );
};

export default GradesTab;