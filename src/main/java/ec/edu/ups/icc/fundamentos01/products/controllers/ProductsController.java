package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.core.dto.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/*
 * Controlador REST encargado de exponer
 * los endpoints HTTP para productos.
 *
 * El context-path global es /api.
 * Por eso aquí solamente se coloca /products.
 */
@Tag(
        name = "Productos",
        description = "Endpoints para la gestión de productos con paginación, roles y validación de ownership"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService productService;

    public ProductsController(
            ProductService productService
    ) {
        this.productService = productService;
    }

    // ============== ENDPOINTS DE CREACIÓN ==============

    /*
     * Crear producto.
     *
     * POST /api/products
     *
     * El owner ya no se recibe desde el body.
     * El owner se obtiene desde el token JWT mediante @AuthenticationPrincipal.
     */
    @Operation(
            summary = "Crear producto",
            description = "Crea un nuevo producto asociado al usuario autenticado. El propietario se obtiene desde el token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no proporcionado o inválido"
            )
    })
    @PostMapping
    public ResponseEntity<ProductResponseDto> create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {

        ProductResponseDto created =
                productService.create(
                        dto,
                        currentUser
                );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        created
                );
    }

    // ============== ENDPOINTS DE CONSULTA ==============

    /*
     * Listar todos los productos sin paginación.
     *
     * GET /api/products
     *
     * Solo ADMIN.
     */
    @Operation(
            summary = "Listar todos los productos",
            description = "Obtiene la lista completa de productos registrados. Este endpoint está permitido solo para usuarios con rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos obtenidos correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no proporcionado o inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tienes permisos para listar todos los productos"
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponseDto>> findAll() {

        List<ProductResponseDto> products =
                productService.findAll();

        return ResponseEntity.ok(
                products
        );
    }

    /*
     * Listar productos con paginación.
     *
     * GET /api/products/page?page=0&size=5
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @Operation(
            summary = "Listar productos paginados",
            description = "Obtiene productos usando paginación, tamaño de página, ordenamiento y dirección."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de productos obtenida correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no proporcionado o inválido"
            )
    })
    @GetMapping("/page")
    public ResponseEntity<Page<ProductResponseDto>> findAllPage(
            @ModelAttribute PaginationDto paginationDto
    ) {

        Page<ProductResponseDto> products =
                productService.findAllPage(
                        paginationDto
                );

        return ResponseEntity.ok(
                products
        );
    }

    /*
     * Listar productos usando Slice.
     *
     * GET /api/products/slice?page=0&size=5
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @Operation(
            summary = "Listar productos con slice",
            description = "Obtiene productos usando paginación tipo slice, útil para navegar sin calcular el total de elementos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Slice de productos obtenido correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no proporcionado o inválido"
            )
    })
    @GetMapping("/slice")
    public ResponseEntity<Slice<ProductResponseDto>> findAllSlice(
            @ModelAttribute PaginationDto paginationDto
    ) {

        Slice<ProductResponseDto> products =
                productService.findAllSlice(
                        paginationDto
                );

        return ResponseEntity.ok(
                products
        );
    }

    /*
     * Obtener producto por ID.
     *
     * GET /api/products/{id}
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @Operation(
            summary = "Buscar producto por ID",
            description = "Obtiene la información de un producto específico mediante su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no proporcionado o inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDto> findOne(
            @PathVariable("id") Long id
    ) {

        ProductResponseDto product =
                productService.findOne(
                        id
                );

        return ResponseEntity.ok(
                product
        );
    }

    /*
     * Productos de un usuario específico.
     *
     * GET /api/products/user/{userId}
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @Operation(
            summary = "Listar productos por usuario",
            description = "Obtiene todos los productos asociados a un usuario específico mediante su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos del usuario obtenidos correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no proporcionado o inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario o productos no encontrados"
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductResponseDto>> findByUserId(
            @PathVariable("userId") Long userId
    ) {

        List<ProductResponseDto> products =
                productService.findByUserId(
                        userId
                );

        return ResponseEntity.ok(
                products
        );
    }

    /*
     * Productos por categoría.
     *
     * GET /api/products/category/{categoryId}
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @Operation(
            summary = "Listar productos por categoría",
            description = "Obtiene todos los productos asociados a una categoría específica mediante su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos de la categoría obtenidos correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no proporcionado o inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría o productos no encontrados"
            )
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> findByCategoryId(
            @PathVariable("categoryId") Long categoryId
    ) {

        List<ProductResponseDto> products =
                productService.findByCategoryId(
                        categoryId
                );

        return ResponseEntity.ok(
                products
        );
    }

    // ============== ENDPOINTS DE MODIFICACIÓN ==============

    /*
     * Actualizar producto completo.
     *
     * PUT /api/products/{id}
     *
     * La validación de ownership se hace en el servicio.
     */
    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza completamente un producto. Solo el dueño del producto o un administrador puede modificarlo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no proporcionado o inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tienes permisos para modificar este producto"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {

        ProductResponseDto updated =
                productService.update(
                        id,
                        dto,
                        currentUser
                );

        return ResponseEntity.ok(
                updated
        );
    }

    /*
     * Actualizar producto parcialmente.
     *
     * PATCH /api/products/{id}
     *
     * La validación de ownership se hace en el servicio.
     */
    @Operation(
            summary = "Actualizar parcialmente producto",
            description = "Actualiza parcialmente un producto. Solo el dueño del producto o un administrador puede modificarlo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado parcialmente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no proporcionado o inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tienes permisos para modificar este producto"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @PatchMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDto> partialUpdate(
            @PathVariable("id") Long id,
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {

        ProductResponseDto updated =
                productService.partialUpdate(
                        id,
                        dto,
                        currentUser
                );

        return ResponseEntity.ok(
                updated
        );
    }

    /*
     * Eliminar producto.
     *
     * DELETE /api/products/{id}
     *
     * La validación de ownership se hace en el servicio.
     */
    @Operation(
            summary = "Eliminar producto",
            description = "Elimina lógicamente un producto. Solo el dueño del producto o un administrador puede eliminarlo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Producto eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no proporcionado o inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tienes permisos para eliminar este producto"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {

        productService.delete(
                id,
                currentUser
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}