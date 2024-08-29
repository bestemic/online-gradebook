import {z} from "zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {useEffect, useState} from "react";
import {ICreateSchoolClass} from "../../interfaces/school_class/CreateSchoolClassInterface.ts";
import classService from "../../services/classes.ts";
import userService from "../../services/users.ts";
import {ROLES} from "../../constants/roles.ts";
import {IUser} from "../../interfaces/user/UserInterface.ts";

const AddClass = () => {
    const schema = z.object({
        name: z.string().min(1, {message: "Subject name is required"}),
        classroom: z.string().min(1, {message: "Classroom is required"}),
    });

    const {
        register,
        handleSubmit,
        setError,
        reset,
        formState: {errors, isSubmitting}
    } = useForm<ICreateSchoolClass>({resolver: zodResolver(schema)});

    const axiosPrivate = useAxiosPrivate();
    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const [allStudents, setAllStudents] = useState<IUser[]>([]);
    const [unassignedStudents, setUnassignedStudents] = useState<IUser[]>([]);
    const [selectedStudents, setSelectedStudents] = useState<IUser[]>([]);
    const [isInitialized, setIsInitialized] = useState(false);
    const [showUnassignedOnly, setShowUnassignedOnly] = useState(true);

    useEffect(() => {
        userService.getAll(axiosPrivate, ROLES.Student)
            .then((data: IUser[]) => {
                setAllStudents(data);
                setUnassignedStudents(data.filter(student => student.classId === null));
                setError("root", {message: ""});
            })
            .catch(error => {
                setError("root", {message: error.message});
            });
    }, [axiosPrivate, setError]);

    useEffect(() => {
        if (!isInitialized) {
            return;
        }

        if (selectedStudents.length === 0) {
            setError("studentsIds", {message: "At least one student must be selected."});
        } else {
            setError("studentsIds", {message: ""});
        }
    }, [isInitialized, selectedStudents.length, setError]);

    const handleAddButton: SubmitHandler<ICreateSchoolClass> = async (data) => {
        if (selectedStudents.length === 0) {
            setError("studentsIds", {message: "At least one student must be selected."});
            return;
        }

        classService.create(axiosPrivate, {...data, studentsIds: selectedStudents.map(student => student.id)})
            .then(() => {
                setSuccessMessage(`Created class with name: ${data.name}`);
                reset();
                setSelectedStudents([]);
                setIsInitialized(false);
            })
            .catch(error => {
                setError("root", {message: error.message});
            });
    };

    const handleSelectStudent = (student: IUser) => {
        setIsInitialized(true);
        setSelectedStudents(prev => [...prev, student]);
        setUnassignedStudents(prev => prev.filter(s => s.id !== student.id));
    };

    const handleDeselectStudent = (student: IUser) => {
        setUnassignedStudents(prev => [...prev, student]);
        setSelectedStudents(prev => prev.filter(s => s.id !== student.id));
    };

    const handleToggleShowUnassigned = () => {
        setShowUnassignedOnly(prev => !prev);
    };

    const filteredStudents = showUnassignedOnly ? unassignedStudents : allStudents;

    const sortStudents = (list: IUser[]) => {
        return list.sort((a, b) => {
            if (a.lastName !== b.lastName) {
                return a.lastName.localeCompare(b.lastName);
            }
            return a.firstName.localeCompare(b.firstName);
        });
    };

    return (
        <div className="h-full flex items-center justify-center p-8">
            <div className="max-w-5xl w-full">
                <form onSubmit={handleSubmit(handleAddButton)}>
                    <div className="flex mb-4 space-x-6">
                        <div className="w-1/2">
                            <label htmlFor="name" className="block mb-1">
                                Class name:
                            </label>
                            <input
                                type="text"
                                id="name"
                                placeholder="Class name"
                                autoComplete="off"
                                {...register("name")}
                                className="border border-gray-300 rounded px-3 py-2 w-full"
                            />
                            {errors.name && <p className="text-red-500 mt-1">{errors.name.message}</p>}
                        </div>
                        <div className="w-1/2">
                            <label htmlFor="classroom" className="block mb-1">
                                Classroom:
                            </label>
                            <input
                                type="text"
                                id="classroom"
                                placeholder="Classroom"
                                autoComplete="off"
                                {...register("classroom")}
                                className="border border-gray-300 rounded px-3 py-2 w-full"
                            />
                            {errors.classroom && <p className="text-red-500 mt-1">{errors.classroom.message}</p>}
                        </div>
                    </div>

                    <div className="mb-4">
                        <label className="block mb-1">Select students:</label>
                        <div className="flex mb-2 items-center">
                            <input
                                type="checkbox"
                                checked={showUnassignedOnly}
                                onChange={handleToggleShowUnassigned}
                                className="mr-2 cursor-pointer"
                            />
                            <span>Show only unassigned students</span>
                        </div>
                        <div className="flex">
                            <div className="w-1/2 pr-2">
                                <ul className="border border-gray-300 rounded p-2 h-64 overflow-y-auto">
                                    {sortStudents(filteredStudents).map(student => (
                                        <li
                                            key={student.id}
                                            className="cursor-pointer hover:bg-gray-200 p-2 flex justify-between"
                                            onClick={() => handleSelectStudent(student)}
                                        >
                                            <span>{student.lastName}, {student.firstName}</span>
                                            <span>{student.email}</span>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                            <div className="w-1/2 pl-2">
                                <ul className="border border-gray-300 rounded p-2 h-64 overflow-y-auto">
                                    {sortStudents(selectedStudents).map(student => (
                                        <li
                                            key={student.id}
                                            className="cursor-pointer hover:bg-gray-200 p-2 flex justify-between"
                                            onClick={() => handleDeselectStudent(student)}
                                        >
                                            <span>{student.lastName}, {student.firstName}</span>
                                            <span>{student.email}</span>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </div>
                        {errors.studentsIds && <p className="text-red-500 mt-1">{errors.studentsIds.message}</p>}
                    </div>

                    <button
                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 transition duration-300"
                        type="submit"
                        disabled={isSubmitting}
                    >
                        Create class
                    </button>

                    {errors.root && <p className="text-red-500 mt-1">{errors.root.message}</p>}
                </form>

                {successMessage && (
                    <div className="mt-4">
                        <h3 className="text-lg font-bold mt-1 text-green-600">{successMessage}</h3>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AddClass;