package mg.pain.generator;

import mg.pain.domain.PainGenerationResult;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * Interface générique de génération de fichiers ISO 20022 pain.*.
 *
 * @param <T> type du DTO d'entrée (ex: SepaTransferRequest pour pain.001)
 */
public interface PainGenerator<T> {

    /**
     * Génère le fichier et retourne le résultat complet (contenu + métadonnées).
     *
     * @param requests  liste des transactions
     * @param messageId identifiant unique du message (MsgId)
     * @return résultat contenant le XML en byte[] et les métadonnées
     */
    PainGenerationResult generate(List<T> requests, String messageId);

    /**
     * Génère le fichier et l'écrit directement dans le stream fourni.
     *
     * @param requests  liste des transactions
     * @param messageId identifiant unique du message
     * @param out       stream de sortie
     */
    void generateToStream(List<T> requests, String messageId, OutputStream out);

    /**
     * Génère le fichier et l'écrit sur le chemin fourni.
     * Les répertoires parents sont créés si nécessaire.
     *
     * @param requests   liste des transactions
     * @param messageId  identifiant unique du message
     * @param outputPath chemin complet du fichier de sortie
     */
    void generateToFile(List<T> requests, String messageId, Path outputPath);
}
