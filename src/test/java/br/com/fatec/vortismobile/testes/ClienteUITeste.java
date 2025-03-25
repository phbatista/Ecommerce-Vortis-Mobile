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

public class ClienteUITeste {

    private static WebDriver driver;

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testCadastroCliente() throws InterruptedException {
        driver.get("http://localhost:8080/clientes_cadastro");

        //dados pessoais
        driver.findElement(By.id("nome")).sendKeys("João Teste");
        driver.findElement(By.id("dataNascimento")).sendKeys("15/05/1995");
        driver.findElement(By.id("cpf")).sendKeys("14185296351");
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

        //clica no botão cadastrar
        driver.findElement(By.id("submit")).click();
        Thread.sleep(2000);

        //clica em ok
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println(alert.getText());
            alert.accept();
        } catch (Exception e) {
            System.out.println("Nenhum alerta encontrado.");
        }

        //redireciona para lista
        driver.get("http://localhost:8080/clientes_lista");
        Thread.sleep(2000);

        //seleciona CPF
        WebElement filtroSelect = wait.until(ExpectedConditions.elementToBeClickable
                (By.cssSelector("select")));
        filtroSelect.click();
        WebElement opcaoCpf = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//option[contains(text(),'CPF')]")));
        opcaoCpf.click();
        Thread.sleep(1000);

        //insere CPF
        WebElement inputCPF = wait.until(ExpectedConditions.presenceOfElementLocated
                (By.cssSelector("input[placeholder='Digite sua busca']")));
        inputCPF.sendKeys("14185296351");
        Thread.sleep(1000);

