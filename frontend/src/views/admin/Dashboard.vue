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
            <div class="number">{{ dashboardData.statistics.studentCount ?? '—' }}</div>
            <div class="trend">当前系统记录</div>
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
            <div class="number">{{ dashboardData.statistics.teacherCount ?? '—' }}</div>
            <div class="trend">当前系统记录</div>
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
            <div class="number">{{ dashboardData.statistics.courseCount ?? '—' }}</div>
            <div class="trend">当前系统记录</div>
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
            <div class="number">{{ attendanceStats.totalCount ? `${attendanceStats.attendanceRate}%` : '—' }}</div>
            <div class="trend">{{ attendanceStats.totalCount ? `近7天有效出勤率 · ${attendanceStats.totalCount} 条记录` : '近7天暂无考勤记录' }}</div>
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
                <el-radio-button :value="'semester'">最新学期</el-radio-button>
                <el-radio-button :value="'year'">对应学年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div v-loading="scoreDistributionLoading" class="chart-container">
            <div v-if="!scoreDistributionLoading && !hasScoreDistribution" class="empty-chart">
              <el-empty description="暂无成绩数据" />
            </div>
            <template v-else>
              <div class="chart-period">统计范围：{{ scoreDistributionPeriod || '加载中' }}</div>
              <v-chart :option="scoreDistributionOption" autoresize />
            </template>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>考勤趋势</span>
              <el-radio-group v-model="attendanceChartType" size="small">
                <el-radio-button value="week">近7天</el-radio-button>
                <el-radio-button value="month">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div v-loading="attendanceTrendLoading" class="chart-container">
            <div v-if="!attendanceTrendLoading && !hasAttendanceTrend" class="empty-chart">
            <el-empty description="当前没有可用的考勤统计数据" />
            </div>
            <v-chart v-else :option="attendanceTrendOption" autoresize />
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
            </div>
          </template>
          <el-empty description="当前没有可用的操作动态数据" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card class="todo-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待办事项</span>
            </div>
          </template>
          <el-empty description="待办事项功能尚未接入" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
  import { ref, reactive, computed, onMounted, watch } from 'vue'
  import * as echarts from 'echarts/core'
  import { CanvasRenderer } from 'echarts/renderers'
  import { PieChart, LineChart } from 'echarts/charts'
  import {
    TooltipComponent,
    LegendComponent,
    GridComponent
  } from 'echarts/components'
  import VChart from 'vue-echarts'
  import {
    User,
    UserFilled,
    Reading,
    Calendar
  } from '@element-plus/icons-vue'
  import { getStudentList } from '@/api/student'
  import { getTeacherList } from '@/api/teacher'
  import { getCourseList } from '@/api/course'
  import { getScoreDistribution } from '@/api/score'
  import { getAttendanceStatistics, getAttendanceTrend } from '@/api/attendance'

  // 注册 ECharts 组件
  echarts.use([
    CanvasRenderer,
    PieChart,
    LineChart,
    TooltipComponent,
    LegendComponent,
    GridComponent
  ])

  // 首页实体数量和成绩分布均来自后端数据库。
  const dashboardData = reactive({
    statistics: {
      studentCount: null,
      teacherCount: null,
      courseCount: null
    }
  })

  const scoreChartType = ref('semester')
  const attendanceChartType = ref('week')
  const scoreDistributionLoading = ref(false)
  const scoreDistributionPeriod = ref('')
  const attendanceTrendLoading = ref(false)
  const attendanceRows = ref([])
  const attendanceStats = reactive({ attendanceRate: null, totalCount: 0 })

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
      data: []
    }]
  })
  const hasScoreDistribution = computed(() => scoreDistributionOption.value.series[0].data.some(item => Number(item.value) > 0))
  const hasAttendanceTrend = computed(() => attendanceRows.value.some(item => Number(item.totalCount) > 0))
  const attendanceTrendOption = computed(() => ({
    tooltip: { trigger: 'axis', formatter: (items) => `${items[0]?.axisValue || ''}<br/>有效出勤率：${items[0]?.value ?? 0}%` },
    grid: { left: 48, right: 24, top: 24, bottom: 36 },
    xAxis: { type: 'category', boundaryGap: false, data: attendanceRows.value.map(item => item.date?.slice(5)) },
    yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value}%' } },
    series: [{ name: '有效出勤率', type: 'line', smooth: true, data: attendanceRows.value.map(item => Number(item.attendanceRate)), areaStyle: { opacity: 0.16 } }]
  }))

  const fetchScoreDistribution = async () => {
    scoreDistributionLoading.value = true
    try {
      const response = await getScoreDistribution(scoreChartType.value)
      if (response?.code !== 200 || !response.data) throw new Error(response?.message || '获取成绩统计失败')
      scoreDistributionOption.value.series[0].data = response.data.distribution || []
      scoreDistributionPeriod.value = response.data.periodLabel || ''
    } catch (error) {
      console.error('获取成绩分布失败:', error)
      scoreDistributionOption.value.series[0].data = []
      scoreDistributionPeriod.value = ''
    } finally {
      scoreDistributionLoading.value = false
    }
  }

  const fetchAttendanceStats = async () => {
    try {
      const response = await getAttendanceStatistics('week')
      if (response?.code === 200 && response.data) Object.assign(attendanceStats, response.data)
    } catch (error) {
      console.error('获取考勤统计失败:', error)
    }
  }

  const fetchAttendanceTrend = async () => {
    attendanceTrendLoading.value = true
    try {
      const response = await getAttendanceTrend(attendanceChartType.value)
      if (response?.code !== 200) throw new Error(response?.message || '获取考勤趋势失败')
      attendanceRows.value = response.data || []
    } catch (error) {
      console.error('获取考勤趋势失败:', error)
      attendanceRows.value = []
    } finally {
      attendanceTrendLoading.value = false
    }
  }

  watch(scoreChartType, fetchScoreDistribution)
  watch(attendanceChartType, fetchAttendanceTrend)

  const fetchEntityCounts = async () => {
    const requests = [
      ['studentCount', getStudentList({ page: 1, size: 1 })],
      ['teacherCount', getTeacherList({ page: 1, size: 1 })],
      ['courseCount', getCourseList({ page: 1, size: 1 })]
    ]
    const results = await Promise.allSettled(requests.map(([, request]) => request))
    results.forEach((result, index) => {
      if (result.status !== 'fulfilled') return
      const [key] = requests[index]
      const total = result.value?.data?.total
      if (result.value?.code === 200 && Number.isFinite(Number(total))) {
        dashboardData.statistics[key] = Number(total)
      }
    })
  }

  onMounted(() => {
    fetchEntityCounts()
    fetchScoreDistribution()
    fetchAttendanceStats()
    fetchAttendanceTrend()
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
