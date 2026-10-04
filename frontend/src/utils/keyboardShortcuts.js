const NON_SUBMIT_INPUT_TYPES = new Set([
	"button",
	"checkbox",
	"file",
	"hidden",
	"radio",
	"reset",
	"submit",
]);

const CONTROLS_WITH_ENTER_BEHAVIOR =
	".el-select, .el-date-editor, .el-cascader, .el-autocomplete, .el-tree-select";

function findPrimaryAction(input) {
	const form = input.closest("form");
	const formButton = form?.querySelector("button.el-button--primary:not(:disabled)");
	if (formButton) return formButton;

	const dialog = input.closest(".el-dialog");
	return dialog?.querySelector(".el-dialog__footer button.el-button--primary:not(:disabled)") || null;
}

function handleEnter(event) {
	if (
		event.key !== "Enter" ||
		event.isComposing ||
		event.shiftKey ||
		event.altKey ||
		event.ctrlKey ||
		event.metaKey ||
		!(event.target instanceof HTMLInputElement) ||
		NON_SUBMIT_INPUT_TYPES.has(event.target.type) ||
		event.target.closest(CONTROLS_WITH_ENTER_BEHAVIOR)
	) {
		return;
	}

	const form = event.target.closest("form");
	if (!form && !event.target.closest(".el-dialog")) return;

	const primaryAction = findPrimaryAction(event.target);
	event.preventDefault();
	primaryAction?.click();
}

export function installEnterShortcuts() {
	document.addEventListener("keydown", handleEnter);
}
