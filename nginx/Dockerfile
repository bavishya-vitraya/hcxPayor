FROM nginx:1.19-alpine
COPY hcxPayorConnector-service.crt /etc/ssl/
COPY hcxPayorConnector-service.key /etc/ssl/

# The following is for healthcheck
WORKDIR /etc/nginx/html
RUN echo "1" > index.html

COPY nginx.conf /etc/nginx/

EXPOSE 9090