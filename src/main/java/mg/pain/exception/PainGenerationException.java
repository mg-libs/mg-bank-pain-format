package mg.pain.exception;

/**
 * Exception levée lors d'une erreur de génération d'un fichier pain.*.
 */
public class PainGenerationException extends RuntimeException {

    public PainGenerationException(String message) {
        super(message);
    }

    public PainGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
