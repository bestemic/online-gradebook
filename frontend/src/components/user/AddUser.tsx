import {SubmitHandler, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {ROLES} from "../../constants/roles.ts";
import {useEffect, useState} from "react";
import userService from "../../services/users.ts";
import roleService from "../../services/roles.ts";
import {ICreateUser} from "../../interfaces/CreateUserInterface.ts";
import {IRole} from "../../interfaces/RoleInterface.ts";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";


const AddUser = () => {
    const schema = z.object({
        firstName: z.string().min(2, {message: "First name is required"}),
        lastName: z.string().min(2, {message: "Last name is required"}),
        email: z.string().email("Invalid email address"),
        birth: z.coerce.string()
            .date()
            .refine(date => new Date(date) <= new Date(), {
                message: "Birth date cannot be in the future"
            })
            .optional()
            .or(z.literal('')),
        phoneNumber: z.string()
            .regex(/^\d{9}$/, {message: "Invalid phone number"})
            .optional()
            .or(z.literal('')),
        roleIds: z.array(z.coerce.number({message: "Not a number"}), {message: "At least one role is required"}).nonempty({message: "At least one role is required"}),
    }).refine(data => {
        const studentRoleIndex = mapRoleNameToIndex(ROLES.Student);
        const hasStudentRole = data.roleIds.includes(studentRoleIndex);
        return !(hasStudentRole && data.birth == '');
    }, {
        message: "Birth date is required for selected role",
        path: ["birth"]
    }).refine(data => {
        const studentRoleIndex = mapRoleNameToIndex(ROLES.Student);
        const hasStudentRole = data.roleIds.includes(studentRoleIndex);
        const hasOtherRoles = data.roleIds.length > 1 || (data.roleIds.length === 1 && !hasStudentRole);
        return !(hasOtherRoles && data.phoneNumber == '');
    }, {
        message: "Phone number is required for selected role",
        path: ["phoneNumber"]
    });

    const {
        register,
        handleSubmit,
        getValues,
        setValue,
        setError,
        reset,
        formState: {errors, isSubmitting}
    } = useForm<ICreateUser>({resolver: zodResolver(schema)});

    const axiosPrivate = useAxiosPrivate();
    const [roles, setRoles] = useState<IRole[]>([]);
    const [createdPassword, setCreatedPassword] = useState<string | null>(null);

    useEffect(() => {
        roleService.getAll(axiosPrivate)
            .then(roles => {
                setRoles(roles);
            })
            .catch(error => {
                setError("root", {message: error.message});
            });
    }, [axiosPrivate, setError])

    const mapRoleNameToIndex = (name: string): number => {
        const role = roles.find(role => role.name === name);
        return role!.id;
    };

    const handleAddButton: SubmitHandler<ICreateUser> = async (data) => {
        userService.create(axiosPrivate, data)
            .then(password => {
                setCreatedPassword(password);
                setValue('roleIds', []);
                reset()
            })
            .catch(error => {
                setError("root", {message: error.message});
            });
    };

    const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const roleName = e.target.value;
        const roleIndex = mapRoleNameToIndex(roleName);
        const currentRoleIds = getValues('roleIds') || [];

        if (e.target.checked) {
            setValue('roleIds', [...currentRoleIds, roleIndex], {shouldValidate: true});
        } else {
            const updatedRoleIds = currentRoleIds.filter(item => item !== roleIndex) as [number, ...number[]];
            setValue('roleIds', updatedRoleIds, {shouldValidate: true});
        }
    };

    const copyToClipboard = (text: string) => {
        navigator.clipboard.writeText(text);
    };

    return (
        <div className="h-full flex items-center justify-center p-8">
            <div className="max-w-xl w-full">
                <form onSubmit={handleSubmit(handleAddButton)}>
                    <div className="flex mb-4 space-x-6">
                        <div className="w-1/2">
                            <label htmlFor="firstName" className="block mb-1">
                                First name:
                            </label>
                            <input
                                type="text"
                                id="firstName"
                                placeholder="First name"
                                autoComplete="off"
                                {...register("firstName")}
                                className="border border-gray-300 rounded px-3 py-2 w-full"
                            />
                            {errors.firstName && <p className="text-red-500 mt-1">{errors.firstName.message}</p>}
                        </div>
                        <div className="w-1/2">
                            <label htmlFor="lastName" className="block mb-1">
                                Last name:
                            </label>
                            <input
                                type="text"
                                id="lastName"
                                placeholder="Last name"
                                autoComplete="off"
                                {...register("lastName")}
                                className="border border-gray-300 rounded px-3 py-2 w-full"
                            />
                            {errors.lastName && <p className="text-red-500 mt-1">{errors.lastName.message}</p>}
                        </div>
                    </div>
                    <div className="mb-4">
                        <label htmlFor="email" className="block mb-1">
                            Email:
                        </label>
                        <input
                            type="text"
                            id="email"
                            placeholder="Email"
                            autoComplete="off"
                            {...register("email")}
                            className="border border-gray-300 rounded px-3 py-2 w-full"
                        />
                        {errors.email && <p className="text-red-500 mt-1">{errors.email.message}</p>}
                    </div>
                    <div className="flex mb-4 space-x-6">
                        <div className="w-1/2">
                            <label htmlFor="birth" className="block mb-1">
                                Birth date:
                            </label>
                            <input
                                type="date"
                                id="birth"
                                placeholder="Birth date"
                                autoComplete="off"
                                max={new Date().toISOString().split('T')[0]}
                                {...register("birth")}
                                className="border border-gray-300 rounded px-3 py-2 w-full"
                            />
                            {errors.birth && <p className="text-red-500 mt-1">{errors.birth.message}</p>}
                        </div>
                        <div className="w-1/2">
                            <label htmlFor="phoneNumber" className="block mb-1">
                                Phone number:
                            </label>
                            <input
                                type="text"
                                id="phoneNumber"
                                placeholder="Phone number"
                                autoComplete="off"
                                {...register("phoneNumber")}
                                className="border border-gray-300 rounded px-3 py-2 w-full"
                            />
                            {errors.phoneNumber && <p className="text-red-500 mt-1">{errors.phoneNumber.message}</p>}
                        </div>
                    </div>
                    <div className="mb-4">
                        <p className="block mb-1">Roles:</p>
                        <input type='checkbox' id="roleIds" onChange={handleCheckboxChange} value={ROLES.Student}/>
                        <label htmlFor="roleIds" className="ml-2 mr-4">Student</label>

                        <input type='checkbox' id="roleIds" onChange={handleCheckboxChange} value={ROLES.Teacher}/>
                        <label htmlFor="roleIds" className="ml-2 mr-4">Teacher</label>

                        <input type='checkbox' id="roleIds" onChange={handleCheckboxChange} value={ROLES.Admin}/>
                        <label htmlFor="roleIds" className="ml-2 mr-4">Admin</label>

                        {errors.roleIds && <p className="text-red-500 mt-1">{errors.roleIds.message}</p>}
                    </div>

                    <button
                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-500 transition duration-300"
                        type="submit"
                        disabled={isSubmitting}
                    >
                        Create user
                    </button>
                    {errors.root && <p className="text-red-500 mt-1">{errors.root.message}</p>}
                </form>

                {createdPassword && (
                    <div className="mt-4">
                        <h3 className="text-lg font-bold">Created Password</h3>
                        <div className="border border-gray-300 rounded p-2 mt-1 flex justify-between items-center">
                            <span>{createdPassword}</span>
                            <button
                                className="bg-blue-600 text-white px-4 py-1 rounded hover:bg-blue-500 transition duration-300"
                                onClick={() => copyToClipboard(createdPassword)}
                            >
                                Copy
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AddUser;