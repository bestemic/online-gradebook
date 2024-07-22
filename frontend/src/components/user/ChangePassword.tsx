import {useLocation, useNavigate} from "react-router-dom";
import {z} from "zod";
import {zodResolver} from "@hookform/resolvers/zod";
import {SubmitHandler, useForm} from "react-hook-form";
import userService from "../../services/users.ts";
import JwtInterface from "../../interfaces/JwtInterface.ts";
import {jwtDecode} from "jwt-decode";
import useAxiosPrivate from "../../hooks/useAxiosPrivate.ts";
import useAuth from "../../hooks/useAuth.ts";

const schema = z.object({
    currentPassword: z.string().min(4, "Current Password must be at least 4 characters long"),
    newPassword: z.string().min(4, "New Password must be at least 4 characters long"),
    confirmNewPassword: z.string().min(4, "Confirm New Password must be at least 4 characters long"),
});

type FormFields = z.infer<typeof schema>;

const ChangePassword = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const axiosPrivate = useAxiosPrivate();
    const {auth} = useAuth();

    const {
        register,
        handleSubmit,
        setError,
        reset,
        formState: {errors, isSubmitting},
    } = useForm<FormFields>({resolver: zodResolver(schema)});

    const handleChangePassword: SubmitHandler<FormFields> = async (data) => {
        if (data.newPassword !== data.confirmNewPassword) {
            setError("confirmNewPassword", {message: "Passwords do not match"});
            return;
        }

        const decoded: JwtInterface | undefined = auth?.token ? jwtDecode(auth.token) : undefined;
        const userId: number = decoded?.id || 0;

        userService.changePassword(axiosPrivate, userId, data.currentPassword, data.newPassword)
            .then(() => {
                    reset();
                    if (location.state && location.state.from) {
                        navigate(location.state.from, {replace: true});
                    } else {
                        navigate("/", {replace: true});
                    }
                }
            )
            .catch(error => {
                setError("root", {message: error.message});
            });
    };

    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-8 rounded shadow-2xl max-w-md w-full">
                <h2 className="text-3xl font-bold mb-4">Change Password</h2>
                <form onSubmit={handleSubmit(handleChangePassword)}>
                    <div className="mb-4">
                        <label htmlFor="currentPassword" className="block mb-1">
                            Current password:
                        </label>
                        <input
                            type="password"
                            id="currentPassword"
                            placeholder="Current password"
                            {...register("currentPassword")}
                            className="border border-gray-300 rounded px-3 py-2 w-full"
                        />
                        {errors.currentPassword &&
                            <p className="text-red-500 mt-1">{errors.currentPassword.message}</p>}
                    </div>
                    <div className="mb-4">
                        <label htmlFor="newPassword" className="block mb-1">
                            New password:
                        </label>
                        <input
                            type="password"
                            id="newPassword"
                            placeholder="New password"
                            {...register("newPassword")}
                            className="border border-gray-300 rounded px-3 py-2 w-full"
                        />
                        {errors.newPassword && <p className="text-red-500 mt-1">{errors.newPassword.message}</p>}
                    </div>
                    <div className="mb-4">
                        <label htmlFor="confirmNewPassword" className="block mb-1">
                            Confirm new password:
                        </label>
                        <input
                            type="password"
                            id="confirmNewPassword"
                            placeholder="Confirm new password"
                            {...register("confirmNewPassword")}
                            className="border border-gray-300 rounded px-3 py-2 w-full"
                        />
                        {errors.confirmNewPassword && (
                            <p className="text-red-500 mt-1">{errors.confirmNewPassword.message}</p>
                        )}
                    </div>
                    <button
                        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                        type="submit"
                        disabled={isSubmitting}
                    >
                        Change password
                    </button>
                    {errors.root && <p className="text-red-500 mt-1">{errors.root.message}</p>}
                </form>
            </div>
        </div>
    );
};

export default ChangePassword;