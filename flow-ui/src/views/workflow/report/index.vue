<template>
  <div class="report-container">
    <!-- 统计概览 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :span="8">
        <el-card>
          <div class="chart-title">流程状态分布</div>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <div class="chart-title">月度流程趋势</div>
          <div ref="lineChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细统计 -->
    <el-row :gutter="20" class="detail-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>流程发起统计</span>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                size="small"
                @change="loadProcessStartData"
              />
            </div>
          </template>
          <div ref="barChartRef1" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>任务处理效率</span>
              <el-date-picker
                v-model="dateRange2"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                size="small"
                @change="loadTaskEfficiencyData"
              />
            </div>
          </template>
          <el-table :data="taskEfficiencyData" border size="small">
            <el-table-column prop="assignee" label="办理人" />
            <el-table-column prop="taskCount" label="任务数" width="80" />
            <el-table-column prop="averageDurationFormat" label="平均处理时间" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 工作量统计 -->
    <el-row :gutter="20" class="workload-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>个人工作量统计</span>
              <el-date-picker
                v-model="dateRange3"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                size="small"
                @change="loadWorkloadData"
              />
            </div>
          </template>
          <div ref="barChartRef2" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  getProcessStatusDistribution,
  getMonthlyProcessTrend,
  getProcessStartStatistics,
  getTaskEfficiencyStatistics,
  getPersonalWorkloadStatistics
} from '@/api/report'

const pieChartRef = ref()
const lineChartRef = ref()
const barChartRef1 = ref()
const barChartRef2 = ref()

let pieChart = null
let lineChart = null
let barChart1 = null
let barChart2 = null

const dateRange = ref([])
const dateRange2 = ref([])
const dateRange3 = ref([])
const taskEfficiencyData = ref([])

// 初始化饼图（流程状态分布）
const initPieChart = async () => {
  try {
    const res = await getProcessStatusDistribution()
    if (res.code === 200) {
      const data = res.data
      const option = {
        tooltip: { trigger: 'item' },
        legend: { bottom: '5%' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: {
            label: { show: true, fontSize: 20, fontWeight: 'bold' }
          },
          data: [
            { value: data.running, name: '运行中', itemStyle: { color: '#409eff' } },
            { value: data.completed, name: '已完成', itemStyle: { color: '#67c23a' } }
          ]
        }]
      }
      pieChart = echarts.init(pieChartRef.value)
      pieChart.setOption(option)
    }
  } catch (error) {
    console.error(error)
  }
}

// 初始化折线图（月度趋势）
const initLineChart = async () => {
  try {
    const res = await getMonthlyProcessTrend(6)
    if (res.code === 200) {
      const data = res.data
      const months = data.map(item => item.month)
      const started = data.map(item => item.startedCount)
      const completed = data.map(item => item.completedCount)

      const option = {
        tooltip: { trigger: 'axis' },
        legend: { data: ['发起数', '完成数'] },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: months },
        yAxis: { type: 'value' },
        series: [
          { name: '发起数', type: 'line', data: started, smooth: true },
          { name: '完成数', type: 'line', data: completed, smooth: true }
        ]
      }
      lineChart = echarts.init(lineChartRef.value)
      lineChart.setOption(option)
    }
  } catch (error) {
    console.error(error)
  }
}

// 加载流程发起统计
const loadProcessStartData = async () => {
  try {
    const startTime = dateRange.value?.[0]?.toISOString()
    const endTime = dateRange.value?.[1]?.toISOString()
    const res = await getProcessStartStatistics(startTime, endTime)
    if (res.code === 200) {
      const data = res.data.slice(0, 10) // 取前10
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value' },
        yAxis: { type: 'category', data: data.map(item => item.processName) },
        series: [{
          type: 'bar',
          data: data.map(item => item.count),
          itemStyle: { color: '#409eff' }
        }]
      }
      if (!barChart1) {
        barChart1 = echarts.init(barChartRef1.value)
      }
      barChart1.setOption(option)
    }
  } catch (error) {
    console.error(error)
  }
}

// 加载任务效率统计
const loadTaskEfficiencyData = async () => {
  try {
    const startTime = dateRange2.value?.[0]?.toISOString()
    const endTime = dateRange2.value?.[1]?.toISOString()
    const res = await getTaskEfficiencyStatistics(startTime, endTime)
    if (res.code === 200) {
      taskEfficiencyData.value = res.data.slice(0, 10)
    }
  } catch (error) {
    console.error(error)
  }
}

// 加载工作量统计
const loadWorkloadData = async () => {
  try {
    const startTime = dateRange3.value?.[0]?.toISOString()
    const endTime = dateRange3.value?.[1]?.toISOString()
    const res = await getPersonalWorkloadStatistics(startTime, endTime)
    if (res.code === 200) {
      const data = res.data.slice(0, 10)
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['发起流程', '处理任务'] },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: data.map(item => item.userId) },
        yAxis: { type: 'value' },
        series: [
          { name: '发起流程', type: 'bar', data: data.map(item => item.startedCount) },
          { name: '处理任务', type: 'bar', data: data.map(item => item.finishedCount) }
        ]
      }
      if (!barChart2) {
        barChart2 = echarts.init(barChartRef2.value)
      }
      barChart2.setOption(option)
    }
  } catch (error) {
    console.error(error)
  }
}

// 窗口大小改变时重绘图表
const handleResize = () => {
  pieChart?.resize()
  lineChart?.resize()
  barChart1?.resize()
  barChart2?.resize()
}

onMounted(() => {
  initPieChart()
  initLineChart()
  loadProcessStartData()
  loadTaskEfficiencyData()
  loadWorkloadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  lineChart?.dispose()
  barChart1?.dispose()
  barChart2?.dispose()
})
</script>

<style scoped lang="scss">
.report-container {
  .overview-row,
  .detail-row,
  .workload-row {
    margin-bottom: 20px;
  }

  .chart-title {
    font-weight: bold;
    margin-bottom: 16px;
  }

  .chart-container {
    height: 300px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
