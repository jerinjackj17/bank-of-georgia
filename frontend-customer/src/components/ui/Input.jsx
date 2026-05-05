// Reusable form input with label, optional icon, and error message.
export default function Input({
  label,
  id,
  name,
  type = "text",
  placeholder = "",
  value,
  onChange,
  error = "",
  icon: Icon = null,
  disabled = false,
  required = false,
  className = "",
}) {
  // Adds left padding when an icon is shown.
  const inputPadding = Icon ? "pl-11" : "pl-4";

  return (
    <div className={`flex flex-col gap-2 ${className}`}>
      {label && (
        <label htmlFor={id} className="text-sm font-bold text-slate-800">
          {label}
          {required && <span className="ml-1 text-red-600">*</span>}
        </label>
      )}

      <div className="relative">
        {Icon && (
          <Icon
            size={18}
            className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-slate-400"
          />
        )}

        <input
          id={id}
          name={name}
          type={type}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          disabled={disabled}
          required={required}
          className={`
            h-12 w-full rounded-xl border bg-white pr-4 text-sm font-medium text-slate-950
            placeholder:text-slate-500 placeholder:opacity-100 outline-none transition
            focus:border-blue-700 focus:ring-4 focus:ring-blue-100
            disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-500
            ${inputPadding}
            ${error ? "border-red-500 focus:border-red-600 focus:ring-red-100" : "border-slate-300"}
          `}
        />
      </div>

      {error && (
        <p className="text-sm font-medium text-red-600">
          {error}
        </p>
      )}
    </div>
  );
}