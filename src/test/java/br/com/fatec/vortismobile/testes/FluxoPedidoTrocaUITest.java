package br.com.fatec.vortismobile.testes;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FluxoPedidoTrocaUITest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private final String ID_PEDIDO_TESTE = "58";

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
    @DisplayName("Executa o fluxo completo: Venda -> Entrega -> Troca -> Cupom")
    public void testFluxoCompletoDeStatusEtroca() {
        loginAdmin();
        gerenciarStatusPedido();

        loginCliente();
        solicitarTroca();

        loginAdmin();
        aprovarTrocaComoAdmin();

        loginCliente();
        verificarNotificacaoDeCupom();

        System.out.println("LOG: FLUXO COMPLETO FINALIZADO COM SUCESSO!");
        pausaParaDemonstracao(5);
    }


    public void loginAdmin() {
        System.out.println("LOG: Iniciando login como Admin...");
        driver.get("http://localhost:8080/clientes_login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys("admin@vortismobile.com");
        driver.findElement(By.id("senha")).sendKeys("Ph#15915915");
        pausaParaDemonstracao(1);
        driver.findElement(By.id("btnLogin")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuAdmin")));
        System.out.println("LOG: Login como Admin realizado com sucesso.");
    }

    public void gerenciarStatusPedido() {
        System.out.println("LOG: Navegando para a página de gerenciamento de pedidos...");
        driver.get("http://localhost:8080/pedidos_admin");
        pausaParaDemonstracao(1);
        alterarStatusPara(ID_PEDIDO_TESTE, "APROVADO");
        pausaParaDemonstracao(1);
        alterarStatusPara(ID_PEDIDO_TESTE, "EM TRANSPORTE");
        pausaParaDemonstracao(1);
        alterarStatusPara(ID_PEDIDO_TESTE, "ENTREGUE");
        pausaParaDemonstracao(1);
        WebElement menuUsuario = driver.findElement(By.id("menuUsuario"));
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
        WebElement selectStatusElement = linhaPedido.findElement(By.tagName("select"));
        new Select(selectStatusElement).selectByValue(novoStatus);
        WebElement btnSalvar = linhaPedido.findElement(By.tagName("button"));
        js.executeScript("arguments[0].click();", btnSalvar);
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        Alert successAlert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Status atualizado com sucesso!", successAlert.getText());
        successAlert.accept();
        By seletorStatusAtualizado = By.xpath("//tbody[@id='tabelaGerenciarPedidos']/tr[td[1]='" + idPedido + "']/td[4][text()='" + novoStatus + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(seletorStatusAtualizado));
        System.out.println("LOG: Status do pedido " + idPedido + " atualizado para " + novoStatus + " com sucesso.");
    }

    public void loginCliente() {
        System.out.println("LOG: Iniciando login como Cliente...");
        driver.get("http://localhost:8080/clientes_login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys("pedro@outlook.com");
        driver.findElement(By.id("senha")).sendKeys("Ph#15915915");
        pausaParaDemonstracao(1);
        driver.findElement(By.id("btnLogin")).click();
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
        linhaPedido.findElement(By.xpath(".//button[contains(text(), 'Trocar Produto')]")).click();
        System.out.println("LOG: Formulário de troca aberto.");
        pausaParaDemonstracao(1);
        WebElement modalTroca = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("swal2-html-container")));
        WebElement inputQuantidade = modalTroca.findElement(By.cssSelector(".troca-qtd"));
        inputQuantidade.sendKeys(Keys.ARROW_UP);
        System.out.println("LOG: Quantidade para troca preenchida.");
        pausaParaDemonstracao(1);
        driver.findElement(By.cssSelector("button.swal2-confirm")).click();
        WebElement modalSucesso = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("swal2-title")));
        assertEquals("Solicitado!", modalSucesso.getText());
        pausaParaDemonstracao(1);
        driver.findElement(By.cssSelector("button.swal2-confirm")).click();
        System.out.println("LOG: Solicitação de troca confirmada com sucesso.");
    }

    public void aprovarTrocaComoAdmin() {
        System.out.println("LOG: Navegando para a página de gerenciamento de trocas...");
        driver.get("http://localhost:8080/trocas_admin");
        pausaParaDemonstracao(1);

        WebElement linhaTroca = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tbody[@id='tabelaAdminTrocas']/tr[td[2]='" + ID_PEDIDO_TESTE + "']")
        ));
        System.out.println("LOG: Solicitação de troca encontrada para o pedido " + ID_PEDIDO_TESTE);
        pausaParaDemonstracao(1);

        WebElement btnConfirmar = linhaTroca.findElement(By.xpath(".//button[contains(text(), 'Confirmar Recebimento')]"));
        btnConfirmar.click();

        pausaParaDemonstracao(1);
        WebElement btnReporEstoque = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.swal2-confirm")));
        btnReporEstoque.click();
        System.out.println("LOG: Confirmação de reposição de estoque realizada.");

        pausaParaDemonstracao(1);
        WebElement btnOkSucesso = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.swal2-confirm")));
        assertEquals("Sucesso!", driver.findElement(By.id("swal2-title")).getText());
        btnOkSucesso.click();
        System.out.println("LOG: Troca aprovada pelo admin com sucesso.");

        pausaParaDemonstracao(1);
        WebElement menuUsuario = driver.findElement(By.id("menuUsuario"));
        wait.until(ExpectedConditions.elementToBeClickable(menuUsuario.findElement(By.id("btnLogout")))).click();
        System.out.println("LOG: Logout de Admin realizado.");
    }

    public void verificarNotificacaoDeCupom() {
        System.out.println("LOG: Navegando para a página de notificações do cliente...");
        driver.get("http://localhost:8080/notificacoes");

        pausaParaDemonstracao(1);
        WebElement notificacaoCupom = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//ul[@id='listaNotificacoes']/li[contains(., 'Cupom de troca gerado: TROCA-')]")
        ));

        pausaParaDemonstracao(1);
        assertTrue(notificacaoCupom.isDisplayed(), "A notificação de cupom de troca não foi encontrada.");
        System.out.println("LOG: Notificação de cupom de troca encontrada com sucesso: " + notificacaoCupom.getText());
    }
}