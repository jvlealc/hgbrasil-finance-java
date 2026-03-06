# HGBrasil Finance Client - SDK Java (em desenvolvimento...)

![Java 21](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MIT License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)
![Build Status](https://img.shields.io/github/actions/workflow/status/jvlealc/hgbrasil-finance-java/maven-ci.yml?branch=main&style=for-the-badge)

:brazil: Português

HG Brasil Finance Client é um SDK Java de código aberto desenvolvido para simplificar a integração com a API HG Brasil Finance.

O projeto oferece uma interface tipada e segura para o consumo de dados do mercado financeiro, eliminando a necessidade de implementações manuais de HTTP, extenso mapeamento de JSON, e tratamento de erros.

## Tecnologias e Requisitos

O SDK foi construído com foco em performance e modernidade, utilizando as seguintes tecnologias:

* **Java 21:** Utilização de recursos da versão LTS mais recente, aproveitando Virtual Threads para maximizar desempenho de chamadas a baixo custo computacional
* **HTTP Client Nativo:** Uso do `java.net.http.HttpClient` para requisições
* **Jackson 3 (Core e JSR310):** Motor de desempenho para processamento e bind de JSON
* **SLF4J 2:** Abstração para logs da biblioteca
* **Testes e Qualidade:** Cobertura de testes unitários e de integração utilizando **JUnit 5** e **Mockito** 

## Escopo de Dados

O client entrega dados estruturados através de *Records* dos seguintes recursos:

* **Mercado de Capitais (B3):** Ações, FIIs, BDRs, Fundos de Investimentos e índices.
* **Câmbio e Cripto:** Cotação de moedas fiduciárias em Real (BRL) e cotação de criptoativos.
* **Indicadores Econômicos:** Taxas, índices de inflação, grupamentos, desdobramentos de ativos da B3.
* **Série Histórica:** Dados históricos e intradiários de ativos da B3, indicadores, moedas e criptomoedas.

## Início Rápido

Para utilizar o client em seu projeto Maven, adicione a seguinte dependência ao seu arquivo `pom.xml`:

```xml
<dependency>
    <groupId>io.github.jvlealc</groupId>
    <artifactId>hgbrasil-finance-client</artifactId>
    <version>Em Breve...</version>
</dependency>
```

Para projetos utilizando Gradle:
```groovy
implementation 'io.github.jvlealc:hgbrasil-finance-client:Em Breve...'
```

---

### Arquitetura

A classe central que gerencia o ciclo de vida e fornece acesso às operações da API é o `HGBrasilClient.java`.
Ele foi projetado utilizando o padrão Builder para garantir uma configuração e inicialização fluente e segura.

Para começar, o único parâmetro obrigatório é a `apiKey`. No entanto, pensando na flexibilidade arquitetural de cada aplicação e em uma excelente *Developer Experience* (DX),
o `Builder` permite a injeção de componentes customizados para adequar o SDK ao seu ecossistema, como instâncias próprias de `HttpClient`, `ObjectMapper` e `ExecutorService`

**Atenção:** O SDK gerencia automaticamente o mapeamento e conversões de datas do JSON da API para os Records modelo. Caso você opte por injetar um `ObjectMapper` customizado,
é estritamente aconselhável registrar o módulo `JavaTimeModule` no seu *mapper* para garantir que a desserialização ocorra sem falhas.


## Exemplo de Uso

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;

public class Main {
    public static void main(String[] args) {
        try (
                HGBrasilClient client = HGBrasilClient.builder()
                        .apiKey("YOUR_API_KEY")
                        .build()
        ) {
            // Obtendo as operações de ativos da B3
            var assetOp = client.getAssetOperations();

            // Exemplo padrão de consulta de ativos navegando no Map 'results'
            var petr4Result = assetOp.getBySymbol("PETR4")
                    .results()
                    .get("PETR4");

            System.out.printf( "Asset: %s\n", petr4Result.name() );
            System.out.printf( "Price: %s\n", petr4Result.price() );
            System.out.printf( "Percent Change: %s%%\n", petr4Result.changePercent() );

            // Consulta de criptoativo utilizando o utilitário getFirstAssetResult com Optional
            var bitcoinResponse = assetOp.getBySymbol("BTCBRL");

            bitcoinResponse.getFirstAssetResult().ifPresent(asset ->
                    System.out.printf(
                            "Asset: %s\nPrice: %s\nPercent Change: %s%%\nKind: %s\n",
                            asset.name(),
                            asset.price(),
                            asset.changePercent(),
                            asset.kind()
                    )
            );
        }
    }
}
```

---

## Contribuições

Contribuições são extremamente bem-vindas! Se você encontrou algum *bug*, possui sugestões de melhorias, ideia para uma nova funcionalidade,
sinta-se à vontade para fazer um *Fork* e abrir um *Pull-Request*.

Nós prezamos por uma alta cobertura de código. Antes de enviar seu código, garanta que todos os testes estão passando.

**Nota: Os testes de integração realizam chamadas a API real. Você precisará exportar a sua chave da API HG Brasil como VARIÁVEL DE AMBIENTE.**

Para mantermos a qualidade e o padrão do repositório, pedimos que observe dois pontos principais:

1. **Testes:** Garanta que todas as suas alterações estejam cobertas por testes e passando na sua máquina.
     * Testes Unitários: execute `mvn clean test`.
     * Testes de Integração: exporte a variável de ambiente `HGBRASIL_API_KEY` com uma chave de API válida e execute `mvn clean verify`. Caso a variável não seja encontrada, os testes de integração serão ignorados.
2. **Padrão de Commits:** Este projeto adota o [Conventional Commits](https://www.conventionalcommits.org/). Estruture suas mensagens utilizando os prefixos corretos (ex: `feat:`, `fix:`, `refactor:`, `test:`).

---

## Licença

Este projeto é *open-source* e está licenciado sob o *MIT License*.
Consulte o Arquivo `LICENSE` no repositório para obter mais detalhes.

## Contato

Desenvolvido por João Leal

- E-mail: [jv.leal.dev@gmail.com](mailto:jv.leal.dev@gmail.com)
- LinkedIn: [linkedin.com/in/joaovlc](https://linkedin.com/in/joaovlc)
- GitHub: [github.com/jvlealc](https://github.com/jvlealc)