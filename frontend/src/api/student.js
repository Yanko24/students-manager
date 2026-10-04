import request from "@/utils/request";
import { handleApiError } from "@/utils/errorHandler";

/**
 * 获取学生列表（分页查询）
 * @param {Object} params - 查询参数
 * @returns {Promise<Object>} 学生列表数据
 */
export function getStudentList(params) {
	return request({
		url: "/students",
		method: "get",
		params,
	});
}

/**
 * 获取学生详情
 * @param {number} id - 学生ID
 * @returns {Promise<Object>} 学生详情
 */
export function getStudentById(id) {
	return request({
		url: `/students/${id}`,
		method: "get",
	});
}

/**
 * 添加学生
 * @param {Object} data - 学生数据
 * @returns {Promise<Object>} 添加结果
 */
export function createStudent(data) {
	return request({
		url: "/students",
		method: "post",
		data,
	});
}

/** 批量导入学生 CSV 文件 */
export function importStudents(file) {
	const formData = new FormData();
	formData.append("file", file);
	return request({
		url: "/admin/students/import",
		method: "post",
		data: formData,
		timeout: 120000,
	});
}

/**
 * 更新学生信息
 * @param {number} id - 学生ID
 * @param {Object} data - 学生数据
 * @returns {Promise<Object>} 更新结果
 */
export function updateStudent(id, data) {
	return request({
		url: `/students/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除学生
 * @param {number} id - 学生ID
 * @returns {Promise<Object>} 删除结果
 */
export function deleteStudent(id) {
	return request({
		url: `/students/${id}`,
		method: "delete",
	});
}

/**
 * 获取所有学生信息（包含详细信息）
 * @returns {Promise<Object>} 所有学生信息
 */
export function getAllStudentsWithInfo() {
	return request({
		url: "/students/all",
		method: "get",
	});
}

/**
 * 获取在读学生数量
 * @param {Object} params - 查询参数（专业代码、年级、班级等）
 * @returns {Promise<Object>} 在读学生数量
 */
export function getEnrolledStudentCount(params) {
	return request({
		url: "/students/count/enrolled",
		method: "get",
		params,
	});
}

/**
 * 获取班级学生总数
 * @param {Object} params - 查询参数（专业代码、年级、班级等）
 * @returns {Promise<Object>} 班级学生总数
 */
export function getStudentCount(params) {
	return request({
		url: "/students/count/total",
		method: "get",
		params,
	});
}

/**
 * 获取学生课程列表
 * @param {Object} params - 查询参数（分页、学期等）
 * @returns {Promise<Object>} 课程列表数据
 */
export function getStudentCourses(params) {
	return request({
		url: "/students/courses",
		method: "get",
		params,
	});
}

// 获取状态对应的标签类型
export const getStatusType = (status) => {
	switch (status) {
		case 0: // 在读
			return "success";
		case 1: // 休学
			return "warning";
		case 2: // 退学
			return "danger";
		case 3: // 毕业
			return "info";
		default:
			return "info";
	}
};

// 获取状态对应的文本
export const getStatusText = (status) => {
	switch (status) {
		case 0: // 在读
			return "在读";
		case 1: // 休学
			return "休学";
		case 2: // 退学
			return "退学";
		case 3: // 毕业
			return "毕业";
		default:
			return "未知";
	}
};

// 格式化日期
export const formatDate = (date) => {
	if (!date) return "";
	const d = new Date(date);
	const year = d.getFullYear();
	const month = String(d.getMonth() + 1).padStart(2, "0");
	const day = String(d.getDate()).padStart(2, "0");
	const hour = String(d.getHours()).padStart(2, "0");
	const minute = String(d.getMinutes()).padStart(2, "0");
	return `${year}-${month}-${day} ${hour}:${minute}`;
};
