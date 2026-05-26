// ===========================
// 精恩风机ERP系统 v1.0
// ===========================

// ========== GLOBAL STATE ==========
var DB = null;
var currentPage = 'dashboard';
var modalCallback = null;
var idCounters = {};

// ========== DATA PERSISTENCE ==========
function loadDB() {
  var raw = localStorage.getItem('jn_fan_erp_data');
  if (raw) {
    try { DB = JSON.parse(raw); } catch (e) { DB = getDefaultDB(); saveDB(); }
  } else {
    DB = getDefaultDB();
    saveDB();
  }
}

function saveDB() {
  localStorage.setItem('jn_fan_erp_data', JSON.stringify(DB));
}

// ========== UTILITY FUNCTIONS ==========
function toast(msg, type) {
  var t = document.createElement('div');
  t.className = 'toast toast-' + (type === 'w' ? 'w' : type === 'd' ? 'd' : 's');
  t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(function () { if (t.parentNode) t.parentNode.removeChild(t); }, 2600);
}

function genId(prefix) {
  var d = new Date();
  var ds = d.getFullYear() + ('0' + (d.getMonth() + 1)).slice(-2) + ('0' + d.getDate()).slice(-2);
  if (!idCounters[prefix]) idCounters[prefix] = 0;
  idCounters[prefix]++;
  return prefix + '-' + ds + '-' + ('00' + idCounters[prefix]).slice(-3);
}

function tagHtml(status) {
  var map = {
    '待审批': 'tw', '已确认': 'ti', '生产中': 'ti', '已发货': 'ti', '已完成': 'ts', '已取消': 'td',
    '待处理': 'tw', '已处理': 'ts', '已驳回': 'td',
    '正常': 'ts', '库存不足': 'tw', '低于安全库存': 'tw', '负库存': 'td',
    '已结清': 'ts', '部分回款': 'tw', '未回款': 'td', '超期': 'td',
    '合格': 'ts', '不合格': 'td', '让步接收': 'tw',
    '未开始': 'tw', '进行中': 'ti', '已暂停': 'tw',
    '启用': 'ts', '停用': 'td',
    '草稿': 'ti', '已提交': 'ti', '已下达': 'ti'
  };
  var cls = map[status] || 'ti';
  return '<span class="tag ' + cls + '">' + status + '</span>';
}

function $(id) { return document.getElementById(id); }

// ========== MODAL SYSTEM ==========
function openModal(title, bodyHtml, footerHtml, onConfirmCallback, wideFlag) {
  $('mt').textContent = title;
  $('mm').innerHTML = bodyHtml;
  $('mf').innerHTML = footerHtml;
  $('md').classList.add('sh');
  if (wideFlag) $('mb').classList.add('wd'); else $('mb').classList.remove('wd');
  modalCallback = onConfirmCallback || null;
}

function CM() {
  $('md').classList.remove('sh');
  modalCallback = null;
}

function confirmModal() {
  if (modalCallback) {
    try { modalCallback(); } catch (e) { toast('操作失败: ' + e.message, 'd'); }
  }
  CM();
}

// ========== TABLE HELPERS ==========
function renderTable(tbodyId, dataArray, page, pageSize, rowFn) {
  var tbody = $(tbodyId);
  if (!tbody) return;
  var start = (page - 1) * pageSize;
  var slice = dataArray.slice(start, start + pageSize);
  var html = '';
  for (var i = 0; i < slice.length; i++) {
    html += rowFn(slice[i], start + i);
  }
  if (slice.length === 0) {
    var colspan = tbody.parentNode.rows.length > 0 ? tbody.parentNode.rows[0].cells.length : 6;
    html = '<tr><td colspan="' + colspan + '" style="text-align:center;color:#999;padding:20px">暂无数据</td></tr>';
  }
  tbody.innerHTML = html;
}

var filterTableId = null;
function filterTable(tbodyId, list, searchInputId, fields) {
  var kw = $(searchInputId) ? $(searchInputId).value.trim().toLowerCase() : '';
  if (!kw) return list;
  return list.filter(function (item) {
    for (var i = 0; i < fields.length; i++) {
      var val = item[fields[i]];
      if (val !== undefined && val !== null && String(val).toLowerCase().indexOf(kw) !== -1) return true;
    }
    return false;
  });
}

var pageStates = {};
function attachPager(tbodyId, containerId, dataArray, pageSizePtr) {
  var container = $(containerId);
  if (!container) return;
  var ps = pageStates[tbodyId] || { page: 1, pageSize: 5 };
  pageStates[tbodyId] = ps;
  if (pageSizePtr !== undefined && pageSizePtr.val !== undefined) ps.pageSize = pageSizePtr.val;
  var total = Math.ceil(dataArray.length / ps.pageSize);
  var html = '<span>共 ' + dataArray.length + ' 条</span>';
  html += '<button' + (ps.page <= 1 ? ' disabled' : '') + ' onclick="goPage(\'' + tbodyId + '\',' + (ps.page - 1) + ')">上一页</button>';
  for (var p = 1; p <= total; p++) {
    html += '<span' + (p === ps.page ? ' class="ac"' : '') + ' onclick="goPage(\'' + tbodyId + '\',' + p + ')">' + p + '</span>';
  }
  html += '<button' + (ps.page >= total ? ' disabled' : '') + ' onclick="goPage(\'' + tbodyId + '\',' + (ps.page + 1) + ')">下一页</button>';
  container.innerHTML = html;
}

function goPage(tbodyId, p) {
  pageStates[tbodyId].page = p;
  renderPage(currentPage);
}

// ========== PAGE ROUTING ==========
var pageTitles = {
  'dashboard': '📊 经营看板', 'material': '📋 物料管理', 'customer': '👥 客户管理',
  'supplier': '🏭 供应商管理', 'fanmodel': '🔧 风机型号库', 'quotation': '📝 销售报价',
  'salesorder': '📦 销售订单', 'delivery': '🚚 发货管理', 'purchreq': '📋 采购需求',
  'purchorder': '🛒 采购订单', 'receive': '✅ 收货质检', 'instock': '📥 入库管理',
  'outstock': '📤 出库管理', 'inventory': '📊 库存查询', 'bom': '🌳 BOM管理',
  'workorder': '⚙️ 生产工单', 'workreport': '📋 工序报工', 'quality': '🔍 质量检验',
  'receivable': '💰 应收账款', 'payable': '💳 应付账款', 'cost': '📊 成本核算',
  'users': '👤 用户管理', 'roles': '🔑 角色管理'
};

function showPage(name) {
  currentPage = name;
  $('bc').textContent = pageTitles[name] || name;
  var items = document.querySelectorAll('#sm .mi');
  for (var i = 0; i < items.length; i++) {
    items[i].classList.toggle('ac', items[i].getAttribute('data-page') === name);
  }
  renderPage(name);
}

