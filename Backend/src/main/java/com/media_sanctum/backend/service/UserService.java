package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.User;
import com.media_sanctum.backend.model.CreateUserModel;
import com.media_sanctum.backend.model.UpdateUserModel;
import com.media_sanctum.backend.model.UserModel;
import com.media_sanctum.backend.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserModel> getUsers() {
        var sortByCreatedAt = Sort.by("createdAt").descending();
        var pageRequest = PageRequest.of(0, 10, sortByCreatedAt);
        var entities = userRepository.findAll(pageRequest);
        return entities.stream().map(this::fromEntity).toList();
    }

    public Optional<UserModel> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::fromEntity);
    }

    public Optional<UserModel> getUserById(String id) {
        return userRepository.findById(id)
                .map(this::fromEntity);
    }

    public UserModel createUser(CreateUserModel createUserModel) {
        var hashedPassword = passwordEncoder.encode(createUserModel.getPassword());

        var userEntity = new User();
        userEntity.setEmail(createUserModel.getEmail());
        userEntity.setFirstName(createUserModel.getFirstName());
        userEntity.setLastName(createUserModel.getLastName());
        userEntity.setPasswordHash(hashedPassword);
        userEntity.setActive(true);

        var savedEntity = userRepository.save(userEntity);

        return fromEntity(savedEntity);
    }

    public UserModel updateUser(UpdateUserModel updateUserModel) {
        if (StringUtils.isEmpty(updateUserModel.getId())) {
            throw new IllegalArgumentException("User id is required for update");
        }

        var existingEntity = userRepository.findById(updateUserModel.getId()).orElseThrow();
        if (updateUserModel.getEmail() != null) {
            existingEntity.setEmail(updateUserModel.getEmail());
        }
        if (updateUserModel.getPassword() != null) {
            var hashedPassword = passwordEncoder.encode(updateUserModel.getPassword());
            existingEntity.setPasswordHash(hashedPassword);
        }
        if (updateUserModel.getFirstName() != null) {
            existingEntity.setFirstName(updateUserModel.getFirstName());
        }
        if (updateUserModel.getLastName() != null) {
            existingEntity.setLastName(updateUserModel.getLastName());
        }

        var updatedEntity = userRepository.save(existingEntity);
        return fromEntity(updatedEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Set<GrantedAuthority> authorities = new HashSet<>();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive(),
                true,
                true,
                true,
                authorities);
    }

    private UserModel fromEntity(User userEntity) {
        var result = new UserModel();
        result.setId(userEntity.getId());
        result.setFirstName(userEntity.getFirstName());
        result.setLastName(userEntity.getLastName());
        result.setEmail(userEntity.getEmail());
        return result;
    }
}
