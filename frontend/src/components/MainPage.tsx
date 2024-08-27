import {Link} from "react-router-dom";
import RequireRole from "./wrapper/RequireRole.tsx";
import {ROLES} from "../constants/roles.ts";

const MainPage = () => {
    return (
        <div className="h-full flex items-center justify-center">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                <RequireRole allowedRoles={[ROLES.Admin]}>
                    <Link to="/classes">
                        <div
                            className="bg-blue-500 text-white p-16 rounded-xl shadow-xl hover:bg-blue-600 transition duration-300 text-center">
                            <h2 className="text-3xl font-semibold">Classes</h2>
                            <p className="mt-4 text-xl">Manage classes</p>
                        </div>
                    </Link>
                    <Link to="/subjects/admin">
                        <div
                            className="bg-blue-500 text-white p-16 rounded-xl shadow-xl hover:bg-blue-600 transition duration-300 text-center">
                            <h2 className="text-3xl font-semibold">Subjects</h2>
                            <p className="mt-4 text-xl">Manage subjects</p>
                        </div>
                    </Link>
                    <Link to="/users">
                        <div
                            className="bg-blue-500 text-white p-16 rounded-xl shadow-xl hover:bg-blue-600 transition duration-300 text-center">
                            <h2 className="text-3xl font-semibold">Users</h2>
                            <p className="mt-4 text-xl">Manage users</p>
                        </div>
                    </Link>
                </RequireRole>
            </div>
        </div>
    );
};

export default MainPage;