function renderPage(name) {
  pageStates = {};
  var fnMap = {
    'dashboard': renderDashboard, 'material': renderMaterial, 'customer': renderCustomer,
    'supplier': renderSupplier, 'fanmodel': renderFanModel, 'quotation': renderQuotation,
    'salesorder': renderSalesOrder, 'delivery': renderDelivery, 'purchreq': renderPurchReq,
    'purchorder': renderPurchOrder, 'receive': renderReceive, 'instock': renderInStock,
    'outstock': renderOutStock, 'inventory': renderInventory, 'bom': renderBOM,
    'workorder': renderWorkOrder, 'workreport': renderWorkReport, 'quality': renderQuality,
    'receivable': renderReceivable, 'payable': renderPayable, 'cost': renderCost,
    'users': renderUsers, 'roles': renderRoles
  };
  var fn = fnMap[name];
  if (fn) { $('mc').innerHTML = fn(); }
}

// ========== DATA MODEL & SEED ==========
function getDefaultDB() {
  idCounters = {};
  return {
    materials: [
      { code: 'MAT-001', name: '4-72-5A离心风机', spec: '4-72 No.5A', cat: '离心风机', fanType: '离心风机', airflow: 7950, pressure: 3200, power: 11, rpm: 2900, noise: 78, dia: 500, unit: '台', safety: 10, price: 5800 },
      { code: 'MAT-002', name: '9-19-6.3A高压风机', spec: '9-19 No.6.3A', cat: '高压风机', fanType: '离心风机', airflow: 6300, pressure: 9200, power: 30, rpm: 2900, noise: 92, dia: 630, unit: '台', safety: 5, price: 12500 },
      { code: 'MAT-003', name: 'T35-4轴流风机', spec: 'T35 No.4', cat: '轴流风机', fanType: '轴流风机', airflow: 4800, pressure: 150, power: 0.55, rpm: 1450, noise: 62, dia: 400, unit: '台', safety: 15, price: 1200 },
      { code: 'MAT-004', name: 'T35-5轴流风机', spec: 'T35 No.5', cat: '轴流风机', fanType: '轴流风机', airflow: 8800, pressure: 200, power: 1.1, rpm: 1450, noise: 68, dia: 500, unit: '台', safety: 12, price: 1800 },
      { code: 'MAT-005', name: 'SWF-4混流风机', spec: 'SWF No.4', cat: '混流风机', fanType: '混流风机', airflow: 5600, pressure: 500, power: 2.2, rpm: 1450, noise: 70, dia: 400, unit: '台', safety: 8, price: 3200 },
      { code: 'MAT-006', name: '4-72-6C离心风机', spec: '4-72 No.6C', cat: '离心风机', fanType: '离心风机', airflow: 13000, pressure: 2800, power: 18.5, rpm: 2500, noise: 82, dia: 600, unit: '台', safety: 8, price: 8500 },
      { code: 'MAT-007', name: '9-19-5A高压风机', spec: '9-19 No.5A', cat: '高压风机', fanType: '离心风机', airflow: 3500, pressure: 6800, power: 15, rpm: 2900, noise: 88, dia: 500, unit: '台', safety: 6, price: 9500 },
      { code: 'MAT-008', name: 'T35-6.3轴流风机', spec: 'T35 No.6.3', cat: '轴流风机', fanType: '轴流风机', airflow: 16000, pressure: 300, power: 2.2, rpm: 960, noise: 75, dia: 630, unit: '台', safety: 10, price: 2500 },
      { code: 'MAT-009', name: 'SWF-5混流风机', spec: 'SWF No.5', cat: '混流风机', fanType: '混流风机', airflow: 9500, pressure: 650, power: 4, rpm: 1450, noise: 74, dia: 500, unit: '台', safety: 7, price: 4500 }
    ],
    customers: [
      { code: 'CUS-001', name: '苏州恒通设备有限公司', contact: '张伟', phone: '13912345601', region: '江苏苏州', credit: 'A' },
      { code: 'CUS-002', name: '上海建工通风工程有限公司', contact: '李明', phone: '13912345602', region: '上海浦东', credit: 'A' },
      { code: 'CUS-003', name: '武汉中建暖通工程有限公司', contact: '王芳', phone: '13912345603', region: '湖北武汉', credit: 'B' },
      { code: 'CUS-004', name: '广州粤创机电设备有限公司', contact: '陈伟强', phone: '13912345604', region: '广东广州', credit: 'A' }
    ],
    suppliers: [
      { code: 'SUP-001', name: '无锡钢达钢材有限公司', contact: '赵经理', biz: '钢板/型材', rating: 'A' },
      { code: 'SUP-002', name: '常州精工电机有限公司', contact: '钱工', biz: '电机/轴承', rating: 'A' },
      { code: 'SUP-003', name: '江苏凯博铸造有限公司', contact: '孙总', biz: '铸件/叶轮', rating: 'B' }
    ],
    quotes: [],
    salesOrders: [
      { id: 'SO-20260401-001', customer: '苏州恒通设备有限公司', product: '4-72-5A离心风机', qty: 5, amount: 29000, deliveryDate: '2026-05-15', status: '已完成' },
      { id: 'SO-20260510-002', customer: '上海建工通风工程有限公司', product: '9-19-6.3A高压风机', qty: 3, amount: 37500, deliveryDate: '2026-06-20', status: '生产中' },
      { id: 'SO-20260520-003', customer: '武汉中建暖通工程有限公司', product: 'T35-5轴流风机', qty: 10, amount: 18000, deliveryDate: '2026-07-01', status: '待审批' }
    ],
    deliveries: [
      { id: 'DL-20260501-001', salesOrder: 'SO-20260401-001', customer: '苏州恒通设备有限公司', product: '4-72-5A离心风机', qty: 5, date: '2026-05-12', logistics: '顺丰物流 SF1234567890', status: '已签收' }
    ],
    purchReqs: [],
    purchOrders: [],
    receives: [],
    workOrders: [
      { id: 'WO-20260510-001', product: '9-19-6.3A高压风机', qty: 3, salesOrder: 'SO-20260510-002', customer: '上海建工通风工程有限公司', start: '2026-05-12', end: '2026-06-15', progress: 60, status: '生产中' }
    ],
    workReports: [
      { wo: 'WO-20260510-001', seq: 1, op: '下料切割', wc: '下料车间', worker: '周师傅', start: '2026-05-12', end: '2026-05-15', output: 3, defect: 0, hours: 24, status: '已完成' },
      { wo: 'WO-20260510-001', seq: 2, op: '焊接组装', wc: '焊接车间', worker: '吴师傅', start: '2026-05-16', end: '2026-05-22', output: 3, defect: 0, hours: 40, status: '已完成' },
      { wo: 'WO-20260510-001', seq: 3, op: '叶轮装配', wc: '装配车间', worker: '郑师傅', start: '2026-05-23', end: '', output: 0, defect: 0, hours: 0, status: '进行中' }
    ],
    qualityChecks: [],
    receivables: [
      { customer: '苏州恒通设备有限公司', total: 29000, received: 29000, balance: 0, age: 5, status: '已结清' },
      { customer: '上海建工通风工程有限公司', total: 37500, received: 15000, balance: 22500, age: 16, status: '部分回款' },
      { customer: '武汉中建暖通工程有限公司', total: 18000, received: 0, balance: 18000, age: 3, status: '未回款' }
    ],
    payables: [
      { supplier: '无锡钢达钢材有限公司', total: 35000, paid: 20000, balance: 15000, dueDate: '2026-06-15', status: '部分付款' },
      { supplier: '常州精工电机有限公司', total: 18000, paid: 18000, balance: 0, dueDate: '2026-05-10', status: '已结清' },
      { supplier: '江苏凯博铸造有限公司', total: 22000, paid: 0, balance: 22000, dueDate: '2026-06-30', status: '未付款' }
    ],
    inventory: [
      { code: 'MAT-001', name: '4-72-5A离心风机', warehouse: '成品仓A区', stock: 25, safety: 10, status: '正常' },
      { code: 'MAT-002', name: '9-19-6.3A高压风机', warehouse: '成品仓A区', stock: 3, safety: 5, status: '低于安全库存' },
      { code: 'MAT-003', name: 'T35-4轴流风机', warehouse: '成品仓B区', stock: 48, safety: 15, status: '正常' },
      { code: 'MAT-004', name: 'T35-5轴流风机', warehouse: '成品仓B区', stock: 18, safety: 12, status: '正常' },
      { code: 'MAT-005', name: 'SWF-4混流风机', warehouse: '成品仓A区', stock: 6, safety: 8, status: '低于安全库存' },
      { code: 'MAT-006', name: '4-72-6C离心风机', warehouse: '成品仓C区', stock: 12, safety: 8, status: '正常' },
      { code: 'MAT-007', name: '9-19-5A高压风机', warehouse: '成品仓A区', stock: 0, safety: 6, status: '库存不足' },
      { code: 'MAT-008', name: 'T35-6.3轴流风机', warehouse: '成品仓B区', stock: 22, safety: 10, status: '正常' },
      { code: 'MAT-009', name: 'SWF-5混流风机', warehouse: '成品仓C区', stock: 14, safety: 7, status: '正常' }
    ],
    users: [
      { username: 'admin', name: '系统管理员', dept: '信息部', phone: '13900000001', status: '启用', ctime: '2025-01-01' },
      { username: 'zhangwei', name: '张伟', dept: '销售部', phone: '13900000002', status: '启用', ctime: '2025-03-15' },
      { username: 'liming', name: '李明', dept: '生产部', phone: '13900000003', status: '启用', ctime: '2025-06-01' },
      { username: 'wangjie', name: '王洁', dept: '采购部', phone: '13900000004', status: '停用', ctime: '2025-08-20' }
    ],
    roles: [
      { name: '超级管理员', key: 'admin', sort: 1, status: '启用' },
      { name: '销售经理', key: 'sales_mgr', sort: 2, status: '启用' },
      { name: '生产主管', key: 'prod_mgr', sort: 3, status: '启用' },
      { name: '采购专员', key: 'purchase', sort: 4, status: '启用' },
      { name: '财务主管', key: 'finance', sort: 5, status: '启用' }
    ]
  };
}

