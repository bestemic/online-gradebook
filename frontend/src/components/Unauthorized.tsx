import {useNavigate} from "react-router-dom";

const Unauthorized = () => {
    const navigate = useNavigate();
    const goBack = () => navigate(-1);

    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-10 rounded shadow-2xl max-w-md w-full text-center">
                <h1 className="text-4xl font-bold text-gray-800 mb-6">Oops!</h1>
                <h2 className="text-2xl font-semibold text-gray-700 mb-5">Unauthorized</h2>
                <button
                    onClick={goBack}
                    className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300"
                >
                    Go Back
                </button>
            </div>
        </div>
    );
};

export default Unauthorized;