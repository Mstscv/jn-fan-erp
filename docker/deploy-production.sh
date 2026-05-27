#!/bin/bash

PROJECT_DIR=/opt/jn-erp
BACKUP_DIR=/opt/jn-erp-backups
LOG_DIR=/var/log/jn-erp
DB_USER=root
DB_PASS=JnErp_2026!
DB_NAME=jn_erp
COMPOSE_FILE=docker-compose-production.yml

GATEWAY_PORT=8080
AUTH_PORT=9200
SYSTEM_PORT=9201
MATERIAL_PORT=9203
SALES_PORT=9204
PURCHASE_PORT=9205
WAREHOUSE_PORT=9206
PRODUCTION_PORT=9207
FINANCE_PORT=9212
NACOS_PORT=8848
SENTINEL_PORT=8718

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

check_directories() {
    for dir in "$PROJECT_DIR" "$BACKUP_DIR" "$LOG_DIR"; do
        if [ ! -d "$dir" ]; then
            log_info "Creating directory: $dir"
            mkdir -p "$dir"
        fi
    done
}

backup_database() {
    local timestamp=$(date '+%Y%m%d_%H%M%S')
    local backup_file="${BACKUP_DIR}/jn_erp_${timestamp}.sql.gz"
    log_info "Starting database backup: $DB_NAME"
    mysqldump -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" --single-transaction --routines --events --triggers | gzip > "$backup_file"
    if [ $? -eq 0 ]; then
        log_info "Database backup completed: $backup_file"
        echo "$backup_file"
    else
        log_error "Database backup failed"
        return 1
    fi
}

restore_database() {
    local backup_file=$1
    if [ ! -f "$backup_file" ]; then
        log_error "Backup file not found: $backup_file"
        return 1
    fi
    log_info "Starting database restore from: $backup_file"
    gunzip -c "$backup_file" | mysql -u"$DB_USER" -p"$DB_PASS" "$DB_NAME"
    if [ $? -eq 0 ]; then
        log_info "Database restore completed"
    else
        log_error "Database restore failed"
        return 1
    fi
}

check_services() {
    local all_healthy=true
    local services=(
        "gateway:${GATEWAY_PORT}"
        "auth:${AUTH_PORT}"
        "system:${SYSTEM_PORT}"
        "material:${MATERIAL_PORT}"
        "sales:${SALES_PORT}"
        "purchase:${PURCHASE_PORT}"
        "warehouse:${WAREHOUSE_PORT}"
        "production:${PRODUCTION_PORT}"
        "finance:${FINANCE_PORT}"
        "nacos:${NACOS_PORT}"
    )
    log_info "Checking service health..."
    for service in "${services[@]}"; do
        local name="${service%%:*}"
        local port="${service##*:}"
        if curl -sf "http://localhost:${port}/actuator/health" > /dev/null 2>&1; then
            log_info "  [OK] $name is healthy"
        else
            log_warn "  [WARN] $name health check failed"
            all_healthy=false
        fi
    done
    if [ "$all_healthy" = true ]; then
        log_info "All services are healthy"
    else
        log_warn "Some services are not healthy, check logs for details"
    fi
    $all_healthy
}

deploy_services() {
    log_info "Starting deployment..."
    check_directories
    cd "$PROJECT_DIR" || exit 1
    log_info "Pulling latest images..."
    docker-compose -f "$COMPOSE_FILE" pull
    log_info "Starting services..."
    docker-compose -f "$COMPOSE_FILE" up -d
    if [ $? -eq 0 ]; then
        log_info "Services started successfully"
        log_info "Waiting for services to become healthy (60s)..."
        sleep 60
        check_services
    else
        log_error "Failed to start services"
        return 1
    fi
}

rollback_version() {
    local backup_file=$1
    log_info "Starting rollback..."
    if [ -n "$backup_file" ]; then
        restore_database "$backup_file"
    fi
    cd "$PROJECT_DIR" || exit 1
    log_info "Restarting previous version..."
    docker-compose -f "$COMPOSE_FILE" down
    docker-compose -f "$COMPOSE_FILE" up -d
    if [ $? -eq 0 ]; then
        log_info "Rollback completed"
        sleep 30
        check_services
    else
        log_error "Rollback failed"
        return 1
    fi
}

restart_services() {
    log_info "Restarting all services..."
    cd "$PROJECT_DIR" || exit 1
    docker-compose -f "$COMPOSE_FILE" restart
    if [ $? -eq 0 ]; then
        log_info "Restart completed"
        sleep 30
        check_services
    else
        log_error "Restart failed"
        return 1
    fi
}

show_status() {
    log_info "Service status:"
    cd "$PROJECT_DIR" || exit 1
    docker-compose -f "$COMPOSE_FILE" ps
    echo ""
    check_services
}

show_logs() {
    local service=$1
    cd "$PROJECT_DIR" || exit 1
    if [ -n "$service" ]; then
        docker-compose -f "$COMPOSE_FILE" logs -f "$service"
    else
        docker-compose -f "$COMPOSE_FILE" logs -f
    fi
}

case "${1:-deploy}" in
    deploy)
        backup_database
        deploy_services
        ;;
    rollback)
        rollback_version "$2"
        ;;
    backup)
        backup_database
        ;;
    restore)
        restore_database "$2"
        ;;
    restart)
        restart_services
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs "$2"
        ;;
    *)
        echo "Usage: bash deploy-production.sh [command]"
        echo "Commands:"
        echo "  deploy              Full deployment with backup"
        echo "  rollback <backup>   Rollback to previous version"
        echo "  backup              Database backup only"
        echo "  restore <file>      Database restore from backup"
        echo "  restart             Restart all services"
        echo "  status              Check service status"
        echo "  logs [service]      View service logs"
        exit 1
        ;;
esac
