package br.com.gammonsistemas.asheos.domain.occurrence;

import br.com.gammonsistemas.asheos.domain.user.UserMock;

public class OccurrenceMock {
    public static final String OCCURENCE_TITLE = "Poste apagado";
    public static final String OCCURRENCE_DESCRIPTION = "Reportando poste 5E apagado, conferir sensor e lâmpada";

    public static final Occurrence OCCURRENCE_LAMPPOST() {
        final Occurrence occurrence = new Occurrence();
        occurrence.setTitle(OCCURENCE_TITLE);
        occurrence.setDescription(OCCURRENCE_DESCRIPTION);
        occurrence.setReportedBy(UserMock.USER_JOHN_DOE());

        return occurrence;
    }
}
