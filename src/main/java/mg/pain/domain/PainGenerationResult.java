package mg.pain.domain;

import java.math.BigDecimal;

/**
 * Résultat de la génération d'un fichier pain.*.
 * Le consommateur décide quoi faire du contenu (HTTP, disque, S3, etc.).
 */
public class PainGenerationResult {

    /** Contenu XML généré. */
    private final byte[] content;

    /** Nom de fichier suggéré (ex: virements_MSG001.xml). */
    private final String filename;

    /** MessageId utilisé dans le fichier XML. */
    private final String messageId;

    /** Nombre de transactions incluses. */
    private final int transactionCount;

    /** Somme de contrôle (CtrlSum) — doit correspondre à la somme des montants. */
    private final BigDecimal controlSum;

    public PainGenerationResult(byte[] content, String filename, String messageId,
                                int transactionCount, BigDecimal controlSum) {
        this.content = content;
        this.filename = filename;
        this.messageId = messageId;
        this.transactionCount = transactionCount;
        this.controlSum = controlSum;
    }

    public byte[] getContent() { return content; }
    public String getFilename() { return filename; }
    public String getMessageId() { return messageId; }
    public int getTransactionCount() { return transactionCount; }
    public BigDecimal getControlSum() { return controlSum; }
}
