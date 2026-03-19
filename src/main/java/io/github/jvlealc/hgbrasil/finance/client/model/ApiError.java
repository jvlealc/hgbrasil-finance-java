package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Modelo de resposta de erros quando ocorre input de dados inválidos.
 * Este objeto é compartilhado por diversos endpoints da API HGBrasil.
 * <p>
 * NOTE: Nem todos os endpoints utilizam este modelo em casos de erro.
 * </p>
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiError(
        String code,
        String message,
        String help,
        Map<String, String> details
) {}
