//package com.example.rolepermission.controller;
//
//import com.example.rolepermission.ExcelExporter;
//import com.example.rolepermission.dto.response.UserResponse;
//import com.example.rolepermission.entity.Permission;
//import com.example.rolepermission.entity.Role;
//import com.example.rolepermission.entity.User;
//import com.example.rolepermission.repository.RoleRepository;
//import com.example.rolepermission.repository.UserRepository;
//import com.example.rolepermission.service.RoleService;
//import com.example.rolepermission.service.UserService;
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@RestController
//@RequestMapping("/excel")
//public class ExcelController {
//
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private RoleService roleService;
//
//    @Autowired
//    private RoleRepository roleRepository;
//    @Autowired
//    private UserRepository userRepository;
//    @GetMapping("/export")
//    public ResponseEntity<byte[]> exportToExcel() {
//        List<User> users = userService.getAllUsers();
//        List<Role> roles = roleService.getAllRoles();
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheetUser = workbook.createSheet("Users");
//        Sheet sheetRole = workbook.createSheet("Roles");
//
//        CellStyle headerStyle = workbook.createCellStyle();
//        Font headerFont = workbook.createFont();
//        headerFont.setBold(true);
//        headerFont.setColor(IndexedColors.WHITE.getIndex());
//        headerStyle.setFont(headerFont);
//        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//        // Tạo tiêu đề cho các cột
//        Row headerRow = sheetUser.createRow(0);
//        headerRow.createCell(0).setCellValue("ID");
//        headerRow.createCell(1).setCellValue("Username");
//        headerRow.createCell(2).setCellValue("Email");
//        headerRow.createCell(3).setCellValue("Roles");
//        headerRow.createCell(4).setCellValue("Permission");
//        for (int i = 0; i < 5; i++) {
//            Cell cell = headerRow.getCell(i);
//            cell.setCellStyle(headerStyle);
//        }
//
//
//        int rowCount = 1;
//        for (User user : users) {
//            Row row = sheetUser.createRow(rowCount++);
//            row.createCell(0).setCellValue(user.getId());
//            row.createCell(1).setCellValue(user.getUsername());
//            row.createCell(2).setCellValue(user.getEmail());
//            StringBuilder rolesString = new StringBuilder();
//            if (user.getRoles() != null) {
//                for (Role role : user.getRoles()) {
//                    rolesString.append(role.getName()).append(", ");
//                }
//                // Xóa dấu phẩy và khoảng trắng ở cuối chuỗi
//                if (rolesString.length() > 0) {
//                    rolesString.setLength(rolesString.length() - 2);
//                }
//            }
//            row.createCell(3).setCellValue(rolesString.toString()); // Ghi roles vào ô
//            StringBuilder permissionsString = new StringBuilder();
//            if (user.getRoles() != null) {
//                for (Role role : user.getRoles()) {
//                    if (role.getPermissions() != null) {
//                        for (Permission permission : role.getPermissions()) {
//                            permissionsString.append(permission.getName()).append(", ");
//                        }
//                    }
//                }
//                if (permissionsString.length() > 0) {
//                    permissionsString.setLength(permissionsString.length() - 2);
//                }
//            }
//            row.createCell(4).setCellValue(permissionsString.toString());
//        }
//
//        Row headerRoleRow = sheetRole.createRow(0);
//        headerRoleRow.createCell(0).setCellValue("Name");
//        headerRoleRow.createCell(1).setCellValue("Description");
//        headerRoleRow.createCell(2).setCellValue("Permission");
//
//        for (int i = 0; i < 3; i++) {
//            Cell cell = headerRoleRow.getCell(i);
//            cell.setCellStyle(headerStyle);
//        }
//
//
//        int rowRoleCount = 1;
//        for (Role role : roles) {
//            Row row = sheetRole.createRow(rowRoleCount++);
//            row.createCell(0).setCellValue(role.getName());
//            row.createCell(1).setCellValue(role.getDescription());
//            StringBuilder permmissionRoleString = new StringBuilder();
//            if (role.getPermissions() != null) {
//                for (Permission permission : role.getPermissions()) {
//                    permmissionRoleString.append(permission.getName()).append(", ");
//                }
//                if (permmissionRoleString.length() > 0) {
//                    permmissionRoleString.setLength(permmissionRoleString.length() - 2);
//                }
//            }
//            row.createCell(2).setCellValue(permmissionRoleString.toString()); // Ghi roles vào ô
//        }
//            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//                workbook.write(outputStream);
//                byte[] bytes = outputStream.toByteArray();
//
//                // Thiết lập header cho file Excel
//                HttpHeaders headers = new HttpHeaders();
//                headers.add("Content-Disposition", "attachment; filename=users.xlsx");
//                headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//
//                return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            } finally {
//                try {
//                    workbook.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//    }
//
//    @PostMapping("/import")
//    public ResponseEntity<String> importExcelFile(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return new ResponseEntity<>("Please upload a file!", HttpStatus.BAD_REQUEST);
//        }
//
//        try (InputStream inputStream = file.getInputStream()) {
//            Workbook workbook = new XSSFWorkbook(inputStream);
//            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên từ file Excel
//
//            List<User> users = new ArrayList<>();
//
//            // Duyệt qua các dòng và đọc dữ liệu
//            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bắt đầu từ dòng 1 vì dòng 0 là header
//                Row row = sheet.getRow(i);
//
//                if (row != null) {
//                    String username = row.getCell(1).getStringCellValue();
//                    String email = row.getCell(2).getStringCellValue();
//                    String roles = row.getCell(3).getStringCellValue();
//                    // Bạn có thể thêm logic để tạo User và lưu vào DB
//                    User user = new User();
//                    user.setUsername(username);
//                    user.setEmail(email);
//                    user.setPassword(passwordEncoder.encode("123")); //config password
//                    Set<Role> roleSet = new HashSet<>();
//                    Role role = roleRepository.findByName(roles).orElse(null);
//                    roleSet.add(role);
//                    // Set roles cho user
//                    user.setRoles(roleSet);
//                    // Thêm User vào danh sách
//                    users.add(user);
//                }
//            }
//
//            // Xử lý danh sách người dùng lưu vào database
//            userRepository.saveAll(users);
//
//            return new ResponseEntity<>("File uploaded and processed successfully!", HttpStatus.OK);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ResponseEntity<>("Error processing the file", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//}
