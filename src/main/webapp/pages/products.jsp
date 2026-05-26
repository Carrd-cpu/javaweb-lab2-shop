<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>商品列表</title>
  <link rel="stylesheet" href="https://unpkg.com/element-plus/dist/index.css">
  <script src="https://unpkg.com/vue@3"></script>
  <script src="https://unpkg.com/element-plus"></script>
  <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
</head>
<body>
<div id="app" style="max-width: 1000px;margin: 30px auto;">
  <el-row justify="space-between">
    <h2>商品列表</h2>
    <div>
      <el-button @click="goMyOrders">我的订单</el-button>
      <el-button v-if="isAdmin" type="warning" @click="goAdminProducts">商品管理</el-button>
      <el-button v-if="isAdmin" type="warning" @click="goAdminOrders">订单管理</el-button>
      <el-button @click="goCart">购物车</el-button>
      <el-button type="danger" @click="logout">退出</el-button>
    </div>
  </el-row>

  <el-table :data="list" style="width: 100%;margin-top: 10px;">
    <el-table-column prop="id" label="ID" width="80"></el-table-column>
    <el-table-column prop="name" label="商品名"></el-table-column>
    <el-table-column prop="price" label="价格" width="120"></el-table-column>
    <el-table-column prop="stock" label="库存" width="120"></el-table-column>
    <el-table-column label="操作" width="220">
      <template #default="scope">
        <el-input-number v-model="scope.row._qty" :min="1" :max="99" size="small" />
        <el-button size="small" type="primary" @click="addToCart(scope.row)">加入</el-button>
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
      :page-sizes="[5,8,10,20]">
    </el-pagination>
  </div>
</div>

<script>
const { createApp, reactive, onMounted } = Vue;

createApp({
  setup(){
    const state = reactive({
      page: 1,
      pageSize: 8,
      total: 0,
      list: [],
      isAdmin: false
    });

    async function load(){
      const res = await axios.get("<%=request.getContextPath()%>/api/products", {
        params: { page: state.page, pageSize: state.pageSize }
      });
      if(res.data.code !== 0){
        ElementPlus.ElMessage.error(res.data.msg);
        return;
      }
      state.list = res.data.data.list.map(x => ({...x, _qty: 1}));
      state.total = res.data.data.total;
    }

    async function addToCart(row){
      const form = new URLSearchParams();
      form.append("productId", row.id);
      form.append("quantity", row._qty);

      const res = await axios.post("<%=request.getContextPath()%>/api/cart/add", form);
      if(res.data.code === 0){
        ElementPlus.ElMessage.success("已加入购物车");
      }else if(res.data.code === 401){
        window.location.href = "<%=request.getContextPath()%>/pages/login.jsp";
      }else{
        ElementPlus.ElMessage.error(res.data.msg);
      }
    }

    async function loadMe(){
      const res = await axios.get("<%=request.getContextPath()%>/api/auth/me");
      if (res.data.code !== 0) {
        window.location.href = "<%=request.getContextPath()%>/pages/login.jsp";
        return;
      }
      state.isAdmin = res.data.data.role === "admin";
    }

    function onPageChange(p){ state.page = p; load(); }
    function onSizeChange(ps){ state.pageSize = ps; state.page = 1; load(); }

    function goCart(){
      window.location.href = "<%=request.getContextPath()%>/pages/cart.jsp";
    }
    function goMyOrders(){
      window.location.href = "<%=request.getContextPath()%>/pages/my_orders.jsp";
    }
    function goAdminProducts(){
      window.location.href = "<%=request.getContextPath()%>/pages/admin_products.jsp";
    }
    function goAdminOrders(){
      window.location.href = "<%=request.getContextPath()%>/pages/admin_orders.jsp";
    }

    async function logout(){
      await axios.post("<%=request.getContextPath()%>/api/auth/logout");
      window.location.href = "<%=request.getContextPath()%>/pages/login.jsp";
    }

    onMounted(async () => { await loadMe(); await load(); });
    return { ...Vue.toRefs(state), addToCart, onPageChange, onSizeChange, goCart, goMyOrders, goAdminProducts, goAdminOrders, logout };
  }
}).use(ElementPlus).mount("#app");
</script>
</body>
</html>
