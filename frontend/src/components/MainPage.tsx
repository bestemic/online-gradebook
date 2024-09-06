import {Link} from "react-router-dom";
import RequireRole from "./wrapper/RequireRole.tsx";
import {ROLES} from "../constants/roles.ts";

const MainPage = () => {
    return (
        <div className="h-full  flex items-center justify-center">
            <div className="flex w-4/5 flex-wrap justify-center gap-8  items-stretch">
                <RequireRole allowedRoles={[ROLES.Admin, ROLES.Teacher, ROLES.Student]}>
                    <Link to="/subjects" className="w-1/4">
                        <div
                            className="bg-blue-500 text-white p-16 rounded-xl shadow-xl hover:bg-blue-600 transition duration-300 text-center">
                            <h2 className="text-3xl font-semibold">Subjects</h2>
                            <p className="mt-4 text-xl">View and explore subjects</p>
                        </div>
                    </Link>
                </RequireRole>
                <RequireRole allowedRoles={[ROLES.Admin]}>
                    <Link to="/classes" className="w-1/4">
                        <div
                            className="bg-blue-500 text-white p-16 rounded-xl shadow-xl hover:bg-blue-600 transition duration-300 text-center">
                            <h2 className="text-3xl font-semibold">Classes</h2>
                            <p className="mt-4 text-xl">Manage classes</p>
                        </div>
                    </Link>
                    <Link to="/users" className="w-1/4">
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