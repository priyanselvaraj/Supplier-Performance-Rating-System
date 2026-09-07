package com.supplier.sprsystem.service.impl;

import com.supplier.sprsystem.dto.request.LoginRequest;
import com.supplier.sprsystem.dto.request.RegisterRequest;
import com.supplier.sprsystem.dto.request.UserUpdateRequest;
import com.supplier.sprsystem.dto.response.JwtAuthResponse;
import com.supplier.sprsystem.dto.response.UserResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.DuplicateResourceException;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.exception.UnauthorizedException;
import com.supplier.sprsystem.model.entity.ERole;
import com.supplier.sprsystem.model.entity.Role;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.model.entity.Supplier;
import com.supplier.sprsystem.repository.RoleRepository;
import com.supplier.sprsystem.repository.SupplierRepository;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.security.jwt.JwtService;
import com.supplier.sprsystem.security.jwt.JwtUtils;
import com.supplier.sprsystem.security.services.UserDetailsImpl;
import com.supplier.sprsystem.model.entity.NotificationPriority;
import com.supplier.sprsystem.model.entity.NotificationType;
import com.supplier.sprsystem.service.AuthService;
import com.supplier.sprsystem.service.EmailService;
import com.supplier.sprsystem.service.NotificationService;
import com.supplier.sprsystem.service.SmsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SupplierRepository supplierRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtUtils;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SmsService smsService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           SupplierRepository supplierRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtUtils,
                           NotificationService notificationService,
                           EmailService emailService,
                           SmsService smsService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.supplierRepository = supplierRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    @Override
    public JwtAuthResponse authenticateUser(LoginRequest loginRequest) {
        String identifier = loginRequest.getUsernameOrEmail();
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new BadRequestException("Username or email is required for authentication");
        }

        // Verify account exists & check if enabled
        User user = userRepository.findByUsername(identifier.trim())
                .or(() -> userRepository.findByEmail(identifier.trim().toLowerCase()))
                .orElseThrow(() -> new UnauthorizedException("Invalid username/email or password"));

        if (!user.isActive() || !user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled. Please contact system administrator.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            Long supplierId = user.getSupplier() != null ? user.getSupplier().getId() : null;
            String supplierName = user.getSupplier() != null ? user.getSupplier().getName() : null;

            // Dispatch Login Security Notifications (In-App, Email, SMS)
            dispatchLoginNotifications(user);

            return JwtAuthResponse.builder()
                    .token(jwt)
                    .type("Bearer")
                    .tokenType("Bearer")
                    .id(userDetails.getId())
                    .userId(userDetails.getId())
                    .username(userDetails.getUsername())
                    .email(userDetails.getEmail())
                    .fullName(userDetails.getFullName())
                    .roles(roles)
                    .supplierId(supplierId)
                    .supplierName(supplierName)
                    .build();
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username/email or password");
        }
    }

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest registerRequest) {
        // Validate password and confirm password match
        if (registerRequest.getConfirmPassword() != null && !registerRequest.getConfirmPassword().isEmpty()) {
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                throw new BadRequestException("Password and confirm password do not match");
            }
        }

        String username = registerRequest.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = registerRequest.getEmail().substring(0, registerRequest.getEmail().indexOf('@')).trim();
        } else {
            username = username.trim();
        }

        String email = registerRequest.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Error: Email is already in use by another account!");
        }

        Set<Role> roles = new HashSet<>();
        Set<String> strRoles = registerRequest.getRoles();

        if (strRoles == null || strRoles.isEmpty()) {
            Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(ERole.ROLE_MANAGER).build()));
            roles.add(managerRole);
        } else {
            for (String role : strRoles) {
                String normalized = role.toUpperCase();
                if (normalized.equals("ADMIN") || normalized.equals("ROLE_ADMIN")) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseGet(() -> roleRepository.save(Role.builder().name(ERole.ROLE_ADMIN).build()));
                    roles.add(adminRole);
                } else if (normalized.equals("MANAGER") || normalized.equals("ROLE_MANAGER")) {
                    Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                            .orElseGet(() -> roleRepository.save(Role.builder().name(ERole.ROLE_MANAGER).build()));
                    roles.add(managerRole);
                } else if (normalized.equals("SUPPLIER") || normalized.equals("ROLE_SUPPLIER")) {
                    Role supplierRole = roleRepository.findByName(ERole.ROLE_SUPPLIER)
                            .orElseGet(() -> roleRepository.save(Role.builder().name(ERole.ROLE_SUPPLIER).build()));
                    roles.add(supplierRole);
                } else {
                    throw new BadRequestException("Invalid role specified: " + role + ". Allowed roles: ADMIN, MANAGER, SUPPLIER.");
                }
            }
        }

        Supplier supplier = null;
        if (roles.stream().anyMatch(r -> r.getName() == ERole.ROLE_SUPPLIER)) {
            if (registerRequest.getSupplierId() != null) {
                supplier = supplierRepository.findById(registerRequest.getSupplierId()).orElse(null);
            }
            if (supplier == null) {
                // Link by email or fallback to first supplier in database
                supplier = supplierRepository.findByEmail(email)
                        .orElseGet(() -> supplierRepository.findAll().stream().findFirst().orElse(null));
            }
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName().trim())
                .phone(registerRequest.getPhone() != null ? registerRequest.getPhone().trim() : null)
                .department(registerRequest.getDepartment())
                .supplier(supplier)
                .active(true)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);

        // Dispatch Welcome Notifications (In-App, Email, SMS)
        dispatchWelcomeNotifications(savedUser);

        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = getAuthenticatedUserEntity();
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentProfile(UserUpdateRequest updateRequest) {
        User user = getAuthenticatedUserEntity();

        if (!user.getEmail().equalsIgnoreCase(updateRequest.getEmail().trim())
                && userRepository.existsByEmail(updateRequest.getEmail().trim())) {
            throw new DuplicateResourceException("Error: Email is already in use by another account!");
        }

        user.setFullName(updateRequest.getFullName().trim());
        user.setEmail(updateRequest.getEmail().trim().toLowerCase());
        user.setPhone(updateRequest.getPhone() != null ? updateRequest.getPhone().trim() : null);
        user.setDepartment(updateRequest.getDepartment());

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    private User getAuthenticatedUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("User is not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
    }

    private UserResponse mapToUserResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .active(user.isActive())
                .roles(roleNames)
                .supplierId(user.getSupplier() != null ? user.getSupplier().getId() : null)
                .supplierName(user.getSupplier() != null ? user.getSupplier().getName() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private void dispatchWelcomeNotifications(User user) {
        if (user == null) return;

        try {
            notificationService.createNotification(
                    user.getId(),
                    "Welcome to SPRS!",
                    "Welcome to Supplier Performance Rating System, " + user.getFullName() + "! Your account is active and ready to use.",
                    NotificationType.SECURITY,
                    NotificationPriority.HIGH,
                    "USER",
                    user.getId()
            );
        } catch (Exception e) {
            // Non-blocking notification dispatch
        }

        try {
            emailService.sendWelcomeEmail(user);
        } catch (Exception e) {
            // Non-blocking email dispatch
        }

        try {
            smsService.sendWelcomeSms(user);
        } catch (Exception e) {
            // Non-blocking SMS dispatch
        }
    }

    private void dispatchLoginNotifications(User user) {
        if (user == null) return;

        String[] meta = getClientRequestMetadata();
        String ipAddress = meta[0];
        String userAgent = meta[1];
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(DATE_FORMATTER);

        try {
            notificationService.createNotification(
                    user.getId(),
                    "New Login Detected",
                    "Successful login to your SPRS account at " + formattedTime + " (IP: " + ipAddress + "). If this was not you, please change your password immediately.",
                    NotificationType.SECURITY,
                    NotificationPriority.LOW,
                    "AUTH",
                    user.getId()
            );
        } catch (Exception e) {
            // Non-blocking notification dispatch
        }

        try {
            emailService.sendLoginAlertEmail(user, ipAddress, userAgent, now);
        } catch (Exception e) {
            // Non-blocking email dispatch
        }

        try {
            smsService.sendLoginAlertSms(user, ipAddress, now);
        } catch (Exception e) {
            // Non-blocking SMS dispatch
        }
    }

    private String[] getClientRequestMetadata() {
        String clientIp = "127.0.0.1";
        String userAgent = "Web Browser";
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xfHeader = request.getHeader("X-Forwarded-For");
                if (xfHeader != null && !xfHeader.trim().isEmpty()) {
                    clientIp = xfHeader.split(",")[0].trim();
                } else if (request.getRemoteAddr() != null) {
                    clientIp = request.getRemoteAddr();
                }
                String ua = request.getHeader("User-Agent");
                if (ua != null && !ua.trim().isEmpty()) {
                    userAgent = ua.trim();
                }
            }
        } catch (Exception ignored) {
        }
        return new String[]{clientIp, userAgent};
    }
}
