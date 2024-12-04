package be.pxl.services.controller;

import be.pxl.services.domain.Product;
import be.pxl.services.domain.dto.ProductRequest;
import be.pxl.services.domain.dto.ProductResponse;
import be.pxl.services.services.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productcatalog")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final IProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        log.info("Received request to fetch all products");
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable Long id) {
        log.info("Received request to fetch product with ID: {}", id);
        return productService.getProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody @Valid ProductRequest productRequest) {
        log.info("Received request to create a new product");
        return productService.createProduct(productRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ProductResponse updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequest productRequest) {
        log.info("Received request to update product with ID: {}", id);
        return productService.updateProduct(id, productRequest);
    }

//    @PutMapping("/{productId}/label/{labelId}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void addLabelToProduct(@PathVariable Long productId, @PathVariable Long labelId) {
//        log.info("Received request to add label with ID: {} to product with ID: {}", labelId, productId);
//        productService.addLabel(productId, labelId);
//    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id){
        log.info("Received request to delete product with ID: {}", id);
        productService.deleteProduct(id);
    }
}
