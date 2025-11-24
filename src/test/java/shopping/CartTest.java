package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import product.Product;

/**
 * <b>Тестирование класса {@link Cart}</b>
 */
class CartTest {

    private Customer customer;

    private Cart cart;

    private Product product;

    /**
     * Явное создание объектов перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        customer = new Customer(1L, "8-888-888-88-88");
        cart = new Cart(customer);
        product = new Product("milk", 5);
    }

    /**
     * <b>Тест для метода {@link Cart#add(Product, int)}</b>
     * <p>Проверяется, что добавление всего доступного товара в корзину работает корректно</p>
     * Тест не проходит, потому что валидация количества продуктов в корзине реализована некорректно.
     * Она запрещает добавлять весь доступный товар в корзину
     */
    @Test
    void testAddToCartAllProducts(){
        cart.add(product, 5);
        Assertions.assertEquals(5, cart.getProducts().get(product));
    }

    /**
     * <b>Тест для метода {@link Cart#add(Product, int)}</b>
     * <p>Проверяется, что добавление товаров в корзину работает корректно</p>
     * Тест не проходит, потому что текущая реализация перезаписывает количество товара
     * при его повторном добавлении, а не увеличивает
     */
    @Test
    void testBuyAddTwoTimesProductInCart() {
        cart.add(product, 2);
        cart.add(product, 2);
        Assertions.assertEquals(4, cart.getProducts().get(product));
    }

    /**
     * <b>Тест для метода {@link Cart#add(Product, int)}</b>
     * <p>Проверяется, что нельзя добавить в корзину больше товара, чем задано</p>
     * Тест проходит, так как для этого случая валидация при добавлении в корзину работает корректно
     */
    @Test
    void testAddMoreProductsThanExists(){
        Product product = new Product("milk", 1);
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> cart.add(product, 2)
        );

        Assertions.assertTrue(exception.getMessage().contains(
                "Невозможно добавить товар 'milk' в корзину, т.к. нет необходимого количества товаров"));
    }

    /**
     * <b>Тест для метода {@link Cart#add(Product, int)}</b>
     * <p>Проверяется, что нельзя добавить в корзину 0 и менее товаров</p>
     */
    @Test
    void testAddIncorrectProductsCountInCart(){
        Exception exAddZeroProducts = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> cart.add(product, 0)
        );
        Assertions.assertEquals(
                "Количество товаров, добавляемых в корзину, должно быть больше нуля", exAddZeroProducts.getMessage());
        Exception exAddNegativeProductsCount = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> cart.add(product, -1)
        );
        Assertions.assertEquals(
                "Количество товаров, добавляемых в корзину, должно быть больше нуля", exAddNegativeProductsCount.getMessage());

    }
}