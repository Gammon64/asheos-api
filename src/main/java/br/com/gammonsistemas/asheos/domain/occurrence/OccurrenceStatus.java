package br.com.gammonsistemas.asheos.domain.occurrence;

public enum OccurrenceStatus {
    OPENED,      // Aberta (Padrão)
    IN_PROGRESS, // Em andamento (Alocada para um responsável)
    CLOSED,      // Fechada (Resolvida ou Rejeitada)
    PENDING      // Pendente (Aguardando mais informações)
}
