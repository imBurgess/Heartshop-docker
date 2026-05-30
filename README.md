新環境啟動步驟
1. 安裝 Docker Desktop
下載安裝後，確認 Docker Desktop 正在執行中。

2. Clone 專案

git clone https://github.com/imBurgess/Heartshop-docker.git
cd Heartshop-docker

3. 設定環境變數

copy .env.example .env
打開 .env，修改這兩行：


DB_PASSWORD=你的資料庫密碼
JWT_SECRET=任意長字串

4. 啟動

docker compose up --build
第一次約需 5～10 分鐘（下載 image、編譯 Java、npm install）

啟動完成後開啟
服務	網址
前台	http://localhost:3000
後台管理	http://localhost:5173
後端 API	http://localhost:8080/api
常用指令

# 背景執行
docker compose up --build -d

# 查看 log
docker compose logs -f

# 停止
docker compose down

# 完全重置（含資料庫）
docker compose down -v














1. nuxt.config.ts
新增 dir 設定，告訴 Nuxt 去哪裡找 layout：


dir: {
  layouts: "Layouts",
},
2. Layouts/default.vue
把三個地方的 Naive UI 元件包上 <ClientOnly>：

① 通知鈴鐺（n-popover）


<ClientOnly>
  <n-popover ...> ... </n-popover>
  <template #fallback>
    <div class="icon-btn notification"></div>
  </template>
</ClientOnly>
② 購物車數量 badge（n-badge）


<ClientOnly>
  <n-badge v-if="cartStore.totalQty > 0" .../>
</ClientOnly>
③ 會員下拉選單（n-dropdown）


<ClientOnly>
  <n-dropdown ...> ... </n-dropdown>
  <template #fallback>
    <button class="icon-btn user"></button>
  </template>
</ClientOnly>
④ 購物說明 & 關於我們 dropdown


<ClientOnly>
  <n-dropdown ...> ... </n-dropdown>
  <template #fallback>
    <button class="nav-link-btn">購物說明</button>
  </template>
</ClientOnly>
⑤ 登入彈窗（LoginRegister / n-modal）


<ClientOnly>
  <LoginRegister v-model:show="showLogin" .../>
</ClientOnly>

總結：兩個問題
問題一：Navbar 消失
原因：大小寫不一致

你的資料夾名稱是 Layouts/（大寫 L）
Nuxt 預設找 layouts/（小寫 l）
Windows 不分大小寫，本機開發正常
Docker 跑 Linux，嚴格區分大小寫 → Nuxt 找不到 layout → Navbar 完全不渲染
修復： 在 nuxt.config.ts 加 dir: { layouts: "Layouts" }

問題二：Navbar 連結和下拉選單失效
原因：SSR 水合（Hydration）失敗

Docker 是 SSR 模式（伺服器端渲染）
Naive UI 的 n-dropdown、n-popover、n-modal 在伺服器端產生的 DOM 結構，與客戶端初始化後不一致
Vue 試圖對齊兩邊的 DOM 時失敗（Hydration Mismatch）
結果：事件監聽器沒有正確掛上 → hover、click 全部失效
修復： 把 Naive UI 互動元件包在 <ClientOnly> 裡，讓它們跳過 SSR，只在瀏覽器端渲染

一句話總結
Navbar 不見 是 Linux 大小寫問題；互動失效 是 Naive UI 不適合直接在 SSR 環境下使用，需要用 <ClientOnly> 保護。
