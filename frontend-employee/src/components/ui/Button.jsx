// Reusable button component for forms, actions, and navigation.
export default function Button({
  children,
  onClick,
  type = "button",
  variant = "primary",
  loading = false,
  disabled = false,
  fullWidth = false,
  className = "",
}) {
  // Shared button layout, spacing, font, and focus styles.
  const base =
    "inline-flex h-11 items-center justify-center gap-2 rounded-xl px-5 text-sm font-bold shadow-sm outline-none transition focus:ring-4 disabled:cursor-not-allowed disabled:opacity-60";

  // Button color styles by use case.
  const variants = {
    primary:
      "bg-blue-900 text-white shadow-blue-900/20 hover:bg-blue-800 focus:ring-blue-100 active:scale-[0.98]",
    outline:
      "border border-slate-300 bg-white text-slate-700 hover:border-blue-300 hover:bg-blue-50 hover:text-blue-900 focus:ring-blue-100",
    danger:
      "bg-red-600 text-white shadow-red-600/20 hover:bg-red-700 focus:ring-red-100 active:scale-[0.98]",
    ghost:
      "bg-transparent text-slate-600 shadow-none hover:bg-slate-100 hover:text-slate-950 focus:ring-slate-100",
  };

  // Prevents invalid variant names from breaking styling.
  const variantClass = variants[variant] || variants.primary;

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled || loading}
      className={`${base} ${variantClass} ${fullWidth ? "w-full" : ""} ${className}`}
    >
      {loading && (
        <svg
          className="h-4 w-4 animate-spin"
          viewBox="0 0 24 24"
          fill="none"
        >
          <circle
            className="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            strokeWidth="4"
          />

          <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
          />
        </svg>
      )}

      <span>{children}</span>
    </button>
  );
}