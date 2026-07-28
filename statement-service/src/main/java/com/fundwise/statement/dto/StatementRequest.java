package com.fundwise.statement.dto;
import jakarta.validation.constraints.NotNull;
public record StatementRequest(@NotNull Long folioId,String statementType){}
