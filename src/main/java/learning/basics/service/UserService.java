package learning.basics.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import learning.basics.dto.ContactDto;
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
    private final EmailService emailService;
    private final TimerService timerService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, EmailService emailService, TimerService timerService  ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.timerService = timerService;
    }

    public boolean existsUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));
    }

    public boolean isPasswordCorrect(String username, String rawPassword) {
        User user = findByUsername(username);
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public User registerUser(RegisterDto registerDto) {
        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());
        User user = userMapper.toEntity(registerDto, encodedPassword);

        // timer für automatische account löschung nach 2 jahren (365*2 tage)
        timerService.scheduleAccountDeletion(user.getUsername(), 365 * 2);
        
        return userRepository.save(user);
    }

    // --- Kontaktformular verarbeiten ---
    @Transactional(readOnly = true)
    public void processContactForm(String username, ContactDto contactDto) {
        User currentUser = findByUsername(username);
        emailService.sendContactEmail(currentUser, contactDto);
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

    @Transactional
    public boolean isEmailTakenByAnotherUser(String email, String currentUsername) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.findByEmail(email)
                .map(user -> !user.getUsername().equals(currentUsername))
                .orElse(false);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        userRepository.delete(user);
    }
}