// ========== SIDEBAR BUILDER ==========
function buildSidebar() {
  var groups = [
    { title: '📈 经营总览', items: [
      { page: 'dashboard', icon: '📊', label: '经营看板' }
    ]},
    { title: '🏗️ 基础数据', items: [
      { page: 'material', icon: '📋', label: '物料管理' },
      { page: 'customer', icon: '👥', label: '客户管理' },
      { page: 'supplier', icon: '🏭', label: '供应商管理' },
      { page: 'fanmodel', icon: '🔧', label: '风机型号库' }
    ]},
    { title: '📦 销售管理', items: [
      { page: 'quotation', icon: '📝', label: '销售报价' },
      { page: 'salesorder', icon: '📦', label: '销售订单' },
      { page: 'delivery', icon: '🚚', label: '发货管理' }
    ]},
    { title: '🛒 采购管理', items: [
      { page: 'purchreq', icon: '📋', label: '采购需求' },
      { page: 'purchorder', icon: '🛒', label: '采购订单' },
      { page: 'receive', icon: '✅', label: '收货质检' }
    ]},
    { title: '📊 库存管理', items: [
      { page: 'instock', icon: '📥', label: '入库管理' },
      { page: 'outstock', icon: '📤', label: '出库管理' },
      { page: 'inventory', icon: '📊', label: '库存查询' }
    ]},
    { title: '⚙️ 生产管理', items: [
      { page: 'bom', icon: '🌳', label: 'BOM管理' },
      { page: 'workorder', icon: '⚙️', label: '生产工单' },
      { page: 'workreport', icon: '📋', label: '工序报工' },
      { page: 'quality', icon: '🔍', label: '质量检验' }
    ]},
    { title: '💰 财务管理', items: [
      { page: 'receivable', icon: '💰', label: '应收账款' },
      { page: 'payable', icon: '💳', label: '应付账款' },
      { page: 'cost', icon: '📊', label: '成本核算' }
    ]},
    { title: '🔐 系统管理', items: [
      { page: 'users', icon: '👤', label: '用户管理' },
      { page: 'roles', icon: '🔑', label: '角色管理' }
    ]}
  ];

  var html = '';
  for (var g = 0; g < groups.length; g++) {
    var grp = groups[g];
    html += '<div class="mti" onclick="this.nextElementSibling.style.display=this.nextElementSibling.style.display===\'none\'?\'\':\'none\'">' + grp.title + '</div>';
    for (var i = 0; i < grp.items.length; i++) {
      var it = grp.items[i];
      html += '<div class="mi" data-page="' + it.page + '" onclick="showPage(\'' + it.page + '\')"><span class="dot"></span>' + it.icon + ' ' + it.label + '</div>';
    }
  }
  $('sm').innerHTML = html;
}

// ========== LOGIN/LOGOUT ==========
function L() {
  var un = $('un').value.trim();
  var pw = $('pw').value.trim();
  if (un === 'admin' && pw === 'admin123') {
    $('lg').style.display = 'none';
    $('ma').style.display = 'block';
    $('ck').textContent = 'admin · 在线';
    loadDB();
    buildSidebar();
    showPage('dashboard');
    toast('登录成功，欢迎使用精恩风机ERP系统', 's');
  } else {
    toast('用户名或密码错误，请重试', 'd');
  }
}

function O() {
  if (confirm('确定要退出系统吗？')) {
    saveDB();
    $('ma').style.display = 'none';
    $('lg').style.display = 'flex';
    toast('已安全退出', 'w');
  }
}

document.addEventListener('keydown', function (e) {
  if (e.key === 'Enter' && $('lg').style.display !== 'none') {
    L();
  }
});

// ===========================
// MODULE RENDERERS
// ===========================

