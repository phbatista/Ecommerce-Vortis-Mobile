package br.com.fatec.vortismobile.testes;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FluxoPedidoTrocaUITest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private final String ID_PEDIDO_TESTE = "56";

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void pausaParaDemonstracao(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFluxoCompletoDeStatusEtroca() {
        loginAdmin();
        gerenciarStatusPedido();

        loginCliente();
        solicitarTroca();
    }


    public void loginAdmin() {
        System.out.println("LOG: Iniciando login como Admin...");
        driver.get("http://localhost:8080/clientes_login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys("admin@vortismobile.com");
        driver.findElement(By.id("senha")).sendKeys("Ph#15915915"); // Assumindo que a senha do admin seja "admin"
        driver.findElement(By.id("btnLogin")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuAdmin")));
        System.out.println("LOG: Login como Admin realizado com sucesso.");
    }

    public void gerenciarStatusPedido() {
        System.out.println("LOG: Navegando para a página de gerenciamento de pedidos...");
        driver.get("http://localhost:8080/pedidos_admin");

        alterarStatusPara(ID_PEDIDO_TESTE, "APROVADO");
        pausaParaDemonstracao(1);
        alterarStatusPara(ID_PEDIDO_TESTE, "EM TRANSPORTE");
        pausaParaDemonstracao(1);
        alterarStatusPara(ID_PEDIDO_TESTE, "ENTREGUE");
        pausaParaDemonstracao(1);

        WebElement menuUsuario = driver.findElement(By.id("menuUsuario"));
        pausaParaDemonstracao(1);
        wait.until(ExpectedConditions.elementToBeClickable(menuUsuario.findElement(By.id("btnLogout")))).click();
        System.out.println("LOG: Logout de Admin realizado.");
    }


    private void alterarStatusPara(String idPedido, String novoStatus) {
        System.out.println("LOG: Alterando status do pedido " + idPedido + " para " + novoStatus);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement linhaPedido = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tbody[@id='tabelaGerenciarPedidos']/tr[td[1]='" + idPedido + "']")
        ));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", linhaPedido);

        pausaParaDemonstracao(1);
        WebElement selectStatusElement = linhaPedido.findElement(By.tagName("select"));
        new Select(selectStatusElement).selectByValue(novoStatus);

        WebElement btnSalvar = linhaPedido.findElement(By.tagName("button"));
        js.executeScript("arguments[0].click();", btnSalvar);
        pausaParaDemonstracao(1);

        wait.until(ExpectedConditions.alertIsPresent()).accept();
        System.out.println("LOG: Primeiro alerta (confirmação) aceito.");
        pausaParaDemonstracao(1);

        Alert successAlert = wait.until(ExpectedConditions.alertIsPresent());

        assertEquals("Status atualizado com sucesso!", successAlert.getText());
        System.out.println("LOG: Segundo alerta ('" + successAlert.getText() + "') verificado.");

        pausaParaDemonstracao(1);
        successAlert.accept();

        By seletorStatusAtualizado = By.xpath("//tbody[@id='tabelaGerenciarPedidos']/tr[td[1]='" + idPedido + "']/td[4][text()='" + novoStatus + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(seletorStatusAtualizado));
        pausaParaDemonstracao(1);

        System.out.println("LOG: Status do pedido " + idPedido + " atualizado para " + novoStatus + " com sucesso.");
    }

    public void loginCliente() {
        System.out.println("LOG: Iniciando login como Cliente...");
        driver.get("http://localhost:8080/clientes_login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys("pedro@outlook.com");
        driver.findElement(By.id("senha")).sendKeys("Ph#15915915");
        driver.findElement(By.id("btnLogin")).click();

        pausaParaDemonstracao(1);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnLogout")));
        System.out.println("LOG: Login como Cliente realizado com sucesso.");
    }

    public void solicitarTroca() {
        System.out.println("LOG: Navegando para a página de 'Meus Pedidos'...");
        driver.get("http://localhost:8080/pedidos");

        pausaParaDemonstracao(1);
        WebElement linhaPedido = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tbody[@id='tabelaPedidos']/tr[td[2]='" + ID_PEDIDO_TESTE + "' and contains(td[4], 'ENTREGUE')]")
        ));

        pausaParaDemonstracao(1);
        linhaPedido.findElement(By.xpath(".//button[contains(text(), 'Trocar Produto')]")).click();
        System.out.println("LOG: Formulário de troca aberto.");

        pausaParaDemonstracao(1);
        WebElement modalTroca = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("swal2-html-container")));

        pausaParaDemonstracao(1);
        WebElement inputQuantidade = modalTroca.findElement(By.cssSelector(".troca-qtd"));
        inputQuantidade.sendKeys(Keys.ARROW_UP);
        System.out.println("LOG: Quantidade para troca preenchida.");

        pausaParaDemonstracao(1);
        driver.findElement(By.cssSelector("button.swal2-confirm")).click();

        pausaParaDemonstracao(1);
        WebElement modalSucesso = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("swal2-title")));

        pausaParaDemonstracao(1);
        assertEquals("Solicitado!", modalSucesso.getText());
        System.out.println("LOG: Solicitação de troca confirmada com sucesso.");

        pausaParaDemonstracao(1);
        driver.findElement(By.cssSelector("button.swal2-confirm")).click();
    }
}