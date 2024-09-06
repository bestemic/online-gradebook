import useAuth from "../../hooks/useAuth.ts";
import useSubject from "../../hooks/useSubject.ts";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import React, {useState} from "react";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {jwtDecode} from "jwt-decode";
import RequireRole from "../wrapper/RequireRole.tsx";
import {ROLES} from "../../constants/roles.ts";
import {z} from "zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {ICreateMaterial} from "../../interfaces/material/CreateMaterialInterface.ts";
import materialService from "../../services/materials.ts";
import {IMaterial} from "../../interfaces/material/MaterialInterface.ts";

const MaterialsTab = () => {
    const {auth} = useAuth();
    const {subject} = useSubject();
    const axiosPrivate = useAxiosPrivate();
    const [isAddingMaterial, setIsAddingMaterial] = useState(false);
    const [materials, setMaterials] = useState<IMaterial[]>([]);
    const [fileName, setFileName] = useState<string | null>(null);

    const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
    const currentUserId: number = decoded?.id || 0;

    const schema = z.object({
        name: z.string().min(2, {message: "Material name is required"}),
        description: z.string().min(2, {message: "Material description is required"}),
        subjectId: z.number().positive({message: "Valid subject ID is required"}).default(subject?.id || 0),
        file: z
            .instanceof(File, {message: "File is required"})
            .refine((file) => file.size > 0, {message: "File is required"})
            .refine((file) => file.size <= 2 * 1024 * 1024, {message: "File size must be less than 2MB"})
    });

    const {
        register,
        handleSubmit,
        setValue,
        setError,
        reset,
        formState: {errors, isSubmitting}
    } = useForm<ICreateMaterial>({resolver: zodResolver(schema)});

    const handleAddMaterialSection = (event: React.FormEvent) => {
        event.preventDefault();
        reset();
        setFileName(null);
        setIsAddingMaterial(!isAddingMaterial);
    }

    const handleSubmitAdd: SubmitHandler<ICreateMaterial> = async (data) => {
        materialService.create(axiosPrivate, data)
            .then((data: IMaterial) => {
                setMaterials([...materials, data]);
                reset()
                setIsAddingMaterial(false);
            })
            .catch(error => {
                setError("root", {message: error.message});
            });
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            if (file.size > 2 * 1024 * 1024) {
                setError("file", {message: "File size must be less than 2MB"});
                return;
            }
            setValue('file', file);
            setFileName(file.name);
        }
    };

    return (
        <div className="h-full flex flex-col items-center justify-center">
            <div className="w-full max-w-3xl mt-6">
                {isAddingMaterial ? (
                    <form onSubmit={handleSubmit(handleSubmitAdd)}>
                        <div className="mb-4">
                            <label htmlFor="name" className="block mb-1">
                                Name:
                            </label>
                            <input
                                type="text"
                                id="name"
                                placeholder="Material name"
                                autoComplete="off"
                                {...register('name')}
                                className="border border-gray-300 rounded px-3 py-2 w-full"
                            />
                            {errors.name && <p className="text-red-500 mt-1">{errors.name.message}</p>}
                        </div>

                        <div className="mb-4">
                            <label htmlFor="description" className="block mb-1">
                                Description:
                            </label>
                            <input
                                type="text"
                                id="description"
                                placeholder="Material description"
                                autoComplete="off"
                                {...register('description')}
                                className="border border-gray-300 rounded px-3 py-2 w-full"
                            />
                            {errors.description && <p className="text-red-500 mt-1">{errors.description.message}</p>}
                        </div>

                        <div className="mb-4">
                            <label htmlFor="file" className="block mb-1">
                                File:
                            </label>
                            <div className="relative">
                                <input
                                    type="file"
                                    id="file"
                                    onChange={handleFileChange}
                                    className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                                />
                                <button
                                    type="button"
                                    className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-400 transition duration-300 cursor-pointer"
                                >
                                    Select File
                                </button>
                                {fileName && <span className="ml-4">{fileName}</span>}
                            </div>
                            {errors.file && <p className="text-red-500 mt-1">{errors.file.message}</p>}
                        </div>

                        <div className="mt-4 flex space-x-4">
                            <button
                                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 transition duration-300"
                                type="submit"
                                disabled={isSubmitting}
                            >
                                Upload material
                            </button>
                            <button
                                onClick={handleAddMaterialSection}
                                className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition duration-300"
                            >
                                Cancel
                            </button>
                        </div>
                        {errors.root && <p className="text-red-500 mt-1">{errors.root.message}</p>}
                    </form>
                ) : (
                    <RequireRole allowedRoles={[ROLES.Teacher]}>
                        {currentUserId === subject?.teacher.id && (
                            <button
                                onClick={handleAddMaterialSection}
                                className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                            >
                                Upload material
                            </button>
                        )}
                    </RequireRole>
                )}
            </div>
        </div>
    );
};

export default MaterialsTab;