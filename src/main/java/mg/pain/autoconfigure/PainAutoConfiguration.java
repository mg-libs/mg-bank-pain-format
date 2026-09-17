package mg.pain.autoconfigure;

import mg.pain.domain.SepaDirectDebitRequest;
import mg.pain.domain.SepaTransferRequest;
import mg.pain.generator.PainGenerator;
import mg.pain.generator.pain001.Pain001V03Generator;
import mg.pain.generator.pain001.Pain001V09Generator;
import mg.pain.generator.pain008.Pain008V02Generator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration Spring Boot du starter mg-bank-pain-format.
 *
 * <p>Expose deux beans nommés, injectables via {@code @Qualifier} :
 * <ul>
 *   <li>{@code pain001V03Generator} — pain.001.001.03 (SEPA Credit Transfer standard)</li>
 *   <li>{@code pain001V09Generator} — pain.001.001.09 (SEPA Credit Transfer Instant)</li>
 * </ul>
 *
 * <p>Le consommateur choisit la version en injectant le bean souhaité :
 * <pre>
 *   {@literal @}Autowired
 *   {@literal @}Qualifier("pain001V03Generator")
 *   private PainGenerator&lt;SepaTransferRequest&gt; generator;
 * </pre>
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "mg-bank.pain", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(PainProperties.class)
public class PainAutoConfiguration {

    @Bean("pain001V03Generator")
    public PainGenerator<SepaTransferRequest> pain001V03Generator(PainProperties properties) {
        return new Pain001V03Generator(properties);
    }

    @Bean("pain001V09Generator")
    public PainGenerator<SepaTransferRequest> pain001V09Generator(PainProperties properties) {
        return new Pain001V09Generator(properties);
    }

    @Bean("pain008V02CoreGenerator")
    public PainGenerator<SepaDirectDebitRequest> pain008V02CoreGenerator(PainProperties properties) {
        return new Pain008V02Generator(properties, "CORE");
    }

    @Bean("pain008V02B2bGenerator")
    public PainGenerator<SepaDirectDebitRequest> pain008V02B2bGenerator(PainProperties properties) {
        return new Pain008V02Generator(properties, "B2B");
    }
}
