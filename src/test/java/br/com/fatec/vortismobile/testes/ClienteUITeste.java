package br.com.fatec.vortismobile.testes;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ClienteUITeste {

    private static WebDriver driver;

    @BeforeAll
    public static void setup() {
        WebDriverManager.edgedriver().clearDriverCache().setup();
        driver = new EdgeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testCadastroCliente() {
        driver.get("http://localhost:8080/clientes_cadastro");

        //dados pessoais
        driver.findElement(By.id("nome")).sendKeys("João Teste");
        driver.findElement(By.id("dataNascimento")).sendKeys("15/05/1995");
        driver.findElement(By.id("cpf")).sendKeys("12345678901");
        driver.findElement(By.id("genero")).sendKeys("Masculino");
        driver.findElement(By.id("email")).sendKeys("joao@email.com");
        driver.findElement(By.id("tipoTelefone")).sendKeys("Celular");
        driver.findElement(By.id("ddd")).sendKeys("11");
        driver.findElement(By.id("telefone")).sendKeys("999888777");
        driver.findElement(By.id("senha")).sendKeys("Senha#123");
        driver.findElement(By.id("senhaConfirm")).sendKeys("Senha#123");

        //residencial
        driver.findElement(By.id("tipo_residencia_residencial")).sendKeys("Casa");
        driver.findElement(By.id("tipo_logradouro_residencial")).sendKeys("Rua");
        driver.findElement(By.id("nome_logradouro_residencial")).sendKeys("Av. Paulista");
        driver.findElement(By.id("numero_residencial")).sendKeys("1000");
        driver.findElement(By.id("cep_residencial")).sendKeys("01311000");
        driver.findElement(By.id("bairro_residencial")).sendKeys("Bela Vista");
        driver.findElement(By.id("cidade_residencial")).sendKeys("São Paulo");
        driver.findElement(By.id("estado_residencial")).sendKeys("SP");
        driver.findElement(By.id("pais_residencial")).sendKeys("Brasil");

        //cobrança
        driver.findElement(By.id("tipo_residencia_cobranca")).sendKeys("Casa");
        driver.findElement(By.id("tipo_logradouro_cobranca")).sendKeys("Rua");
        driver.findElement(By.id("nome_logradouro_cobranca")).sendKeys("Av. Paulista");
        driver.findElement(By.id("numero_cobranca")).sendKeys("1000");
        driver.findElement(By.id("cep_cobranca")).sendKeys("01311000");
        driver.findElement(By.id("bairro_cobranca")).sendKeys("Bela Vista");
        driver.findElement(By.id("cidade_cobranca")).sendKeys("São Paulo");
        driver.findElement(By.id("estado_cobranca")).sendKeys("SP");
        driver.findElement(By.id("pais_cobranca")).sendKeys("Brasil");

        //entrega
        driver.findElement(By.id("tipo_residencia_entrega")).sendKeys("Casa");
        driver.findElement(By.id("tipo_logradouro_entrega")).sendKeys("Rua");
        driver.findElement(By.id("nome_logradouro_entrega")).sendKeys("Av. Paulista");
        driver.findElement(By.id("numero_entrega")).sendKeys("1000");
        driver.findElement(By.id("cep_entrega")).sendKeys("01311000");
        driver.findElement(By.id("bairro_entrega")).sendKeys("Bela Vista");
        driver.findElement(By.id("cidade_entrega")).sendKeys("São Paulo");
        driver.findElement(By.id("estado_entrega")).sendKeys("SP");
        driver.findElement(By.id("pais_entrega")).sendKeys("Brasil");

        //cartão
        driver.findElement(By.cssSelector(".cartaoNumero")).sendKeys("7536854125225874");
        driver.findElement(By.cssSelector(".cartaoBandeira")).sendKeys("Visa");
        driver.findElement(By.cssSelector(".cartaoValidade")).sendKeys("12/26");
        driver.findElement(By.cssSelector(".cartaoCVV")).sendKeys("123");
        driver.findElement(By.cssSelector(".nomeCartao")).sendKeys("JOAO TESTE");
        driver.findElement(By.cssSelector(".cartaoPrincipal")).click();

        //cadastrar
        driver.findElement(By.id("submit")).click();

        //procura mensagem com sucesso
        WebElement successMessage = driver.findElement(By.id("mensagem-sucesso"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", successMessage);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(successMessage));
        System.out.println("Texto da mensagem de sucesso: " + successMessage.getText());
    }
}