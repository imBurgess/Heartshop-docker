# ── Stage 1: 建置 ───────────────────────────────────────────────
FROM node:22-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# Docker build-arg：SSR proxy 需指向容器內部的 backend 服務
ARG VITE_API_BASE_URL=http://backend:8080
ARG VITE_ADMIN_URL=http://localhost:5173
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_ADMIN_URL=$VITE_ADMIN_URL

RUN npm run build

# ── Stage 2: 執行環境 ────────────────────────────────────────────
FROM node:22-alpine
WORKDIR /app

COPY --from=builder /app/.output ./.output

ENV HOST=0.0.0.0
ENV PORT=3000
EXPOSE 3000

CMD ["node", ".output/server/index.mjs"]
