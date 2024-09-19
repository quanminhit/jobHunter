package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.Response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.Response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.Response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.Response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;

    public UserService(UserRepository userRepository, CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    public User handleCreateUser(User user) throws IdInvalidException {
        if (user.getCompany() != null) {
            Optional<Company> currentCompany = this.companyService.FetchCompanyById(user.getCompany().getId());
            user.setCompany(currentCompany.isPresent() ? currentCompany.get() : null);
        }
        return this.userRepository.save(user);
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO userDTO = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();
        userDTO.setId(user.getId());
        userDTO.setAddress(user.getAddress());
        userDTO.setName(user.getName());
        userDTO.setAge(user.getAge());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender());
        userDTO.setCreateAt(user.getCreatedAt());
        userDTO.setCreateBy(user.getCreatedBy());
        userDTO.setUpdateAt(user.getUpdatedAt());
        userDTO.setUpdateBy(user.getUpdatedBy());
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            userDTO.setCompany(com);
        }
        return userDTO;
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO resUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();

        resUserDTO.setId(user.getId());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setName(user.getName());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setGender(user.getGender());

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resUserDTO.setCompany(com);
        }
        return resUserDTO;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO userDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();

        userDTO.setId(user.getId());
        userDTO.setAddress(user.getAddress());
        userDTO.setName(user.getName());
        userDTO.setAge(user.getAge());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setUpdatedBy(user.getUpdatedBy());

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            userDTO.setCompany(com);
        }

        return userDTO;
    }

    public boolean handleExistUserByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);
        List<ResUserDTO> list = pageUser.getContent().stream().map(item -> new ResUserDTO(
                item.getId(),
                item.getName(),
                item.getEmail(),
                item.getAge(),
                item.getGender(),
                item.getAddress(),
                item.getCreatedBy(),
                item.getCreatedAt(),
                item.getUpdatedBy(),
                item.getUpdatedAt(),
                new ResUserDTO.CompanyUser(
                        item.getCompany() != null ? item.getCompany().getId() : 0,
                        item.getCompany() != null ? item.getCompany().getName() : null)))
                .collect(Collectors.toList());
        rs.setResult(list);
        return rs;
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setName(reqUser.getName());
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setAge(reqUser.getAge());
            currentUser.setGender(reqUser.getGender());
            // check Company
            if (reqUser.getCompany() != null) {
                Optional<Company> currentCompany = this.companyService.FetchCompanyById(reqUser.getCompany().getId());
                reqUser.setCompany(currentCompany.isPresent() ? currentCompany.get() : null);
            }
            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateRefreshToken(String refresh_token, String email) {
        User user = this.userRepository.findByEmail(email);
        if (user != null) {
            user.setRefreshToken(refresh_token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
