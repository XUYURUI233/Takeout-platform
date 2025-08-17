<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 5px">活动名称：</label>
        <el-input
          v-model="input"
          placeholder="请输入活动名称"
          style="width: 15%"
          clearable
          @clear="init"
          @keyup.enter.native="initFun"
        />
        <label style="margin-left: 20px; margin-right: 5px">活动状态：</label>
        <el-select 
          v-model="statusFilter" 
          placeholder="请选择状态" 
          style="width: 120px"
          clearable
          @clear="init"
          @change="initFun"
        >
          <el-option label="未开始" :value="0"></el-option>
          <el-option label="进行中" :value="1"></el-option>
          <el-option label="已结束" :value="2"></el-option>
          <el-option label="已取消" :value="3"></el-option>
        </el-select>
        <el-button class="normal-btn continue" @click="init(true)">
          查询
        </el-button>
        <el-button
          type="primary"
          style="float: right"
          @click="addSeckillHandle('add')"
        >
          + 添加秒杀活动
        </el-button>
      </div>
      <el-table
        :data="tableData"
        stripe
        v-if="tableData.length"
        class="tableBox"
      >
        <el-table-column prop="name" label="活动名称" />
        <el-table-column label="活动图片" width="100" align="center">
          <template slot-scope="scope">
            <el-image
              style="width: 60px; height: 40px"
              :src="scope.row.image"
              :preview-src-list="[scope.row.image]"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column prop="endTime" label="结束时间" />
        <el-table-column label="活动状态" width="100" align="center">
          <template slot-scope="scope">
            <div
              class="tableColumn-status"
              :class="{ 
                'stop-use': scope.row.status === 0 || scope.row.status === 3,
                'processing': scope.row.status === 1,
                'finished': scope.row.status === 2
              }"
            >
              {{ getStatusText(scope.row.status) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="goodsCount" label="商品数量" width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              class="blueBug"
              @click="viewSeckillHandle(scope.row.id)"
            >
              查看
            </el-button>
            <el-button
              type="text"
              size="small"
              class="blueBug"
              :disabled="scope.row.status !== 0"
              :class="{ 'disabled-text': scope.row.status !== 0 }"
              @click="addSeckillHandle(scope.row.id)"
            >
              修改
            </el-button>
            <el-button
              type="text"
              size="small"
              :class="scope.row.status === 0 ? 'blueBug' : 'delBut'"
              :disabled="scope.row.status === 2 || scope.row.status === 3"
              @click="statusHandle(scope.row)"
            >
              {{ scope.row.status === 0 ? '启用' : '停用' }}
            </el-button>
            <el-button
              type="text"
              size="small"
              class="delBut"
              :disabled="scope.row.status !== 0"
              :class="{ 'disabled-text': scope.row.status !== 0 }"
              @click="deleteHandle(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <Empty v-else :is-search="isSearch" />
      <el-pagination
        class="pageList"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="counts"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { 
  getSeckillActivityPage, 
  deleteSeckillActivity,
  updateSeckillActivityStatus 
} from '@/api/seckill'
import Empty from '@/components/Empty/index.vue'

@Component({
  name: 'Seckill',
  components: {
    Empty,
  },
})
export default class extends Vue {
  private input: string = ''
  private statusFilter: number | string = ''
  private counts: number = 0
  private page: number = 1
  private pageSize: number = 10
  private tableData: any[] = []
  private isSearch: boolean = false

  created() {
    this.init()
  }

  initFun() {
    this.page = 1
    this.init()
  }

  private async init(isSearch?: boolean) {
    this.isSearch = !!isSearch
    const params = {
      page: this.page,
      pageSize: this.pageSize,
      name: this.input || undefined,
      status: this.statusFilter !== '' ? this.statusFilter : undefined,
    }
    
    try {
      const res = await getSeckillActivityPage(params)
      if (String(res.data.code) === '1') {
        this.tableData = res.data.data.records || []
        this.counts = res.data.data.total || 0
      }
    } catch (err: any) {
      this.$message.error('请求出错了：' + err.message)
    }
  }

  // 添加/修改秒杀活动
  private addSeckillHandle(st: string) {
    if (st === 'add') {
      this.$router.push({ path: '/seckill/add' })
    } else {
      this.$router.push({ path: '/seckill/add', query: { id: st } })
    }
  }

  // 查看秒杀活动详情
  private viewSeckillHandle(id: string) {
    this.$router.push({ path: '/seckill/add', query: { id: id, view: 'true' } })
  }

  // 状态修改
  private statusHandle(row: any) {
    // 只有未开始(0)和进行中(1)的活动可以切换状态
    if (row.status !== 0 && row.status !== 1) {
      this.$message.warning('该活动状态不允许修改')
      return
    }
    
    const action = row.status === 0 ? '启用' : '停用'
    const newStatus = row.status === 0 ? 1 : 0
    
    this.$confirm(`确认${action}该秒杀活动?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      try {
        const res = await updateSeckillActivityStatus(newStatus, { id: row.id })
        if (res.status === 200) {
          this.$message.success(`活动${action}成功！`)
          this.init()
        }
      } catch (err: any) {
        this.$message.error('请求出错了：' + err.message)
      }
    })
  }

  // 删除活动
  private deleteHandle(row: any) {
    if (row.status !== 0) {
      this.$message.warning('只能删除未开始的活动')
      return
    }
    
    this.$confirm('确认删除该秒杀活动?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      try {
        const res = await deleteSeckillActivity({ id: row.id })
        if (res.status === 200) {
          this.$message.success('删除成功！')
          this.init()
        }
      } catch (err: any) {
        this.$message.error('请求出错了：' + err.message)
      }
    })
  }

  // 获取状态文本
  private getStatusText(status: number): string {
    const statusMap: { [key: number]: string } = {
      0: '未开始',
      1: '进行中', 
      2: '已结束',
      3: '已取消'
    }
    return statusMap[status] || '未知'
  }

  private handleSizeChange(val: number) {
    this.pageSize = val
    this.init()
  }

  private handleCurrentChange(val: number) {
    this.page = val
    this.init()
  }
}
</script>

<style lang="scss" scoped>
.disabled-text {
  color: #bac0cd !important;
}

.tableColumn-status {
  &.stop-use {
    color: #999;
  }
  
  &.processing {
    color: #52c41a;
  }
  
  &.finished {
    color: #1890ff;
  }
}
</style>