// ---------- DASHBOARD ----------
function renderDashboard() {
  var inv = DB.inventory;
  var soList = DB.salesOrders;
  var woList = DB.workOrders;
  var soTotal = soList.reduce(function (s, o) { return s + o.amount; }, 0);
  var soActive = soList.filter(function (o) { return o.status !== '已完成' && o.status !== '已取消'; }).length;
  var lowInv = inv.filter(function (i) { return i.status === '低于安全库存' || i.status === '库存不足'; }).length;
  var pendingPR = DB.purchReqs.filter(function (p) { return p.status === '待处理'; }).length;
  var woInProd = woList.filter(function (w) { return w.status === '生产中'; }).length;
  var overDue = DB.receivables.filter(function (r) { return r.status === '超期'; }).length;

  var alerts = [];
  for (var i = 0; i < inv.length; i++) {
    if (inv[i].status === '低于安全库存' || inv[i].status === '库存不足') alerts.push('⚠️ ' + inv[i].name + ' 库存不足 (当前' + inv[i].stock + '/安全' + inv[i].safety + ')');
  }
  for (var j = 0; j < soList.length; j++) {
    if (soList[j].status === '待审批') alerts.push('📋 销售订单 ' + soList[j].id + ' 等待审批');
  }
  for (var k = 0; k < woList.length; k++) {
    if (woList[k].progress >= 80 && woList[k].status === '生产中') alerts.push('🔔 工单 ' + woList[k].id + ' 即将完成 (' + woList[k].progress + '%)');
  }
  var recBal = DB.receivables.filter(function (r) { return r.balance > 0; });
  for (var m = 0; m < recBal.length; m++) {
    if (recBal[m].age > 30) alerts.push('💰 应收账款超期: ' + recBal[m].customer + ' 欠款 ¥' + recBal[m].balance.toLocaleString());
  }

  if (alerts.length === 0) alerts.push('✅ 系统运行正常，无异常告警');

  var h = '<div class="stats">';
  h += '<div class="sc" onclick="showPage(\'salesorder\')"><div class="n">¥' + (soTotal / 10000).toFixed(1) + '万</div><div class="l">销售订单总额(' + soList.length + '笔)</div></div>';
  h += '<div class="sc" onclick="showPage(\'inventory\')"><div class="n">' + lowInv + '</div><div class="l">低库存预警项</div></div>';
  h += '<div class="sc" onclick="showPage(\'workorder\')"><div class="n">' + woInProd + '</div><div class="l">生产中工单</div></div>';
  h += '<div class="sc" onclick="showPage(\'purchreq\')"><div class="n">' + pendingPR + '</div><div class="l">待处理采购需求</div></div>';
  h += '</div>';

  h += '<div class="card"><div class="card-h"><div class="card-t">🔔 实时告警</div></div>';
  for (var a = 0; a < alerts.length; a++) {
    h += '<div style="padding:6px 0;font-size:13px;border-bottom:1px solid #f5f5f5">' + alerts[a] + '</div>';
  }
  h += '</div>';

  h += '<div class="card"><div class="card-h"><div class="card-t">📈 近期待交付订单</div></div><div style="overflow-x:auto"><table id="dash-so-tbl"><thead><tr><th>订单号</th><th>客户</th><th>产品</th><th>数量</th><th>金额</th><th>交货期</th><th>状态</th></tr></thead><tbody></tbody></table></div></div>';

  setTimeout(function () {
    var activeSO = soList.filter(function (o) { return o.status !== '已完成' && o.status !== '已取消'; });
    renderTable('dash-so-tbl', activeSO, 1, 10, function (o) {
      return '<tr><td>' + o.id + '</td><td>' + o.customer + '</td><td>' + o.product + '</td><td>' + o.qty + '</td><td>¥' + o.amount.toLocaleString() + '</td><td>' + o.deliveryDate + '</td><td>' + tagHtml(o.status) + '</td></tr>';
    });
  }, 100);

  return h;
}

// ---------- MATERIAL MANAGEMENT ----------
function renderMaterial() {
  var list = DB.materials;
  var h = '<div class="card"><div class="card-h"><div class="card-t">📋 物料列表</div>';
  h += '<div><button class="btn bp btn-sm" onclick="openMaterialForm(null)">+ 新增物料</button>';
  h += '<button class="btn bw btn-sm" onclick="materialExcelImport()">📥 导入Excel</button></div></div>';
  h += '<div class="tb">';
  h += '<input type="text" id="mat-search" placeholder="搜索编码/名称..." oninput="renderPage(\'material\')">';
  h += '<select id="mat-cat-filter" onchange="renderPage(\'material\')"><option value="">全部类别</option><option value="离心风机">离心风机</option><option value="轴流风机">轴流风机</option><option value="混流风机">混流风机</option><option value="高压风机">高压风机</option></select>';
  h += '</div>';
  h += '<div style="overflow-x:auto"><table><thead><tr><th>编码</th><th>名称</th><th>类别</th><th>风量(CFM)</th><th>全压(Pa)</th><th>功率(kW)</th><th>转速(RPM)</th><th>噪声(dB)</th><th>直径(mm)</th><th>安全库存</th><th>单价</th><th>操作</th></tr></thead><tbody id="mat-tbody"></tbody></table></div>';
  h += '<div class="pg" id="mat-pager"></div></div>';

  setTimeout(function () {
    var kw = $('mat-search') ? $('mat-search').value.trim().toLowerCase() : '';
    var cat = $('mat-cat-filter') ? $('mat-cat-filter').value : '';
    var filtered = list.filter(function (m) {
      var matchKw = !kw || m.code.toLowerCase().indexOf(kw) !== -1 || m.name.toLowerCase().indexOf(kw) !== -1;
      var matchCat = !cat || m.cat === cat || m.fanType === cat;
      return matchKw && matchCat;
    });
    var ps = pageStates['mat-tbody'] || { page: 1 };
    pageStates['mat-tbody'] = ps;
    renderTable('mat-tbody', filtered, ps.page, 5, function (m) {
      return '<tr><td>' + m.code + '</td><td>' + m.name + '</td><td>' + m.cat + '</td><td>' + m.airflow.toLocaleString() + '</td><td>' + m.pressure.toLocaleString() + '</td><td>' + m.power + '</td><td>' + m.rpm + '</td><td>' + m.noise + '</td><td>' + m.dia + '</td><td>' + m.safety + '</td><td>¥' + m.price.toLocaleString() + '</td><td><button class="btn bp btn-sm" onclick="openMaterialForm(\'' + m.code + '\')">编辑</button><button class="btn br btn-sm" onclick="deleteMaterial(\'' + m.code + '\')">删除</button></td></tr>';
    });
    attachPager('mat-tbody', 'mat-pager', filtered, { val: 5 });
  }, 100);
  return h;
}

