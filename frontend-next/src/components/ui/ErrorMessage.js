function ErrorMessage({ message, className = "" }) {
  return (
    <p className={`${className} text-danger mt-1 pl-1 text-sm`}>{message}</p>
  );
}

export default ErrorMessage;
