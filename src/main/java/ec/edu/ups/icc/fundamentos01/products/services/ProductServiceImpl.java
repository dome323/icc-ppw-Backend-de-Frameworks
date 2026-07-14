package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.core.dto.PaginationDto;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByCategoryDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByUserDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import ec.edu.ups.icc.fundamentos01.users.entity.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /*
     * Lista todos los productos activos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findAll() {

        return productRepository
                .findByDeletedFalse()
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    /*
     * Busca un producto activo por ID.
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto findOne(Long id) {

        ProductEntity entity = findActiveEntity(id);

        return ProductMapper.toResponse(
                ProductMapper.toModelFromEntity(entity)
        );
    }

    /*
     * Crea un producto usando como owner
     * al usuario autenticado.
     *
     * El owner ya no se toma desde el body.
     * Se toma desde el token JWT.
     */
    @Override
    public ProductResponseDto create(
            CreateProductDto dto,
            UserDetailsImpl currentUser
    ) {

        UserEntity owner = findCurrentUserEntity(currentUser);

        Set<CategoryEntity> categories =
                validateAndGetCategories(dto.getCategoryIds());

        if (productRepository
                .findByNameIgnoreCaseAndDeletedFalse(dto.getName())
                .isPresent()) {

            throw new ConflictException(
                    "Product name already registered"
            );
        }

        ProductEntity entity = new ProductEntity();

        entity.setName(dto.getName().trim());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setOwner(owner);
        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);

        return ProductMapper.toResponse(
                ProductMapper.toModelFromEntity(savedEntity)
        );
    }

    /*
     * Actualización completa.
     *
     * Primero se valida que el usuario autenticado
     * sea dueño del producto o tenga ROLE_ADMIN.
     */
    @Override
    public ProductResponseDto update(
            Long id,
            UpdateProductDto dto,
            UserDetailsImpl currentUser
    ) {

        ProductEntity entity = findActiveEntity(id);

        validateOwnership(entity, currentUser);

        Set<CategoryEntity> categories =
                validateAndGetCategories(dto.getCategoryIds());

        productRepository
                .findByNameIgnoreCaseAndDeletedFalse(dto.getName())
                .filter(product -> !product.getId().equals(id))
                .ifPresent(product -> {
                    throw new ConflictException(
                            "Product name already registered"
                    );
                });

        entity.setName(dto.getName().trim());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());

        /*
         * Reemplaza las categorías anteriores.
         */
        entity.getCategories().clear();
        entity.getCategories().addAll(categories);

        ProductEntity savedEntity = productRepository.save(entity);

        return ProductMapper.toResponse(
                ProductMapper.toModelFromEntity(savedEntity)
        );
    }

    /*
     * Actualización parcial.
     *
     * También valida ownership antes de modificar.
     */
    @Override
    public ProductResponseDto partialUpdate(
            Long id,
            PartialUpdateProductDto dto,
            UserDetailsImpl currentUser
    ) {

        ProductEntity entity = findActiveEntity(id);

        validateOwnership(entity, currentUser);

        if (dto.getName() != null) {

            productRepository
                    .findByNameIgnoreCaseAndDeletedFalse(dto.getName())
                    .filter(product -> !product.getId().equals(id))
                    .ifPresent(product -> {
                        throw new ConflictException(
                                "Product name already registered"
                        );
                    });

            entity.setName(dto.getName().trim());
        }

        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }

        if (dto.getStock() != null) {
            entity.setStock(dto.getStock());
        }

        /*
         * Si se envían categoryIds,
         * reemplaza todas las categorías.
         */
        if (dto.getCategoryIds() != null) {

            Set<CategoryEntity> categories =
                    validateAndGetCategories(dto.getCategoryIds());

            entity.getCategories().clear();
            entity.getCategories().addAll(categories);
        }

        ProductEntity savedEntity = productRepository.save(entity);

        return ProductMapper.toResponse(
                ProductMapper.toModelFromEntity(savedEntity)
        );
    }

    /*
     * Eliminación lógica.
     *
     * Solo el dueño o un ADMIN puede eliminar.
     */
    @Override
    public void delete(
            Long id,
            UserDetailsImpl currentUser
    ) {

        ProductEntity entity = findActiveEntity(id);

        validateOwnership(entity, currentUser);

        entity.setDeleted(true);

        productRepository.save(entity);
    }

    /*
     * Endpoint técnico:
     *
     * GET /api/products/user/{userId}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findByUserId(Long userId) {

        if (!userRepository.existsByIdAndDeletedFalse(userId)) {
            throw new NotFoundException("User not found");
        }

        return productRepository
                .findByOwner_IdAndDeletedFalse(userId)
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    /*
     * Endpoint técnico:
     *
     * GET /api/products/category/{categoryId}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findByCategoryId(Long categoryId) {

        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        return productRepository
                .findDistinctByCategories_IdAndDeletedFalse(categoryId)
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    /*
     * Productos desde el contexto del usuario.
     *
     * GET /api/users/{id}/products
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findByUserIdWithFilters(
            Long userId,
            ProductFilterByUserDto filters
    ) {

        if (!userRepository.existsByIdAndDeletedFalse(userId)) {
            throw new NotFoundException("User not found");
        }

        ProductFilterByUserDto safeFilters =
                filters != null ? filters : new ProductFilterByUserDto();

        validateUserFilters(safeFilters);

        String normalizedName = normalizeName(safeFilters.getName());

        return productRepository
                .findByOwnerIdWithFilters(
                        userId,
                        normalizedName,
                        safeFilters.getMinPrice(),
                        safeFilters.getMaxPrice(),
                        safeFilters.getCategoryId()
                )
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    /*
     * Productos desde el contexto de categoría.
     *
     * GET /api/categories/{id}/products
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findByCategoryIdWithFilters(
            Long categoryId,
            ProductFilterByCategoryDto filters
    ) {

        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        ProductFilterByCategoryDto safeFilters =
                filters != null ? filters : new ProductFilterByCategoryDto();

        validateCategoryFilters(safeFilters);

        String normalizedName = normalizeName(safeFilters.getName());

        return productRepository
                .findByCategoryIdWithFilters(
                        categoryId,
                        normalizedName,
                        safeFilters.getMinPrice(),
                        safeFilters.getMaxPrice(),
                        safeFilters.getUserId()
                )
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    /*
     * Retorna productos activos usando Page.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAllPage(PaginationDto pagination) {

        Pageable pageable = createPageable(pagination);

        return productRepository.findActivePage(pageable)
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse);
    }

    /*
     * Retorna productos activos usando Slice.
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findAllSlice(PaginationDto pagination) {

        Pageable pageable = createPageable(pagination);

        return productRepository.findActiveSlice(pageable)
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse);
    }

    /*
     * Retorna productos activos de una categoría usando Page.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findByCategoryIdWithFiltersPage(
            Long categoryId,
            ProductFilterByCategoryDto filters,
            PaginationDto pagination
    ) {

        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        ProductFilterByCategoryDto safeFilters =
                filters != null ? filters : new ProductFilterByCategoryDto();

        validateCategoryFilters(safeFilters);

        String normalizedName = normalizeName(safeFilters.getName());

        Pageable pageable = createPageable(pagination);

        return productRepository.findByCategoryIdWithFiltersPage(
                        categoryId,
                        normalizedName,
                        safeFilters.getMinPrice(),
                        safeFilters.getMaxPrice(),
                        safeFilters.getUserId(),
                        pageable
                )
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse);
    }

    /*
     * Retorna productos activos de una categoría usando Slice.
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findByCategoryIdWithFiltersSlice(
            Long categoryId,
            ProductFilterByCategoryDto filters,
            PaginationDto pagination
    ) {

        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        ProductFilterByCategoryDto safeFilters =
                filters != null ? filters : new ProductFilterByCategoryDto();

        validateCategoryFilters(safeFilters);

        String normalizedName = normalizeName(safeFilters.getName());

        Pageable pageable = createPageable(pagination);

        return productRepository.findByCategoryIdWithFiltersSlice(
                        categoryId,
                        normalizedName,
                        safeFilters.getMinPrice(),
                        safeFilters.getMaxPrice(),
                        safeFilters.getUserId(),
                        pageable
                )
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse);
    }

    /*
     * Valida filtros recibidos desde:
     *
     * /users/{id}/products
     */
    private void validateUserFilters(ProductFilterByUserDto filters) {

        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException(
                    "El precio máximo debe ser mayor o igual al precio mínimo"
            );
        }

        if (filters.getCategoryId() != null
                && !categoryRepository.existsByIdAndDeletedFalse(
                        filters.getCategoryId()
                )) {

            throw new NotFoundException("Category not found");
        }
    }

    /*
     * Valida filtros recibidos desde:
     *
     * /categories/{id}/products
     */
    private void validateCategoryFilters(
            ProductFilterByCategoryDto filters
    ) {

        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException(
                    "El precio máximo debe ser mayor o igual al precio mínimo"
            );
        }

        if (filters.getUserId() != null
                && !userRepository.existsByIdAndDeletedFalse(
                        filters.getUserId()
                )) {

            throw new NotFoundException("User not found");
        }
    }

    /*
     * Valida todas las categorías enviadas
     * para crear o actualizar un producto.
     */
    private Set<CategoryEntity> validateAndGetCategories(
            Set<Long> categoryIds
    ) {

        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new BadRequestException(
                    "Debe seleccionar al menos una categoría"
            );
        }

        Set<CategoryEntity> categories = new HashSet<>();

        for (Long categoryId : categoryIds) {

            if (categoryId == null || categoryId < 1) {
                throw new BadRequestException(
                        "Los IDs de categorías deben ser mayores a 0"
                );
            }

            CategoryEntity category = categoryRepository
                    .findById(categoryId)
                    .orElseThrow(() ->
                            new NotFoundException("Category not found")
                    );

            if (category.isDeleted()) {
                throw new NotFoundException("Category not found");
            }

            categories.add(category);
        }

        return categories;
    }

    /*
     * Obtiene el usuario autenticado como entidad JPA.
     *
     * currentUser viene desde el token JWT.
     */
    private UserEntity findCurrentUserEntity(
            UserDetailsImpl currentUser
    ) {

        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        return userRepository
                .findByIdAndDeletedFalse(currentUser.getId())
                .orElseThrow(() ->
                        new AccessDeniedException("Usuario no autorizado")
                );
    }

    /*
     * Valida si el usuario autenticado puede modificar
     * o eliminar el producto.
     *
     * ROLE_ADMIN puede modificar cualquier producto.
     * ROLE_USER solo puede modificar productos propios.
     */
    private void validateOwnership(
            ProductEntity product,
            UserDetailsImpl currentUser
    ) {

        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        if (hasRole(currentUser, "ROLE_ADMIN")) {
            return;
        }

        if (product.getOwner() == null
                || product.getOwner().getId() == null) {

            throw new AccessDeniedException(
                    "El producto no tiene propietario válido"
            );
        }

        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "No puedes modificar productos ajenos"
            );
        }
    }

    /*
     * Verifica si el usuario tiene un rol específico.
     */
    private boolean hasRole(
            UserDetailsImpl user,
            String role
    ) {

        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }

    /*
     * Convierte un texto vacío en null.
     */
    private String normalizeName(String name) {

        if (name == null || name.isBlank()) {
            return null;
        }

        return name.trim();
    }

    /*
     * Busca un producto activo por ID.
     */
    private ProductEntity findActiveEntity(Long id) {

        return productRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new NotFoundException("Product not found")
                );
    }

    /*
     * Construye el objeto Pageable validando:
     * página, tamaño, campo de ordenamiento y dirección.
     */
    private Pageable createPageable(PaginationDto pagination) {

        String sortBy = normalizeSortBy(pagination.getSortBy());

        Sort.Direction direction = normalizeDirection(
                pagination.getDirection()
        );

        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                sort
        );
    }

    /*
     * Valida que el campo de ordenamiento exista y esté permitido.
     */
    private String normalizeSortBy(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "id";
        }

        Set<String> allowedFields = Set.of(
                "id",
                "name",
                "price",
                "stock",
                "createdAt",
                "updatedAt"
        );

        if (!allowedFields.contains(sortBy)) {
            throw new BadRequestException(
                    "Campo de ordenamiento no permitido: " + sortBy
            );
        }

        return sortBy;
    }

    /*
     * Convierte la dirección recibida por query param
     * en Sort.Direction.
     */
    private Sort.Direction normalizeDirection(String direction) {

        if (direction == null || direction.isBlank()) {
            return Sort.Direction.ASC;
        }

        if (direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }

        if (direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }

        throw new BadRequestException(
                "Dirección de ordenamiento no válida: " + direction
        );
    }
}