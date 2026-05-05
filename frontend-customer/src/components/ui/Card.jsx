// Reusable white card wrapper for dashboard sections and forms.
export default function Card({
  children,
  className = "",
  padding = true,
  onClick,
}) {
  // Adds pointer styling only when the card is clickable.
  const clickable = onClick
    ? "cursor-pointer hover:-translate-y-0.5 hover:shadow-xl"
    : "";

  return (
    <div
      onClick={onClick}
      className={`
        rounded-3xl border border-slate-200 bg-white shadow-lg shadow-slate-200/70 transition
        ${padding ? "p-6" : ""}
        ${clickable}
        ${className}
      `}
    >
      {children}
    </div>
  );
}