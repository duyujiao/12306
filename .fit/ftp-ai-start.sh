#!/bin/bash
# FTP AI CLI 安装和启动脚本
# 用于远程开发环境下自动安装和启动 ftp-ai 服务

set -e

# 参数
PORT="${1:-1054}"
API_KEY="${2:-prj-JdSLVNCkjbXtT3d7LWEMQkCC}"

# 路径配置
FTP_AI_PATH="$HOME/.local/bin/ftp-ai"
KEY_DIR="$HOME/.config/ftp-ai"
KEY_FILE="$KEY_DIR/key.txt"

# 日志函数
log() {
    echo "[FTP AI] $(date '+%Y-%m-%d %H:%M:%S') $1"
}

# 检测系统类型和架构
detect_platform() {
    OS=$(uname -s)
    ARCH=$(uname -m)
    
    case "$OS" in
        Darwin)
            if [ "$ARCH" = "arm64" ]; then
                DOWNLOAD_URL="https://mirrors.tencent.com/repository/generic/fit_scaffold/ftp-ai-install/darwin_arm/latest/ftp-ai-install"
            else
                DOWNLOAD_URL="https://mirrors.tencent.com/repository/generic/fit_scaffold/ftp-ai-install/darwin_amd64/latest/ftp-ai-install"
            fi
            ;;
        Linux)
            DOWNLOAD_URL="https://mirrors.tencent.com/repository/generic/fit_scaffold/ftp-ai-install/linux/latest/ftp-ai-install"
            ;;
        *)
            log "不支持的操作系统: $OS"
            exit 1
            ;;
    esac
    
    log "检测到系统: $OS, 架构: $ARCH"
}

# 配置 API 密钥
configure_key() {
    log "配置 API 密钥..."
    mkdir -p "$KEY_DIR"
    echo "$API_KEY" > "$KEY_FILE"
    log "API 密钥已配置"
}

# 安装 ftp-ai
install_ftp_ai() {
    if [ -f "$FTP_AI_PATH" ]; then
        log "ftp-ai 已安装，跳过安装步骤"
        return 0
    fi
    
    log "开始安装 ftp-ai..."
    
    # 检测平台并设置下载 URL
    detect_platform
    
    # 下载安装器
    TEMP_DIR=$(mktemp -d)
    cd "$TEMP_DIR"
    
    log "下载安装器: $DOWNLOAD_URL"
    curl -L -o ftp-ai-install "$DOWNLOAD_URL"
    chmod +x ftp-ai-install
    
    # 执行安装
    log "执行安装..."
    ./ftp-ai-install
    
    # 清理临时文件
    cd /
    rm -rf "$TEMP_DIR"
    
    # 验证安装
    if [ -f "$FTP_AI_PATH" ]; then
        log "ftp-ai 安装成功"
    else
        log "ftp-ai 安装失败"
        exit 1
    fi
}

# 停止已有服务
stop_existing_service() {
    log "停止已有服务 (端口: $PORT)..."
    pkill -f "ftp-ai api --port $PORT" 2>/dev/null || true
    # 等待端口释放
    sleep 1
}

# 启动服务
start_service() {
    log "启动 ftp-ai 服务 (端口: $PORT)..."
    nohup "$FTP_AI_PATH" api --port "$PORT" > /dev/null 2>&1 &
    log "服务已在后台启动"
}

# 主流程
main() {
    log "========== FTP AI 启动脚本 =========="
    log "端口: $PORT"
    
    # 1. 配置密钥
    configure_key
    
    # 2. 安装 ftp-ai（如果需要）
    install_ftp_ai
    
    # 3. 停止已有服务
    stop_existing_service
    
    # 4. 启动服务
    start_service
    
    log "========== 启动完成 =========="
}

# 执行主流程
main
