# HG Brasil Finance Client - SDK Java

![Java 17+](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MIT License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)
![Build Status](https://img.shields.io/github/actions/workflow/status/jvlealc/hgbrasil-finance-java/maven-ci.yml?branch=main&style=for-the-badge)

:brazil: Português

HG Brasil Finance Client é um SDK Java de código aberto desenvolvido para simplificar a integração com a API HG Brasil Finance.

O projeto oferece uma interface tipada e segura para o consumo de dados do mercado financeiro, eliminando a necessidade de implementações manuais de chamadas HTTP, mapeamento extensivo de JSON e tratamento de erros boilerplate.

## Tecnologias

O SDK foi projetado com forte foco em performance e práticas modernas de Java.

* **HTTP Client (Nativo):** Uso do `java.net.http.HttpClient` para requisições de rede.
* **Jackson 3 (Core e JSR310):** Motor de alto desempenho para processamento e data binding de JSON.
* **SLF4J 2:** Camada de abstração de logs para a biblioteca.
* **JUnit 5 & Mockito:** Cobertura abrangente de testes unitários e de integração.

## Arquitetura

### Concorrência via Reflection (Java 17 e 21+)

Um dos principais pontos fortes deste SDK é o seu **Motor de Execução Adaptativo**. O projeto é compilado tendo como alvo o **Java 17** para garantir ampla compatibilidade corporativa. No entanto, em tempo de execução, o SDK usa **Java Reflection** para detectar dinamicamente as capacidades da sua JVM:

* **Java 21+ (Virtual Threads):** Se o SDK detectar uma JVM moderna e nenhum `HttpClient` ou `Executor` customizado for fornecido, ele instanciará automaticamente um `VirtualThreadPerTaskExecutor`. Isso permite concorrência massiva e I/O não-bloqueante "out-of-the-box", ideal para consultas de dados financeiros em alta frequência.
* **Java 17 (Platform Threads):** Se rodar em uma JVM mais antiga, ele fará o fallback graciosamente para o executor padrão do `HttpClient` nativo, garantindo total estabilidade.

### Cliente Principal (HGBrasilClient)
A classe central que gerencia o ciclo de vida e fornece acesso às operações da API é o `HGBrasilClient.java`.
Ele foi projetado utilizando o padrão Builder para garantir uma configuração e inicialização fluente e segura.

Para começar, o único parâmetro obrigatório é a `apiKey`. No entanto, pensando na flexibilidade arquitetural de cada aplicação e em uma excelente *Developer Experience* (DX),
o `Builder` permite a injeção de componentes customizados, possibilitando que você utilize suas próprias instâncias de `HttpClient`, `ObjectMapper` e `ExecutorService` para melhor integração com o seu ecossistema.

**Atenção:** O SDK gerencia automaticamente as conversões de datas e o mapeamento seguro de valores predefinidos (`Enums`) do JSON da API para os *Records* modelo.
Caso opte por injetar um `ObjectMapper` customizado, é **estritamente aconselhável** registrar o módulo `JavaTimeModule` e habilitar a feature `READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE`
no seu *mapper*. Isso garante que a desserialização ocorra sem falhas.

---

## Features

O client entrega dados estruturados através de *Records* dos seguintes recursos:

* **Mercado de Capitais (B3):** Ações, FIIs, BDRs, Fundos de Investimentos e índices de mercado.
* **Dividendos e Proventos:** Histórico detalhado de distribuição de dividendos, JCP e bonificações dos ativos da B3.
* **Grupamentos e Desdobramentos (Splits):** Histórico de eventos de desdobramento e grupamento de ações, fundos imobiliários e BDRs.
* **Câmbio e Cripto:** Cotação de moedas fiduciárias em Real (BRL) e cotação de criptoativos em Dólar (USD).
* **Indicadores Econômicos:** Taxas de juros (SELIC, CDI, TR) e indicadores de inflação (IPCA, IGP, INCC) com séries históricas.
* **Série Histórica:** Dados históricos e intradiários de ativos da B3, índices, moedas e criptomoedas.

---

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

```java
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import io.github.jvlealc.hgbrasil.finance.client.AssetOperations;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResult;

public class Main {
   public static void main(String[] args) {
      try (HGBrasilClient client = HGBrasilClient.builder()
              .apiKey("YOUR_API_KEY")
              .build()) {

         AssetOperations assetOperations = client.getAssetOperations();

        // Consulta utilizando o método utilitário findFirstResult()
         AssetResult petr4 = assetOperations.getBySymbol("PETR4")
                 .findFirstResult()
                 .orElseThrow();

         System.out.println("Asset: " + petr4.name());
         System.out.println("Price: R$ " + petr4.price());
         System.out.println("Percent Change: " + petr4.changePercent() + "%");
      }
   }
}
```

## Operações Disponíveis

| Operação                      | Descrição                                                                            |
|:------------------------------|:-------------------------------------------------------------------------------------|
| `getAssetOperations()`        | Cotações em tempo real para Ações, FIIs, BDRs, Cripto e Índices.                     |
| `getAssetHistoryOperations()` | Dados de séries históricas OHLCV para ativos da B3.                                  |
| `getExchangeOperations()`     | Moedas fiduciárias globais (USD, EUR, etc.) em relação ao BRL e cotações de Bitcoin. |
| `getDividendOperations()`     | Histórico abrangente de dividendos, JCP (Juros sobre Capital Próprio) e proventos.   |
| `getSplitOperations()`        | Histórico de eventos corporativos (Desdobramentos e Grupamentos).                    |
| `getIndicatorOperations()`    | Indicadores econômicos brasileiros (SELIC, CDI, IPCA, IGP-M, TR).                    |
| `getIbovespaOperations()`     | Pontos intradiários e dados históricos do índice de mercado Ibovespa.                |

---

## Contribuições

Contribuições são extremamente bem-vindas! Se você encontrou algum *bug*, possui sugestões de melhorias ou ideia para uma nova funcionalidade,
sinta-se à vontade para fazer um *Fork* e abrir um *Pull-Request*.

Prezamos por uma alta cobertura de código. Antes de enviar seu código, garanta que todos os testes estão passando.

**Nota: Os testes de integração realizam chamadas a API real. Você precisará exportar a sua chave da API HG Brasil como VARIÁVEL DE AMBIENTE.**

Para mantermos a qualidade e o padrão do repositório, pedimos que observe dois pontos principais:

1. **Testes:** Garanta que todas as suas alterações estejam cobertas por testes e passando na sua máquina.
     * Testes Unitários: execute `mvn clean test`.
     * Testes de Integração: exporte a variável de ambiente `HGBRASIL_API_KEY` com uma chave de API válida e execute `mvn clean verify`. Caso a variável não seja encontrada, os testes de integração serão ignorados.
2. **Padrão de Commits:** Este projeto adota o [Conventional Commits](https://www.conventionalcommits.org/). Estruture suas mensagens utilizando os prefixos corretos (ex: `feat:`, `fix:`, `refactor:`, `test:`).

---

## Licença

Este projeto é *open-source* e está licenciado sob o *MIT License*.
Consulte o arquivo `LICENSE` no repositório para obter mais detalhes.

## Contato

Desenvolvido por João Leal

- E-mail: [jv.leal.dev@gmail.com](mailto:jv.leal.dev@gmail.com)
- LinkedIn: [linkedin.com/in/joaovlc](https://linkedin.com/in/joaovlc)
- GitHub: [github.com/jvlealc](https://github.com/jvlealc)`