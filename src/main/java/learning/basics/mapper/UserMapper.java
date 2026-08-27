package learning.basics.mapper;

import org.springframework.stereotype.Component;

import learning.basics.dto.ProfileDto;
import learning.basics.dto.RegisterDto;
import learning.basics.model.User;

@Component
public class UserMapper {

    public User toEntity(RegisterDto dto, String encodedPassword) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encodedPassword);
        user.setRole("ROLE_USER");
        return user;
    }

    public ProfileDto toProfileDto(User user) {
        ProfileDto dto = new ProfileDto();
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setCompanyName(user.getCompanyName());
        dto.setContactPerson(user.getContactPerson());
        return dto;
    }
}
