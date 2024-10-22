package com.example.rolepermission.controller;

import com.example.rolepermission.ExcelExporter;
import com.example.rolepermission.dto.response.UserResponse;
import com.example.rolepermission.entity.Permission;
import com.example.rolepermission.entity.Role;
import com.example.rolepermission.entity.User;
import com.example.rolepermission.service.RoleService;
import com.example.rolepermission.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel() {
        List<User> users = userService.getAllUsers();
        List<Role> roles = roleService.getAllRoles();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheetUser = workbook.createSheet("Users");
        Sheet sheetRole = workbook.createSheet("Roles");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Tạo tiêu đề cho các cột
        Row headerRow = sheetUser.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Username");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Roles");
        headerRow.createCell(4).setCellValue("Permission");
        for (int i = 0; i < 5; i++) {
            Cell cell = headerRow.getCell(i);
            cell.setCellStyle(headerStyle);
        }


        int rowCount = 1;
        for (User user : users) {
            Row row = sheetUser.createRow(rowCount++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getEmail());
            StringBuilder rolesString = new StringBuilder();
            if (user.getRoles() != null) {
                for (Role role : user.getRoles()) {
                    rolesString.append(role.getName()).append(", ");
                }
                // Xóa dấu phẩy và khoảng trắng ở cuối chuỗi
                if (rolesString.length() > 0) {
                    rolesString.setLength(rolesString.length() - 2);
                }
            }
            row.createCell(3).setCellValue(rolesString.toString()); // Ghi roles vào ô
            StringBuilder permissionsString = new StringBuilder();
            if (user.getRoles() != null) {
                for (Role role : user.getRoles()) {
                    if (role.getPermissions() != null) {
                        for (Permission permission : role.getPermissions()) {
                            permissionsString.append(permission.getName()).append(", ");
                        }
                    }
                }
                if (permissionsString.length() > 0) {
                    permissionsString.setLength(permissionsString.length() - 2);
                }
            }
            row.createCell(4).setCellValue(permissionsString.toString());
        }

        Row headerRoleRow = sheetRole.createRow(0);
        headerRoleRow.createCell(0).setCellValue("Name");
        headerRoleRow.createCell(1).setCellValue("Description");
        headerRoleRow.createCell(2).setCellValue("Permission");

        for (int i = 0; i < 3; i++) {
            Cell cell = headerRoleRow.getCell(i);
            cell.setCellStyle(headerStyle);
        }


        int rowRoleCount = 1;
        for (Role role : roles) {
            Row row = sheetRole.createRow(rowRoleCount++);
            row.createCell(0).setCellValue(role.getName());
            row.createCell(1).setCellValue(role.getDescription());
            StringBuilder permmissionRoleString = new StringBuilder();
            if (role.getPermissions() != null) {
                for (Permission permission : role.getPermissions()) {
                    permmissionRoleString.append(permission.getName()).append(", ");
                }
                // Xóa dấu phẩy và khoảng trắng ở cuối chuỗi
                if (permmissionRoleString.length() > 0) {
                    permmissionRoleString.setLength(permmissionRoleString.length() - 2);
                }
            }
            row.createCell(2).setCellValue(permmissionRoleString.toString()); // Ghi roles vào ô
        }
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                byte[] bytes = outputStream.toByteArray();

                // Thiết lập header cho file Excel
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=users.xlsx");
                headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
}