function openMaterialForm(oldCode) {
  var mat = null;
  if (oldCode) {
    for (var i = 0; i < DB.materials.length; i++) {
      if (DB.materials[i].code === oldCode) { mat = DB.materials[i]; break; }
    }
  }
  var isEdit = !!mat;
  var h = '<div class="fr"><div><label>物料编码</label><input id="mf-code" value="' + (mat ? mat.code : '自动生成') + '" ' + (isEdit ? 'readonly' : '') + '></div>';
  h += '<div><label>物料名称</label><input id="mf-name" value="' + (mat ? mat.name : '') + '"></div></div>';
  h += '<div class="fr"><div><label>规格型号</label><input id="mf-spec" value="' + (mat ? mat.spec : '') + '"></div>';
  h += '<div><label>类别</label><select id="mf-cat"><option value="离心风机"' + (mat && mat.cat === '离心风机' ? ' selected' : '') + '>离心风机</option><option value="轴流风机"' + (mat && mat.cat === '轴流风机' ? ' selected' : '') + '>轴流风机</option><option value="混流风机"' + (mat && mat.cat === '混流风机' ? ' selected' : '') + '>混流风机</option><option value="高压风机"' + (mat && mat.cat === '高压风机' ? ' selected' : '') + '>高压风机</option></select></div></div>';
  h += '<div class="fr"><div><label>风机类型</label><select id="mf-fanType"><option value="离心风机"' + (mat && mat.fanType === '离心风机' ? ' selected' : '') + '>离心风机</option><option value="轴流风机"' + (mat && mat.fanType === '轴流风机' ? ' selected' : '') + '>轴流风机</option><option value="混流风机"' + (mat && mat.fanType === '混流风机' ? ' selected' : '') + '>混流风机</option></select></div>';
  h += '<div><label>单位</label><select id="mf-unit"><option value="台"' + (mat && mat.unit === '台' ? ' selected' : '') + '>台</option><option value="套"' + (mat && mat.unit === '套' ? ' selected' : '') + '>套</option></select></div></div>';
  h += '<h4 style="margin:12px 0 4px;color:var(--p)">⚙️ 风机性能参数</h4>';
  h += '<div class="fr3"><div><label>风量(CFM)</label><input id="mf-airflow" type="number" value="' + (mat ? mat.airflow : '') + '"></div>';
  h += '<div><label>全压(Pa)</label><input id="mf-pressure" type="number" value="' + (mat ? mat.pressure : '') + '"></div>';
  h += '<div><label>功率(kW)</label><input id="mf-power" type="number" step="0.1" value="' + (mat ? mat.power : '') + '"></div></div>';
  h += '<div class="fr3"><div><label>转速(RPM)</label><input id="mf-rpm" type="number" value="' + (mat ? mat.rpm : '') + '"></div>';
  h += '<div><label>噪声(dB)</label><input id="mf-noise" type="number" value="' + (mat ? mat.noise : '') + '"></div>';
  h += '<div><label>叶轮直径(mm)</label><input id="mf-dia" type="number" value="' + (mat ? mat.dia : '') + '"></div></div>';
  h += '<div class="fr"><div><label>安全库存</label><input id="mf-safety" type="number" value="' + (mat ? mat.safety : '') + '"></div>';
  h += '<div><label>参考单价(¥)</label><input id="mf-price" type="number" step="0.01" value="' + (mat ? mat.price : '') + '"></div></div>';

  openModal(isEdit ? '编辑物料' : '新增物料', h, '<button class="btn bp" onclick="confirmModal()">保存</button><button class="btn" onclick="CM()">取消</button>', function () {
    saveMaterial(oldCode);
  });
}

function saveMaterial(oldCode) {
  var mat = {
    code: $('mf-code').value.trim(),
    name: $('mf-name').value.trim(),
    spec: $('mf-spec').value.trim(),
    cat: $('mf-cat').value,
    fanType: $('mf-fanType').value,
    unit: $('mf-unit').value,
    airflow: parseInt($('mf-airflow').value) || 0,
    pressure: parseInt($('mf-pressure').value) || 0,
    power: parseFloat($('mf-power').value) || 0,
    rpm: parseInt($('mf-rpm').value) || 0,
    noise: parseInt($('mf-noise').value) || 0,
    dia: parseInt($('mf-dia').value) || 0,
    safety: parseInt($('mf-safety').value) || 0,
    price: parseFloat($('mf-price').value) || 0
  };
  if (!mat.name || !mat.spec) { toast('请填写物料名称和规格', 'w'); return; }

  if (oldCode) {
    for (var i = 0; i < DB.materials.length; i++) {
      if (DB.materials[i].code === oldCode) { DB.materials[i] = mat; break; }
    }
    for (var j = 0; j < DB.inventory.length; j++) {
      if (DB.inventory[j].code === oldCode) { DB.inventory[j].name = mat.name; break; }
    }
    toast('物料更新成功', 's');
  } else {
    mat.code = genId('MAT');
    DB.materials.push(mat);
    DB.inventory.push({ code: mat.code, name: mat.name, warehouse: '成品仓A区', stock: 0, safety: mat.safety, status: '库存不足' });
    toast('物料创建成功: ' + mat.code, 's');
  }
  saveDB();
  renderPage('material');
}

function deleteMaterial(code) {
  var mat = null;
  for (var i = 0; i < DB.materials.length; i++) {
    if (DB.materials[i].code === code) { mat = DB.materials[i]; break; }
  }
  if (!mat) return;
  if (!confirm('确定删除物料 ' + mat.name + ' (' + code + ') ？')) return;
  DB.materials = DB.materials.filter(function (m) { return m.code !== code; });
  DB.inventory = DB.inventory.filter(function (m) { return m.code !== code; });
  saveDB();
  toast('物料已删除', 's');
  renderPage('material');
}

function materialExcelImport() {
  toast('Excel导入功能开发中，请使用手动录入', 'w');
}

// ---------- CUSTOMER MANAGEMENT ----------
function renderCustomer() {
  var list = DB.customers;
  var h = '<div class="card"><div class="card-h"><div class="card-t">👥 客户列表</div>';
  h += '<button class="btn bp btn-sm" onclick="openCustomerForm(null)">+ 新增客户</button></div>';
  h += '<div class="tb"><input type="text" id="cus-search" placeholder="搜索客户..." oninput="renderPage(\'customer\')"></div>';
  h += '<table><thead><tr><th>编码</th><th>名称</th><th>联系人</th><th>电话</th><th>区域</th><th>信用等级</th><th>操作</th></tr></thead><tbody id="cus-tbody"></tbody></table>';
  h += '<div class="pg" id="cus-pager"></div></div>';

  setTimeout(function () {
    var kw = $('cus-search') ? $('cus-search').value.trim().toLowerCase() : '';
    var filtered = list.filter(function (c) { return !kw || c.code.toLowerCase().indexOf(kw) !== -1 || c.name.toLowerCase().indexOf(kw) !== -1; });
    var ps = pageStates['cus-tbody'] || { page: 1 };
    pageStates['cus-tbody'] = ps;
    renderTable('cus-tbody', filtered, ps.page, 5, function (c) {
      return '<tr><td>' + c.code + '</td><td>' + c.name + '</td><td>' + c.contact + '</td><td>' + c.phone + '</td><td>' + c.region + '</td><td>' + c.credit + '</td><td><button class="btn bp btn-sm" onclick="openCustomerForm(\'' + c.code + '\')">编辑</button><button class="btn br btn-sm" onclick="deleteCustomer(\'' + c.code + '\')">删除</button></td></tr>';
    });
    attachPager('cus-tbody', 'cus-pager', filtered, { val: 5 });
  }, 100);
  return h;
}

