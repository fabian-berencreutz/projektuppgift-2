package se.iths.fabian.webshop;

import se.iths.fabian.webshop.controller.ProductController;
import se.iths.fabian.webshop.repository.ProductRepository;
import se.iths.fabian.webshop.service.ProductService;
import se.iths.fabian.webshop.ui.ConsoleUi;
import se.iths.fabian.webshop.ui.JOptionMenu;
import se.iths.fabian.webshop.ui.UI;

public class Main {
    public static void main(String[] args) {
        UI consoleUi = new ConsoleUi();
        UI jOptionMenu = new JOptionMenu();

        ProductRepository repository = new ProductRepository();
        ProductService service = new ProductService(repository);
        ProductController controller = new ProductController(jOptionMenu, service);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            repository.close();
        }));

        controller.start();

        repository.close();
    }
}