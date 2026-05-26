<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>商品管理</title>
  <link rel="stylesheet" href="https://unpkg.com/element-plus/dist/index.css">
  <script src="https://unpkg.com/vue@3"></script>
  <script src="https://unpkg.com/element-plus"></script>
  <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
</head>
<body>
<div id="app" style="max-width: 1100px;margin: 30px auto;">
  <el-row justify="space-between">
    <h2>管理员 - 商品管理</h2>
    <div>
      <el-button @click="goOrders">订单管理</el-button>
      <el-button @click="backProducts">返回商城</el-button>
    </div>
  </el-row>

  <el-card style="margin: 10px 0;">
    <el-form :inline="true">
      <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="价格"><el-input v-model="form.price" /></el-form-item>
      <el-form-item label="库存"><el-input v-model="form.stock" /></el-form-item>
      <el-form-item label="封面URL"><el-input v-model="form.coverUrl" /></el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">{{ form.id ? '更新' : '新增' }}</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>

  <el-table :data="list" style="width:100%;">
    <el-table-column prop="id" label="ID" width="80"></el-table-column>
    <el-table-column prop="name" label="名称"></el-table-column>
    <el-table-column prop="price" label="价格" width="120"></el-table-column>
    <el-table-column prop="stock" label="库存" width="120"></el-table-column>
    <el-table-column prop="coverUrl" label="封面"></el-table-column>
    <el-table-column label="操作" width="180">
      <template #default="scope">
        <el-button type="primary" link @click="edit(scope.row)">编辑</el-button>
        <el-button type="danger" link @click="del(scope.row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</div>

<script>
const { createApp, reactive, onMounted } = Vue;

createApp({
  setup(){
    const state = reactive({
      list: [],
      form: { id: null, name: "", price: "", stock: "", coverUrl: "" }
    });

    async function load(){
      const res = await axios.get("<%=request.getContextPath()%>/api/admin/products/list");
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
      state.list = res.data.data;
    }

    async function submit(){
      const form = new URLSearchParams();
      if (state.form.id) form.append("id", state.form.id);
      form.append("name", state.form.name);
      form.append("price", state.form.price);
      form.append("stock", state.form.stock);
      form.append("coverUrl", state.form.coverUrl);
      const api = state.form.id ? "/update" : "/create";
      const res = await axios.post("<%=request.getContextPath()%>/api/admin/products" + api, form);
      if (res.data.code !== 0) {
        ElementPlus.ElMessage.error(res.data.msg);
        return;
      }
      ElementPlus.ElMessage.success(state.form.id ? "更新成功" : "新增成功");
      reset();
      load();
    }

    async function del(row){
      const form = new URLSearchParams();
      form.append("id", row.id);
      const res = await axios.post("<%=request.getContextPath()%>/api/admin/products/delete", form);
      if (res.data.code !== 0) {
        ElementPlus.ElMessage.error(res.data.msg);
        return;
      }
      ElementPlus.ElMessage.success("删除成功");
      load();
    }

    function edit(row){
      state.form = {
        id: row.id,
        name: row.name,
        price: row.price,
        stock: row.stock,
        coverUrl: row.coverUrl || ""
      };
    }

    function reset(){
      state.form = { id: null, name: "", price: "", stock: "", coverUrl: "" };
    }

    function goOrders(){
      window.location.href = "<%=request.getContextPath()%>/pages/admin_orders.jsp";
    }

    function backProducts(){
      window.location.href = "<%=request.getContextPath()%>/pages/products.jsp";
    }

    onMounted(load);
    return { ...Vue.toRefs(state), load, submit, del, edit, reset, goOrders, backProducts };
  }
}).use(ElementPlus).mount("#app");
</script>
</body>
</html>
