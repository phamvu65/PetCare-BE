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
import vn.vuxnye.exception.PermissionDenyException;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.AddressEntity;
import vn.vuxnye.model.RoleEntity;
import vn.vuxnye.model.UserEntity;
import vn.vuxnye.repository.AddressRepository;
import vn.vuxnye.repository.RoleRepository;
import vn.vuxnye.repository.UserRepository;
import vn.vuxnye.service.EmailService;
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

    private final EmailService emailService;

    private final RoleRepository roleRepository;


    @Override
    public UserPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("findAll start");

        // Sorting
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)) {
            //Goi search method
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); // ten cot:asc|desc
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
        //Xu ly truong hop fe muon bat dau trang bang 1
        int pageNo = 0;
        if(page > 0){
            pageNo = page - 1;
        }

        //Paging
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<UserEntity> entityPage = null;

        if (StringUtils.hasLength(keyword)) {
            keyword = "%" +keyword.toLowerCase() +"%";
            entityPage =userRepository.searchByKeyWord(keyword,pageable);
        } else {
            entityPage= userRepository.findAll(pageable);
        }

         // Phai tra ve page no, page size, list
        UserPageResponse response = getUserPageResponse(page, size, entityPage);
        return response;
    }



    @Override
    public UserResponse findById(Long id) {
        log.info("find user by id:{}", id);

        UserEntity userEntity= getUserEntity(id);

        return UserResponse.builder()
                .id(id)
                .fistName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUsername())
                .phone(userEntity.getPhone())
                .email(userEntity.getEmail())
                .build();
    }

    @Override
    public UserResponse findByUsername(String username) {
        log.info("find user by username:{}", username);
        UserEntity userEntity= userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
        if(userEntity != null){
            return UserResponse.builder()
                    .id(userEntity.getId())
                    .fistName(userEntity.getFirstName())
                    .lastName(userEntity.getLastName())
                    .userName(userEntity.getUsername())
                    .phone(userEntity.getPhone())
                    .email(userEntity.getEmail())
                    .build();
        }else {
            throw new ResourceNotFoundException("User not found");
        }
    }

    @Override
    public UserResponse findByEmail(String email) {
        log.info("find user by email:{}", email);
        UserEntity userEntity= userRepository.findByEmail(email);
        if(userEntity != null){
            return UserResponse.builder()
                    .id(userEntity.getId())
                    .fistName(userEntity.getFirstName())
                    .lastName(userEntity.getLastName())
                    .userName(userEntity.getUsername())
                    .phone(userEntity.getPhone())
                    .email(userEntity.getEmail())
                    .build();
        }else {
            throw new ResourceNotFoundException("User not found");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(UserCreationRequest req) {
    log.info("saving user:{}", req);

    UserEntity userByEmail= userRepository.findByEmail(req.getEmail());
    if(userByEmail != null){
        throw new InvalidDataException("Email already exists");
    }

    RoleEntity role = roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id = " + req.getRoleId()));
    if(role.getName().toUpperCase().equals("ADMIN")){
        throw new PermissionDenyException("bạn không thể đăng ký tài khoản admin");
    }

    //convert UserCreationRequest -> UserEntity
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
    log.info("Saved user:{}", user);

        if (req.getAddresses() != null && !req.getAddresses().isEmpty()) {
            List<AddressEntity> addresses = new ArrayList<>();

            // 1. Tìm index (vị trí) của địa chỉ default ĐẦU TIÊN
            int defaultIndex = -1;
            for (int i = 0; i < req.getAddresses().size(); i++) {
                Boolean isDefault = req.getAddresses().get(i).getIsDefault();
                if (isDefault != null && isDefault) {
                    defaultIndex = i;
                    break; // Tìm thấy cái đầu tiên -> Dừng ngay
                }
            }

            // 2. Nếu không có cái nào được set default, MẶC ĐỊNH cái đầu tiên (index 0)
            if (defaultIndex == -1) {
                defaultIndex = 0;
            }

            // 3. Lặp lại và lưu
            for (int i = 0; i < req.getAddresses().size(); i++) {
                var address= req.getAddresses().get(i);
                AddressEntity addressEntity = new AddressEntity();
                addressEntity.setUser(user);
                addressEntity.setRecipientName(address.getRecipientName());
                addressEntity.setRecipientPhone(address.getRecipientPhone());
                addressEntity.setCity(address.getCity());
                addressEntity.setWard(address.getWard());
                addressEntity.setAddressDetail(address.getAddressDetail());

                // 4. Chỉ set true cho cái index đã chọn
                addressEntity.setIsDefault(i == defaultIndex);

                addresses.add(addressEntity);
            }
        addressRepository.saveAll(addresses);
        log.info("Saved addresses:{}", addresses);


    }
        //Send email confirm
        try {
            emailService.emailValidation(req.getEmail(), req.getUserName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    return user.getId() != null ? user.getId() : 0L ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        log.info("update user:{}", req);
        // Get user by id
        UserEntity user = getUserEntity(req.getId());
        //set data
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUserName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());

        userRepository.save(user);
        log.info("Updated user:{}", user);

    }

    @Override
    public void changePassword(UserPasswordRequest req) {
        log.info("change password:{}", req);

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        //Get user by username
        UserEntity user = userRepository.findByUsername(currentUsername).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!passwordEncoder.matches(req.getOldPassword(), user.getPassword())){
            throw new InvalidDataException("Old password is incorrect");
        }


        if(req.getPassword().equals(req.getConfirmPassword())){
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            userRepository.save(user);
        }else {
            throw new InvalidDataException("New password and confirm password are not match");
        }
        log.info("Password changed successfully for user: {}", currentUsername);

    }

    @Override
    public void delete(Long id) {
    log.info("delete user:{}", id);
    UserEntity user = getUserEntity(id);
    user.setStatus(UserStatus.INACTIVE);
    userRepository.save(user);

    }

    /**
     * Get user by id
     * @param id
     * @return
     */
    private UserEntity getUserEntity(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    }

    /**
     * Convert UserEntities to UserResponse
     * @param page
     * @param size
     * @param userEntities
     * @return
     */
    private static UserPageResponse getUserPageResponse(int page, int size, Page<UserEntity> userEntities) {
        log.info("Convert User Entity Page");
        List<UserResponse> userList = userEntities.stream().map(entity -> UserResponse.builder()
                .id(entity.getId())
                .fistName(entity.getFirstName())
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
