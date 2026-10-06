FROM node:24.21.0-alpine3.24 AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build -- --outDir dist

FROM nginx:1.30.5-alpine3.24
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
