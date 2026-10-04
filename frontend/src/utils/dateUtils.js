/**
 * 格式化日期为 YYYY-MM-DD 格式
 * @param {string|Date} date - 要格式化的日期
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date) => {
	if (!date) return "";
	// 如果是字符串格式的日期，直接返回
	if (typeof date === "string" && date.match(/^\d{4}-\d{2}-\d{2}$/)) {
		return date;
	}
	// 如果是 Date 对象或时间戳，进行格式化
	const d = new Date(date);
	const year = d.getFullYear();
	const month = String(d.getMonth() + 1).padStart(2, "0");
	const day = String(d.getDate()).padStart(2, "0");
	return `${year}-${month}-${day}`;
};

/**
 * 将日期字符串转换为 Date 对象
 * @param {string} dateStr - YYYY-MM-DD 格式的日期字符串
 * @returns {Date} Date 对象
 */
export const parseDate = (dateStr) => {
	if (!dateStr) return null;
	return new Date(dateStr);
};
