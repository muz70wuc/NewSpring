package learning.basics.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import learning.basics.dto.ProfileDto;
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

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));
    }

    public User registerUser(RegisterDto registerDto) {
        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());
        User user = userMapper.toEntity(registerDto, encodedPassword);
        return userRepository.save(user);
    }

    // --- Profil auslesen für GET /profile ---
    @Transactional(readOnly = true)
    public ProfileDto getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));
        
        return userMapper.toProfileDto(user);
    }

    // --- Profil aktualisieren für POST /profile ---
    @Transactional
    public void updateProfile(String username, ProfileDto profileDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        user.setEmail(profileDto.getEmail());
        user.setPhoneNumber(profileDto.getPhoneNumber());
        user.setCompanyName(profileDto.getCompanyName());
        user.setContactPerson(profileDto.getContactPerson());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean isProfileIncomplete(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        // Wenn E-Mail ODER Firmenname fehlt/leer ist, gilt das Profil als unvollständig
        return user.getEmail() == null || user.getEmail().isBlank()
            || user.getCompanyName() == null || user.getCompanyName().isBlank();
    }
}
