package com.jn.erp.common.core.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ErpExcelUtils {

    private static final Logger log = LoggerFactory.getLogger(ErpExcelUtils.class);

    public static <T> void exportExcel(HttpServletResponse response, List<T> list,
                                        String fileName, String sheetName, Class<T> clazz) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), clazz)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet(sheetName)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(list);
        } catch (IOException e) {
            log.error("导出Excel失败: {}", fileName, e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage(), e);
        }
    }

    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) {
        try {
            return EasyExcel.read(file.getInputStream(), clazz, null)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error("导入Excel失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("导入Excel失败: " + e.getMessage(), e);
        }
    }

    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz, int sheetNo) {
        try {
            return EasyExcel.read(file.getInputStream(), clazz, null)
                    .sheet(sheetNo)
                    .doReadSync();
        } catch (IOException e) {
            log.error("导入Excel失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("导入Excel失败: " + e.getMessage(), e);
        }
    }
}
