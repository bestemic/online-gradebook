import Login from "./components/Login.tsx";
import {Route, Routes} from "react-router-dom";
import UserManagement from "./components/UserManagement.tsx";
import NotFound from "./components/NotFound.tsx";
import Unauthorized from "./components/Unauthorized.tsx";
import MainPage from "./components/MainPage.tsx";
import RequireAuth from "./components/RequireAuth.tsx";
import Navigation from "./components/Navigation.tsx";
import {ROLES} from "./constants/roles.ts";
import Classes from "./components/Classes.tsx";

function App() {

    return (
        <div className="App">
            <Routes>
                <Route path="/" element={<Navigation/>}>
                    <Route path="login" element={<Login/>}/>
                    <Route path="unauthorized" element={<Unauthorized/>}/>

                    <Route element={<RequireAuth allowedRoles={[ROLES.Admin, ROLES.Teacher, ROLES.Student]}/>}>
                        <Route index element={<MainPage/>}/>
                    </Route>

                    <Route element={<RequireAuth allowedRoles={[ROLES.Admin]}/>}>
                        <Route path="users" element={<UserManagement/>}/>
                        <Route path="classes" element={<Classes/>}/>
                    </Route>

                    <Route path="*" element={<NotFound/>}/>
                </Route>
            </Routes>
        </div>
    )
}

export default App
