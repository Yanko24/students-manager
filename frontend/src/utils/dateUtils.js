const pad = (value) => String(value).padStart(2, "0");

const formatLocalDate = (date) =>
	`${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;

/** Format API dates as YYYY-MM-DD without timezone shifting date-only values. */
export const formatDate = (value) => {
	if (!value) return "";
	if (typeof value === "string") {
		const match = value.trim().match(/^(\d{4}-\d{2}-\d{2})/);
		if (match) return match[1];
	}
	const date = value instanceof Date ? value : new Date(value);
	return Number.isNaN(date.getTime()) ? String(value) : formatLocalDate(date);
};

/** Format ISO/API timestamps as YYYY-MM-DD HH:mm:ss for readable page display. */
export const formatDateTime = (value) => {
	if (!value) return "";
	if (typeof value === "string") {
		const match = value.trim().match(/^(\d{4}-\d{2}-\d{2})[T ](\d{2}:\d{2})(?::(\d{2}))?/);
		if (match) return `${match[1]} ${match[2]}:${match[3] || "00"}`;
		return formatDate(value);
	}
	const date = value instanceof Date ? value : new Date(value);
	if (Number.isNaN(date.getTime())) return String(value);
	return `${formatLocalDate(date)} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

/**
 * 将日期字符串转换为 Date 对象
 * @param {string} dateStr - YYYY-MM-DD 格式的日期字符串
 * @returns {Date} Date 对象
 */
export const parseDate = (dateStr) => {
	if (!dateStr) return null;
	const match = String(dateStr).match(/^(\d{4})-(\d{2})-(\d{2})$/);
	if (match) return new Date(Number(match[1]), Number(match[2]) - 1, Number(match[3]));
	return new Date(dateStr);
};
