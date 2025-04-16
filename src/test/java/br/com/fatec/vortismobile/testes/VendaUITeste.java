package br.com.fatec.vortismobile.testes;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class VendaUITeste {

    private static WebDriver driver;

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testLoginCliente() throws InterruptedException {
        driver.get("http://localhost:8080/clientes_login");

        Thread.sleep(2000);
        WebElement emailInput = driver.findElement(By.id("email"));
        WebElement senhaInput = driver.findElement(By.id("senha"));
        WebElement btnLogin = driver.findElement(By.id("btnLogin"));

        emailInput.sendKeys("pedro@outlook.com");
        senhaInput.sendKeys("Ph#15915915");
        btnLogin.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement menuUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuUsuario")));

        Assertions.assertTrue(menuUsuario.getText().contains("Olá,"));

        testAdicionarProdutoCarrinho();
        testFinalizarPedido();
    }

    @Test
    public void testAdicionarProdutoCarrinho() {
        driver.get("http://localhost:8080/catalogo");

        // Aguarda o carregamento do catálogo
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement botaoAdicionar = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Comprar')]"))
        );

        botaoAdicionar.click();

        // Vai para o carrinho
        driver.get("http://localhost:8080/carrinho");

        // Verifica se o produto aparece no carrinho
        WebElement tabela = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("tbody")));
        List<WebElement> linhas = tabela.findElements(By.tagName("tr"));

        Assertions.assertFalse(linhas.isEmpty(), "Carrinho deve conter pelo menos um item");
    }

    @Test
    public void testFinalizarPedido() throws InterruptedException {
        driver.get("http://localhost:8080/carrinho");

        Thread.sleep(2000); // Espera a página carregar

        // Seleciona endereço de entrega
        WebElement enderecoSelect = driver.findElement(By.id("enderecoEntrega"));
        Select selectEndereco = new Select(enderecoSelect);
        selectEndereco.selectByIndex(1); // Seleciona o primeiro endereço disponível (altere se necessário)

        Thread.sleep(1000);

        // Seleciona cartão
        WebElement cartaoSelect = driver.findElement(By.id("cartao1"));
        Select selectCartao = new Select(cartaoSelect);
        selectCartao.selectByIndex(1); // Seleciona o primeiro cartão salvo

        Thread.sleep(1000);

        // Clica no botão de finalizar
        WebElement btnFinalizar = driver.findElement(By.id("btnFinalizarPedido"));
        btnFinalizar.click();

        Thread.sleep(3000); // Aguarda redirecionamento ou modal do SweetAlert

        // Verifica se foi para o catálogo novamente ou mostra alerta de sucesso
        Assertions.assertTrue(driver.getCurrentUrl().contains("catalogo") ||
                driver.getPageSource().contains("Pedido Confirmado"));
    }

}