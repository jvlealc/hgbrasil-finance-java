# HGBrasil Finance Client - SDK Java (Em desenvolvimento...)

HGBrasil Finance Client é um SDK Java de código aberto desenvolvida para simplificar a integração com a API HGBrasil Finance.

O projeto oferece uma interface tipada e segura para o consumo de dados do mercado financeiro, eliminando a necessidade de implementações manuais de HTTP, extenso mapeamento de JSON, e tratamento de erros.

## Tecnologias e Requisitos

O SDK foi construído com foco em performance e modernidade, utilizando as seguintes tecnologias:

* **Java 21:** Utilização de recursos da versão LTS mais recente, aproveitando Virtual Threads para maximizar desempenho de chamadas a baixo custo computacional.
* **HTTP Client Nativo:** Uso do `java.net.http.HttpClient` para requisições.
* **Jackson 3 (Core e JSR310):** Motor de desempenho para processamento e bind de JSON.
* **SLF4J 2:** Abstração para logs da biblioteca.

## Escopo de Dados

O client entrega dados estruturados através de *Records* dos seguintes recursos:

* **Mercado de Capitais (B3):** Ações, FIIs, BDRs, Fundos de Investimentos e índices.
* **Câmbio e Cripto:** Cotação de moedas fiduciárias em Real (BRL) e cotação de criptoativos.
* **Indicadores Econômicos:** Taxas, índices de inflação, grupamentos, desdobramentos de ativos da B3.
* **Serie Histórica:** Dados históricos e intradiários de ativos da B3, indicadores, moedas e criptomoedas.

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

## Exemplo de Uso

### Arquitetura

A classe central que gerencia o ciclo de vida e fornece acesso as operações da API é o `HGBrasilClient.java`. 
Ele foi projetado utilizando o padrão Builder para garantir uma configuração e inicialização fluente e segura.

Para começar, o único parâmetro obrigatório é a `apiKey`. No entanto, pensando na flexibilidade arquitetural de cada aplicação e em uma excelente *Developer Experience* (DX),
o `Builder` permite a injeção de componentes customizados para adequar o SDK ao seu ecossistema, como instâncias próprias de `HttpClient`, `ObjectMapper` e `ExecutorService`

**Atenção:** O SDK gerencia automaticamente o mapeamento e conversões de datas do JSON da API para os Records modelo. Caso você opte por injetar um `ObjectMapper` customizado,
é estritamente aconselhável registrar o módulo `JavaTimeModule` no seu *mapper* para garantir que a desserialização ocorra sem falhas.

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

            // Exemplo clássico de consulta de ativos navegando no Map 'results'
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

## Contribuições

## Licença

Este projeto é *open-source* e está licenciado sob o *MIT License*.
Consulte o Arquivo `LICENSE` no repositório para obter mais detalhes.

## Contato

Desenvolvido por João Leal

- E-mail: [jv.leal.dev@gmail.com](jv.leal.dev@gmail.com)
- LinkedIn: [https://linkedin.com/in/joaovlc](linkedin.com/in/joaovlc)
- GitHub: [https://github.com/jvlealc](github.com/jvlealc)