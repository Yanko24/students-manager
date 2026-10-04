<template>
  <div class="dashboard-container">
    <div class="page-header dashboard-heading">
      <div class="heading-copy">
        <span class="eyebrow">管理驾驶舱</span>
        <h2>数据总览</h2>
        <p>查看学生、教师、课程和考勤的最新统计</p>
      </div>
      <div class="dashboard-meta">
        <span class="live-indicator"><i></i> 数据已连接</span>
        <span class="dashboard-date">{{ todayLabel }}</span>
      </div>
    </div>

    <div class="section-heading">
      <div>
        <h3>核心指标</h3>
        <p>当前系统中的业务数据概况</p>
      </div>
      <span>数据来自系统数据库</span>
    </div>

    <el-row :gutter="16" class="metrics-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card metric-card--students" shadow="never">
          <div class="metric-top">
            <span class="metric-label">学生总数</span>
            <span class="metric-icon"><el-icon><User /></el-icon></span>
          </div>
          <div class="metric-value">{{ formatCount(dashboardData.statistics.studentCount) }}</div>
          <div class="metric-foot"><span class="metric-dot"></span>在籍学生档案</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card metric-card--teachers" shadow="never">
          <div class="metric-top">
            <span class="metric-label">教师总数</span>
            <span class="metric-icon"><el-icon><UserFilled /></el-icon></span>
          </div>
          <div class="metric-value">{{ formatCount(dashboardData.statistics.teacherCount) }}</div>
          <div class="metric-foot"><span class="metric-dot"></span>教师档案记录</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card metric-card--courses" shadow="never">
          <div class="metric-top">
            <span class="metric-label">课程总数</span>
            <span class="metric-icon"><el-icon><Reading /></el-icon></span>
          </div>
          <div class="metric-value">{{ formatCount(dashboardData.statistics.courseCount) }}</div>
          <div class="metric-foot"><span class="metric-dot"></span>课程库记录</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card metric-card--attendance" shadow="never">
          <div class="metric-top">
            <span class="metric-label">近 7 天考勤率</span>
            <span class="metric-icon"><el-icon><Calendar /></el-icon></span>
          </div>
          <div class="metric-value">{{ attendanceStats.totalCount ? `${attendanceStats.attendanceRate}%` : '—' }}</div>
          <div class="metric-foot"><span class="metric-dot"></span>{{ attendanceStats.totalCount ? `${attendanceStats.totalCount} 条考勤记录` : '暂无考勤记录' }}</div>
        </el-card>
      </el-col>
    </el-row>

    <div class="section-heading chart-section-heading">
      <div>
        <h3>教学数据分析</h3>
        <p>按学期和时间范围查看趋势</p>
      </div>
    </div>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-mark chart-title-mark--score"></span>
                <div><h3>成绩分布</h3><p>各成绩区间的记录占比</p></div>
              </div>
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
              <div class="chart-period"><span>统计范围</span>{{ scoreDistributionPeriod || '加载中' }}</div>
              <v-chart class="chart-visual" :option="scoreDistributionOption" autoresize />
            </template>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-mark chart-title-mark--attendance"></span>
                <div><h3>考勤趋势</h3><p>每日有效出勤率变化</p></div>
              </div>
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
            <v-chart v-else class="chart-visual" :option="attendanceTrendOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="dashboard-note">
      <span class="note-icon"><el-icon><Calendar /></el-icon></span>
      <div><strong>统计说明</strong><p>总量指标与趋势图均根据当前数据库记录生成；考勤率统计近 7 天数据。</p></div>
      <span class="note-date">更新日期：{{ todayLabel }}</span>
    </div>
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
  import { formatDate } from '@/utils/dateUtils'

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
  const todayLabel = formatDate(new Date())

  // 成绩分布图配置
  const scoreDistributionOption = ref({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 4,
      itemGap: 18,
      textStyle: { color: '#64748b' }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '44%'],
      color: ['#3978d4', '#43a889', '#e3a548', '#e16f77', '#8b6bd6'],
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
    grid: { left: 48, right: 24, top: 30, bottom: 36 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: attendanceRows.value.map(item => item.date?.slice(5)),
      axisLine: { lineStyle: { color: '#e7edf5' } },
      axisTick: { show: false },
      axisLabel: { color: '#8995a7' }
    },
    yAxis: {
      type: 'value', min: 0, max: 100,
      axisLabel: { formatter: '{value}%', color: '#8995a7' },
      splitLine: { lineStyle: { color: '#edf1f6', type: 'dashed' } }
    },
    series: [{
      name: '有效出勤率', type: 'line', smooth: true, symbol: 'circle', symbolSize: 7,
      data: attendanceRows.value.map(item => Number(item.attendanceRate)),
      lineStyle: { width: 3, color: '#43a889' },
      itemStyle: { color: '#43a889', borderColor: '#fff', borderWidth: 2 },
      areaStyle: { color: '#43a889', opacity: 0.12 }
    }]
  }))

  const formatCount = (value) => value == null ? '—' : new Intl.NumberFormat('zh-CN').format(value)

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
  .dashboard-container { box-sizing: border-box; max-width: 1600px; margin: 0 auto; padding-bottom: 20px; }

  .dashboard-heading {
    align-items: flex-end;
    margin-bottom: 30px;

    .heading-copy { display: grid; gap: 5px; }
    .eyebrow { color: #3978d4; font-size: 11px; font-weight: 750; letter-spacing: 1.2px; }
    h2 { line-height: 1.2; }
    p { color: #7f8ba0; font-size: 13px; }
  }

  .dashboard-meta { display: flex; align-items: center; gap: 12px; }
  .dashboard-date, .live-indicator {
    display: inline-flex; align-items: center; gap: 8px; white-space: nowrap;
    border: 1px solid #e5ebf3; border-radius: 999px; background: #fff;
    padding: 8px 13px; color: #64748b; font-size: 12px;
  }
  .live-indicator { color: #4d627c; }
  .live-indicator i { width: 7px; height: 7px; border-radius: 50%; background: #39ae82; box-shadow: 0 0 0 3px #e7f7f0; }

  .section-heading {
    display: flex; justify-content: space-between; align-items: flex-end; gap: 12px;
    margin: 0 0 14px;
    h3 { margin: 0; color: #25344d; font-size: 16px; font-weight: 700; }
    p { margin: 4px 0 0; color: #8793a5; font-size: 12px; }
    > span { color: #8793a5; font-size: 12px; }
  }

  .metrics-row { margin-bottom: 30px; }
  .metric-card {
    position: relative; height: 154px; margin-bottom: 16px; overflow: hidden;
    border: 1px solid #e8edf4; border-radius: 14px;
    transition: transform .18s ease, box-shadow .18s ease;
    &:hover { transform: translateY(-3px); box-shadow: 0 12px 26px rgba(31, 50, 82, .09); }
    :deep(.el-card__body) { height: 100%; padding: 18px 20px; display: flex; flex-direction: column; }
    &::after { content: ''; position: absolute; right: -22px; bottom: -36px; width: 110px; height: 110px; border-radius: 50%; background: var(--metric-soft); }
    &--students { --metric-color: #3978d4; --metric-soft: #f0f5ff; }
    &--teachers { --metric-color: #36a879; --metric-soft: #edf8f3; }
    &--courses { --metric-color: #8665ce; --metric-soft: #f4f0fc; }
    &--attendance { --metric-color: #d39832; --metric-soft: #fcf6e9; }
  }
  .metric-top { display: flex; justify-content: space-between; align-items: center; }
  .metric-label { color: #6b788c; font-size: 13px; font-weight: 600; }
  .metric-icon { display: grid; place-items: center; width: 38px; height: 38px; border-radius: 11px; color: var(--metric-color); background: var(--metric-soft); font-size: 19px; }
  .metric-value { margin-top: auto; color: #23334c; font-size: 34px; font-weight: 750; line-height: 1; letter-spacing: -.7px; font-variant-numeric: tabular-nums; }
  .metric-foot { display: flex; align-items: center; gap: 7px; margin-top: 9px; color: #929daf; font-size: 11px; }
  .metric-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--metric-color); opacity: .75; }

  .chart-section-heading { margin-bottom: 14px; }
  .chart-row { margin-bottom: 16px; }
  .chart-card { height: 100%; min-height: 414px; margin-bottom: 16px; border: 1px solid #e8edf4; border-radius: 14px; }
  .chart-card :deep(.el-card__header) { padding: 19px 21px; border-bottom: 1px solid #edf1f6; }
  .chart-card :deep(.el-card__body) { padding: 14px 20px 18px; }
  .card-header { display: flex; justify-content: space-between; align-items: center; gap: 16px; }
  .chart-title { display: flex; align-items: center; gap: 11px; min-width: 0; }
  .chart-title h3 { margin: 0; color: #2b3a51; font-size: 14px; font-weight: 700; }
  .chart-title p { margin: 4px 0 0; color: #929daf; font-size: 11px; }
  .chart-title-mark { width: 4px; height: 30px; flex: 0 0 auto; border-radius: 5px; background: #3978d4; }
  .chart-title-mark--attendance { background: #43a889; }
  .chart-container { position: relative; height: 322px; }
  .chart-visual { display: block; width: 100%; height: 100%; }
  .chart-period { display: flex; align-items: center; gap: 7px; color: #40516a; font-size: 12px; }
  .chart-period span { color: #9aa5b5; }
  .empty-chart { display: grid; place-items: center; height: 100%; }
  .empty-chart :deep(.el-empty) { padding: 0; }
  .dashboard-note {
    display: flex; align-items: center; gap: 12px; padding: 14px 17px;
    border: 1px solid #e7edf5; border-radius: 12px; background: #f9fbfd;
  }
  .note-icon { display: grid; place-items: center; width: 34px; height: 34px; flex: 0 0 auto; color: #6280ac; background: #edf3fb; border-radius: 10px; }
  .dashboard-note strong { color: #53657e; font-size: 12px; }
  .dashboard-note p { margin: 3px 0 0; color: #8b97a8; font-size: 11px; }
  .note-date { margin-left: auto; color: #98a3b2; font-size: 11px; white-space: nowrap; }

  @media (max-width: 768px) {
    .dashboard-heading { align-items: flex-start; flex-direction: column; margin-bottom: 23px; }
    .dashboard-meta { width: 100%; justify-content: space-between; }
    .section-heading > span { display: none; }
    .metric-card { height: 140px; }
    .card-header { align-items: flex-start; flex-direction: column; }
    .chart-card { min-height: 460px; }
    .chart-container { height: 300px; }
    .dashboard-note { align-items: flex-start; }
    .note-date { display: none; }
  }
</style>
