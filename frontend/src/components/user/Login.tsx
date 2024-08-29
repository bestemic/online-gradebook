import useAuth from "../../hooks/useAuth.ts";
import {useLocation, useNavigate} from "react-router-dom";
import userService from "../../services/users.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {z} from "zod";
import JwtInterface from "../../interfaces/helper/JwtInterface.ts";
import {jwtDecode} from "jwt-decode";

const schema = z.object({
    email: z.string().email("Invalid email address"),
    password: z.string().min(4, "Password must be at least 4 characters long"),
});

type FormFields = z.infer<typeof schema>;

const Login = () => {
    const {setAuth} = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/";

    const {
        register,
        handleSubmit,
        setError,
        reset,
        formState: {errors, isSubmitting}
    } = useForm<FormFields>({resolver: zodResolver(schema)});

    const handleLoginButton: SubmitHandler<FormFields> = async (data) => {
        userService.login(data.email, data.password)
            .then(token => {
                setAuth({email: data.email, token: token});
                reset();

                const decoded: JwtInterface | undefined = token ? jwtDecode(token) : undefined;
                const changed: boolean = decoded?.changed || false;

                if (!changed) {
                    navigate("/change-password", {replace: true, state: {from: from}});
                } else {
                    navigate(from, {replace: true});
                }
            })
            .catch(error => {
                setError("root", {message: error.message});
            });
    };

    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-8 rounded shadow-2xl max-w-md w-full">
                <h2 className="text-3xl font-bold mb-4">Sign In</h2>
                <form onSubmit={handleSubmit(handleLoginButton)}>
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
                    <div className="mb-4">
                        <label htmlFor="password" className="block mb-1">
                            Password:
                        </label>
                        <input
                            type="password"
                            id="password"
                            placeholder="Password"
                            {...register("password")}
                            className="border border-gray-300 rounded px-3 py-2 w-full"
                        />
                        {errors.password && <p className="text-red-500 mt-1">{errors.password.message}</p>}
                    </div>
                    <button
                        className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                        type="submit"
                        disabled={isSubmitting}
                    >
                        Sign In
                    </button>
                    {errors.root && <p className="text-red-500 mt-1">{errors.root.message}</p>}
                </form>
            </div>
        </div>
    );
};

export default Login;