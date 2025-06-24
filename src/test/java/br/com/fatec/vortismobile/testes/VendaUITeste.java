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
    public void testAdicionarProdutosAoCarrinho() throws InterruptedException {
        driver.get("http://localhost:8080/catalogo");
        Thread.sleep(1000);

        // Produto 16
        driver.get("http://localhost:8080/produto.html?id=16");
        Thread.sleep(1000);
        WebElement btnAdicionar1 = driver.findElement(By.cssSelector("button[onclick='adicionarAoCarrinho(16)']"));
        btnAdicionar1.click();
        Thread.sleep(1000);
        driver.switchTo().alert().accept();
        Thread.sleep(500);

        driver.get("http://localhost:8080/catalogo");
        Thread.sleep(500);

        // Produto 15
        driver.get("http://localhost:8080/produto.html?id=15");
        Thread.sleep(1000);
        WebElement btnAdicionar2 = driver.findElement(By.cssSelector("button[onclick='adicionarAoCarrinho(15)']"));
        btnAdicionar2.click();
        Thread.sleep(1000);
        driver.switchTo().alert().accept();
        Thread.sleep(500);

        driver.get("http://localhost:8080/catalogo");
        Thread.sleep(500);

        realizarLogin();
        testCarrinho();
    }

    public void realizarLogin() throws InterruptedException {
        driver.get("http://localhost:8080/clientes_login");
        Thread.sleep(500);

        driver.findElement(By.id("email")).sendKeys("pedro@outlook.com");
        driver.findElement(By.id("senha")).sendKeys("Ph#15915915");
        Thread.sleep(500);

        driver.findElement(By.xpath("//button[contains(text(),'Entrar')]")).click();
        Thread.sleep(2000);
    }

    public void testCarrinho() throws InterruptedException {
        driver.get("http://localhost:8080/carrinho");
        Thread.sleep(500);

        //quantidade
        WebElement inputQtd = driver.findElement(By.cssSelector("input[onchange='atualizarQuantidade(16, this.value)']"));
        inputQtd.click();
        inputQtd.sendKeys(Keys.ARROW_UP);
        Thread.sleep(500);

        //endereço
        Select selectEndereco = new Select(driver.findElement(By.id("enderecoEntrega")));
        selectEndereco.selectByIndex(2);
        Thread.sleep(500);

        //cartao
        Select selectCartao = new Select(driver.findElement(By.id("cartao1")));
        selectCartao.selectByIndex(1);
        Thread.sleep(500);

        //cupom
        WebElement inputCupom = driver.findElement(By.id("cupomPromocional"));
        inputCupom.sendKeys("BEMVINDO");
        WebElement inputCupomTroca = driver.findElement(By.id("cupomTroca"));
        inputCupomTroca.click();
        Thread.sleep(500);

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement alerta = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("swal2-confirm")));
            alerta.click();
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Sem alerta após cupom.");
        }

        //segundo cartao
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement btnDoisCartoes = driver.findElement(By.xpath("//button[contains(text(),'Pagar com 2 cartões')]"));
        btnDoisCartoes.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cartao2")));
        Select selectCartao2 = new Select(driver.findElement(By.id("cartao2")));
        selectCartao2.selectByIndex(2);
        Thread.sleep(500);

        //valores cartao
        WebElement valorCartao1 = driver.findElement(By.id("valorCartao1"));
        valorCartao1.sendKeys("3063.70");
        WebElement valorCartao2 = driver.findElement(By.id("valorCartao2"));
        valorCartao2.sendKeys("1500");
        Thread.sleep(500);

        // Finaliza o pedido
        WebElement btnFinalizar = driver.findElement(By.id("btnFinalizarPedido"));
        btnFinalizar.click();
        Thread.sleep(2000);

        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            WebElement alerta = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.className("swal2-confirm")));
            alerta.click();
            System.out.println("Pedido finalizado com sucesso.");
        } catch (Exception e) {
            System.out.println("Não foi possível confirmar alerta do pedido.");
        }
    }
}