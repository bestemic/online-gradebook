import {SubmitHandler, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import subjectService from "../../services/subjects.ts";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import {ICreateSubject} from "../../interfaces/CreateSubjectInterface.ts";
import {useState} from "react";

const AddSubject = () => {
    const schema = z.object({
        name: z.string().min(2, {message: "Subject name is required"}),
    });

    const {
        register,
        handleSubmit,
        setError,
        reset,
        formState: {errors, isSubmitting}
    } = useForm<ICreateSubject>({resolver: zodResolver(schema)});

    const axiosPrivate = useAxiosPrivate();
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const handleAddButton: SubmitHandler<ICreateSubject> = async (data) => {
        subjectService.create(axiosPrivate, data)
            .then(() => {
                setSuccessMessage(`Created subject with name: ${data.name}`);
                reset();
            })
            .catch(error => {
                setError("root", {message: error.message});
            });
    };

    return (
        <div className="h-full flex items-center justify-center p-8">
            <div className="max-w-xl w-full">
                <form onSubmit={handleSubmit(handleAddButton)}>
                    <div className="mb-4">
                        <label htmlFor="name" className="block mb-1">
                            Subject name:
                        </label>
                        <input
                            type="text"
                            id="name"
                            placeholder="Subject name"
                            autoComplete="off"
                            {...register("name")}
                            className="border border-gray-300 rounded px-3 py-2 w-full"
                        />
                        {errors.name && <p className="text-red-500 mt-1">{errors.name.message}</p>}
                    </div>
                    <button
                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 transition duration-300"
                        type="submit"
                        disabled={isSubmitting}
                    >
                        Create Subject
                    </button>
                    {errors.root && <p className="text-red-500 mt-1">{errors.root.message}</p>}
                </form>

                {successMessage && (
                    <div className="mt-4">
                        <h3 className="text-lg font-bold mt-1 text-green-600">
                            {successMessage}
                        </h3>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AddSubject;