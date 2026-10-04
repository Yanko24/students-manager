FROM node:20.19.1-alpine AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM nginx:1.25.4-alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
