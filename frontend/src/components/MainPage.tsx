import {Link, useNavigate} from "react-router-dom";
import useAuth from "../hooks/useAuth.ts";

const MainPage = () => {
    const navigate = useNavigate();
    const {setAuth} = useAuth();

    const signOut = async () => {
        setAuth({email: null, token: null});
        navigate('/login');
    }

    return (
        <div>
            <h1>Main Page</h1>
            <h2>You are logged in!</h2>
            <br/>
            <Link to="/admin">Go to Admin page</Link>
            <br/>
            <Link to="/teacher">Go to Teacher page</Link>
            <br/>
            <Link to="/student">Go to Student page</Link>
            <br/>
            <button onClick={signOut}>Sign Out</button>
        </div>
    );
};

export default MainPage;