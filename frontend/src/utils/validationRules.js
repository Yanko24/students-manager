/**
 * 通用表单验证规则
 */
export const rules = {
	// 必填规则
	required: (message = "此项为必填项", trigger = "blur") => ({
		required: true,
		message,
		trigger,
	}),

	// 长度规则
	length: (
		min,
		max,
		message = `长度应在${min}-${max}个字符之间`,
		trigger = "blur"
	) => ({
		min,
		max,
		message,
		trigger,
	}),

	// 手机号规则
	phone: (message = "请输入正确的手机号码", trigger = "blur") => ({
		pattern: /^1[3-9]\d{9}$/,
		message,
		trigger,
	}),

	// 邮箱规则
	email: (message = "请输入正确的邮箱地址", trigger = "blur") => ({
		type: "email",
		message,
		trigger,
	}),

	// 学号规则
	studentNumber: (
		message = "学号格式：两位大写字母+8位数字",
		trigger = "blur"
	) => ({
		pattern: /^[A-Z]{2}\d{8}$/,
		message,
		trigger,
	}),
};

/**
 * 学生表单验证规则
 */
export const studentFormRules = {
	studentNumber: [rules.required("请输入学号"), rules.studentNumber()],
	realName: [
		rules.required("请输入姓名"),
		rules.length(2, 20, "姓名长度在2-20个字符之间"),
	],
	gender: [rules.required("请选择性别", "change")],
	classId: [rules.required("请选择班级", "change")],
	phone: [rules.required("请输入联系电话"), rules.phone()],
	email: [rules.required("请输入邮箱"), rules.email()],
	admissionDate: [rules.required("请选择入学日期", "change")],
};
