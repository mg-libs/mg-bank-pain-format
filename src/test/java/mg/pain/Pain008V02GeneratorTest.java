package mg.pain;

import mg.pain.autoconfigure.PainProperties;
import mg.pain.domain.PainGenerationResult;
import mg.pain.domain.SepaDirectDebitRequest;
import mg.pain.generator.pain008.Pain008V02Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Pain008V02GeneratorTest {

    private Pain008V02Generator coreGenerator;
    private Pain008V02Generator b2bGenerator;

    @BeforeEach
    void setUp() {
        PainProperties properties = new PainProperties();
        properties.getCreditor().setName("MA SOCIÉTÉ SAS");
        properties.getCreditor().setIban("FR7630004000031234567890143");
        properties.getCreditor().setBic("BNPAFRPPXXX");
        properties.getCreditor().setCreditorId("FR72ZZZ123456");
        coreGenerator = new Pain008V02Generator(properties, "CORE");
        b2bGenerator  = new Pain008V02Generator(properties, "B2B");
    }

    @Test
    void generate_shouldReturnValidXml() {
        List<SepaDirectDebitRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("150.00"), "DUPONT JEAN",
                        "FR7630006000011234567890189", "AGRIFRPP882",
                        "MND-2025-001", LocalDate.of(2025, 1, 15),
                        LocalDate.now().plusDays(5), "RCUR")
        );

        PainGenerationResult result = coreGenerator.generate(requests, "SDD20260917001");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getMessageId()).isEqualTo("SDD20260917001");
        assertThat(result.getTransactionCount()).isEqualTo(1);
        assertThat(result.getControlSum()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(result.getFilename()).isEqualTo("prelevements_SDD20260917001.xml");
    }

    @Test
    void generate_xmlShouldContainExpectedStructure() {
        List<SepaDirectDebitRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("100.00"), "DUPONT JEAN",
                        "FR7630006000011234567890189", "AGRIFRPP882",
                        "MND-2025-001", LocalDate.of(2025, 1, 15),
                        LocalDate.of(2026, 10, 1), "FRST")
        );

        PainGenerationResult result = coreGenerator.generate(requests, "SDDTEST001");
        String xml = new String(result.getContent());

        assertThat(xml).contains("pain.008.001.02");
        assertThat(xml).contains("<MsgId>SDDTEST001</MsgId>");
        assertThat(xml).contains("<NbOfTxs>1</NbOfTxs>");
        assertThat(xml).contains("<CtrlSum>100.00</CtrlSum>");
        assertThat(xml).contains("<PmtMtd>DD</PmtMtd>");
        assertThat(xml).contains("<Cd>SEPA</Cd>");
        assertThat(xml).contains("<Cd>CORE</Cd>");
        assertThat(xml).contains("<SeqTp>FRST</SeqTp>");
        assertThat(xml).contains("<ReqdColltnDt>2026-10-01</ReqdColltnDt>");
        assertThat(xml).contains("<EndToEndId>E2E-001</EndToEndId>");
        assertThat(xml).contains("Ccy=\"EUR\"");
        assertThat(xml).contains("DUPONT JEAN");
        assertThat(xml).contains("MND-2025-001");
        assertThat(xml).contains("<DtOfSgntr>2025-01-15</DtOfSgntr>");
        assertThat(xml).contains("FR7630006000011234567890189");
        assertThat(xml).contains("AGRIFRPP882");
    }

    @Test
    void generate_b2b_shouldContainB2bInstrument() {
        List<SepaDirectDebitRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("500.00"), "SOCIÉTÉ ABC",
                        "FR7614508059405133736269085", "CMCIFRPP",
                        "MND-B2B-001", LocalDate.of(2024, 6, 1),
                        LocalDate.now().plusDays(3), "RCUR")
        );

        PainGenerationResult result = b2bGenerator.generate(requests, "B2BTEST001");
        String xml = new String(result.getContent());

        assertThat(xml).contains("<Cd>B2B</Cd>");
        assertThat(xml).doesNotContain("<Cd>CORE</Cd>");
    }

    @Test
    void generate_controlSumShouldMatchSumOfAmounts() {
        List<SepaDirectDebitRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("100.00"), "A", "FR76300040000312345678", "BBBBB",
                        "MND-001", LocalDate.of(2025, 1, 1), LocalDate.now().plusDays(5), "RCUR"),
                buildRequest("E2E-002", new BigDecimal("200.50"), "B", "FR76300040000312345678", "BBBBB",
                        "MND-002", LocalDate.of(2025, 1, 1), LocalDate.now().plusDays(5), "RCUR"),
                buildRequest("E2E-003", new BigDecimal("50.25"), "C", "FR76300040000312345678", "BBBBB",
                        "MND-003", LocalDate.of(2025, 1, 1), LocalDate.now().plusDays(5), "RCUR")
        );

        PainGenerationResult result = coreGenerator.generate(requests, "SDD003");

        assertThat(result.getControlSum()).isEqualByComparingTo(new BigDecimal("350.75"));
        assertThat(result.getTransactionCount()).isEqualTo(3);
    }

    @Test
    void generateToFile_shouldWriteXmlFile(@TempDir Path tempDir) throws Exception {
        List<SepaDirectDebitRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("100.00"), "DUPONT JEAN",
                        "FR7630006000011234567890189", "AGRIFRPP882",
                        "MND-001", LocalDate.of(2025, 1, 15),
                        LocalDate.now().plusDays(5), "OOFF")
        );
        Path outputFile = tempDir.resolve("prelevements_test.xml");

        coreGenerator.generateToFile(requests, "SDDFILE001", outputFile);

        assertThat(outputFile).exists();
        String content = Files.readString(outputFile);
        assertThat(content).contains("SDDFILE001");
        assertThat(content).contains("pain.008.001.02");
        assertThat(content).contains("<Cd>CORE</Cd>");
    }

    @Test
    void generate_pmtInfIdShouldBeDifferentFromMsgId() {
        List<SepaDirectDebitRequest> requests = List.of(
                buildRequest("E2E-001", new BigDecimal("50.00"), "DUPONT JEAN",
                        "FR7630006000011234567890189", "AGRIFRPP882",
                        "MND-001", LocalDate.of(2025, 1, 1),
                        LocalDate.now().plusDays(5), "RCUR")
        );

        PainGenerationResult result = coreGenerator.generate(requests, "SDDID001");
        String xml = new String(result.getContent());

        assertThat(xml).contains("<MsgId>SDDID001</MsgId>");
        assertThat(xml).contains("<PmtInfId>SDDID001-1</PmtInfId>");
    }

    // -------------------------------------------------------------------------

    private SepaDirectDebitRequest buildRequest(String endToEndId, BigDecimal amount,
                                                String debtorName, String debtorIban,
                                                String debtorBic, String mandateId,
                                                LocalDate mandateDate, LocalDate collectionDate,
                                                String sequenceType) {
        return new SepaDirectDebitRequest(
                endToEndId, amount, "EUR",
                debtorName, debtorIban, debtorBic,
                "Prélèvement " + endToEndId,
                mandateId, mandateDate, collectionDate, sequenceType
        );
    }
}
