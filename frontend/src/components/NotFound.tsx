const NotFound = () => {
    return (
        <div className="h-full flex items-center justify-center">
            <div className="bg-white p-10 rounded shadow-2xl max-w-md w-full text-center">
                <h1 className="text-4xl font-bold text-gray-800 mb-6">Oops!</h1>
                <h2 className="text-2xl font-semibold text-gray-700">Page Not Found</h2>
            </div>
        </div>
    );
};

export default NotFound;
