<template>
  <div class="page-container">
    <div class="page-heading"><div><h2>培养进度</h2><p>按培养方案查看已修学分和毕业要求</p></div></div>
    <el-card v-loading="loading" shadow="never" class="progress-card">
      <template v-if="progress.planConfigured">
        <div class="plan-heading"><div><span class="eyebrow">{{ progress.grade }}级 · {{ progress.majorCode }}</span><h3>{{ progress.planName }}</h3></div><el-tag type="success" effect="plain">已匹配培养方案</el-tag></div>
        <div class="overall-progress"><div><strong>总学分进度</strong><span>{{ progress.earnedTotalCredits }} / {{ progress.totalCredits }} 学分</span></div><el-progress :percentage="Number(progress.completionRate || 0)" :stroke-width="12" /></div>
        <div class="credit-grid">
          <div class="credit-item"><span>毕业总学分</span><strong>{{ progress.earnedTotalCredits }} <small>/ {{ progress.totalCredits }}</small></strong><el-progress :percentage="percent(progress.earnedTotalCredits, progress.totalCredits)" :show-text="false" /><em>还差 {{ progress.remainingTotalCredits }} 学分</em></div>
          <div class="credit-item"><span>必修学分</span><strong>{{ progress.earnedRequiredCredits }} <small>/ {{ progress.requiredCredits }}</small></strong><el-progress :percentage="percent(progress.earnedRequiredCredits, progress.requiredCredits)" status="success" :show-text="false" /><em>还差 {{ progress.remainingRequiredCredits }} 学分</em></div>
          <div class="credit-item"><span>选修学分</span><strong>{{ progress.earnedElectiveCredits }} <small>/ {{ progress.electiveCredits }}</small></strong><el-progress :percentage="percent(progress.earnedElectiveCredits, progress.electiveCredits)" status="warning" :show-text="false" /><em>还差 {{ progress.remainingElectiveCredits }} 学分</em></div>
        </div>
        <el-alert title="学分按已通过成绩计算，同一课程目录只累计一次。培养方案要求由教务管理员维护。" type="info" :closable="false" />
      </template>
      <el-empty v-else-if="!loading" description="当前专业和年级还没有配置培养方案">
        <p class="empty-hint">目前已取得 {{ progress.earnedTotalCredits }} 学分。请联系教务管理员配置培养方案后查看毕业进度。</p>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { showApiError } from '@/utils/errorHandler'
import { getStudentCurriculumProgress } from '@/api/curriculum'

const loading = ref(false)
const progress = ref({ earnedTotalCredits: 0 })
const percent = (earned, required) => Number(required) > 0 ? Math.min(100, Number((Number(earned || 0) * 100 / Number(required)).toFixed(1))) : 100
onMounted(async () => {
  loading.value = true
  try { const response = await getStudentCurriculumProgress(); progress.value = response?.data || { earnedTotalCredits: 0 } }
  catch (error) { showApiError(error, '获取培养进度失败') }
  finally { loading.value = false }
})
</script>

<style scoped>
.page-container { display: grid; gap: 18px; }
.page-heading h2 { margin: 0; font-size: 24px; }
.page-heading p { margin: 8px 0 0; color: var(--el-text-color-secondary); }
.progress-card { min-height: 360px; }
.plan-heading { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 28px; }
.plan-heading h3 { margin: 8px 0 0; font-size: 21px; }
.eyebrow { color: var(--el-text-color-secondary); }
.overall-progress { max-width: 760px; margin-bottom: 30px; }
.overall-progress > div:first-child { display: flex; justify-content: space-between; margin-bottom: 10px; }
.credit-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 16px; margin-bottom: 24px; }
.credit-item { display: grid; gap: 12px; padding: 20px; border: 1px solid var(--el-border-color-lighter); border-radius: 12px; }
.credit-item > span, .credit-item em { color: var(--el-text-color-secondary); font-style: normal; }
.credit-item strong { font-size: 24px; }
.credit-item small { color: var(--el-text-color-secondary); font-size: 14px; font-weight: 400; }
.empty-hint { color: var(--el-text-color-secondary); }
@media (max-width: 760px) { .credit-grid { grid-template-columns: 1fr; } .plan-heading { align-items: flex-start; flex-direction: column; } }
</style>
