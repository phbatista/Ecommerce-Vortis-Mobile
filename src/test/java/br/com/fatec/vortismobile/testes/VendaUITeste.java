package br.com.fatec.vortismobile.testes;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VendaUITeste {

    private void pausaParaDemonstracao(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    //teste sem erro
    @Test
    @DisplayName("Deve executar o fluxo completo de venda, desde o cadastro de estoque até a compra pelo cliente")
    public void testFluxoDeVendaCompleto() {

        loginAdmin();
        cadastrarEstoqueDosProdutos();
        logout();

        loginCliente();
        adicionarProdutosAoCarrinho();
        preencherCarrinhoEFinalizar();
    }

    //teste com erro
    @Test
    @DisplayName("Deve exibir mensagem de erro ao tentar usar um cupom inválido")
    public void testCompraComCupomInvalido() {

        loginCliente();
        adicionarProdutosAoCarrinho();

        System.out.println("LOG: Navegando para o carrinho para testar cupom inválido...");
        driver.get("http://localhost:8080/carrinho");

        WebElement inputCupom = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cupomPromocional")));
        inputCupom.sendKeys("CUPOM-QUE-NAO-EXISTE");

        driver.findElement(By.id("cupomTroca")).click();

        Alert cupomAlert = wait.until(ExpectedConditions.alertIsPresent());
        System.out.println("LOG: Alerta de erro encontrado: " + cupomAlert.getText());

        assertEquals("Cupom inválido", cupomAlert.getText());

        cupomAlert.accept();

        System.out.println("LOG: Teste de cupom inválido finalizado com sucesso.");
        pausaParaDemonstracao(3);
    }

    public void loginAdmin() {
        System.out.println("LOG: Iniciando login como Admin...");
        driver.get("http://localhost:8080/clientes_login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys("admin@vortismobile.com");
        driver.findElement(By.id("senha")).sendKeys("Ph#15915915");
        driver.findElement(By.id("btnLogin")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuAdmin")));
        System.out.println("LOG: Login como Admin bem-sucedido.");
    }

    public void cadastrarEstoqueDosProdutos() {
        System.out.println("LOG: Navegando para a página de Estoque...");
        driver.get("http://localhost:8080/estoque");

        cadastrarProdutoNoEstoque("16", "10", "1500.00", "Fornecedor Tech");
        pausaParaDemonstracao(2);

        cadastrarProdutoNoEstoque("15", "5", "2500.00", "Fornecedor Global");
        pausaParaDemonstracao(2);

        System.out.println("LOG: Cadastro de estoque finalizado.");
    }

    private void cadastrarProdutoNoEstoque(String idProduto, String quantidade, String custo, String fornecedor) {
        System.out.println("LOG: Cadastrando estoque para o produto ID: " + idProduto);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("produto")));

        new Select(driver.findElement(By.id("produto"))).selectByValue(idProduto);
        driver.findElement(By.id("dataEntrada")).sendKeys(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        driver.findElement(By.id("fornecedor")).sendKeys(fornecedor);
        driver.findElement(By.id("custoUnitario")).sendKeys(custo);
        driver.findElement(By.id("quantidade")).sendKeys(quantidade);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Estoque cadastrado com sucesso!", alert.getText());
        alert.accept();
        System.out.println("LOG: Estoque para o produto ID " + idProduto + " cadastrado com sucesso.");
    }

    public void logout() {
        WebElement btnLogout = wait.until(ExpectedConditions.elementToBeClickable(By.id("btnLogout")));
        btnLogout.click();
        System.out.println("LOG: Logout realizado.");
        pausaParaDemonstracao(2);
    }

    public void loginCliente() {
        System.out.println("LOG: Iniciando login como Cliente...");
        driver.get("http://localhost:8080/clientes_login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys("pedro@outlook.com");
        driver.findElement(By.id("senha")).sendKeys("Ph#15915915");
        driver.findElement(By.id("btnLogin")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnLogout")));
        System.out.println("LOG: Login como Cliente bem-sucedido.");
        pausaParaDemonstracao(2);
    }

    public void adicionarProdutosAoCarrinho() {
        System.out.println("LOG: Adicionando produtos ao carrinho...");
        adicionarProdutoEspecificoAoCarrinho("16");
        pausaParaDemonstracao(2);
        adicionarProdutoEspecificoAoCarrinho("15");
        pausaParaDemonstracao(2);
    }

    private void adicionarProdutoEspecificoAoCarrinho(String idProduto) {
        driver.get("http://localhost:8080/produto.html?id=" + idProduto);
        WebElement btnAdicionar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[onclick^='adicionarAoCarrinho']")));
        btnAdicionar.click();
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        System.out.println("LOG: Produto ID " + idProduto + " adicionado ao carrinho.");
        pausaParaDemonstracao(2);
    }

    public void preencherCarrinhoEFinalizar() {
        System.out.println("LOG: Preenchendo dados no carrinho e finalizando a compra...");
        driver.get("http://localhost:8080/carrinho");

        WebElement inputQtd = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[onchange*='atualizarQuantidade(16']")));
        inputQtd.sendKeys(Keys.ARROW_UP);
        pausaParaDemonstracao(1);

        new Select(driver.findElement(By.id("enderecoEntrega"))).selectByIndex(1);
        new Select(driver.findElement(By.id("cartao1"))).selectByIndex(1);
        driver.findElement(By.xpath("//button[contains(text(),'Pagar com 2 cartões')]")).click();
        WebElement cartao2Select = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cartao2")));
        new Select(cartao2Select).selectByIndex(2);
        pausaParaDemonstracao(1);

        WebElement inputCupom = driver.findElement(By.id("cupomPromocional"));
        inputCupom.sendKeys("BEMVINDO");
        driver.findElement(By.id("cupomTroca")).click();

        Alert cupomAlert = wait.until(ExpectedConditions.alertIsPresent());
        System.out.println("LOG: Alerta de cupom encontrado: " + cupomAlert.getText());
        cupomAlert.accept();
        pausaParaDemonstracao(2);

        WebElement valorCartao1 = driver.findElement(By.id("valorCartao1"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", valorCartao1);
        valorCartao1.sendKeys("4500");

        WebElement valorCartao2 = driver.findElement(By.id("valorCartao2"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", valorCartao2);
        valorCartao2.sendKeys("4723.7");
        pausaParaDemonstracao(1);

        System.out.println("LOG: Procurando e clicando no botão Finalizar Pedido...");
        WebElement btnFinalizar = driver.findElement(By.id("btnFinalizarPedido"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnFinalizar);
        pausaParaDemonstracao(1); // Pausa para ver o scroll acontecer
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnFinalizar);

        WebElement alertConfirmButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("swal2-confirm")));
        assertEquals("Pedido Confirmado!", driver.findElement(By.id("swal2-title")).getText());
        pausaParaDemonstracao(1);
        alertConfirmButton.click();
        System.out.println("LOG: Pedido finalizado com sucesso.");
    }
}