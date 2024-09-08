FROM nginx:alpine
WORKDIR /usr/share/nginx/
RUN rm -rf html
RUN mkdir html
WORKDIR /
COPY nginx.conf /etc/nginx
COPY dist /usr/share/nginx/html
ENV PORT 8080
ENV HOST 0.0.0.0
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]