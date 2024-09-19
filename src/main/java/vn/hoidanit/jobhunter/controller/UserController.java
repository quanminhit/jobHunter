package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.Response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.Response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.Response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResUserDTO> createNewUser(@RequestBody User postManUser)
            throws IdInvalidException {
        if (this.userService.handleExistUserByEmail(postManUser.getEmail())) {
            throw new IdInvalidException("Email" + postManUser.getEmail() + "da ton tai, vui long su dung email khac");
        }
        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User ericUser = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResUserDTO(ericUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User voi id: " + id + " khong ton tai");
        }

        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    // fetch user by id
    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) {
        User fetchUser = this.userService.fetchUserById(id);
        // return ResponseEntity.ok(fetchUser);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
    }

    // fetch all users
    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(
                this.userService.fetchAllUser(spec, pageable));
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(user.getId());
        if (currentUser == null) {
            throw new IdInvalidException("User voi id: " + user.getId() + " khong ton tai");
        }
        User ericUser = this.userService.handleUpdateUser(user);
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(ericUser));
    }

}
