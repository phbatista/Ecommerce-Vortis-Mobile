package br.com.fatec.vortismobile.testes;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;
import org.openqa.selenium.Alert;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FluxoPedidoTrocaUITest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // NOTA: O ID do pedido a ser testado. Em um cenário real,
    // este ID seria obtido dinamicamente após a criação de um pedido.
    private final String ID_PEDIDO_TESTE = "4"; // MODIFICAR SE NECESSÁRIO

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Usar espera explícita é a melhor prática para aguardar elementos.
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testFluxoCompletoDeStatusEtroca() {
        // --- ETAPA 1: FLUXO DO ADMIN ---
        loginAdmin();
        gerenciarStatusPedido();

        // --- ETAPA 2: FLUXO DO CLIENTE ---
        loginCliente();
        solicitarTroca();
    }


    public void loginAdmin() {
        System.out.println("LOG: Iniciando login como Admin...");
        driver.get("http://localhost:8080/clientes_login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys("admin@vortismobile.com");
        driver.findElement(By.id("senha")).sendKeys("Ph#15915915"); // Assumindo que a senha do admin seja "admin"
        driver.findElement(By.id("btnLogin")).click();

        // Verificação: Aguarda o link "Admin" aparecer no navbar
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuAdmin")));
        System.out.println("LOG: Login como Admin realizado com sucesso.");
    }

    public void gerenciarStatusPedido() {
        System.out.println("LOG: Navegando para a página de gerenciamento de pedidos...");
        driver.get("http://localhost:8080/pedidos_admin");

        // Altera o status para APROVADO
        // A chamada do método abaixo já cuida de todos os alertas necessários.
        alterarStatusPara(ID_PEDIDO_TESTE, "APROVADO");

        // Altera o status para EM TRANSPORTE
        alterarStatusPara(ID_PEDIDO_TESTE, "EM TRANSPORTE");

        // Altera o status para ENTREGUE
        alterarStatusPara(ID_PEDIDO_TESTE, "ENTREGUE");

        // Logout
        WebElement menuUsuario = driver.findElement(By.id("menuUsuario"));
        // Usar espera explícita aqui também é uma boa prática
        wait.until(ExpectedConditions.elementToBeClickable(menuUsuario.findElement(By.id("btnLogout")))).click();
        System.out.println("LOG: Logout de Admin realizado.");
    }


    private void alterarStatusPara(String idPedido, String novoStatus) {
        System.out.println("LOG: Alterando status do pedido " + idPedido + " para " + novoStatus);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1. Localiza a linha da tabela e aguarda ela estar visível
        WebElement linhaPedido = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tbody[@id='tabelaGerenciarPedidos']/tr[td[1]='" + idPedido + "']")
        ));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", linhaPedido);

        // 2. Seleciona o novo status no dropdown
        WebElement selectStatusElement = linhaPedido.findElement(By.tagName("select"));
        new Select(selectStatusElement).selectByValue(novoStatus);

        // 3. Clica no botão para salvar a alteração
        WebElement btnSalvar = linhaPedido.findElement(By.tagName("button"));
        js.executeScript("arguments[0].click();", btnSalvar);

        // 4. Lida com o PRIMEIRO alerta (confirmação da ação)
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        System.out.println("LOG: Primeiro alerta (confirmação) aceito.");

        // 5. Lida com o SEGUNDO alerta (confirmação de sucesso)
        Alert successAlert = wait.until(ExpectedConditions.alertIsPresent());

        // Verificação (Opcional, mas recomendado): Checa o texto do alerta de sucesso
        assertEquals("Status atualizado com sucesso!", successAlert.getText());
        System.out.println("LOG: Segundo alerta ('" + successAlert.getText() + "') verificado.");

        // Aceita o segundo alerta, o que provavelmente fará a página recarregar
        successAlert.accept();

        // 6. Agora sim, espera o status ser atualizado na tabela
        By seletorStatusAtualizado = By.xpath("//tbody[@id='tabelaGerenciarPedidos']/tr[td[1]='" + idPedido + "']/td[4][text()='" + novoStatus + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(seletorStatusAtualizado));

        System.out.println("LOG: Status do pedido " + idPedido + " atualizado para " + novoStatus + " com sucesso.");
    }

    public void loginCliente() {
        System.out.println("LOG: Iniciando login como Cliente...");
        driver.get("http://localhost:8080/clientes_login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys("pedro@outlook.com");
        driver.findElement(By.id("senha")).sendKeys("Ph#15915915");
        driver.findElement(By.id("btnLogin")).click();

        // Verificação: Aguarda o link "Sair" aparecer no navbar
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnLogout")));
        System.out.println("LOG: Login como Cliente realizado com sucesso.");
    }

    public void solicitarTroca() {
        System.out.println("LOG: Navegando para a página de 'Meus Pedidos'...");
        driver.get("http://localhost:8080/pedidos");

        // Localiza a linha do pedido que foi entregue
        WebElement linhaPedido = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tbody[@id='tabelaPedidos']/tr[td[2]='" + ID_PEDIDO_TESTE + "' and contains(td[4], 'ENTREGUE')]")
        ));

        // Clica no botão "Trocar Produto"
        linhaPedido.findElement(By.xpath(".//button[contains(text(), 'Trocar Produto')]")).click();
        System.out.println("LOG: Formulário de troca aberto.");

        // Interage com o modal do SweetAlert2
        WebElement modalTroca = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("swal2-html-container")));

        // Define a quantidade do item a ser trocado (neste caso, o primeiro item da lista)
        WebElement inputQuantidade = modalTroca.findElement(By.cssSelector(".troca-qtd"));
        inputQuantidade.sendKeys("1");
        System.out.println("LOG: Quantidade para troca preenchida.");

        // Clica no botão para confirmar a troca
        driver.findElement(By.cssSelector("button.swal2-confirm")).click();

        // Aguarda o modal de sucesso
        WebElement modalSucesso = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("swal2-title")));

        // Verificação: Confirma se a mensagem de sucesso apareceu
        assertEquals("Solicitado!", modalSucesso.getText());
        System.out.println("LOG: Solicitação de troca confirmada com sucesso.");

        // Clica em "OK" no modal de sucesso
        driver.findElement(By.cssSelector("button.swal2-confirm")).click();
    }
}