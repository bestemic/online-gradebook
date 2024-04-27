import {Link} from "react-router-dom";

const MainPage = () => {
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
        </div>
    );
};

export default MainPage;