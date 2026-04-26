function ErrorMessage({ message, className = "" }) {
  return (
    <p className={`${className} mt-1 pl-1 text-sm text-red-400`}>{message}</p>
  );
}

export default ErrorMessage;
