import request from "@/utils/request";

/**
 * 获取仪表盘统计数据
 * @returns {Promise} 返回统计数据
 * @example
 * {
 *   studentCount: 1000,      // 学生总数
 *   studentGrowth: 5.2,      // 学生增长率
 *   teacherCount: 100,       // 教师总数
 *   teacherGrowth: 2.1,      // 教师增长率
 *   courseCount: 50,         // 课程总数
 *   courseGrowth: 3.5,       // 课程增长率
 *   attendanceRate: 95.5,    // 考勤率
 *   attendanceGrowth: 1.2    // 考勤率增长
 * }
 */
export function getDashboardStatistics() {
	return request({
		url: "/dashboard/statistics",
		method: "get",
	});
}

/**
 * 获取成绩分布数据
 * @param {string} type - 统计类型：semester-本学期, year-本学年
 * @returns {Promise} 返回成绩分布数据
 * @example
 * [
 *   { value: 20, name: '优秀(90-100)' },
 *   { value: 30, name: '良好(80-89)' },
 *   { value: 25, name: '中等(70-79)' },
 *   { value: 15, name: '及格(60-69)' },
 *   { value: 10, name: '不及格(<60)' }
 * ]
 */
export function getScoreDistribution(type = "semester") {
	return request({
		url: "/dashboard/score-distribution",
		method: "get",
		params: { type },
	});
}

/**
 * 获取考勤趋势数据
 * @param {string} type - 统计类型：week-本周, month-本月
 * @returns {Promise} 返回考勤趋势数据
 * @example
 * {
 *   dates: ['03-01', '03-02', '03-03', ...],
 *   rates: [95.5, 96.2, 94.8, ...]
 * }
 */
export function getAttendanceTrend(type = "week") {
	return request({
		url: "/dashboard/attendance-trend",
		method: "get",
		params: { type },
	});
}

/**
 * 获取最新动态列表
 * @param {Object} params - 查询参数
 * @param {number} [params.page=1] - 页码
 * @param {number} [params.size=10] - 每页条数
 * @returns {Promise} 返回动态列表
 * @example
 * [
 *   {
 *     id: 1,
 *     type: 'primary',           // primary-普通, success-成功, warning-警告, danger-危险
 *     content: '新增学生xxx',
 *     time: '2024-03-20 10:00'
 *   },
 *   ...
 * ]
 */
export function getActivities(params = { page: 1, size: 10 }) {
	return request({
		url: "/dashboard/activities",
		method: "get",
		params,
	});
}

/**
 * 获取待办事项列表
 * @returns {Promise} 返回待办事项列表
 * @example
 * [
 *   {
 *     id: 1,
 *     content: '审核学生请假申请',
 *     deadline: '2024-03-21 12:00',
 *     completed: false
 *   },
 *   ...
 * ]
 */
export function getTodos() {
	return request({
		url: "/dashboard/todos",
		method: "get",
	});
}

/**
 * 添加待办事项
 * @param {Object} data - 待办事项数据
 * @param {string} data.content - 待办内容
 * @param {string} data.deadline - 截止时间
 * @returns {Promise}
 */
export function addTodo(data) {
	return request({
		url: "/dashboard/todos",
		method: "post",
		data,
	});
}

/**
 * 更新待办事项状态
 * @param {number} id - 待办事项ID
 * @param {Object} data - 更新数据
 * @param {boolean} data.completed - 是否完成
 * @returns {Promise}
 */
export function updateTodo(id, data) {
	return request({
		url: `/dashboard/todos/${id}`,
		method: "put",
		data,
	});
}

/**
 * 删除待办事项
 * @param {number} id - 待办事项ID
 * @returns {Promise}
 */
export function deleteTodo(id) {
	return request({
		url: `/dashboard/todos/${id}`,
		method: "delete",
	});
}
