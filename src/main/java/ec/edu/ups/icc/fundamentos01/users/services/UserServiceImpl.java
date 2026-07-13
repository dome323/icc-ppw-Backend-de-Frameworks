package ec.edu.ups.icc.fundamentos01.users.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entity.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    /*
     * Devuelve solamente usuarios activos.
     */
    @Override
    public List<UserResponseDto> findAll() {

        return userRepository.findAll()
                .stream()
                .filter(user -> !user.isDeleted())
                .map(this::toResponse)
                .toList();
    }

    /*
     * Busca un usuario activo por ID.
     */
    @Override
    public UserResponseDto findOne(Long id) {

        UserEntity entity = findActiveUser(id);

        return toResponse(entity);
    }

    /*
     * Crea un usuario y lo guarda en PostgreSQL.
     */
    @Override
    public UserResponseDto create(
            CreateUserDto dto) {

        Optional<UserEntity> existingUser =
                userRepository.findByEmail(
                        dto.getEmail());

        if (existingUser.isPresent()
                && !existingUser.get().isDeleted()) {

            throw new ConflictException(
                    "Email already registered");
        }

        UserEntity entity = new UserEntity();

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());

        /*
         * En esta práctica se guarda el valor en passwordHash.
         * En un sistema real debe cifrarse con BCrypt.
         */
entity.setPasswordHash(
        "hash_" + dto.getPassword()
);
        UserEntity savedEntity =
                userRepository.save(entity);

        return toResponse(savedEntity);
    }

    /*
     * Actualización completa.
     */
    @Override
    public UserResponseDto update(
            Long id,
            UpdateUserDto dto) {

        UserEntity entity = findActiveUser(id);

        validateEmailAvailable(dto.getEmail(), id);

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());

        UserEntity savedEntity =
                userRepository.save(entity);

        return toResponse(savedEntity);
    }

    /*
     * Actualización parcial.
     * Permite cambiar nombre, email y contraseña.
     */
    @Override
public UserResponseDto partialUpdate(
        Long id,
        PartialUpdateUserDto dto) {

    UserEntity entity = findActiveUser(id);

    if (dto.getName() != null) {
        entity.setName(dto.getName());
    }

    if (dto.getEmail() != null) {
        validateEmailAvailable(dto.getEmail(), id);
        entity.setEmail(dto.getEmail());
    }

    if (dto.getPassword() != null) {
        entity.setPasswordHash(
                "hash_" + dto.getPassword()
        );
    }

    UserEntity savedEntity =
            userRepository.save(entity);

    return toResponse(savedEntity);
}

    /*
     * Eliminación lógica.
     */
    @Override
    public void delete(Long id) {

        UserEntity entity = findActiveUser(id);

        entity.setDeleted(true);

        userRepository.save(entity);
    }

    /*
     * Busca un usuario y comprueba que esté activo.
     */
    private UserEntity findActiveUser(Long id) {

        UserEntity entity =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "User not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException(
                    "User not found");
        }

        return entity;
    }

    /*
     * Evita usar un correo registrado
     * por otro usuario.
     */
    private void validateEmailAvailable(
            String email,
            Long currentUserId) {

        Optional<UserEntity> existingUser =
                userRepository.findByEmail(email);

        if (existingUser.isPresent()
                && !existingUser.get()
                        .getId()
                        .equals(currentUserId)) {

            throw new ConflictException(
                    "Email already registered");
        }
    }

    /*
     * Convierte UserEntity en UserResponseDto.
     * No expone la contraseña.
     */
    private UserResponseDto toResponse(
            UserEntity entity) {

        UserResponseDto response =
                new UserResponseDto();

        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());

        return response;
    }
}