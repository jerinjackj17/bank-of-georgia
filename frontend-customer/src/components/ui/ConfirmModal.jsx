import Button from "./Button";

// Reusable confirmation dialog for logout, delete, and important actions.
export default function ConfirmModal({
  open,
  title = "Are you sure?",
  message = "This action cannot be undone.",
  confirmLabel = "Confirm",
  cancelLabel = "Cancel",
  variant = "primary",
  onConfirm,
  onCancel,
  loading = false,
}) {
  // Does not render the modal when it is closed.
  if (!open) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/70 px-4 backdrop-blur-sm">
      <div className="w-full max-w-md overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-2xl shadow-slate-950/30">
        <div className="border-b border-slate-200 px-6 py-5">
          <h3 className="text-xl font-bold text-slate-950">
            {title}
          </h3>
        </div>

        <div className="px-6 py-5">
          <p className="text-sm leading-6 text-slate-600">
            {message}
          </p>
        </div>

        <div className="flex justify-end gap-3 border-t border-slate-200 bg-slate-50 px-6 py-5">
          <Button
            variant="outline"
            onClick={onCancel}
            disabled={loading}
          >
            {cancelLabel}
          </Button>

          <Button
            variant={variant}
            onClick={onConfirm}
            loading={loading}
          >
            {confirmLabel}
          </Button>
        </div>
      </div>
    </div>
  );
}