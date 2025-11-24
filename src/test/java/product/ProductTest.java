package product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <b>Тестирование класса {@link Product}</b>
 * <p>В этом классе реализованы только те тесты,
 * которые демонстрируют некорректную работу методов {@link Product}.</p>
 */
class ProductTest {

    /**
     * <b>Тестирование метода {@link Product#subtractCount(int)}</b>
     * <p>Предполагается, что при попытке сделать количество доступных товаров отрицательным,
     * будет выброшено исключение</p>
     * Тест не пройдёт, так как в данной реализации этот случай не обрабатывается
     */
    @Test
    void testSubtractProductCountToNegative(){
        Product product = new Product("milk", 2);
        Exception exception = Assertions.assertThrows(Exception.class, () -> product.subtractCount(3));
        Assertions.assertTrue(exception.getMessage().contains(
                "Количество доступных товаров не должно быть отрицательным"));
    }
}