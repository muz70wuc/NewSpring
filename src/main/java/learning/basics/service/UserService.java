package learning.basics.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import learning.basics.dto.RegisterDto;
import learning.basics.mapper.UserMapper;
import learning.basics.model.User;
import learning.basics.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public boolean existsUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User registerUser(RegisterDto registerDto) {
        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());
        User user = userMapper.toEntity(registerDto, encodedPassword);
        return userRepository.save(user);
    }
}
