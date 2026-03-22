package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.vuxnye.common.UserStatus;
import vn.vuxnye.dto.request.UserCreationRequest;
import vn.vuxnye.dto.request.UserPasswordRequest;
import vn.vuxnye.dto.request.UserUpdateRequest;
import vn.vuxnye.dto.response.UserPageResponse;
import vn.vuxnye.dto.response.UserResponse;
import vn.vuxnye.exception.InvalidDataException;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.AddressEntity;
import vn.vuxnye.model.RoleEntity;
import vn.vuxnye.model.UserEntity;
import vn.vuxnye.repository.AddressRepository;
import vn.vuxnye.repository.RoleRepository;
import vn.vuxnye.repository.UserRepository;
import vn.vuxnye.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserPageResponse findAll(String keyword, Long roleId, UserStatus status, String sort, int page, int size) {
        log.info("findAll start. RoleId: {}, Status: {}, Keyword: {}", roleId, status, keyword);

        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()){
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    order = new Sort.Order(Sort.Direction.ASC, columnName);
                }else {
                    order = new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }

        int pageNo = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        String searchKey = null;
        if (StringUtils.hasLength(keyword)) {
            searchKey = "%" + keyword.toLowerCase() + "%";
        }

        Page<UserEntity> entityPage = userRepository.searchByRoleStatusAndKeyword(roleId, status, searchKey, pageable);

        return getUserPageResponse(page, size, entityPage);
    }

    @Override
    public UserResponse findById(Long id) {
        UserEntity userEntity = getUserEntity(id);
        return UserResponse.builder()
                .id(id)
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUsername())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .build();
    }

    @Override
    public UserResponse findByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserResponse.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUsername())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .build();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(UserCreationRequest req) {
        log.info("saving user:{}", req);

        UserEntity userByEmail = userRepository.findByEmail(req.getEmail());
        if(userByEmail != null){
            throw new InvalidDataException("Email already exists");
        }

        Long roleIdToUse = req.getRoleId();
        if (roleIdToUse == null || roleIdToUse <= 0) {
            roleIdToUse = 1L;
        }

        RoleEntity role = roleRepository.findById(roleIdToUse)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        UserEntity user = new UserEntity();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUserName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.getRoles().add(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        userRepository.save(user);

        // Address Logic
        if (req.getAddresses() != null && !req.getAddresses().isEmpty()) {
            List<AddressEntity> addresses = new ArrayList<>();
            int defaultIndex = -1;
            for (int i = 0; i < req.getAddresses().size(); i++) {
                if (Boolean.TRUE.equals(req.getAddresses().get(i).getIsDefault())) {
                    defaultIndex = i;
                    break;
                }
            }
            if (defaultIndex == -1) defaultIndex = 0;

            for (int i = 0; i < req.getAddresses().size(); i++) {
                var addressReq = req.getAddresses().get(i);
                AddressEntity addressEntity = new AddressEntity();
                addressEntity.setUser(user);
                addressEntity.setRecipientName(addressReq.getRecipientName());
                addressEntity.setRecipientPhone(addressReq.getRecipientPhone());
                addressEntity.setCity(addressReq.getCity());
                addressEntity.setWard(addressReq.getWard());
                addressEntity.setAddressDetail(addressReq.getAddressDetail());
                addressEntity.setIsDefault(i == defaultIndex);
                addresses.add(addressEntity);
            }
            addressRepository.saveAll(addresses);
        }

//        // Email logic
//        try {
//            emailService.emailValidation(req.getEmail(), req.getUserName());
//        } catch (Exception e) {
//            log.error("Failed to send email", e);
//        }

        return user.getId() != null ? user.getId() : 0L ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        log.info("update user:{}", req);
        UserEntity user = getUserEntity(req.getId());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUserName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        userRepository.save(user);
    }

    @Override
    public void changePassword(UserPasswordRequest req) {
        log.info("Processing change password request");

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new InvalidDataException("Mật khẩu cũ không chính xác");
        }

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new InvalidDataException("Mật khẩu xác nhận không khớp");
        }

        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", currentUsername);
    }

    @Override
    public void delete(Long id) {
        UserEntity user = getUserEntity(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    public void restore(Long id) {
        UserEntity user = getUserEntity(id);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    private UserEntity getUserEntity(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private static UserPageResponse getUserPageResponse(int page, int size, Page<UserEntity> userEntities) {
        List<UserResponse> userList = userEntities.stream().map(entity -> UserResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .userName(entity.getUsername())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .build()
        ).toList();

        UserPageResponse response = new UserPageResponse();
        response.setUsers(userList);
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalPages(userEntities.getTotalPages());
        response.setTotalElements(userEntities.getTotalElements());
        return response;
    }
}