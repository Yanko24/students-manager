<template>
  <div class="dashboard-container">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="data-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>学生总数</span>
              <el-icon class="icon">
                <User />
              </el-icon>
            </div>
          </template>
          <div class="card-body">
            <div class="number">{{ mockData.statistics.studentCount }}</div>
            <div class="trend"
              :class="{ 'up': mockData.statistics.studentGrowth > 0, 'down': mockData.statistics.studentGrowth < 0 }">
              较上月{{ Math.abs(mockData.statistics.studentGrowth) }}%
              <el-icon>
                <component :is="mockData.statistics.studentGrowth > 0 ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="data-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>教师总数</span>
              <el-icon class="icon">
                <UserFilled />
              </el-icon>
            </div>
          </template>
          <div class="card-body">
            <div class="number">{{ mockData.statistics.teacherCount }}</div>
            <div class="trend"
              :class="{ 'up': mockData.statistics.teacherGrowth > 0, 'down': mockData.statistics.teacherGrowth < 0 }">
              较上月{{ Math.abs(mockData.statistics.teacherGrowth) }}%
              <el-icon>
                <component :is="mockData.statistics.teacherGrowth > 0 ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="data-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>课程总数</span>
              <el-icon class="icon">
                <Reading />
              </el-icon>
            </div>
          </template>
          <div class="card-body">
            <div class="number">{{ mockData.statistics.courseCount }}</div>
            <div class="trend"
              :class="{ 'up': mockData.statistics.courseGrowth > 0, 'down': mockData.statistics.courseGrowth < 0 }">
              较上月{{ Math.abs(mockData.statistics.courseGrowth) }}%
              <el-icon>
                <component :is="mockData.statistics.courseGrowth > 0 ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="data-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>考勤率</span>
              <el-icon class="icon">
                <Calendar />
              </el-icon>
            </div>
          </template>
          <div class="card-body">
            <div class="number">{{ mockData.statistics.attendanceRate }}%</div>
            <div class="trend"
              :class="{ 'up': mockData.statistics.attendanceGrowth > 0, 'down': mockData.statistics.attendanceGrowth < 0 }">
              较上月{{ Math.abs(mockData.statistics.attendanceGrowth) }}%
              <el-icon>
                <component :is="mockData.statistics.attendanceGrowth > 0 ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>成绩分布</span>
              <el-radio-group v-model="scoreChartType" size="small">
                <el-radio-button :value="'semester'">本学期</el-radio-button>
                <el-radio-button :value="'year'">本学年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="scoreDistributionOption" autoresize />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>考勤趋势</span>
              <el-radio-group v-model="attendanceChartType" size="small">
                <el-radio-button :value="'week'">本周</el-radio-button>
                <el-radio-button :value="'month'">本月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="attendanceTrendOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新动态和待办事项 -->
    <el-row :gutter="20" class="activity-row">
      <el-col :xs="24" :lg="16">
        <el-card class="activity-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最新动态</span>
              <el-button link type="primary" @click="loadMoreActivities">查看更多</el-button>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item v-for="activity in mockData.activities" :key="activity.id" :timestamp="activity.time"
              :type="activity.type">
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card class="todo-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待办事项</span>
              <el-button link type="primary" @click="addTodo">添加</el-button>
            </div>
          </template>
          <el-checkbox-group v-model="checkedTodos">
            <div v-for="todo in mockData.todos" :key="todo.id" class="todo-item">
              <el-checkbox :value="todo.id">{{ todo.content }}</el-checkbox>
              <span class="todo-time">{{ todo.deadline }}</span>
            </div>
          </el-checkbox-group>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
  import { ref, reactive, onMounted, watch } from 'vue'
  import * as echarts from 'echarts/core'
  import { CanvasRenderer } from 'echarts/renderers'
  import { PieChart, LineChart } from 'echarts/charts'
  import {
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent
  } from 'echarts/components'
  import VChart, { THEME_KEY } from 'vue-echarts'
  import {
    User,
    UserFilled,
    Reading,
    Calendar,
    ArrowUp,
    ArrowDown
  } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'

  // 注册 ECharts 组件
  echarts.use([
    CanvasRenderer,
    PieChart,
    LineChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent
  ])

  // 模拟数据
  const mockData = reactive({
    statistics: {
      studentCount: 1258,
      studentGrowth: 5.2,
      teacherCount: 86,
      teacherGrowth: -1.5,
      courseCount: 45,
      courseGrowth: 2.8,
      attendanceRate: 95.5,
      attendanceGrowth: 0.8
    },
    scoreDistribution: {
      semester: [
        { value: 156, name: '优秀(90-100)' },
        { value: 285, name: '良好(80-89)' },
        { value: 420, name: '中等(70-79)' },
        { value: 280, name: '及格(60-69)' },
        { value: 117, name: '不及格(<60)' }
      ],
      year: [
        { value: 320, name: '优秀(90-100)' },
        { value: 580, name: '良好(80-89)' },
        { value: 750, name: '中等(70-79)' },
        { value: 450, name: '及格(60-69)' },
        { value: 158, name: '不及格(<60)' }
      ]
    },
    attendanceTrend: {
      week: {
        dates: ['周一', '周二', '周三', '周四', '周五'],
        rates: [96.5, 95.8, 97.2, 94.8, 95.5]
      },
      month: {
        dates: ['第1周', '第2周', '第3周', '第4周'],
        rates: [95.5, 96.2, 94.8, 95.7]
      }
    },
    activities: [
      { id: 1, type: 'success', content: '新增学生张三入学信息', time: '2024-03-20 10:00' },
      { id: 2, type: 'warning', content: '更新教师李四课程安排', time: '2024-03-20 09:30' },
      { id: 3, type: 'primary', content: '系统完成每日数据备份', time: '2024-03-20 09:00' },
      { id: 4, type: 'danger', content: '发现异常登录尝试，已阻止', time: '2024-03-20 08:45' },
      { id: 5, type: 'success', content: '完成本月教师考核统计', time: '2024-03-20 08:30' }
    ],
    todos: [
      { id: 1, content: '审核新生入学申请', deadline: '2024-03-21 12:00', completed: false },
      { id: 2, content: '准备期中考试安排', deadline: '2024-03-22 18:00', completed: false },
      { id: 3, content: '教师资格证年审', deadline: '2024-03-23 15:00', completed: false },
      { id: 4, content: '更新教学大纲', deadline: '2024-03-24 17:00', completed: false }
    ]
  })

  const scoreChartType = ref('semester')
  const attendanceChartType = ref('week')
  const checkedTodos = ref([])

  // 成绩分布图配置
  const scoreDistributionOption = ref({
    title: {
      text: '成绩分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 'bottom'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '14',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: mockData.scoreDistribution.semester
    }]
  })

  // 考勤趋势图配置
  const attendanceTrendOption = ref({
    title: {
      text: '考勤趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: mockData.attendanceTrend.week.dates
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      },
      min: 90
    },
    series: [{
      data: mockData.attendanceTrend.week.rates,
      type: 'line',
      smooth: true,
      areaStyle: {
        opacity: 0.3
      },
      lineStyle: {
        width: 3
      },
      itemStyle: {
        borderWidth: 2
      }
    }]
  })

  // 监听图表类型变化
  watch(scoreChartType, (newType) => {
    scoreDistributionOption.value.series[0].data = mockData.scoreDistribution[newType]
  })

  watch(attendanceChartType, (newType) => {
    attendanceTrendOption.value.xAxis.data = mockData.attendanceTrend[newType].dates
    attendanceTrendOption.value.series[0].data = mockData.attendanceTrend[newType].rates
  })

  // 加载更多活动
  const loadMoreActivities = () => {
    ElMessage.success('加载更多活动')
  }

  // 添加待办事项
  const addTodo = () => {
    ElMessage.success('添加待办事项')
  }

  onMounted(() => {
    // 在实际项目中，这里会调用API获取真实数据
    console.log('Dashboard mounted')
  })
</script>

<style lang="scss" scoped>
  .dashboard-container {
    padding: 20px;
    box-sizing: border-box;

    .data-card {
      margin-bottom: 20px;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .icon {
          font-size: 24px;
          color: var(--el-color-primary);
        }
      }

      .card-body {
        text-align: center;

        .number {
          font-size: 24px;
          font-weight: bold;
          margin: 10px 0;
          color: var(--el-text-color-primary);
        }

        .trend {
          font-size: 14px;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 4px;

          &.up {
            color: #67c23a;
          }

          &.down {
            color: #f56c6c;
          }
        }
      }
    }

    .chart-row {
      margin-bottom: 20px;

      .chart-card {
        margin-bottom: 20px;

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;
        }

        .chart-container {
          height: 300px;
        }
      }
    }

    .activity-row {

      .activity-card,
      .todo-card {
        margin-bottom: 20px;

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
        }
      }

      .todo-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
        padding: 8px 0;
        border-bottom: 1px solid var(--el-border-color-lighter);

        &:last-child {
          border-bottom: none;
        }

        .todo-time {
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }
    }
  }

  @media screen and (max-width: 768px) {
    .dashboard-container {
      padding: 10px;

      .chart-container {
        height: 250px;
      }
    }
  }
</style>