import {z} from "zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {ICreateLesson} from "../../interfaces/lesson/CreateLessonInterface.ts";
import {useState} from "react";
import {ROLES} from "../../constants/roles.ts";
import RequireRole from "../wrapper/RequireRole.tsx";
import useSubject from "../../hooks/useSubject.ts";
import {useNavigate} from "react-router-dom";
import {ILesson} from "../../interfaces/lesson/LessonInterface.ts";
import lessonsService from "../../services/lessons.ts";


const AddLesson = () => {
    const {subject} = useSubject();
    const axiosPrivate = useAxiosPrivate();
    const [isAddingLesson, setIsAddingLesson] = useState(false);
    const navigate = useNavigate();

    const schema = z.object({
        title: z.string().min(2, {message: "Lesson title is required"}),
        description: z.string().min(2, {message: "Lesson description is required"}),
        date: z.string().default(new Date().toISOString().split('T')[0]),
        subjectId: z.number().int().default(subject?.id || 0)
    });

    const {
        register,
        handleSubmit,
        setError,
        reset,
        formState: {errors, isSubmitting},
    } = useForm<ICreateLesson>({
        resolver: zodResolver(schema),
        defaultValues: {
            date: new Date().toISOString().split('T')[0],
            subjectId: subject?.id || 0
        },
    });

    const handleAddLesson: SubmitHandler<ICreateLesson> = async (data) => {
        lessonsService.create(axiosPrivate, data)
            .then((data: ILesson) => {
                handleAddLessonSection();
                console.log(data);
                navigate(`/lessons/${data.id}`);
            })
            .catch(error => {
                setError("root", {
                    type: "manual",
                    message: error.message
                });
            });
    };

    const handleAddLessonSection = () => {
        reset();
        setIsAddingLesson(!isAddingLesson);
    }

    return (
        <div>
            {isAddingLesson ? (
                <>
                    <form onSubmit={handleSubmit(handleAddLesson)}>
                        <div className="mb-4">
                            <label htmlFor="title" className="block mb-1">
                                Lesson Title:
                            </label>
                            <input
                                type="text"
                                id="title"
                                placeholder="Lesson title"
                                autoComplete="off"
                                {...register("title")}
                                className="border border-gray-300 rounded px-3 py-2 w-full"/>
                            {errors.title && <p className="text-red-500 mt-1">{errors.title.message}</p>}
                        </div>

                        <div className="mb-4">
                            <label htmlFor="description" className="block mb-1">
                                Description:
                            </label>
                            <textarea
                                id="description"
                                {...register("description")}
                                className="border border-gray-300 rounded px-3 py-2 w-full"/>
                            {errors.description && <p className="text-red-500 mt-1">{errors.description.message}</p>}
                        </div>

                        <button
                            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 transition duration-300"
                            type="submit"
                            disabled={isSubmitting}
                        >
                            Add Lesson
                        </button>

                        {errors.root && <p className="text-red-500 mt-1">{errors.root.message}</p>}
                    </form>
                </>
            ) : (
                <RequireRole allowedRoles={[ROLES.Teacher]}>
                    <button
                        onClick={handleAddLessonSection}
                        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                    >
                        Add Lesson
                    </button>
                </RequireRole>
            )}
        </div>
    );
};

export default AddLesson;