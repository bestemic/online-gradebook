import {useEffect, useState} from "react";
import useAxiosPrivate from "../hooks/useAxiosPrivate.ts";
import {useLocation, useNavigate} from "react-router-dom";

const Admin = () => {
    const [users, setUsers] = useState();
    const axiosPrivate = useAxiosPrivate();
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        axiosPrivate.get('/users')
            .then(response => {
                console.log(response)
                setUsers(response.data);
            })
            .catch(error => {
                console.error(error);
                navigate('/login', {state: {from: location}, replace: true});
            });
    }, []);

    return (
        <div>
            <h1>Admin page</h1>
            <h2>Users List</h2>
            {users?.length
                ? (
                    <ul>
                        {users.map((user, i) => (
                            <li key={i}>
                                <p>Email: {user?.email}</p>
                                <p>Roles: {user?.roles[0].name}</p>
                            </li>
                        ))}
                    </ul>
                ) : <p>No users to display</p>
            }
        </div>
    );
};

export default Admin;