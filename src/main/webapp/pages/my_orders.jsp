<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>我的订单</title>
  <link rel="stylesheet" href="https://unpkg.com/element-plus/dist/index.css">
  <script src="https://unpkg.com/vue@3"></script>
  <script src="https://unpkg.com/element-plus"></script>
  <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
</head>
<body>
<div id="app" style="max-width: 1000px;margin: 30px auto;">
  <el-row justify="space-between">
    <h2>我的订单</h2>
    <div>
      <el-button @click="backProducts">返回商品页</el-button>
    </div>
  </el-row>

  <el-table :data="orders" style="width:100%; margin-top:10px;">
    <el-table-column prop="id" label="订单号" width="120"></el-table-column>
    <el-table-column prop="totalAmount" label="总金额" width="140"></el-table-column>
    <el-table-column prop="status" label="状态" width="140"></el-table-column>
    <el-table-column prop="createdAt" label="创建时间"></el-table-column>
    <el-table-column label="操作" width="140">
      <template #default="scope">
        <el-button type="primary" link @click="viewDetail(scope.row)">详情</el-button>
      </template>
    </el-table-column>
  </el-table>

  <el-dialog v-model="detailVisible" title="订单详情" width="700px">
    <div v-if="detail.order">
      <p>订单号：{{ detail.order.id }} | 状态：{{ detail.order.status }} | 总金额：{{ detail.order.totalAmount }}</p>
      <el-table :data="detail.items" style="width:100%;">
        <el-table-column prop="productId" label="商品ID" width="100"></el-table-column>
        <el-table-column prop="productNameSnapshot" label="商品名"></el-table-column>
        <el-table-column prop="priceSnapshot" label="下单价格" width="140"></el-table-column>
        <el-table-column prop="quantity" label="数量" width="100"></el-table-column>
      </el-table>
    </div>
  </el-dialog>
</div>

<script>
const { createApp, reactive, onMounted } = Vue;

createApp({
  setup(){
    const state = reactive({
      orders: [],
      detailVisible: false,
      detail: { order: null, items: [] }
    });

    async function load(){
      const res = await axios.get("<%=request.getContextPath()%>/api/order/my");
      if (res.data.code === 401) {
        window.location.href = "<%=request.getContextPath()%>/pages/login.jsp";
        return;
      }
      if (res.data.code !== 0) {
        ElementPlus.ElMessage.error(res.data.msg);
        return;
      }
      state.orders = res.data.data;
    }

    async function viewDetail(row){
      const res = await axios.get("<%=request.getContextPath()%>/api/order/detail", { params: { orderId: row.id } });
      if (res.data.code !== 0) {
        ElementPlus.ElMessage.error(res.data.msg);
        return;
      }
      state.detail = res.data.data;
      state.detailVisible = true;
    }

    function backProducts(){
      window.location.href = "<%=request.getContextPath()%>/pages/products.jsp";
    }

    onMounted(load);
    return { ...Vue.toRefs(state), load, viewDetail, backProducts };
  }
}).use(ElementPlus).mount("#app");
</script>
</body>
</html>
