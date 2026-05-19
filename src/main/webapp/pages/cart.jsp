<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>购物车</title>
  <link rel="stylesheet" href="https://unpkg.com/element-plus/dist/index.css">
  <script src="https://unpkg.com/vue@3"></script>
  <script src="https://unpkg.com/element-plus"></script>
  <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
</head>
<body>
<div id="app" style="max-width: 1000px;margin: 30px auto;">
  <el-row justify="space-between">
    <h2>购物车</h2>
    <div>
      <el-button type="primary" @click="checkout">结算</el-button>
      <el-button @click="goMyOrders">我的订单</el-button>
      <el-button @click="back">返回商品页</el-button>
    </div>
  </el-row>

  <el-table :data="list" style="width: 100%;margin-top: 10px;">
    <el-table-column prop="productId" label="商品ID" width="100"></el-table-column>
    <el-table-column prop="productName" label="商品名"></el-table-column>
    <el-table-column prop="price" label="单价" width="120"></el-table-column>

    <el-table-column label="数量" width="220">
      <template #default="scope">
        <el-input-number v-model="scope.row.quantity" :min="1" :max="99" size="small"/>
        <el-button size="small" @click="update(scope.row)">更新</el-button>
      </template>
    </el-table-column>

    <el-table-column label="操作" width="120">
      <template #default="scope">
        <el-button size="small" type="danger" @click="del(scope.row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <div style="margin-top: 15px;display:flex;justify-content:center;">
    <el-pagination
      layout="prev, pager, next, sizes, total"
      :total="total"
      :page-size="pageSize"
      :current-page="page"
      @current-change="onPageChange"
      @size-change="onSizeChange"
      :page-sizes="[3,5,10]">
    </el-pagination>
  </div>
</div>

<script>
const { createApp, reactive, onMounted } = Vue;

createApp({
  setup(){
    const state = reactive({ page: 1, pageSize: 5, total: 0, list: [] });

    async function load(){
      const res = await axios.get("<%=request.getContextPath()%>/api/cart/list", {
        params: { page: state.page, pageSize: state.pageSize }
      });
      if(res.data.code === 401){
        window.location.href = "<%=request.getContextPath()%>/pages/login.jsp";
        return;
      }
      if(res.data.code !== 0){
        ElementPlus.ElMessage.error(res.data.msg);
        return;
      }
      state.list = res.data.data.list;
      state.total = res.data.data.total;
    }

    async function update(row){
      const form = new URLSearchParams();
      form.append("productId", row.productId);
      form.append("quantity", row.quantity);
      const res = await axios.post("<%=request.getContextPath()%>/api/cart/update", form);
      if(res.data.code === 0) ElementPlus.ElMessage.success("已更新");
      else ElementPlus.ElMessage.error(res.data.msg);
    }

    async function del(row){
      const form = new URLSearchParams();
      form.append("productId", row.productId);
      const res = await axios.post("<%=request.getContextPath()%>/api/cart/delete", form);
      if(res.data.code === 0){
        ElementPlus.ElMessage.success("已删除");
        load();
      } else {
        ElementPlus.ElMessage.error(res.data.msg);
      }
    }

    function onPageChange(p){ state.page = p; load(); }
    function onSizeChange(ps){ state.pageSize = ps; state.page = 1; load(); }
    function back(){ window.location.href = "<%=request.getContextPath()%>/pages/products.jsp"; }
    function goMyOrders(){ window.location.href = "<%=request.getContextPath()%>/pages/my_orders.jsp"; }

    async function checkout(){
      const res = await axios.post("<%=request.getContextPath()%>/api/order/checkout");
      if (res.data.code === 0) {
        ElementPlus.ElMessage.success("结算成功，订单号: " + res.data.data.orderId);
        load();
        return;
      }
      ElementPlus.ElMessage.error(res.data.msg);
    }

    onMounted(load);
    return { ...Vue.toRefs(state), load, update, del, onPageChange, onSizeChange, back, goMyOrders, checkout };
  }
}).use(ElementPlus).mount("#app");
</script>
</body>
</html>
