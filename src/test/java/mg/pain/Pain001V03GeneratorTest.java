package mg.pain;

import mg.pain.autoconfigure.PainProperties;
import mg.pain.domain.PainGenerationResult;
import mg.pain.domain.SepaTransferRequest;
import mg.pain.generator.pain001.Pain001V03Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Pain001V03GeneratorTest {

    private Pain001V03Generator generator;

    @BeforeEach
    void setUp() {
        PainProperties properties = new PainProperties();
        properties.getDebtor().setName("SOCIÉTÉ TEST SAS");
        properties.getDebtor().setIban("FR7630004000031234567890143");
        properties.getDebtor().setBic("BNPAFRPPXXX");
        generator = new Pain001V03Generator(properties);
    }

    @Test
    void generate_shouldReturnValidXml() {
        List<SepaTransferRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("150.00"), "DUPONT JEAN",
                        "FR7630006000011234567890189", "AGRIFRPP882", "Remboursement A123"),
                buildRequest("E2E-002", new BigDecimal("75.50"), "MARTIN SOPHIE",
                        "FR7614508059405133736269085", "CMCIFRPP", "Remboursement A124")
        );

        PainGenerationResult result = generator.generate(requests, "MSG20260917001");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getMessageId()).isEqualTo("MSG20260917001");
        assertThat(result.getTransactionCount()).isEqualTo(2);
        assertThat(result.getControlSum()).isEqualByComparingTo(new BigDecimal("225.50"));
        assertThat(result.getFilename()).isEqualTo("virements_MSG20260917001.xml");
    }

    @Test
    void generate_xmlShouldContainExpectedStructure() {
        List<SepaTransferRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("100.00"), "DUPONT JEAN",
                        "FR7630006000011234567890189", "AGRIFRPP882", "Libellé test")
        );

        PainGenerationResult result = generator.generate(requests, "MSGTEST001");
        String xml = new String(result.getContent());

        assertThat(xml).contains("pain.001.001.03");
        assertThat(xml).contains("<MsgId>MSGTEST001</MsgId>");
        assertThat(xml).contains("<NbOfTxs>1</NbOfTxs>");
        assertThat(xml).contains("<CtrlSum>100.00</CtrlSum>");
        assertThat(xml).contains("<EndToEndId>E2E-001</EndToEndId>");
        assertThat(xml).contains("Ccy=\"EUR\"");
        assertThat(xml).contains("DUPONT JEAN");
        assertThat(xml).contains("FR7630006000011234567890189");
        assertThat(xml).contains("AGRIFRPP882");
        assertThat(xml).contains("Libellé test");
    }

    @Test
    void generate_controlSumShouldMatchSumOfAmounts() {
        List<SepaTransferRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("100.00"), "A", "FR76300040000312345678", "BBBBB", "ref1"),
                buildRequest("E2E-002", new BigDecimal("200.50"), "B", "FR76300040000312345678", "BBBBB", "ref2"),
                buildRequest("E2E-003", new BigDecimal("50.25"), "C", "FR76300040000312345678", "BBBBB", "ref3")
        );

        PainGenerationResult result = generator.generate(requests, "MSG003");

        assertThat(result.getControlSum()).isEqualByComparingTo(new BigDecimal("350.75"));
    }

    @Test
    void generateToFile_shouldWriteXmlFile(@TempDir Path tempDir) throws Exception {
        List<SepaTransferRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("100.00"), "DUPONT JEAN",
                        "FR7630006000011234567890189", "AGRIFRPP882", "Test fichier")
        );
        Path outputFile = tempDir.resolve("virements_test.xml");

        generator.generateToFile(requests, "MSGFILE001", outputFile);

        assertThat(outputFile).exists();
        String content = Files.readString(outputFile);
        assertThat(content).contains("MSGFILE001");
        assertThat(content).contains("pain.001.001.03");
    }

    @Test
    void generate_remittanceInfoShouldBeTruncatedAt140Chars() {
        String longRemittance = "A".repeat(200);
        List<SepaTransferRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("100.00"), "DUPONT JEAN",
                        "FR7630006000011234567890189", "AGRIFRPP882", longRemittance)
        );

        PainGenerationResult result = generator.generate(requests, "MSGTRUNC001");
        String xml = new String(result.getContent());

        assertThat(xml).contains("A".repeat(140));
        assertThat(xml).doesNotContain("A".repeat(141));
    }

    @Test
    void generate_pmtInfIdShouldBeDifferentFromMsgId() {
        List<SepaTransferRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("50.00"), "DUPONT JEAN",
                        "FR7630006000011234567890189", "AGRIFRPP882", "ref")
        );

        PainGenerationResult result = generator.generate(requests, "MSGID001");
        String xml = new String(result.getContent());

        assertThat(xml).contains("<MsgId>MSGID001</MsgId>");
        assertThat(xml).contains("<PmtInfId>MSGID001-1</PmtInfId>");
    }

    // -------------------------------------------------------------------------

    private SepaTransferRequest buildRequest(String endToEndId, BigDecimal amount,
                                             String creditorName, String creditorIban,
                                             String creditorBic, String remittanceInfo) {
        return new SepaTransferRequest(endToEndId, amount, "EUR",
                creditorName, creditorIban, creditorBic, remittanceInfo);
    }
}