        //clica no botão buscar
        WebElement btnBuscar = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'Buscar')]")));
        btnBuscar.click();
        Thread.sleep(1000);

        //clica no botão editar
        WebElement btnEditar = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//tr[td[contains(text(), '14185296351')]]//button[contains(@class, 'btn-warning')]")));
        btnEditar.click();
        Thread.sleep(1000);

        WebElement btnAlterarDados = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'ALTERAR DADOS CADASTRAIS')]")));
        btnAlterarDados.click();
        Thread.sleep(2000);

        //alterando dados
        WebElement nome = wait.until(ExpectedConditions.elementToBeClickable(By.id("nome")));
        nome.clear();
        nome.sendKeys("João Alterado");

        WebElement email = driver.findElement(By.id("email"));
        email.clear();
        email.sendKeys("joao.alterado@email.com");

        WebElement telefone = driver.findElement(By.id("telefone"));
        telefone.clear();
        telefone.sendKeys("999777666");
        Thread.sleep(2000);

        //salvar dados
        WebElement btnSalvar = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'Salvar Alterações')]")));
        btnSalvar.click();
        Thread.sleep(2000);

        //clica em ok
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            Alert alert = wait1.until(ExpectedConditions.alertIsPresent());
            System.out.println(alert.getText());
            alert.accept();
        } catch (Exception e) {
            System.out.println("Nenhum alerta encontrado.");
        }
        Thread.sleep(2000);

        //sequencia
        testEditarEndereco();
        testEditarCartao();
        testEditarSenha();
        testAtivarDesativar();
        testExcluir();
    }

    //editar endereço
    public void testEditarEndereco() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //botão alterar endereço
        WebElement btnAlterarEndereco = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'ALTERAR ENDEREÇOS')]")));
        btnAlterarEndereco.click();
        Thread.sleep(2000);

        //altera o endereço residencial
        driver.findElement(By.id("nome_logradouro_residencial")).clear();
        driver.findElement(By.id("nome_logradouro_residencial")).sendKeys("Rua das Flores");
        driver.findElement(By.id("numero_residencial")).clear();
        driver.findElement(By.id("numero_residencial")).sendKeys("123");
        driver.findElement(By.id("cep_residencial")).clear();
        driver.findElement(By.id("cep_residencial")).sendKeys("15915915");
        driver.findElement(By.id("bairro_residencial")).clear();
        driver.findElement(By.id("bairro_residencial")).sendKeys("Centro");
        driver.findElement(By.id("cidade_residencial")).clear();
        driver.findElement(By.id("cidade_residencial")).sendKeys("Manaus");
        driver.findElement(By.id("estado_residencial")).clear();
        driver.findElement(By.id("estado_residencial")).sendKeys("AM");

        //copia o endereço residencial para os outros endereços
        WebElement btnCopiar = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'Copiar Endereço para Cobrança e Entrega')]")));
        btnCopiar.click();
        Thread.sleep(1000);

        //salvar endereços
        WebElement btnSalvar = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'Salvar Alterações')]")));
        btnSalvar.click();
        Thread.sleep(2000);

        //clica em ok
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            Alert alert = wait1.until(ExpectedConditions.alertIsPresent());
            System.out.println(alert.getText());
            alert.accept();
        } catch (Exception e) {
            System.out.println("Nenhum alerta encontrado.");
        }
        Thread.sleep(2000);
    }

    //alterar cartão
    public void testEditarCartao() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //clica no botão alterar cartões
        WebElement btnAlterarCartao = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'ALTERAR CARTÕES')]")));
        btnAlterarCartao.click();
        Thread.sleep(2000);

        //primeiro cartão
        List<WebElement> cartoes = driver.findElements(By.className("cartao"));
        WebElement primeiroCartao = cartoes.get(0);
        primeiroCartao.findElement(By.cssSelector(".cartaoNumero")).clear();
        primeiroCartao.findElement(By.cssSelector(".cartaoNumero")).sendKeys("5555555555554444");
        new Select(primeiroCartao.findElement(By.cssSelector(".cartaoBandeira"))).selectByVisibleText("Elo");
        primeiroCartao.findElement(By.cssSelector(".nomeCartao")).clear();
        primeiroCartao.findElement(By.cssSelector(".nomeCartao")).sendKeys("JOAO ALTERADO");
        primeiroCartao.findElement(By.cssSelector(".cartaoValidade")).clear();
        primeiroCartao.findElement(By.cssSelector(".cartaoValidade")).sendKeys("12/28");
        primeiroCartao.findElement(By.cssSelector(".cartaoCVV")).clear();
        primeiroCartao.findElement(By.cssSelector(".cartaoCVV")).sendKeys("123");
        Thread.sleep(1000);

        //adicionar segundo cartao
        WebElement btnAdicionarCartao = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Adicionar Cartão')]")));
        btnAdicionarCartao.click();
        Thread.sleep(1000);

        //segundo cartão
        cartoes = driver.findElements(By.className("cartao"));
        WebElement segundoCartao = cartoes.get(1);
        segundoCartao.findElement(By.cssSelector(".cartaoNumero")).sendKeys("4111111111111111");
        new Select(segundoCartao.findElement(By.cssSelector(".cartaoBandeira"))).selectByVisibleText("Visa");
        segundoCartao.findElement(By.cssSelector(".nomeCartao")).sendKeys("CARTAO SECUNDARIO");
        segundoCartao.findElement(By.cssSelector(".cartaoValidade")).sendKeys("11/29");
        segundoCartao.findElement(By.cssSelector(".cartaoCVV")).sendKeys("456");
        Thread.sleep(1000);

        //salvar cartões
        WebElement btnSalvar = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'Salvar Alterações')]")));
        btnSalvar.click();
        Thread.sleep(2000);

        //clica em ok
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            Alert alert = wait1.until(ExpectedConditions.alertIsPresent());
            System.out.println(alert.getText());
            alert.accept();
        } catch (Exception e) {
            System.out.println("Nenhum alerta encontrado.");
        }
        Thread.sleep(2000);
    }

    //alterar senha
    public void testEditarSenha() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //clica no botão alterar senha
        WebElement btnAlterarSenha = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'ALTERAR SENHA')]")));
        btnAlterarSenha.click();
        Thread.sleep(2000);

        //bota senha atual
        WebElement senhaAtual = driver.findElement(By.id("senhaAtual"));
        senhaAtual.sendKeys("Senha#123");

        //bota senha nova
        WebElement novaSenha = driver.findElement(By.id("novaSenha"));
        novaSenha.sendKeys("NovaSenha#123");
        WebElement confirmarSenha = driver.findElement(By.id("confirmarSenha"));
        confirmarSenha.sendKeys("NovaSenha#123");
        Thread.sleep(2000);

        //salvar senha
        WebElement btnSalvar = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'Alterar Senha')]")));
        btnSalvar.click();
        Thread.sleep(2000);

        //clica em ok
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            Alert alert = wait1.until(ExpectedConditions.alertIsPresent());
            System.out.println(alert.getText());
            alert.accept();
        } catch (Exception e) {
            System.out.println("Nenhum alerta encontrado.");
        }
        Thread.sleep(2000);
    }

    //ativar/desativar
    public void testAtivarDesativar() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/clientes_lista");

        //seleciona CPF
        WebElement filtroSelect = wait.until(ExpectedConditions.elementToBeClickable
                (By.cssSelector("select")));
        filtroSelect.click();
        WebElement opcaoCpf = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//option[contains(text(),'CPF')]")));
        opcaoCpf.click();
        Thread.sleep(1000);

        //insere CPF
        WebElement inputCPF = wait.until(ExpectedConditions.presenceOfElementLocated
                (By.cssSelector("input[placeholder='Digite sua busca']")));
        inputCPF.sendKeys("14185296351");
        Thread.sleep(1000);

        //clica no botão buscar
        WebElement btnBuscar = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'Buscar')]")));
        btnBuscar.click();
        Thread.sleep(1000);

        //clica no botão ativar/desativar
        WebElement btnEditar = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'Ativar/Desativar')]")));
        btnEditar.click();
        Thread.sleep(2000);

        //clica em ok
        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            Alert alert = wait2.until(ExpectedConditions.alertIsPresent());
            System.out.println(alert.getText());
            alert.accept();
        } catch (Exception e) {
            System.out.println("Nenhum alerta encontrado.");
        }
        Thread.sleep(2000);

        //clica em ok
        WebDriverWait wait3 = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            Alert alert = wait3.until(ExpectedConditions.alertIsPresent());
            System.out.println(alert.getText());
            alert.accept();
        } catch (Exception e) {
            System.out.println("Nenhum alerta encontrado.");
        }
        Thread.sleep(2000);
    }

    //excluir cliente
    public void testExcluir() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //seleciona CPF
        WebElement filtroSelect1 = wait.until(ExpectedConditions.elementToBeClickable
                (By.cssSelector("select")));
        filtroSelect1.click();
        WebElement opcaoCpf1 = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//option[contains(text(),'CPF')]")));
        opcaoCpf1.click();
        Thread.sleep(1000);

        //insere CPF
        WebElement inputCPF1 = wait.until(ExpectedConditions.presenceOfElementLocated
                (By.cssSelector("input[placeholder='Digite sua busca']")));
        inputCPF1.sendKeys("14185296351");
        Thread.sleep(1000);

        //clica no botão buscar
        WebElement btnBuscar1 = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'Buscar')]")));
        btnBuscar1.click();
        Thread.sleep(1000);

        //clica no botão excluir
        WebElement btnExcluir = wait.until(ExpectedConditions.elementToBeClickable
                (By.xpath("//button[contains(text(),'Excluir')]")));
        btnExcluir.click();
        Thread.sleep(2000);

        //clica em ok
        WebDriverWait wait4 = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            Alert alert = wait4.until(ExpectedConditions.alertIsPresent());
            System.out.println(alert.getText());
            alert.accept();
        } catch (Exception e) {
            System.out.println("Nenhum alerta encontrado.");
        }
    }
}
