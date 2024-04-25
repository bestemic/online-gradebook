import {useState} from "react";
import {Link, Outlet, useNavigate} from "react-router-dom";

const Navigation = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(true);
    const navigate = useNavigate();

    const handleLogout = () => {
        setIsLoggedIn(false);
        navigate("/login");
    };

    return (
        <div className="bg-gray-100 h-screen flex flex-col">
            <nav className="bg-gray-800 py-4">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex justify-between items-center">
                    <div className="flex-shrink-0">
                        <Link to="/" className="text-white text-2xl font-bold">
                            Your App
                        </Link>
                    </div>
                    {isLoggedIn && (
                        <div>
                            <ul className="flex space-x-4 text-white">
                                <li>
                                    <Link to="/admin" className="hover:text-gray-300">
                                        Admin
                                    </Link>
                                </li>
                                <li>
                                    <Link to="/teacher" className="hover:text-gray-300">
                                        Teacher
                                    </Link>
                                </li>
                                <li>
                                    <Link to="/student" className="hover:text-gray-300">
                                        Student
                                    </Link>
                                </li>
                                <li>
                                    <button
                                        onClick={handleLogout}
                                        className="hover:text-gray-300 focus:outline-none"
                                    >
                                        Logout
                                    </button>
                                </li>
                            </ul>
                        </div>
                    )}
                </div>
            </nav>

            <div className="flex-1 p-10">
                <Outlet/>
            </div>
        </div>
    );
};

export default Navigation;
