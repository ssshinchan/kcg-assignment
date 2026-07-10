# 阶段一：构建阶段 (使用 Maven 缓存依赖并打包)
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /tmp
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
WORKDIR /build
WORKDIR /build

# 复制 Maven 配置以缓存依赖项
COPY pom.xml .
COPY assignment-admin/pom.xml assignment-admin/
COPY assignment-common/pom.xml assignment-common/
COPY assignment-framework/pom.xml assignment-framework/
COPY assignment-generator/pom.xml assignment-generator/
COPY assignment-quartz/pom.xml assignment-quartz/
COPY assignment-system/pom.xml assignment-system/
COPY assignment-ai/pom.xml assignment-ai/

RUN mvn dependency:go-offline -B

# 复制源码并编译 (frontend 文件夹已通过 .dockerignore 排除)
COPY . .
RUN mvn clean package -DskipTests

# 阶段二：运行阶段
FROM eclipse-temurin:25-jre
WORKDIR /app

# 从构建阶段复制打包好的 jar 包
COPY --from=builder /build/assignment-admin/target/assignment-admin.jar app.jar

# 环境变量设置
ENV SERVER_PORT=8080
ENV TZ=Asia/Tokyo

EXPOSE 8080

# 启动应用，并使用非阻塞的随机数生成器以加快容器内启动速度
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
