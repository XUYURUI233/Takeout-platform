<template>
  <div class="addBrand-container">
    <div class="container">
      <el-form
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        :inline="true"
        label-width="180px"
        class="demo-ruleForm"
      >
        <div>
          <el-form-item label="活动名称:" prop="name">
            <el-input
              v-model="ruleForm.name"
              placeholder="请填写活动名称"
              maxlength="50"
              :disabled="isView"
            />
          </el-form-item>
        </div>
        
        <div>
          <el-form-item label="活动时间:" prop="timeRange">
            <el-date-picker
              v-model="ruleForm.timeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"
              :disabled="isView"
              @change="timeRangeChange"
            />
          </el-form-item>
        </div>

        <div>
          <el-form-item label="活动图片:" prop="image">
            <image-upload
              :prop-image-url="imageUrl"
              :disabled="isView"
              @imageChange="imageChange"
            >
              图片大小不超过2M<br>仅能上传 PNG JPEG JPG类型图片<br>建议上传800*400尺寸的横幅图片
            </image-upload>
          </el-form-item>
        </div>

        <div class="address">
          <el-form-item label="活动描述:" prop="description">
            <el-input
              v-model="ruleForm.description"
              type="textarea"
              :rows="3"
              maxlength="200"
              placeholder="请填写活动描述"
              :disabled="isView"
            />
          </el-form-item>
        </div>

        <div class="goods-section">
          <el-form-item label="参与商品:">
            <div class="goods-container">
              <div class="goods-header">
                <el-button
                  v-if="!isView"
                  type="primary"
                  size="small"
                  @click="showAddGoodsDialog"
                >
                  + 添加商品
                </el-button>
                <span class="goods-count">已添加商品：{{ goodsList.length }} 个</span>
              </div>
              
              <el-table
                v-if="goodsList.length > 0"
                :data="goodsList"
                class="goods-table"
                border
              >
                <el-table-column label="商品图片" width="100" align="center">
                  <template slot-scope="scope">
                    <el-image
                      style="width: 60px; height: 40px"
                      :src="scope.row.goodsImage"
                      fit="cover"
                    />
                  </template>
                </el-table-column>
                <el-table-column prop="goodsName" label="商品名称" />
                <el-table-column label="商品类型" width="100" align="center">
                  <template slot-scope="scope">
                    {{ scope.row.goodsType === 1 ? '菜品' : '套餐' }}
                  </template>
                </el-table-column>
                <el-table-column label="原价" width="100" align="center">
                  <template slot-scope="scope">
                    ¥{{ scope.row.originalPrice }}
                  </template>
                </el-table-column>
                <el-table-column label="秒杀价" width="140" align="center">
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.seckillPrice"
                      type="number"
                      :min="0.01"
                      :max="scope.row.originalPrice"
                      step="0.01"
                      size="small"
                      :disabled="isView"
                      style="width: 120px"
                      @input="validatePrice(scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="总库存" width="140" align="center">
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.totalStock"
                      type="number"
                      :min="1"
                      :max="9999"
                      size="small"
                      :disabled="isView"
                      style="width: 120px"
                      @input="validateStock(scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="限购数量" width="140" align="center">
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.limitCount"
                      type="number"
                      :min="1"
                      :max="scope.row.totalStock"
                      size="small"
                      :disabled="isView"
                      style="width: 120px"
                      @input="validateLimit(scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column v-if="!isView" label="操作" width="80" align="center">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      size="small"
                      class="delBut"
                      @click="removeGoods(scope.$index)"
                    >
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              
              <div v-else class="no-goods">
                <span>暂无商品，请点击"添加商品"按钮添加</span>
              </div>
            </div>
          </el-form-item>
        </div>

        <div class="subBox address">
          <el-form-item>
            <el-button v-if="!isView" @click="goBack('ruleForm')">
              取消
            </el-button>
            <el-button v-if="!isView" type="primary" @click="submitForm('ruleForm')">
              保存
            </el-button>
            <el-button v-if="isView" @click="goBack('ruleForm')">
              返回
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </div>

    <!-- 添加商品对话框 -->
    <el-dialog
      title="选择商品"
      :visible.sync="addGoodsDialogVisible"
      width="80%"
      @close="closeAddGoodsDialog"
    >
      <div class="add-goods-dialog">
        <div class="search-bar">
          <label style="margin-right: 10px">商品类型：</label>
          <el-select v-model="goodsTypeFilter" placeholder="请选择类型" style="width: 120px">
            <el-option label="菜品" :value="1"></el-option>
            <el-option label="套餐" :value="2"></el-option>
          </el-select>
          <label style="margin-left: 20px; margin-right: 10px">商品名称：</label>
          <el-input
            v-model="goodsNameFilter"
            placeholder="请输入商品名称"
            style="width: 200px"
          />
          <el-button type="primary" @click="searchAvailableGoods">查询</el-button>
        </div>
        
        <el-table
          :data="availableGoods"
          @selection-change="handleSelectionChange"
          max-height="400"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="商品图片" width="100" align="center">
            <template slot-scope="scope">
              <el-image
                style="width: 60px; height: 40px"
                :src="scope.row.image"
                fit="cover"
              />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="商品名称" />
          <el-table-column label="商品类型" width="100" align="center">
            <template slot-scope="scope">
              {{ scope.row.type === 1 ? '菜品' : '套餐' }}
            </template>
          </el-table-column>
          <el-table-column label="分类" prop="categoryName" width="120" />
          <el-table-column label="价格" width="100" align="center">
            <template slot-scope="scope">
              ¥{{ scope.row.price }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="addGoodsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddGoods">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import ImageUpload from '@/components/ImgUpload/index.vue'
import { 
  addSeckillActivity, 
  updateSeckillActivity, 
  getSeckillActivityById,
  getAvailableGoods
} from '@/api/seckill'

@Component({
  name: 'AddSeckill',
  components: {
    ImageUpload,
  },
})
export default class extends Vue {
  private ruleForm: any = {
    name: '',
    timeRange: [],
    startTime: '',
    endTime: '',
    image: '',
    description: '',
  }

  private rules = {
    name: [{ required: true, message: '请填写活动名称', trigger: 'blur' }],
    timeRange: [{ required: true, message: '请选择活动时间', trigger: 'change' }],
    image: [{ required: true, message: '请上传活动图片', trigger: 'change' }],
  }

  private imageUrl: string = ''
  private goodsList: any[] = []
  private isView: boolean = false
  private actionType: string = ''
  private activityId: string = ''
  
  // 添加商品对话框相关
  private addGoodsDialogVisible: boolean = false
  private availableGoods: any[] = []
  private selectedGoods: any[] = []
  private goodsTypeFilter: number = 1
  private goodsNameFilter: string = ''

  created() {
    this.actionType = this.$route.query.id ? 'edit' : 'add'
    this.isView = this.$route.query.view === 'true'
    this.activityId = this.$route.query.id as string || ''
    
    if (this.actionType === 'edit') {
      this.init()
    }
  }

  private async init() {
    if (!this.activityId) return
    
    try {
      const res = await getSeckillActivityById(this.activityId)
      if (res.data.code === 1) {
        const data = res.data.data
        this.ruleForm = {
          name: data.name,
          timeRange: [data.startTime, data.endTime],
          startTime: data.startTime,
          endTime: data.endTime,
          image: data.image,
          description: data.description,
        }
        this.imageUrl = data.image
        this.goodsList = data.goodsList || []
      }
    } catch (err: any) {
      this.$message.error('获取活动详情失败：' + err.message)
    }
  }

  private imageChange(imageUrl: string) {
    this.ruleForm.image = imageUrl
    this.imageUrl = imageUrl
  }

  private timeRangeChange(timeRange: string[]) {
    if (timeRange && timeRange.length === 2) {
      this.ruleForm.startTime = timeRange[0]
      this.ruleForm.endTime = timeRange[1]
    } else {
      this.ruleForm.startTime = ''
      this.ruleForm.endTime = ''
    }
  }

  private async showAddGoodsDialog() {
    this.addGoodsDialogVisible = true
    this.searchAvailableGoods()
  }

  private async searchAvailableGoods() {
    try {
      const params = {
        type: this.goodsTypeFilter,
        name: this.goodsNameFilter || undefined
      }
      const res = await getAvailableGoods(params)
      if (res.data.code === 1) {
        // 过滤掉已添加的商品
        const addedGoodsIds = this.goodsList.map(item => item.goodsId)
        this.availableGoods = (res.data.data || []).filter((item: any) => 
          !addedGoodsIds.includes(item.id)
        )
      }
    } catch (err: any) {
      this.$message.error('获取商品列表失败：' + err.message)
    }
  }

  private handleSelectionChange(selection: any[]) {
    this.selectedGoods = selection
  }

  private confirmAddGoods() {
    if (this.selectedGoods.length === 0) {
      this.$message.warning('请选择要添加的商品')
      return
    }

    this.selectedGoods.forEach(goods => {
      this.goodsList.push({
        goodsType: goods.type,
        goodsId: goods.id,
        goodsName: goods.name,
        goodsImage: goods.image,
        originalPrice: goods.price,
        seckillPrice: parseFloat((goods.price * 0.8).toFixed(2)), // 默认8折，转换为数字
        totalStock: 100, // 默认库存
        limitCount: 2, // 默认限购
      })
    })

    this.addGoodsDialogVisible = false
    this.selectedGoods = []
  }

  private closeAddGoodsDialog() {
    this.selectedGoods = []
    this.goodsNameFilter = ''
    this.goodsTypeFilter = 1
  }

  private removeGoods(index: number) {
    this.goodsList.splice(index, 1)
  }

  private submitForm(formName: string) {
    (this.$refs[formName] as any).validate(async (valid: boolean) => {
      if (valid) {
        if (this.goodsList.length === 0) {
          this.$message.warning('请至少添加一个商品')
          return
        }

        // 准备提交的数据，确保字段名与后端DTO匹配
        const formData = {
          name: this.ruleForm.name,
          image: this.ruleForm.image,
          startTime: this.ruleForm.startTime,
          endTime: this.ruleForm.endTime,
          description: this.ruleForm.description,
          goodsList: this.goodsList
        }

        if (this.actionType === 'edit') {
          formData.id = this.activityId
        }

        try {
          const res = this.actionType === 'add' 
            ? await addSeckillActivity(formData)
            : await updateSeckillActivity(formData)
            
          if (res.status === 200) {
            this.$message.success(`${this.actionType === 'add' ? '添加' : '修改'}成功！`)
            this.$router.push({ path: '/seckill' })
          }
        } catch (err: any) {
          this.$message.error('请求出错了：' + err.message)
        }
      }
    })
  }

  private goBack() {
    this.$router.back()
  }

  // 验证价格输入
  private validatePrice(row: any) {
    const price = parseFloat(row.seckillPrice)
    if (isNaN(price) || price <= 0) {
      row.seckillPrice = 0.01
    } else if (price > row.originalPrice) {
      row.seckillPrice = row.originalPrice
    } else {
      row.seckillPrice = parseFloat(price.toFixed(2))
    }
  }

  // 验证库存输入
  private validateStock(row: any) {
    const stock = parseInt(row.totalStock)
    if (isNaN(stock) || stock <= 0) {
      row.totalStock = 1
    } else if (stock > 9999) {
      row.totalStock = 9999
    } else {
      row.totalStock = stock
    }
  }

  // 验证限购数量输入
  private validateLimit(row: any) {
    const limit = parseInt(row.limitCount)
    if (isNaN(limit) || limit <= 0) {
      row.limitCount = 1
    } else if (limit > row.totalStock) {
      row.limitCount = row.totalStock
    } else {
      row.limitCount = limit
    }
  }
}
</script>

<style lang="scss" scoped>
.goods-section {
  width: 100%;
  
  .goods-container {
    width: 100%;
    
    .goods-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
      
      .goods-count {
        color: #666;
        font-size: 14px;
      }
    }
    
    .goods-table {
      width: 100%;
    }
    
    .no-goods {
      text-align: center;
      padding: 40px;
      color: #999;
      border: 1px dashed #ddd;
      border-radius: 4px;
    }
  }
}

.add-goods-dialog {
  .search-bar {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    
    label {
      white-space: nowrap;
    }
  }
}

.address {
  width: 100%;
}
</style>