function openCustomerForm(oldCode) {
  var cus = null;
  if (oldCode) {
    for (var i = 0; i < DB.customers.length; i++) {
      if (DB.customers[i].code === oldCode) { cus = DB.customers[i]; break; }
    }
  }
  var isEdit = !!cus;
  var h = '<div class="fr"><div><label>客户编码</label><input id="cf-code" value="' + (cus ? cus.code : '自动生成') + '" ' + (isEdit ? 'readonly' : '') + '></div>';
  h += '<div><label>客户名称</label><input id="cf-name" value="' + (cus ? cus.name : '') + '"></div></div>';
  h += '<div class="fr"><div><label>联系人</label><input id="cf-contact" value="' + (cus ? cus.contact : '') + '"></div>';
  h += '<div><label>电话</label><input id="cf-phone" value="' + (cus ? cus.phone : '') + '"></div></div>';
  h += '<div class="fr"><div><label>区域</label><input id="cf-region" value="' + (cus ? cus.region : '') + '"></div>';
  h += '<div><label>信用等级</label><select id="cf-credit"><option value="A"' + (cus && cus.credit === 'A' ? ' selected' : '') + '>A</option><option value="B"' + (cus && cus.credit === 'B' ? ' selected' : '') + '>B</option><option value="C"' + (cus && cus.credit === 'C' ? ' selected' : '') + '>C</option></select></div></div>';
  openModal(isEdit ? '编辑客户' : '新增客户', h, '<button class="btn bp" onclick="confirmModal()">保存</button><button class="btn" onclick="CM()">取消</button>', function () {
    saveCustomer(oldCode);
  });
}

function saveCustomer(oldCode) {
  var cus = {
    code: $('cf-code').value.trim(),
    name: $('cf-name').value.trim(),
    contact: $('cf-contact').value.trim(),
    phone: $('cf-phone').value.trim(),
    region: $('cf-region').value.trim(),
    credit: $('cf-credit').value
  };
  if (!cus.name) { toast('请填写客户名称', 'w'); return; }
  if (oldCode) {
    for (var i = 0; i < DB.customers.length; i++) {
      if (DB.customers[i].code === oldCode) { DB.customers[i] = cus; break; }
    }
    toast('客户更新成功', 's');
  } else {
    cus.code = genId('CUS');
    DB.customers.push(cus);
    toast('客户创建成功: ' + cus.code, 's');
  }
  saveDB();
  renderPage('customer');
}

function deleteCustomer(code) {
  if (!confirm('确定删除该客户？')) return;
  DB.customers = DB.customers.filter(function (c) { return c.code !== code; });
  saveDB();
  toast('客户已删除', 's');
  renderPage('customer');
}

// ---------- SUPPLIER MANAGEMENT ----------
function renderSupplier() {
  var list = DB.suppliers;
  var h = '<div class="card"><div class="card-h"><div class="card-t">🏭 供应商列表</div>';
  h += '<button class="btn bp btn-sm" onclick="openSupplierForm(null)">+ 新增供应商</button></div>';
  h += '<div class="tb"><input type="text" id="sup-search" placeholder="搜索供应商..." oninput="renderPage(\'supplier\')"></div>';
  h += '<table><thead><tr><th>编码</th><th>名称</th><th>联系人</th><th>主营业务</th><th>评级</th><th>操作</th></tr></thead><tbody id="sup-tbody"></tbody></table>';
  h += '<div class="pg" id="sup-pager"></div></div>';

  setTimeout(function () {
    var kw = $('sup-search') ? $('sup-search').value.trim().toLowerCase() : '';
    var filtered = list.filter(function (s) { return !kw || s.code.toLowerCase().indexOf(kw) !== -1 || s.name.toLowerCase().indexOf(kw) !== -1; });
    var ps = pageStates['sup-tbody'] || { page: 1 };
    pageStates['sup-tbody'] = ps;
    renderTable('sup-tbody', filtered, ps.page, 5, function (s) {
      return '<tr><td>' + s.code + '</td><td>' + s.name + '</td><td>' + s.contact + '</td><td>' + s.biz + '</td><td>' + s.rating + '</td><td><button class="btn bp btn-sm" onclick="openSupplierForm(\'' + s.code + '\')">编辑</button><button class="btn br btn-sm" onclick="deleteSupplier(\'' + s.code + '\')">删除</button></td></tr>';
    });
    attachPager('sup-tbody', 'sup-pager', filtered, { val: 5 });
  }, 100);
  return h;
}

function openSupplierForm(oldCode) {
  var sup = null;
  if (oldCode) {
    for (var i = 0; i < DB.suppliers.length; i++) {
      if (DB.suppliers[i].code === oldCode) { sup = DB.suppliers[i]; break; }
    }
  }
  var isEdit = !!sup;
  var h = '<div><label>供应商编码</label><input id="sf-code" value="' + (sup ? sup.code : '自动生成') + '" ' + (isEdit ? 'readonly' : '') + '></div>';
  h += '<div><label>供应商名称</label><input id="sf-name" value="' + (sup ? sup.name : '') + '"></div>';
  h += '<div><label>联系人</label><input id="sf-contact" value="' + (sup ? sup.contact : '') + '"></div>';
  h += '<div><label>主营业务</label><input id="sf-biz" value="' + (sup ? sup.biz : '') + '"></div>';
  h += '<div><label>评级</label><select id="sf-rating"><option value="A"' + (sup && sup.rating === 'A' ? ' selected' : '') + '>A</option><option value="B"' + (sup && sup.rating === 'B' ? ' selected' : '') + '>B</option><option value="C"' + (sup && sup.rating === 'C' ? ' selected' : '') + '>C</option></select></div>';
  openModal(isEdit ? '编辑供应商' : '新增供应商', h, '<button class="btn bp" onclick="confirmModal()">保存</button><button class="btn" onclick="CM()">取消</button>', function () {
    saveSupplier(oldCode);
  });
}

function saveSupplier(oldCode) {
  var sup = {
    code: $('sf-code').value.trim(),
    name: $('sf-name').value.trim(),
    contact: $('sf-contact').value.trim(),
    biz: $('sf-biz').value.trim(),
    rating: $('sf-rating').value
  };
  if (!sup.name) { toast('请填写供应商名称', 'w'); return; }
  if (oldCode) {
    for (var i = 0; i < DB.suppliers.length; i++) {
      if (DB.suppliers[i].code === oldCode) { DB.suppliers[i] = sup; break; }
    }
    toast('供应商更新成功', 's');
  } else {
    sup.code = genId('SUP');
    DB.suppliers.push(sup);
    toast('供应商创建成功: ' + sup.code, 's');
  }
  saveDB();
  renderPage('supplier');
}

function deleteSupplier(code) {
  if (!confirm('确定删除该供应商？')) return;
  DB.suppliers = DB.suppliers.filter(function (s) { return s.code !== code; });
  saveDB();
  toast('供应商已删除', 's');
  renderPage('supplier');
}

