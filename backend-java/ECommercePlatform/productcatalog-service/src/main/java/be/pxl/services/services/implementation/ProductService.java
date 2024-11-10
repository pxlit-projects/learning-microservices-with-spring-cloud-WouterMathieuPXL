package be.pxl.services.services.implementation;

import be.pxl.services.domain.Label;
import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductRequest;
import be.pxl.services.domain.dto.ProductResponse;
import be.pxl.services.exceptions.ResourceNotFoundException;
import be.pxl.services.repository.LabelRepository;
import be.pxl.services.repository.ProductRepository;
import be.pxl.services.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final LabelRepository labelRepository;


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
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .build();

        productRepository.save(product);
        log.info("Created product with name: {}", productRequest.getName());
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product with ID: {}", id);

        Product product = findProductById(id);
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());

        productRepository.save(product);
        log.info("Updated product with ID: {}", id);

        return mapToProductResponse(product);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        findProductById(id); // This will throw an exception if the product is not found
        productRepository.deleteById(id);
        log.info("Deleted product with ID: {}", id);
    }

    @Override
    public void addLabel(Long productId, Long labelId) {
        log.info("Adding label with ID: {} to product with ID: {}", labelId, productId);
        Product product = findProductById(productId);
        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> {
                    log.error("Label with ID: {} not found", labelId);
                    return new ResourceNotFoundException("Product not found");
                });

        product.getLabels().add(label);
        log.info("Added label with ID: {} to product with ID: {}", labelId, productId);
        productRepository.save(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product with ID: {} not found", id);
                    return new ResourceNotFoundException("Product not found");
                });
    }

    ProductResponse mapToProductResponse(Product product) {
        if (product.getLabels() == null) {
            product.setLabels(new HashSet<>());
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .labels(new ArrayList<>(product.getLabels()))
                .build();
    }
}
