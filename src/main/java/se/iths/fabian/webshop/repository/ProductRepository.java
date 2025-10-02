package se.iths.fabian.webshop.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import se.iths.fabian.webshop.model.Clothing;
import se.iths.fabian.webshop.model.Electronics;
import se.iths.fabian.webshop.model.Furniture;
import se.iths.fabian.webshop.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class ProductRepository {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public ProductRepository() {
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.database = mongoClient.getDatabase("webshop");
        this.collection = database.getCollection("products");
        System.out.println("Connected to MongoDB!");
    }

    public void save(Product product) {
        Document doc = productToDocument(product);
        collection.insertOne(doc);
        System.out.println("Product saved to MongoDB: " + product.getTitle());
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();

        for (Document doc : collection.find()) {
            Product product = documentToProduct(doc);
            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }

    public Optional<Product> findByArticleNumber(String articleNumber) {
        Document doc = collection.find(eq("articleNumber", articleNumber)).first();

        if (doc == null) {
            return Optional.empty();
        }

        Product product = documentToProduct(doc);
        return Optional.ofNullable(product);
    }

    public boolean existsByArticleNumber(String articleNumber) {
        return collection.countDocuments(eq("articleNumber", articleNumber)) > 0;
    }

    public void saveToFile() {
        long count = collection.countDocuments();
        System.out.println("MongoDB contains " + count + " products.");
    }

    private Document productToDocument(Product product) {
        Document doc = new Document();
        doc.append("type", getProductType(product));
        doc.append("articleNumber", product.getArticleNumber());
        doc.append("title", product.getTitle());
        doc.append("price", product.getPrice());
        doc.append("description", product.getDescription());
        return doc;
    }

    private Product documentToProduct(Document doc) {
        String type = doc.getString("type");
        String articleNumber = doc.getString("articleNumber");
        String title = doc.getString("title");
        double price = doc.getDouble("price");
        String description = doc.getString("description");

        return switch (type) {
            case "electronics" -> new Electronics(articleNumber, title, price, description);
            case "furniture" -> new Furniture(articleNumber, title, price, description);
            case "clothing" -> new Clothing(articleNumber, title, price, description);
            default -> null;
        };
    }

    private String getProductType(Product product) {
        return switch (product) {
            case Electronics e -> "electronics";
            case Furniture f -> "furniture";
            case Clothing c -> "clothing";
            default -> "unknown";
        };
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }
}