// ---------- FAN MODEL LIBRARY ----------
function renderFanModel() {
  var list = DB.materials;
  var h = '<div class="card"><div class="card-h"><div class="card-t">🔧 风机型号库</div></div>';
  h += '<div class="tb">';
  h += '<input type="text" id="fm-search" placeholder="搜索型号..." oninput="renderPage(\'fanmodel\')">';
  h += '<select id="fm-type-filter" onchange="renderPage(\'fanmodel\')"><option value="">全部类型</option><option value="离心风机">离心风机</option><option value="轴流风机">轴流风机</option><option value="混流风机">混流风机</option><option value="高压风机">高压风机</option></select>';
  h += '</div>';
  h += '<div class="fg">';

  var kw = '';
  var cat = '';
  setTimeout(function () {
    renderFanModelCards(kw, cat);
  }, 50);

  h += '<div id="fm-cards"></div></div>';
  return h;
}

function renderFanModelCards(kw, cat) {
  var list = DB.materials;
  var fkw = $('fm-search') ? $('fm-search').value.trim().toLowerCase() : '';
  var fcat = $('fm-type-filter') ? $('fm-type-filter').value : '';
  var filtered = list.filter(function (m) {
    var mk = !fkw || m.code.toLowerCase().indexOf(fkw) !== -1 || m.name.toLowerCase().indexOf(fkw) !== -1 || m.spec.toLowerCase().indexOf(fkw) !== -1;
    var mc = !fcat || m.cat === fcat || m.fanType === fcat;
    return mk && mc;
  });

  var h = '';
  for (var i = 0; i < filtered.length; i++) {
    var m = filtered[i];
    var statusCls = '';
    for (var j = 0; j < DB.inventory.length; j++) {
      if (DB.inventory[j].code === m.code) {
        statusCls = DB.inventory[j].status === '正常' ? 'ts' : (DB.inventory[j].status === '低于安全库存' || DB.inventory[j].status === '库存不足' ? 'tw' : 'td');
        break;
      }
    }
    h += '<div class="fc"><div class="md">' + m.name + ' <span class="tag ' + statusCls + '" style="font-size:10px">' + (statusCls === 'ts' ? '正常' : '关注') + '</span></div>';
    h += '<div class="ps"><span>型号</span>' + m.spec + '<span>类别</span>' + m.cat;
    h += '<span>风量</span>' + m.airflow.toLocaleString() + ' CFM<span>全压</span>' + m.pressure.toLocaleString() + ' Pa';
    h += '<span>功率</span>' + m.power + ' kW<span>转速</span>' + m.rpm + ' RPM';
    h += '<span>噪声</span>' + m.noise + ' dB<span>直径</span>' + m.dia + ' mm';
    h += '<span>单价</span>¥' + m.price.toLocaleString() + '</div></div>';
  }
  if (filtered.length === 0) h = '<div style="grid-column:1/-1;text-align:center;color:#999;padding:40px">暂无匹配型号</div>';
  var container = $('fm-cards');
  if (container) container.innerHTML = h;
}

// ---------- QUOTATION MANAGEMENT ----------
function renderQuotation() {
  var list = DB.quotes;
  var h = '<div class="card"><div class="card-h"><div class="card-t">📝 销售报价列表</div>';
  h += '<button class="btn bp btn-sm" onclick="openQuotationForm()">+ 新建报价</button></div>';
  h += '<div class="tb"><input type="text" id="quo-search" placeholder="搜索报价..." oninput="renderPage(\'quotation\')"></div>';
  h += '<table><thead><tr><th>报价单号</th><th>客户</th><th>产品</th><th>数量</th><th>单价</th><th>总额</th><th>有效期</th><th>状态</th><th>操作</th></tr></thead><tbody id="quo-tbody"></tbody></table>';
  h += '<div class="pg" id="quo-pager"></div></div>';

  setTimeout(function () {
    var kw = $('quo-search') ? $('quo-search').value.trim().toLowerCase() : '';
    var filtered = list.filter(function (q) { return !kw || q.id.toLowerCase().indexOf(kw) !== -1 || q.customer.toLowerCase().indexOf(kw) !== -1 || q.product.toLowerCase().indexOf(kw) !== -1; });
    var ps = pageStates['quo-tbody'] || { page: 1 };
    pageStates['quo-tbody'] = ps;
    renderTable('quo-tbody', filtered, ps.page, 5, function (q) {
      return '<tr><td>' + q.id + '</td><td>' + q.customer + '</td><td>' + q.product + '</td><td>' + q.qty + '</td><td>¥' + q.price.toLocaleString() + '</td><td>¥' + q.total.toLocaleString() + '</td><td>' + q.valid + '</td><td>' + tagHtml(q.status) + '</td><td><button class="btn bp btn-sm" onclick="approveQuote(\'' + q.id + '\')" ' + (q.status !== '草稿' ? 'disabled' : '') + '>审批</button><button class="btn br btn-sm" onclick="deleteQuote(\'' + q.id + '\')">删除</button></td></tr>';
    });
    attachPager('quo-tbody', 'quo-pager', filtered, { val: 5 });
  }, 100);
  return h;
}

function openQuotationForm() {
  var custOpts = '';
  for (var i = 0; i < DB.customers.length; i++) {
    custOpts += '<option value="' + DB.customers[i].name + '">' + DB.customers[i].name + '</option>';
  }
  var matOpts = '';
  for (var j = 0; j < DB.materials.length; j++) {
    matOpts += '<option value="' + DB.materials[j].name + '" data-price="' + DB.materials[j].price + '">' + DB.materials[j].name + '</option>';
  }
  var h = '<div class="fr"><div><label>客户</label><select id="qf-customer">' + custOpts + '</select></div>';
  h += '<div><label>产品</label><select id="qf-product" onchange="updateQuotePrice()">' + matOpts + '</select></div></div>';
  h += '<div class="fr3"><div><label>数量</label><input type="number" id="qf-qty" value="1" oninput="updateQuotePrice()"></div>';
  h += '<div><label>单价(¥)</label><input type="number" id="qf-price" value="" step="0.01"></div>';
  h += '<div><label>总额(¥)</label><input id="qf-total" readonly></div></div>';
  h += '<div class="fr"><div><label>有效期</label><input type="date" id="qf-valid"></div></div>';
  openModal('新建报价', h, '<button class="btn bp" onclick="confirmModal()">保存</button><button class="btn" onclick="CM()">取消</button>', function () {
    saveQuotation();
  });
  setTimeout(updateQuotePrice, 200);
}

function updateQuotePrice() {
  var sel = $('qf-product');
  var price = 0;
  if (sel && sel.selectedIndex >= 0 && sel.options[sel.selectedIndex]) {
    price = parseFloat(sel.options[sel.selectedIndex].getAttribute('data-price')) || 0;
  }
  var qty = parseInt($('qf-qty') ? $('qf-qty').value : 1) || 0;
  if ($('qf-price')) $('qf-price').value = price;
  if ($('qf-total')) $('qf-total').value = '¥' + (price * qty).toLocaleString();
}

