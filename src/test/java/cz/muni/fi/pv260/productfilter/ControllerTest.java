package cz.muni.fi.pv260.productfilter;

import static org.mockito.Mockito.*;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ControllerTest {

    @Test
    public void testControllerSendsFilteredProductToOutput() throws ObtainFailedException {
        Product product1 = new Product(1, "A", Color.RED, BigDecimal.valueOf(100));
        Product product2 = new Product(2, "B", Color.BLACK, BigDecimal.valueOf(120));
        Product product3 = new Product(3, "C", Color.GREEN, BigDecimal.valueOf(80));
        ArrayList<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        PriceLessThanFilter filter = new PriceLessThanFilter(BigDecimal.valueOf(50));
        List<Product> expectedProducts = new ArrayList<>();

        Input in = mock(Input.class);
        Output out = mock(Output.class);
        Logger log = mock(Logger.class);
        when(in.obtainProducts()).thenReturn(products);

        ArgumentCaptor<Collection<Product>> argumentCaptor = new ArgumentCaptor<>();

        Controller controller = new Controller(in, out, log);
        controller.select(filter);

        verify(out).postSelectedProducts(argumentCaptor.capture());
        Collection<Product> outputProducts = argumentCaptor.getValue();

        assertEquals(outputProducts, expectedProducts);
    }


    @Test
    public void testSuccessMessageFormatLogging() throws ObtainFailedException{
        Product product1 = new Product(1, "A", Color.RED, BigDecimal.valueOf(100));
        Product product2 = new Product(2, "B", Color.BLACK, BigDecimal.valueOf(120));
        Product product3 = new Product(3, "C", Color.GREEN, BigDecimal.valueOf(80));
        ArrayList<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        PriceLessThanFilter filter = new PriceLessThanFilter(BigDecimal.valueOf(200));

        Input in = mock(Input.class);
        Output out = mock(Output.class);
        Logger log = mock(Logger.class);
        when(in.obtainProducts()).thenReturn(products);

        ArgumentCaptor<String> argumentCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);

        Controller controller = new Controller(in, out, log);
        controller.select(filter);

        verify(log).log(argumentCaptor1.capture(), argumentCaptor2.capture());
        String message = argumentCaptor2.getValue();
        // The format in the select method in the Controller is missing the last dot, but I thought that it just a typo
        // Otherwise this test should fail, since the formats do not exactly match
        assertEquals(message, "Successfully selected 3 out of 3 available products.");
    }


    @Test
    public void testControllerLogsExceptionAfterItOccurs() throws ObtainFailedException {
        Input in = mock(Input.class);
        Output out = mock(Output.class);
        Logger log = mock(Logger.class);
        when(in.obtainProducts()).thenThrow(new ObtainFailedException());
        PriceLessThanFilter filter = new PriceLessThanFilter(BigDecimal.valueOf(100));

        ArgumentCaptor<String> argumentCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);

        Controller controller = new Controller(in, out, log);
        controller.select(filter);

        verify(log).log(argumentCaptor1.capture(), argumentCaptor2.capture());
        String message = argumentCaptor2.getValue();
        assertEquals(message, "Filter procedure failed with exception: cz.muni.fi.pv260.productfilter." +
                "ObtainFailedException");

    }
    

    @Test(expected = org.mockito.exceptions.base.MockitoException.class)
    public void testNothingPassedToOutputWhenExceptionOccurs() throws ObtainFailedException {
        Input in = mock(Input.class);
        Output out = mock(Output.class);
        Logger log = mock(Logger.class);
        when(in.obtainProducts()).thenThrow(new ObtainFailedException());
        PriceLessThanFilter filter = new PriceLessThanFilter(BigDecimal.valueOf(100));

        ArgumentCaptor<Collection<Product>> argumentCaptor = new ArgumentCaptor<>();

        Controller controller = new Controller(in, out, log);
        controller.select(filter);

        verify(out, times(0)).postSelectedProducts(argumentCaptor.capture());
        argumentCaptor.getValue();

    }
}
