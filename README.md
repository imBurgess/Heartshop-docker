新環境啟動步驟

1. 安裝 Docker Desktop
   下載安裝後，確認 Docker Desktop 正在執行中。

2. Clone 專案

git clone https://github.com/imBurgess/Heartshop-docker.git
cd Heartshop-docker

3. 設定環境變數

copy .env.example .env
打開 .env，修改這兩行：

DB_PASSWORD=admin123
JWT_SECRET=任意長字串

4. 啟動

docker compose up --build
第一次約需 5～10 分鐘（下載 image、編譯 Java、npm install）

啟動完成後開啟
服務 網址
前台 http://localhost:3400
後台管理 http://localhost:5173
後端 API http://localhost:8080/api
常用指令

# 背景執行

docker compose up --build -d

# 查看 log

docker compose logs -f

# 停止

docker compose down

# 完全重置（含資料庫）

docker compose down -v

