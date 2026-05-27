#!/bin/bash

SOURCE_DIR=${1:-../..}
PASS=0
FAIL=0
WARN=0

red() { echo -e "\033[31m$1\033[0m"; }
green() { echo -e "\033[32m$1\033[0m"; }
yellow() { echo -e "\033[33m$1\033[0m"; }

check_sql_injection() {
    yellow "[CHECK] SQL Injection patterns..."
    local found=0
    while IFS= read -r file; do
        if grep -Pn '\$\{.*\}.*\+|concat\(.*\$|"\\s*\+\\s*"' "$file" 2>/dev/null | grep -qP 'select|insert|update|delete'; then
            red "    POTENTIAL SQLI: $file"
            found=$((found + 1))
        fi
    done < <(find "$SOURCE_DIR" -name "*.java" -type f 2>/dev/null)
    if [ "$found" -eq 0 ]; then
        green "    No SQL injection patterns detected."
        PASS=$((PASS + 1))
    else
        red "    Found $found potential SQL injection issues."
        FAIL=$((FAIL + found))
        WARN=$((WARN + 1))
    fi
}

check_hardcoded_credentials() {
    yellow "[CHECK] Hardcoded credentials..."
    local found=0
    while IFS= read -r file; do
        if grep -Pq 'password\s*=|jdbc:mysql' "$file" 2>/dev/null; then
            yellow "    WARN (legitimate config): $file"
            found=$((found + 1))
        fi
    done < <(find "$SOURCE_DIR" \( -name "*.yml" -o -name "*.yaml" -o -name "*.properties" -o -name "*.xml" \) -type f 2>/dev/null)
    if [ "$found" -eq 0 ]; then
        green "    No hardcoded credential files found."
    else
        yellow "    $found config files with credentials (review manually)."
        WARN=$((WARN + found))
    fi
    while IFS= read -r file; do
        if grep -Pn '(password\s*=\s*"|secret\s*=\s*"|jwt\s*=\s*")' "$file" 2>/dev/null | grep -Pv '//|/\*'; then
            red "    HARDCODED CRED: $file"
            FAIL=$((FAIL + 1))
        fi
    done < <(find "$SOURCE_DIR" -name "*.java" -type f 2>/dev/null)
}

check_xss_vulnerabilities() {
    yellow "[CHECK] XSS vulnerabilities..."
    local found=0
    while IFS= read -r file; do
        if grep -Pn '\.html\(|\.append\(.*\$|innerHTML\s*=' "$file" 2>/dev/null | grep -Pv 'escape|encode|sanitize|SecurityUtils'; then
            yellow "    WARN (potential XSS): $file"
            found=$((found + 1))
        fi
    done < <(find "$SOURCE_DIR" \( -name "*.java" -o -name "*.js" -o -name "*.vue" \) -type f 2>/dev/null)
    if [ "$found" -eq 0 ]; then
        green "    No XSS patterns detected."
        PASS=$((PASS + 1))
    else
        yellow "    Found $found potential XSS patterns."
        WARN=$((WARN + found))
    fi
}

check_permission_annotations() {
    yellow "[CHECK] Missing @RequiresPermissions in controllers..."
    local found=0
    while IFS= read -r file; do
        local methods
        methods=$(grep -cP '@(GetMapping|PostMapping|PutMapping|DeleteMapping)' "$file" 2>/dev/null)
        local annotated
        annotated=$(grep -cP '@RequiresPermissions' "$file" 2>/dev/null)
        if [ "$methods" -gt 0 ] && [ "$annotated" -eq 0 ]; then
            yellow "    WARN (no permission annotation): $file ($methods endpoints)"
            found=$((found + 1))
        fi
    done < <(find "$SOURCE_DIR" -name "*Controller.java" -type f 2>/dev/null)
    if [ "$found" -eq 0 ]; then
        green "    All controllers have @RequiresPermissions."
        PASS=$((PASS + 1))
    else
        yellow "    Found $found controllers without @RequiresPermissions."
        WARN=$((WARN + found))
    fi
}

check_xxe_vulnerabilities() {
    yellow "[CHECK] XXE (XML External Entity) patterns..."
    local found=0
    while IFS= read -r file; do
        if grep -q 'DOCTYPE' "$file" 2>/dev/null; then
            local isMybatis
            isMybatis=$(grep -c 'DOCTYPE mapper' "$file" 2>/dev/null)
            if [ "$isMybatis" -gt 0 ]; then
                yellow "    WARN (legitimate MyBatis): $file"
            else
                red "    POTENTIAL XXE: $file"
                found=$((found + 1))
            fi
        fi
    done < <(find "$SOURCE_DIR" -name "*.xml" -type f 2>/dev/null)
    if [ "$found" -eq 0 ]; then
        green "    No XXE patterns beyond MyBatis mappers."
        PASS=$((PASS + 1))
    else
        red "    Found $found potential XXE issues."
        FAIL=$((FAIL + found))
    fi
}

check_path_traversal() {
    yellow "[CHECK] Path traversal patterns..."
    local found=0
    while IFS= read -r file; do
        if grep -Pn '\.\./|\.\\.\\|getAbsolutePath|getCanonicalPath' "$file" 2>/dev/null | grep -Pv 'canonical|normalize|validate|FilenameUtils'; then
            yellow "    WARN: $file"
            found=$((found + 1))
        fi
    done < <(find "$SOURCE_DIR" -name "*.java" -type f 2>/dev/null)
    if [ "$found" -eq 0 ]; then
        green "    No path traversal patterns detected."
        PASS=$((PASS + 1))
    else
        yellow "    Found $found potential path traversal patterns."
        WARN=$((WARN + found))
    fi
}

check_spring_security() {
    yellow "[CHECK] Spring Security configuration..."
    local found=0
    while IFS= read -r file; do
        if grep -Pq 'permitAll|anonymous|hasRole|hasAuthority|@PreAuthorize|@PostAuthorize' "$file" 2>/dev/null; then
            found=$((found + 1))
        fi
    done < <(find "$SOURCE_DIR" -name "*.java" -type f 2>/dev/null)
    if [ "$found" -gt 0 ]; then
        green "    Found security annotations in $found files."
        PASS=$((PASS + 1))
    else
        yellow "    WARN: No Spring Security annotations found."
        WARN=$((WARN + 1))
    fi
}

echo "============================================"
echo "  JN-FAN-ERP Security Check Report"
echo "  Source: $SOURCE_DIR"
echo "  Date:   $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================"
echo ""

check_sql_injection
echo ""
check_hardcoded_credentials
echo ""
check_xss_vulnerabilities
echo ""
check_permission_annotations
echo ""
check_xxe_vulnerabilities
echo ""
check_path_traversal
echo ""
check_spring_security
echo ""

echo "============================================"
echo "  Summary: PASS=$PASS  FAIL=$FAIL  WARN=$WARN"
echo "============================================"

if [ "$FAIL" -gt 0 ]; then
    echo ""
    red "  FAIL: $FAIL issues found that require immediate action."
    exit 1
elif [ "$WARN" -gt 0 ]; then
    echo ""
    yellow "  WARN: $WARN items found. Review recommended before release."
    exit 0
else
    echo ""
    green "  All checks passed."
    exit 0
fi
