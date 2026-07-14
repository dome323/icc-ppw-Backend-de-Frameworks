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
import jakarta.validation.Valid;

/*
 * Controlador REST encargado de exponer
 * los endpoints HTTP para productos.
 *
 * El context-path global es /api.
 * Por eso aquí solamente se coloca /products.
 */
@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    // ============== ENDPOINTS DE CREACIÓN ==============

    /**
     * Crear producto
     * POST /api/products
     *
     * El owner ya no se recibe desde el body.
     * El owner se obtiene desde el token JWT mediante @AuthenticationPrincipal.
     */
    @PostMapping
    public ResponseEntity<ProductResponseDto> create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        ProductResponseDto created =
                productService.create(dto, currentUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // ============== ENDPOINTS DE CONSULTA ==============

    /**
     * Listar TODOS los productos sin paginación - SOLO ADMIN
     * GET /api/products
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponseDto>> findAll() {
        List<ProductResponseDto> products = productService.findAll();

        return ResponseEntity.ok(products);
    }

    /**
     * Listar productos con paginación
     * GET /api/products/page?page=0&size=5
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @GetMapping("/page")
    public ResponseEntity<Page<ProductResponseDto>> findAllPage(
            @ModelAttribute PaginationDto paginationDto
    ) {
        Page<ProductResponseDto> products =
                productService.findAllPage(paginationDto);

        return ResponseEntity.ok(products);
    }

    /**
     * Listar productos usando Slice
     * GET /api/products/slice?page=0&size=5
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @GetMapping("/slice")
    public ResponseEntity<Slice<ProductResponseDto>> findAllSlice(
            @ModelAttribute PaginationDto paginationDto
    ) {
        Slice<ProductResponseDto> products =
                productService.findAllSlice(paginationDto);

        return ResponseEntity.ok(products);
    }

    /**
     * Obtener producto por ID
     * GET /api/products/{id}
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDto> findOne(
            @PathVariable("id") Long id
    ) {
        ProductResponseDto product = productService.findOne(id);

        return ResponseEntity.ok(product);
    }

    /**
     * Productos de un usuario específico
     * GET /api/products/user/{userId}
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductResponseDto>> findByUserId(
            @PathVariable("userId") Long userId
    ) {
        List<ProductResponseDto> products =
                productService.findByUserId(userId);

        return ResponseEntity.ok(products);
    }

    /**
     * Productos por categoría
     * GET /api/products/category/{categoryId}
     *
     * Cualquier usuario autenticado puede acceder.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> findByCategoryId(
            @PathVariable("categoryId") Long categoryId
    ) {
        List<ProductResponseDto> products =
                productService.findByCategoryId(categoryId);

        return ResponseEntity.ok(products);
    }

    // ============== ENDPOINTS DE MODIFICACIÓN ==============

    /**
     * Actualizar producto completo
     * PUT /api/products/{id}
     *
     * No lleva @PreAuthorize aquí.
     * La validación de ownership se hace en el servicio.
     */
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        ProductResponseDto updated =
                productService.update(id, dto, currentUser);

        return ResponseEntity.ok(updated);
    }

    /**
     * Actualizar producto parcialmente
     * PATCH /api/products/{id}
     *
     * No lleva @PreAuthorize aquí.
     * La validación de ownership se hace en el servicio.
     */
    @PatchMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponseDto> partialUpdate(
            @PathVariable("id") Long id,
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        ProductResponseDto updated =
                productService.partialUpdate(id, dto, currentUser);

        return ResponseEntity.ok(updated);
    }

    /**
     * Eliminar producto
     * DELETE /api/products/{id}
     *
     * No lleva @PreAuthorize aquí.
     * La validación de ownership se hace en el servicio.
     */
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        productService.delete(id, currentUser);

        return ResponseEntity.noContent().build();
    }
}