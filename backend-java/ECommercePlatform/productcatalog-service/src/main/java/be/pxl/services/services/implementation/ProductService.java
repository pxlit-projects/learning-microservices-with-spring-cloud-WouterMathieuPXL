package be.pxl.services.services.implementation;

import be.pxl.services.audit.AuditLogService;
import be.pxl.services.domain.Category;
import be.pxl.services.domain.Label;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductRequest;
import be.pxl.services.domain.dto.ProductResponse;
import be.pxl.services.exceptions.ResourceNotFoundException;
import be.pxl.services.repository.LabelRepository;
import be.pxl.services.repository.ProductRepository;
import be.pxl.services.services.IImageService;
import be.pxl.services.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final LabelRepository labelRepository;
    private final IImageService imageService;
    private final AuditLogService auditLogService;

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll()
                .stream().map(this::mapToProductResponse).toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        return mapToProductResponse(findProductById(id));
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating product with name: {}", productRequest.getName());
        ProductResponse productResponse = saveOrUpdateProduct(new Product(), productRequest);

        log.info("Created product with name: {}", productRequest.getName());
        auditLogService.sendAuditLog(productResponse.getId(), "created", "admin");

        return productResponse;
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product with ID: {}", id);

        Product existingProduct = findProductById(id);
        ProductResponse productResponse = saveOrUpdateProduct(existingProduct, productRequest);

        log.info("Updated product with ID: {}", id);
        auditLogService.sendAuditLog(id, "updated", "admin");

        return productResponse;
    }

    private ProductResponse saveOrUpdateProduct(Product product, ProductRequest productRequest) {
        String imageUrl = imageService.storeImage(productRequest.getImage());

        Product updatedProduct = Product.builder()
                .id(product.getId())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .imageUrl(imageUrl)
                .build();

        List<Label> labels = labelRepository.findAllById(productRequest.getLabelIds());
        updatedProduct.setLabels(new HashSet<>(labels));

        productRepository.save(updatedProduct);

        return mapToProductResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        findProductById(id); // This will throw an exception if the product is not found
        productRepository.deleteById(id);

        auditLogService.sendAuditLog(id, "deleted", "admin");

        log.info("Deleted product with ID: {}", id);
    }

    @Override
    public Map<String, String> getAllCategories() {
        log.info("Fetching all categories");
        return Arrays.stream(Category.values())
                .collect(Collectors.toMap(Enum::name, Category::getDisplayName));
    }

//    @Override
//    public void addLabel(Long productId, Long labelId) {
//        log.info("Adding label with ID: {} to product with ID: {}", labelId, productId);
//        Product product = findProductById(productId);
//        Label label = labelRepository.findById(labelId)
//                .orElseThrow(() -> {
//                    log.error("Label with ID: {} not found", labelId);
//                    return new ResourceNotFoundException("Product not found");
//                });
//
//        product.getLabels().add(label);
//        log.info("Added label with ID: {} to product with ID: {}", labelId, productId);
//        productRepository.save(product);
//    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product with ID: {} not found", id);
                    return new ResourceNotFoundException("Product not found");
                });
    }

    ProductResponse mapToProductResponse(Product product) {
//        if (product.getLabels() == null) {
//            product.setLabels(new HashSet<>());
//        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .labels(new ArrayList<>(product.getLabels()))
                .imageUrl(product.getImageUrl())
                .build();
    }
    
    
}
