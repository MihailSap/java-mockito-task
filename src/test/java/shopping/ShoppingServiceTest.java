package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import product.Product;
import product.ProductDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Тестирование {@link ShoppingService}
 */
@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {

    private final ShoppingService shoppingService;

    private final ProductDao productDaoMock;

    private Customer customer;

    private Cart cart;

    private Product product;

    public ShoppingServiceTest(@Mock ProductDao productDaoMock) {
        this.productDaoMock = productDaoMock;
        shoppingService = new ShoppingServiceImpl(productDaoMock);
    }

    /**
     * Явное создание объектов перед каждым тестом
     */
    @BeforeEach
    void setUp(){
        customer = new Customer(1L, "8-888-888-88-88");
        cart = new Cart(customer);
        product = new Product("milk", 3);
    }

    /**
     * <b>Тест для метода {@link ShoppingService#getCart(Customer)}</b>
     * <p>Проверяется, что метод корректно вернет объект {@link Cart}</p>
     * В классе {@link Cart} не переопределены методы {@code equals()} и {@code hashCode()}.
     * Поэтому тест не пройдёт проверку даже при корректной работе метода
     */
    @Test
    void testGetCartCheckEquals(){
        Assertions.assertEquals(cart, shoppingService.getCart(customer));
    }

    /**
     * <b>Тест для метода {@link ShoppingService#getCart(Customer)}</b>
     * <p>Проверяется, что метод корректно вернет объект {@link Cart}</p>
     * Данный метод создает новый объект {@link Cart} с указанным {@link Customer},
     * вместо того, чтобы получать существующий.
     * Такой подход не учитывает наличие объектов {@link Product},
     * которые могут быть привязаны к данной {@link Cart}.
     * Поэтому тест не пройдёт проверку
     */
    @Test
    void testGetCartCheckProducts(){
        cart.add(new Product("milk", 3), 2);
        Assertions.assertEquals(
                cart.getProducts(), shoppingService.getCart(customer).getProducts());
    }

    /**
     * <b>Тест для метода {@link ShoppingService#getAllProducts()}</b>
     * <p>Проверяется, что метод возвращает коллекцию {@link Product}</p>
     * Ожидается, что внутри будет вызван метод {@link ProductDao#getAll()} 1 раз
     */
    @Test
    void testGetAllProducts(){
        List<Product> products = new ArrayList<>();
        Mockito.when(productDaoMock.getAll())
                .thenReturn(products);

        Assertions.assertEquals(products, shoppingService.getAllProducts());
        Mockito.verify(productDaoMock, Mockito.times(1))
                .getAll();
    }

    /**
     * <b>Тест для метода {@link ShoppingService#getProductByName(String)}</b>
     * <p>Проверяется, что метод возвращает {@link Product} с указанным названием</p>
     * Ожидается, что внутри будет вызван метод {@link ProductDao#getByName(String)} 1 раз
     */
    @Test
    void testGetProductByExistsName(){
        String productName = "name";
        Product product = new Product(productName, 1);
        Mockito.when(productDaoMock.getByName(productName))
                .thenReturn(product);
        Assertions.assertEquals(product, shoppingService.getProductByName(productName));
        Mockito.verify(productDaoMock, Mockito.times(1))
                .getByName(productName);
    }

    /**
     * <b>Тест для метода {@link ShoppingService#buy(Cart)}</b>
     * <p>Проверяется, что метод возвращает {@code true} при успешной покупке</p>
     * Ожидается, что метод {@link ProductDao#save(Product)} будет вызван 1 раз
     */
    @Test
    void testBuy() throws BuyException {
        cart.add(product, 2);
        Mockito.doNothing()
                .when(productDaoMock)
                .save(Mockito.isA(Product.class));

        Assertions.assertTrue(shoppingService.buy(cart));
        Mockito.verify(productDaoMock, Mockito.times(1))
                .save(product);
    }

    /**
     * <b>Тест для метода {@link ShoppingService#buy(Cart)}</b>
     * <p>Проверяется, что метод возвращает {@code false}
     * при попытке совершить покупку с пустой {@link Cart}</p>
     * Ожидается, что ни разу не будет вызван метод {@link ProductDao#save(Product)}
     */
    @Test
    void testWithEmptyCart() throws BuyException {
        Assertions.assertFalse(shoppingService.buy(cart));
        Mockito.verify(productDaoMock, Mockito.never())
                .save(Mockito.any(Product.class));
    }

    /**
     * <b>Тест для метода {@link ShoppingService#buy(Cart)}</b>
     * <p>Проверяется, что метод очищает объект {@link Cart} от {@link Product},
     * то есть очищает корзину, при успешной покупке.</p>
     * Тест не пройдёт проверку, так как на самом деле в методе не реализовано очищение корзины
     */
    @Test
    void testIsCartClearedAfterCorrectPurchase() throws BuyException {
        cart.add(product, 2);
        Mockito.doNothing()
                .when(productDaoMock)
                .save(Mockito.isA(Product.class));

        shoppingService.buy(cart);
        Assertions.assertTrue(cart.getProducts().isEmpty());
    }

    /**
     * <b>Тест для метода {@link ShoppingService#buy(Cart)}</b>
     * <p>Проверяется, что метод не очищает объект {@link Cart} от {@link Product},
     * то есть не очищает корзину, при неуспешной покупке.</p>
     * Тест пройдёт проверку, так как очищение корзины вообще не реализовано
     */
    @Test
    void testIsCartClearedAfterIncorrectPurchase(){
        cart.add(product, 2);
        Mockito.doThrow(new RuntimeException())
                .when(productDaoMock)
                .save(product);

        Assertions.assertThrows(RuntimeException.class, () -> shoppingService.buy(cart));
        Assertions.assertEquals(Map.of(product, 2), cart.getProducts());
    }

    /**
     * <b>Тест для метода {@link ShoppingService#buy(Cart)}</b>
     * <p>Проверяется, что метод уменьшает количество доступных {@link Product}
     * при успешной покупке продуктов из корзины.</p>
     */
    @Test
    void testIsProductCountSubtractAfterCorrectPurchase() throws BuyException {
        cart.add(product, 2);
        Mockito.doNothing()
                .when(productDaoMock)
                .save(Mockito.isA(Product.class));

        shoppingService.buy(cart);
        Assertions.assertEquals(1, product.getCount());
    }

    /**
     * <b>Тест для метода {@link ShoppingService#buy(Cart)}</b>
     * <p>Проверяется, что метод не уменьшает количество доступных {@link Product}
     * при неуспешной покупке продуктов из корзины.</p>
     * Тест не пройдёт, так как сначала выполняется уменьшение количества товаров
     * и только потом происходит работа с БД, на этапе с которой может возникнуть исключение.
     * Однако, в данной реализации этого не предусмотрено
     */
    @Test
    void testIsProductCountSubtractAfterIncorrectPurchase(){
        cart.add(product, 2);
        Mockito.doThrow(new RuntimeException())
                .when(productDaoMock)
                .save(product);

        Assertions.assertThrows(RuntimeException.class, () -> shoppingService.buy(cart));
        Assertions.assertEquals(3, product.getCount());
    }

    /**
     * <b>Тест для метода {@link ShoppingService#buy(Cart)}</b>
     * <p>Проверяется, что метод возвращает исключение при работе с корзиной,
     * в которой количество товаров равно нулю</p>
     * Тест не пройдёт, так как обработка этого случая не предусмотрена
     */
    @Test
    void testBuyZeroProductsInCart() {
        cart.add(product, 0);
        Mockito.doNothing()
                .when(productDaoMock)
                .save(Mockito.isA(Product.class));

        Assertions.assertThrows(Exception.class, () -> shoppingService.buy(cart));
    }
}