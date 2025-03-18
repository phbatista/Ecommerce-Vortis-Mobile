package br.com.fatec.vortismobile.testes;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(OrderAnnotation.class) //garantir order dos testes
public class ClienteAPITeste {

    private static final String BASE_URL = "http://localhost:8080/api/clientes";
    private static String idCriado; //armazena o id para os teste

    @Test
    @Order(1)
    public void testCriarCliente() {
        String clienteJson = """
        {
            "nome": "Maria Silva",
            "dataNascimento": "15/08/1995",
            "cpf": "12345678901",
            "genero": "Feminino",
            "email": "maria@email.com",
            "senha": "senha123",
            "status": "Ativo",
            "telefone": {
                "tipo": "Celular",
                "ddd": "11",
                "numero": "999888777"
            },
            "enderecos": [
                {
                    "tipoResidencia": "Apartamento",
                    "tipoEndereco": "Residencial",
                    "tipoLogradouro": "Rua",
                    "logradouro": "Av. Paulista",
                    "numero": "1000",
                    "cep": "01311-000",
                    "bairro": "Bela Vista",
                    "cidade": "São Paulo",
                    "estado": "SP",
                    "pais": "Brasil"
                }
            ],
            "cartoes": [
                {
                    "cartaoBandeira": "Visa",
                    "cartaoNumero": "4111111111111111",
                    "cartaoValidade": "12/26",
                    "cartaoCVV": "123",
                    "nomeCartao": "Maria Silva",
                    "cartaoPrincipal": true
                }
            ]
        }
    """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(clienteJson)
                .when()
                .post(BASE_URL)
                .then()
                .extract().response();

        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201, "Falha ao criar cliente!");

        idCriado = response.jsonPath().getString("id");

        System.out.println("cliente created");
    }

    @Test
    @Order(2)
    public void testBuscarCliente() {
        given()
                .when()
                .get(BASE_URL + "/" + idCriado)
                .then()
                .statusCode(200)
                .body("nome", equalTo("Maria Silva"));

        System.out.println("cliente read");
    }

    @Test
    @Order(3)
    public void testAtualizarCliente() {
        String clienteJson = """
                {
                    "nome": "Maria Oliveira",
                    "email": "maria@email.com",
                    "telefone": {
                        "tipo": "Celular",
                        "ddd": "11",
                        "numero": "999888777"
                    }
                }
                """;

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(clienteJson)
                        .when()
                        .put(BASE_URL + "/" + idCriado)
                        .then()
                        .extract().response();

        System.out.println("cliente updated");
    }

    @Test
    @Order(4)
    public void testDeletarCliente() {
        Response response =
                given()
                        .when()
                        .delete(BASE_URL + "/" + idCriado)
                        .then()
                        .extract().response();

        assertTrue(response.getStatusCode() == 204 || response.getStatusCode() == 200, "Falha ao deletar cliente!");

        System.out.println("cliente deleted");
    }
}