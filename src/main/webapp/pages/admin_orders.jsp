<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>订单管理</title>
  <link rel="stylesheet" href="https://unpkg.com/element-plus/dist/index.css">
  <script src="https://unpkg.com/vue@3"></script>
  <script src="https://unpkg.com/element-plus"></script>
  <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
</head>
<body>
<div id="app" style="max-width: 1100px;margin: 30px auto;">
  <el-row justify="space-between">
    <h2>管理员 - 订单管理</h2>
    <div>
      <el-button @click="goProducts">商品管理</el-button>
      <el-button @click="backMall">返回商城</el-button>
    </div>
  </el-row>

  <el-table :data="orders" style="width:100%; margin-top:10px;">
    <el-table-column prop="id" label="订单号" width="110"></el-table-column>
    <el-table-column prop="userId" label="用户ID" width="100"></el-table-column>
    <el-table-column prop="totalAmount" label="总金额" width="140"></el-table-column>
    <el-table-column label="状态" width="220">
      <template #default="scope">
        <el-select v-model="scope.row.status" style="width:140px;">
          <el-option label="CREATED" value="CREATED"></el-option>
          <el-option label="PAID" value="PAID"></el-option>
          <el-option label="SHIPPED" value="SHIPPED"></el-option>
          <el-option label="DONE" value="DONE"></el-option>
          <el-option label="CANCELED" value="CANCELED"></el-option>
        </el-select>
        <el-button type="primary" link @click="updateStatus(scope.row)">保存</el-button>
      </template>
    </el-table-column>
    <el-table-column prop="createdAt" label="创建时间"></el-table-column>
    <el-table-column label="操作" width="180">
      <template #default="scope">
        <el-button type="primary" link @click="viewDetail(scope.row)">详情</el-button>
        <el-button type="danger" link @click="del(scope.row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <el-dialog v-model="detailVisible" title="订单详情" width="750px">
    <div v-if="detail.order">
      <p>订单号：{{ detail.order.id }} | 用户ID：{{ detail.order.userId }} | 状态：{{ detail.order.status }}</p>
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
      const res = await axios.get("<%=request.getContextPath()%>/api/admin/orders/list");
      if (res.data.code === 401) {
        window.location.href = "<%=request.getContextPath()%>/pages/login.jsp";
        return;
      }
      if (res.data.code === 403) {
        ElementPlus.ElMessage.error("无权限访问");
        window.location.href = "<%=request.getContextPath()%>/pages/products.jsp";
        return;
      }
      if (res.data.code !== 0) {
        ElementPlus.ElMessage.error(res.data.msg);
        return;
      }
      state.orders = res.data.data;
    }

    async function viewDetail(row){
      const res = await axios.get("<%=request.getContextPath()%>/api/admin/orders/detail", { params: { orderId: row.id } });
      if (res.data.code !== 0) {
        ElementPlus.ElMessage.error(res.data.msg);
        return;
      }
      state.detail = res.data.data;
      state.detailVisible = true;
    }

    async function updateStatus(row){
      const form = new URLSearchParams();
      form.append("orderId", row.id);
      form.append("status", row.status);
      const res = await axios.post("<%=request.getContextPath()%>/api/admin/orders/status", form);
      if (res.data.code !== 0) {
        ElementPlus.ElMessage.error(res.data.msg);
        return;
      }
      ElementPlus.ElMessage.success("状态已更新");
      load();
    }

    async function del(row){
      const form = new URLSearchParams();
      form.append("orderId", row.id);
      const res = await axios.post("<%=request.getContextPath()%>/api/admin/orders/delete", form);
      if (res.data.code !== 0) {
        ElementPlus.ElMessage.error(res.data.msg);
        return;
      }
      ElementPlus.ElMessage.success("删除成功");
      load();
    }

    function goProducts(){
      window.location.href = "<%=request.getContextPath()%>/pages/admin_products.jsp";
    }

    function backMall(){
      window.location.href = "<%=request.getContextPath()%>/pages/products.jsp";
    }

    onMounted(load);
    return { ...Vue.toRefs(state), load, viewDetail, updateStatus, del, goProducts, backMall };
  }
}).use(ElementPlus).mount("#app");
</script>
</body>
</html>
