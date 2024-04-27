import {Link, Outlet, useNavigate} from "react-router-dom";
import useAuth from "../hooks/useAuth.ts";

const Navigation = () => {
    const navigate = useNavigate();
    const {auth, setAuth} = useAuth();

    const signOut = async () => {
        setAuth({email: null, token: null});
        navigate('/login');
    }

    return (
        <div className="bg-gray-100 h-screen flex flex-col">

            {auth?.token && (
                <nav className="bg-gray-800 py-3 sm:px-2 lg:px-5">
                    <ul className="flex justify-end space-x-5 text-white text-lg">
                        <li>
                            <Link to="/profile" className="hover:text-gray-400">
                                Profile
                            </Link>
                        </li>
                        <li>
                            <button onClick={signOut} className="hover:text-gray-400">
                                Logout
                            </button>
                        </li>
                    </ul>
                </nav>
            )}

            <div className="flex-1 p-10">
                <Outlet/>
            </div>
        </div>
    );
};

export default Navigation;
