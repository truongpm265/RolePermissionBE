package com.example.rolepermission.service;

import com.example.rolepermission.dto.request.UserCreationRequest;
import com.example.rolepermission.dto.request.UserUpdateRequest;
import com.example.rolepermission.dto.response.UserDetailsDTO;
import com.example.rolepermission.dto.response.UserResponse;

import com.example.rolepermission.entity.AppFunction;
import com.example.rolepermission.entity.Role;
import com.example.rolepermission.entity.User;
import com.example.rolepermission.entity.UserSearchRequest;
import com.example.rolepermission.exception.AppException;
import com.example.rolepermission.exception.ErrorCode;
import com.example.rolepermission.mapper.UserMapper;
import com.example.rolepermission.repository.RoleRepository;
import com.example.rolepermission.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    UserMapper userMapper;

    public UserResponse createUser(UserCreationRequest request) {
        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById("USER").ifPresent(roles::add);
        user.setRoles(roles);
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }


    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }


    public UserResponse getUserById(Long id){
        return userMapper.toUserResponse(userRepository.findById(id)
        .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }


    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }


    public void deleteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public User getUserDetails() {
        User user = getCurrentUser();
        return user;
    }

    public UserDetailsDTO getUserDetailsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Chuyển đổi thành UserDetailsDTO
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName()) // Giả sử bạn có phương thức getName() trong Role
                .collect(Collectors.toList());

        List<AppFunction> functions = user.getRoles().stream()
                .flatMap(role -> role.getFunctions().stream()) // Lấy các function từ role
                .distinct() // Loại bỏ duplicates
                .map(appFunction -> new AppFunction(appFunction.getId(),appFunction.getName(), appFunction.getPermissions())) // Giả sử bạn có AppFunctionDTO
                .collect(Collectors.toList());

        return new UserDetailsDTO(username, roles, functions);
    }

    public Page<User> search(UserSearchRequest request, Pageable pageable) {
        // Base SQL statements with 1=1 for easier condition appending
        StringBuilder sql = new StringBuilder("SELECT s FROM User s WHERE 1=1");
        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(s) FROM User s WHERE 1=1");
        StringBuilder where = new StringBuilder();

        // Map to hold parameter values for the query
        Map<String, Object> params = new HashMap<>();

        // Add conditions dynamically based on non-null and non-empty values
        if (request.getName() != null && !request.getName().isEmpty()) {
            where.append(" AND s.name LIKE :name");
            params.put("name", "%" + request.getName().trim().toLowerCase() + "%");
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            where.append(" AND s.email LIKE :email");
            params.put("email", "%" + request.getEmail().trim().toLowerCase() + "%");
        }

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            where.append(" AND s.role LIKE :role");
            params.put("role", "%" + request.getRole().trim().toLowerCase() + "%");
        }

        // Finalize SQL statements with the constructed WHERE clause
        sql.append(where);
        sqlCount.append(where);

        // Query execution (assuming EntityManager is used for the custom query)
        Query query = entityManager.createQuery(sql.toString(), User.class);
        Query countQuery = entityManager.createQuery(sqlCount.toString());

        // Set parameters to both main and count queries
        params.forEach((key, value) -> {
            query.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        // Pagination
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Execute and get results
        List<User> employees = query.getResultList();
        long total = (long) countQuery.getSingleResult();

        // Return paginated result
        return new PageImpl<>(employees, pageable, total);
    }

    public List<User> searchUsers(String name, String email, String role) {
        return userRepository.searchUsers(name, email, role);
    }
}
