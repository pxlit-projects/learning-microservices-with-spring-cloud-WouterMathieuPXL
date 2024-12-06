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

        
        String action = String.format("Created product with name %s, description %s, price %.2f, category %s",
                productResponse.getName(), productResponse.getDescription(),
                productResponse.getPrice(), productResponse.getCategory());
        log.info(action);
        auditLogService.sendAuditLog(productResponse.getId(), action, "admin");

        return productResponse;
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product with ID: {}", id);

        Product existingProduct = findProductById(id);
        Product originalProduct = Product.builder()
                .id(existingProduct.getId())
                .name(existingProduct.getName())
                .description(existingProduct.getDescription())
                .price(existingProduct.getPrice())
                .category(existingProduct.getCategory())
                .build();
        ProductResponse productResponse = saveOrUpdateProduct(existingProduct, productRequest);

        log.info("Updated product with ID: {}", id);
        //auditLogService.sendAuditLog(id, "updated", "admin");
        logUpdateAction(originalProduct, productRequest);

        return productResponse;
    }

    private ProductResponse saveOrUpdateProduct(Product product, ProductRequest productRequest) {
        String imageUrl = productRequest.getImage() != null
                ? imageService.storeImage(productRequest.getImage())
                : product.getImageUrl();

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

    private void logUpdateAction(Product existing, ProductRequest incomingRequest) {
        Map<String, List<Object>> changes = findChanges(existing, incomingRequest);

        changes.forEach((field, values) -> {
            String action = String.format("%s edited from %s to %s", field, values.get(0), values.get(1));
            log.info(action);
            auditLogService.sendAuditLog(existing.getId(), action, "admin");
        });
    }

    private Map<String, List<Object>> findChanges(Product existing, ProductRequest incomingRequest) {
        Map<String, List<Object>> changes = new HashMap<>();
log.info("findChanges");
log.info(existing.getName());
log.info(incomingRequest.getName());
        if (!Objects.equals(existing.getName(), incomingRequest.getName())) {
            log.info("name not equal");
            changes.put("name", Arrays.asList(existing.getName(), incomingRequest.getName()));
        }
        if (!Objects.equals(existing.getDescription(), incomingRequest.getDescription())) {
            changes.put("description", Arrays.asList(existing.getDescription(), incomingRequest.getDescription()));
        }
        if (existing.getPrice() != incomingRequest.getPrice()) {
            changes.put("price", Arrays.asList(existing.getPrice(), incomingRequest.getPrice()));
        }
        if (!Objects.equals(existing.getCategory(), incomingRequest.getCategory())) {
            changes.put("category", Arrays.asList(existing.getCategory(), incomingRequest.getCategory()));
        }

        // Labels and image not done

        return changes;
    }
}
