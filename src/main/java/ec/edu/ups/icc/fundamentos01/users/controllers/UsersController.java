package ec.edu.ups.icc.fundamentos01.users.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService service;

    /*
     * Inyección de dependencias por constructor.
     */
    public UsersController(UserService service) {
        this.service = service;
    }

    /*
     * GET /api/users
     */
    @GetMapping
    public List<UserResponseDto> findAll() {
        return service.findAll();
    }

    /*
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public UserResponseDto findOne(
            @PathVariable Long id) {

        return service.findOne(id);
    }

    /*
     * POST /api/users
     */
    @PostMapping
    public UserResponseDto create(
            @Valid @RequestBody CreateUserDto dto) {

        return service.create(dto);
    }

    /*
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public UserResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDto dto) {

        return service.update(id, dto);
    }

    /*
     * PATCH /api/users/{id}
     *
     * Permite modificar nombre, email o contraseña.
     */
    @PatchMapping("/{id}")
    public UserResponseDto partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody PartialUpdateUserDto dto) {

        return service.partialUpdate(id, dto);
    }

    /*
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
