package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.vuxnye.dto.request.PetCreationRequest;
import vn.vuxnye.dto.request.PetUpdateRequest;
import vn.vuxnye.dto.response.PetPageResponse;
import vn.vuxnye.dto.response.PetResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.PetEntity;
import vn.vuxnye.model.UserEntity;
import vn.vuxnye.repository.PetRepository;
import vn.vuxnye.repository.UserRepository;
import vn.vuxnye.service.PetService;
import vn.vuxnye.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "PET-SERVICE")
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    private final UserRepository userRepository;

    @Override
    public PetPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("Admin findAll pets");

        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if(StringUtils.hasLength(sort)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()){
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    order = new Sort.Order(Sort.Direction.ASC, columnName);
                } else{
                    order = new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo =0;
        if(page > 0 ){
            pageNo = page-1;
        }

        // paging
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<PetEntity> entityPage = null;

        if (StringUtils.hasLength(keyword)) {
            keyword = "%" +keyword.toLowerCase() +"%";
            entityPage =petRepository.searchByKeyWord(keyword,pageable);
        } else {
            entityPage= petRepository.findAll(pageable);
        }

        // Phai tra ve page no, page size, list
        PetPageResponse response = getPetPageResponse(page, size, entityPage);
        return response;
    }

    private PetPageResponse getPetPageResponse(int page, int size, Page<PetEntity> petEntities) {
        log.info("Convert Pet Entity Page");
        List<PetResponse> petList = petEntities.stream().map(entity -> PetResponse.builder()
                        .id(entity.getId())
                        .owner(entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName())
                        .species(entity.getSpecies())
                        .breed(entity.getBreed())
                        .sex(entity.getSex())
                        .color(entity.getColor())
                        .name(entity.getName())
                        .birthDate(entity.getBirthDate())

                .build()
                ).toList();

        PetPageResponse response = new PetPageResponse();
        response.setPets(petList);
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(petEntities.getTotalElements());
        response.setTotalPages(petEntities.getTotalPages());
        return response;
    }

    @Override
    public PetResponse findById(Long id) {
        log.info("find pet by id {}", id);

        PetEntity petEntity = petRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pet not found"));

        return PetResponse.fromEntity(petEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PetResponse> getMyPets(UserDetails userDetails) {
        log.info("User {} is getting their pets", userDetails.getUsername());
        String username = userDetails.getUsername();
        List<PetEntity> pets = petRepository.findByOwnerUsername(username);
        return pets.stream().map(PetResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public PetResponse addPet(PetCreationRequest req, UserDetails userDetails) {
        UserEntity owner;

        if(isAdmin(userDetails)){
            log.info("Admin {} is adding pet", userDetails.getUsername());
            long ownerId = (req.getOwnerId()!= null) ? req.getOwnerId() : getUserId(userDetails);
            owner = userRepository.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        } else {
            log.info("User {} is adding pet", userDetails.getUsername());
            owner = getActiveUser(userDetails);
        }
        PetEntity newPet = new PetEntity();
        newPet.setOwner(owner);
        newPet.setName(req.getName());
        newPet.setSpecies(req.getSpecies());
        newPet.setBreed(req.getBreed());
        newPet.setColor(req.getColor());
        newPet.setBirthDate(req.getBirthDate());
        newPet.setSex(req.getSex());

        petRepository.save(newPet);
        return PetResponse.fromEntity(newPet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PetResponse updatePet(PetUpdateRequest req, UserDetails userDetails) {
        PetEntity petToUpdate;
        UserEntity currentUser = getActiveUser(userDetails);
        if (isAdmin(userDetails)) {
            // Admin: Tìm pet bằng ID
            log.info("Admin {} updating pet {}", userDetails.getUsername(), req.getId());
            petToUpdate = petRepository.findById(req.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
            // (Tùy chọn: Admin có thể đổi chủ của pet)
            if (req.getOwnerId() != null && !petToUpdate.getOwner().getId().equals(req.getOwnerId())) {
                UserEntity newOwner = userRepository.findById(req.getOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException("New Owner not found"));
                petToUpdate.setOwner(newOwner);
            }
        } else {
            // User: Tìm pet bằng ID VÀ ID chủ
            log.info("User {} updating pet {}", userDetails.getUsername(), req.getId());
            petToUpdate = petRepository.findByIdAndOwnerId(req.getId(), currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pet not found or you do not own this pet"));
        }

        petToUpdate.setName(req.getName());
        petToUpdate.setSpecies(req.getSpecies());
        petToUpdate.setBreed(req.getBreed());
        petToUpdate.setColor(req.getColor());
        petToUpdate.setBirthDate(req.getBirthDate());
        petToUpdate.setSex(req.getSex());

        petRepository.save(petToUpdate);
        return PetResponse.fromEntity(petToUpdate);
    }

    @Override
    public void deletePet(Long petId, UserDetails userDetails) {
        PetEntity petToDelete;
        UserEntity currentUser = getActiveUser(userDetails);


        if (isAdmin(userDetails)) {
            log.warn("Admin {} deleting pet {}", userDetails.getUsername(), petId);
            petToDelete = petRepository.findById(petId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
        } else {
            log.warn("User {} deleting pet {}", userDetails.getUsername(), petId);
            petToDelete = petRepository.findByIdAndOwnerId(petId, currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pet not found or you do not own this pet"));
        }
        petRepository.delete(petToDelete);

    }

    /**
     * Determines whether the given user has the "ADMIN" authority.
     *
     * @param userDetails the details of the user whose roles are to be checked
     * @return true if the user has the "ADMIN" authority, false otherwise
     */

    private boolean isAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }

    private UserEntity getActiveUser(UserDetails userDetails) {
       return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
    }

    private Long getUserId(UserDetails userDetails) {
        return getActiveUser(userDetails).getId();
    }


}
