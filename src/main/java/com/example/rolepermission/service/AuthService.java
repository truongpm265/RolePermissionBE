package com.example.rolepermission.service;

import com.example.rolepermission.dto.request.AuthRequest;
import com.example.rolepermission.dto.request.IntrospectRequest;
import com.example.rolepermission.dto.response.AuthResponse;
import com.example.rolepermission.dto.response.IntrospectResponse;
import com.example.rolepermission.entity.User;
import com.example.rolepermission.exception.AppException;
import com.example.rolepermission.exception.ErrorCode;
import com.example.rolepermission.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    static final String SIGNER_KEY = "1231232134356787654323456781212312312312312312312312312312312312365432";
    public AuthResponse authenticate(AuthRequest request){
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
//        String userName = request.getUsername();
//        String userPassword = request.getPassword();

        boolean authenticate =  passwordEncoder.matches(request.getPassword(),user.getPassword());
        if(!authenticate)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(user)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(jwsVerifier);

        return IntrospectResponse.builder()
                .valid(verified && expirationTime.after(new Date()))
                .build();
    }

//    private String generateToken(User user){
//        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
//        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
//                .subject(user.getUsername())
//                .issuer("phammanhtruong")
//                .issueTime(new Date())
//                .expirationTime(new Date(
//                        Instant.now().plus(36000, ChronoUnit.SECONDS).toEpochMilli()))
//                .claim("scope",buildScope(user))
////                .claim("roles",buildRole(user))
////                .claim("functions",buildFunction(user))
////                .claim("permissions",buildPermission(user))
//                .build();
//
//        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
//
//        JWSObject jwsObject = new JWSObject(header,payload);
//        try {
//            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
//            return jwsObject.serialize();
//        } catch (JOSEException e) {
//            log.error("Cannot create token", e);
//            throw new RuntimeException(e);
//        }
//    }
private String generateToken(User user) {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

    // Tạo claims cho JWT
    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .subject(user.getUsername())
            .issuer("phammanhtruong")
            .issueTime(new Date())
            .expirationTime(new Date(Instant.now().plus(3600, ChronoUnit.SECONDS).toEpochMilli())) // Thời gian hết hạn 1 giờ
            .claim("roles", buildRoleArray(user))        // Gọi phương thức buildRoleArray để lấy danh sách roles dưới dạng mảng
            .claim("functions", buildFunctionArray(user)) // Gọi phương thức buildFunctionArray để lấy danh sách functions dưới dạng mảng
            .claim("permissions", buildPermissionArray(user)) // Gọi phương thức buildPermissionArray để lấy danh sách permissions dưới dạng mảng
            .build();

    // Tạo payload từ claims
    Payload payload = new Payload(jwtClaimsSet.toJSONObject());

    // Tạo JWSObject từ header và payload
    JWSObject jwsObject = new JWSObject(header, payload);

    try {
        // Ký và tạo chuỗi JWT
        jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
        return jwsObject.serialize(); // Trả về chuỗi JWT đã ký
    } catch (JOSEException e) {
        log.error("Cannot create token", e);
        throw new RuntimeException(e);
    }
}


    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        Set<String> addedItems = new HashSet<>(); // Set để theo dõi các item đã thêm

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                String roleName = "ROLE_" + role.getName();
                if (addedItems.add(roleName)) { // Chỉ thêm nếu chưa có trong set
                    stringJoiner.add(roleName);
                }
                if (!CollectionUtils.isEmpty(role.getFunctions())) {
                    role.getFunctions().forEach(function -> {
                        String functionName = "FUNCTION_" + function.getName();
                        if (addedItems.add(functionName)) { // Chỉ thêm nếu chưa có trong set
                            stringJoiner.add(functionName); // Thêm tên chức năng
                        }
                        if (!CollectionUtils.isEmpty(function.getPermissions())) {
                            function.getPermissions().forEach(permission -> {
                                String permissionName = permission.getName();
                                if (addedItems.add(permissionName)) { // Chỉ thêm nếu chưa có trong set
                                    stringJoiner.add(permissionName); // Thêm tên quyền
                                }
                            });
                        }
                    });
                }
            });
        }
        return stringJoiner.toString();
    }
    private String buildRole(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add(role.getName());
            });
        }
        return stringJoiner.toString();
    }
    private String buildFunction(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                if (!CollectionUtils.isEmpty(role.getFunctions())) {
                    role.getFunctions().forEach(function -> {
                        stringJoiner.add(function.getName()); // Thêm tên chức năng
                    });
                }
            });
        }
        return stringJoiner.toString();
    }

//    private String buildPermission(User user) {
//        StringJoiner stringJoiner = new StringJoiner(" ");
//        Set<String> addedPermissions = new HashSet<>(); // Khởi tạo Set để theo dõi quyền đã thêm
//
//        if (!CollectionUtils.isEmpty(user.getRoles())) {
//            user.getRoles().forEach(role -> {
//                if (!CollectionUtils.isEmpty(role.getFunctions())) {
//                    role.getFunctions().forEach(function -> {
//                        if (!CollectionUtils.isEmpty(function.getPermissions())) {
//                            function.getPermissions().forEach(permission -> {
//                                String permissionName = permission.getName();
//                                // Kiểm tra xem quyền đã được thêm vào chuỗi chưa
//                                if (!addedPermissions.contains(permissionName)) { // Nếu quyền chưa tồn tại
//                                    addedPermissions.add(permissionName); // Thêm quyền vào Set
//                                    stringJoiner.add(permissionName); // Thêm quyền vào chuỗi
//                                }
//                            });
//                        }
//                    });
//                }
//            });
//        }
//        return stringJoiner.toString();
//    }

    private String[] buildRoleArray(User user) {
        return user.getRoles().stream()
                .map(role -> role.getName()) // Thêm tiền tố ROLE_
                .toArray(String[]::new); // Chuyển đổi thành mảng
    }

    private String[] buildFunctionArray(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getFunctions().stream()
                        .map(function -> function.getName())) // Trích xuất tên chức năng
                .distinct() // Bỏ qua trùng lặp
                .toArray(String[]::new); // Chuyển đổi thành mảng
    }

    private String[] buildPermissionArray(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getFunctions().stream()
                        .flatMap(function -> function.getPermissions().stream()
                                .map(permission -> permission.getName()))) // Trích xuất tên quyền
                .distinct() // Bỏ qua trùng lặp
                .toArray(String[]::new); // Chuyển đổi thành mảng
    }
    public boolean validateToken(SignedJWT signedJWT) {
        try {
            // Kiểm tra chữ ký
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            if (!signedJWT.verify(verifier)) {
                return false; // Token không hợp lệ
            }

            // Kiểm tra thời gian hết hạn
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expirationTime.after(new Date()); // Trả về true nếu chưa hết hạn
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("" + role.getName()))
                        .collect(Collectors.toList())
        );
    }
}