function saveQuotation() {
  var qty = parseInt(($('qf-qty') ? $('qf-qty').value : 1)) || 0;
  var price = parseFloat(($('qf-price') ? $('qf-price').value : 0)) || 0;
  var quo = {
    id: genId('QTN'),
    customer: $('qf-customer') ? $('qf-customer').value : '',
    product: $('qf-product') ? $('qf-product').value : '',
    qty: qty,
    price: price,
    total: qty * price,
    valid: $('qf-valid') ? $('qf-valid').value : '',
    status: '草稿'
  };
  if (!quo.customer || !quo.product) { toast('请选择客户和产品', 'w'); return; }
  DB.quotes.push(quo);
  saveDB();
  toast('报价创建成功: ' + quo.id, 's');
  renderPage('quotation');
}

function approveQuote(id) {
  for (var i = 0; i < DB.quotes.length; i++) {
    if (DB.quotes[i].id === id) { DB.quotes[i].status = '已提交'; break; }
  }
  saveDB();
  toast('报价已审批', 's');
  renderPage('quotation');
}

function deleteQuote(id) {
  if (!confirm('确定删除该报价？')) return;
  DB.quotes = DB.quotes.filter(function (q) { return q.id !== id; });
  saveDB();
  toast('报价已删除', 's');
  renderPage('quotation');
}

// ---------- SALES ORDER MANAGEMENT ----------
function renderSalesOrder() {
  var list = DB.salesOrders;
  var h = '<div class="card"><div class="card-h"><div class="card-t">📦 销售订单列表</div>';
  h += '<button class="btn bp btn-sm" onclick="openSOForm()">+ 新建订单</button></div>';
  h += '<div class="tb"><input type="text" id="so-search" placeholder="搜索订单..." oninput="renderPage(\'salesorder\')">';
  h += '<select id="so-status-filter" onchange="renderPage(\'salesorder\')"><option value="">全部状态</option><option value="待审批">待审批</option><option value="已确认">已确认</option><option value="生产中">生产中</option><option value="已发货">已发货</option><option value="已完成">已完成</option></select></div>';
  h += '<table><thead><tr><th>订单号</th><th>客户</th><th>产品</th><th>数量</th><th>金额</th><th>交货期</th><th>状态</th><th>操作</th></tr></thead><tbody id="so-tbody"></tbody></table>';
  h += '<div class="pg" id="so-pager"></div></div>';

  setTimeout(function () {
    var kw = $('so-search') ? $('so-search').value.trim().toLowerCase() : '';
    var st = $('so-status-filter') ? $('so-status-filter').value : '';
    var filtered = list.filter(function (o) {
      var mk = !kw || o.id.toLowerCase().indexOf(kw) !== -1 || o.customer.toLowerCase().indexOf(kw) !== -1 || o.product.toLowerCase().indexOf(kw) !== -1;
      var ms = !st || o.status === st;
      return mk && ms;
    });
    var ps = pageStates['so-tbody'] || { page: 1 };
    pageStates['so-tbody'] = ps;
    renderTable('so-tbody', filtered, ps.page, 5, function (o) {
      var btns = '';
      switch (o.status) {
        case '待审批': btns = '<button class="btn bs btn-sm" onclick="changeSOStatus(\'' + o.id + '\',\'已确认\')">审批通过</button>'; break;
        case '已确认': btns = '<button class="btn bp btn-sm" onclick="changeSOStatus(\'' + o.id + '\',\'生产中\')">下达生产</button>'; break;
        case '生产中': btns = '<button class="btn bw btn-sm" onclick="changeSOStatus(\'' + o.id + '\',\'已发货\')">标记发货</button>'; break;
        case '已发货': btns = '<button class="btn bs btn-sm" onclick="changeSOStatus(\'' + o.id + '\',\'已完成\')">确认完成</button>'; break;
        case '已完成': btns = '<span style="color:#999;font-size:11px">已完成</span>'; break;
      }
      btns += ' <button class="btn br btn-sm" onclick="deleteSO(\'' + o.id + '\')">删除</button>';
      return '<tr><td>' + o.id + '</td><td>' + o.customer + '</td><td>' + o.product + '</td><td>' + o.qty + '</td><td>¥' + o.amount.toLocaleString() + '</td><td>' + o.deliveryDate + '</td><td>' + tagHtml(o.status) + '</td><td>' + btns + '</td></tr>';
    });
    attachPager('so-tbody', 'so-pager', filtered, { val: 5 });
  }, 100);
  return h;
}

function openSOForm() {
  var custOpts = '';
  for (var i = 0; i < DB.customers.length; i++) {
    custOpts += '<option value="' + DB.customers[i].name + '">' + DB.customers[i].name + '</option>';
  }
  var matOpts = '';
  for (var j = 0; j < DB.materials.length; j++) {
    matOpts += '<option value="' + DB.materials[j].name + '" data-price="' + DB.materials[j].price + '">' + DB.materials[j].name + '</option>';
  }
  var h = '<div class="fr"><div><label>客户</label><select id="sof-customer">' + custOpts + '</select></div>';
  h += '<div><label>产品</label><select id="sof-product" onchange="updateSOAmount()">' + matOpts + '</select></div></div>';
  h += '<div class="fr3"><div><label>数量</label><input type="number" id="sof-qty" value="1" oninput="updateSOAmount()"></div>';
  h += '<div><label>单价(¥)</label><input type="number" id="sof-price" value="" step="0.01" oninput="updateSOAmount()"></div>';
  h += '<div><label>金额(¥)</label><input id="sof-amount" readonly></div></div>';
  h += '<div><label>交货日期</label><input type="date" id="sof-delivery"></div>';
  openModal('新建销售订单', h, '<button class="btn bp" onclick="confirmModal()">保存</button><button class="btn" onclick="CM()">取消</button>', function () {
    saveSO();
  });
  setTimeout(updateSOAmount, 200);
}

function updateSOAmount() {
  var sel = $('sof-product');
  var price = 0;
  if (sel && sel.selectedIndex >= 0 && sel.options[sel.selectedIndex]) {
    price = parseFloat(sel.options[sel.selectedIndex].getAttribute('data-price')) || 0;
  }
  var qty = parseInt($('sof-qty') ? $('sof-qty').value : 1) || 0;
  var manualPrice = parseFloat($('sof-price') ? $('sof-price').value : 0) || price;
  if ($('sof-price') && !$('sof-price').value) $('sof-price').value = price;
  if ($('sof-amount')) $('sof-amount').value = '¥' + (manualPrice * qty).toLocaleString();
}

function saveSO() {
  var qty = parseInt(($('sof-qty') ? $('sof-qty').value : 1)) || 0;
  var price = parseFloat(($('sof-price') ? $('sof-price').value : 0)) || 0;
  var so = {
    id: genId('SO'),
    customer: $('sof-customer') ? $('sof-customer').value : '',
    product: $('sof-product') ? $