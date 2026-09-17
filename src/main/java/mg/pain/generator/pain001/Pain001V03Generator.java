package mg.pain.generator.pain001;

import mg.pain.autoconfigure.PainProperties;
import mg.pain.domain.PainGenerationResult;
import mg.pain.domain.SepaTransferRequest;
import mg.pain.exception.PainGenerationException;
import mg.pain.generator.PainGenerator;
import mg.pain.jaxb.pain001v03.*;
import mg.pain.util.SepaStringUtils;
import mg.pain.util.XmlCalendarUtils;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Générateur de fichiers pain.001.001.03 (SEPA Credit Transfer).
 * Implémentation corrigée et découplée de tout domaine externe.
 */
public class Pain001V03Generator implements PainGenerator<SepaTransferRequest> {

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Document.class);
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final String SCHEMA_LOCATION =
            "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03 pain.001.001.03.xsd";

    private final PainProperties properties;

    public Pain001V03Generator(PainProperties properties) {
        this.properties = properties;
    }

    // -------------------------------------------------------------------------
    // Interface PainGenerator
    // -------------------------------------------------------------------------

    @Override
    public PainGenerationResult generate(List<SepaTransferRequest> requests, String messageId) {
        try {
            Document document = buildDocument(requests, messageId);
            byte[] content = marshal(document);
            BigDecimal controlSum = computeControlSum(requests);
            return new PainGenerationResult(
                    content,
                    "virements_" + messageId + ".xml",
                    messageId,
                    requests.size(),
                    controlSum
            );
        } catch (PainGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new PainGenerationException("Erreur lors de la génération pain.001.001.03", e);
        }
    }

    @Override
    public void generateToStream(List<SepaTransferRequest> requests, String messageId, OutputStream out) {
        try {
            Document document = buildDocument(requests, messageId);
            marshalToStream(document, out);
        } catch (PainGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new PainGenerationException("Erreur lors de la génération pain.001.001.03", e);
        }
    }

    @Override
    public void generateToFile(List<SepaTransferRequest> requests, String messageId, Path outputPath) {
        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
            try (OutputStream out = Files.newOutputStream(outputPath)) {
                generateToStream(requests, messageId, out);
            }
        } catch (PainGenerationException e) {
            throw e;
        } catch (IOException e) {
            throw new PainGenerationException("Erreur d'écriture du fichier : " + outputPath, e);
        }
    }

    // -------------------------------------------------------------------------
    // Construction du document
    // -------------------------------------------------------------------------

    private Document buildDocument(List<SepaTransferRequest> requests, String messageId)
            throws Exception {
        Document document = new Document();
        CustomerCreditTransferInitiationV03 initn = new CustomerCreditTransferInitiationV03();
        document.setCstmrCdtTrfInitn(initn);

        BigDecimal controlSum = computeControlSum(requests);
        String nbTransactions = String.valueOf(requests.size());

        // 1. En-tête
        initn.setGrpHdr(buildHeader(messageId, nbTransactions, controlSum));

        // 2. Instruction de paiement (débit + credits)
        PaymentInstructionInformation3 payment = buildPaymentInstruction(
                messageId, nbTransactions, controlSum);
        for (int i = 0; i < requests.size(); i++) {
            payment.getCdtTrfTxInf().add(buildCreditTransfer(messageId, i, requests.get(i)));
        }
        initn.getPmtInf().add(payment);

        return document;
    }

    private GroupHeader32 buildHeader(String messageId, String nbTransactions,
                                      BigDecimal controlSum) throws Exception {
        GroupHeader32 header = new GroupHeader32();
        header.setMsgId(SepaStringUtils.normalizeId(messageId));
        header.setCreDtTm(XmlCalendarUtils.nowDateTime());
        header.setNbOfTxs(nbTransactions);
        header.setCtrlSum(controlSum);
        header.setInitgPty(buildParty(properties.getDebtor().getName()));
        return header;
    }

    private PaymentInstructionInformation3 buildPaymentInstruction(String messageId,
                                                                    String nbTransactions,
                                                                    BigDecimal controlSum)
            throws Exception {
        PaymentInstructionInformation3 payment = new PaymentInstructionInformation3();
        payment.setPmtInfId(messageId + "-1");
        payment.setPmtMtd(PaymentMethod3Code.TRF);
        payment.setNbOfTxs(nbTransactions);
        payment.setCtrlSum(controlSum);

        PaymentTypeInformation19 paymentType = new PaymentTypeInformation19();
        ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
        serviceLevel.setCd("SEPA");
        paymentType.setSvcLvl(serviceLevel);
        payment.setPmtTpInf(paymentType);

        payment.setReqdExctnDt(XmlCalendarUtils.nowDate());
        payment.setDbtr(buildParty(properties.getDebtor().getName()));
        payment.setDbtrAcct(buildAccount(properties.getDebtor().getIban()));
        payment.setDbtrAgt(buildBic(properties.getDebtor().getBic()));
        payment.setChrgBr(ChargeBearerType1Code.SLEV);

        return payment;
    }

    private CreditTransferTransactionInformation10 buildCreditTransfer(String messageId,
                                                                        int index,
                                                                        SepaTransferRequest request) {
        CreditTransferTransactionInformation10 tx = new CreditTransferTransactionInformation10();

        // Identification
        PaymentIdentification1 paymentId = new PaymentIdentification1();
        paymentId.setInstrId(SepaStringUtils.normalizeId(messageId + "/" + (index + 1)));
        paymentId.setEndToEndId(SepaStringUtils.normalizeId(request.getEndToEndId()));
        tx.setPmtId(paymentId);

        // Montant
        AmountType3Choice amountType = new AmountType3Choice();
        ActiveOrHistoricCurrencyAndAmount amount = new ActiveOrHistoricCurrencyAndAmount();
        amount.setCcy(request.getCurrency() != null ? request.getCurrency() : "EUR");
        amount.setValue(request.getAmount());
        amountType.setInstdAmt(amount);
        tx.setAmt(amountType);

        // Bénéficiaire
        tx.setCdtrAgt(buildBic(request.getCreditorBic()));
        tx.setCdtr(buildParty(SepaStringUtils.normalizeName(request.getCreditorName())));
        tx.setCdtrAcct(buildAccount(request.getCreditorIban()));

        // Libellé
        RemittanceInformation5 rmtInfo = new RemittanceInformation5();
        rmtInfo.setUstrd(SepaStringUtils.normalizeRemittanceInfo(request.getRemittanceInfo()));
        tx.setRmtInf(rmtInfo);

        return tx;
    }

    // -------------------------------------------------------------------------
    // Helpers de construction JAXB
    // -------------------------------------------------------------------------

    private PartyIdentification32 buildParty(String name) {
        PartyIdentification32 party = new PartyIdentification32();
        party.setNm(name);
        return party;
    }

    private CashAccount16 buildAccount(String iban) {
        CashAccount16 account = new CashAccount16();
        AccountIdentification4Choice accountId = new AccountIdentification4Choice();
        accountId.setIBAN(iban);
        account.setId(accountId);
        return account;
    }

    private BranchAndFinancialInstitutionIdentification4 buildBic(String bic) {
        BranchAndFinancialInstitutionIdentification4 institution =
                new BranchAndFinancialInstitutionIdentification4();
        FinancialInstitutionIdentification7 finInstnId = new FinancialInstitutionIdentification7();
        finInstnId.setBIC(bic);
        institution.setFinInstnId(finInstnId);
        return institution;
    }

    // -------------------------------------------------------------------------
    // Marshalling
    // -------------------------------------------------------------------------

    private byte[] marshal(Document document) throws JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshalToStream(document, baos);
        return baos.toByteArray();
    }

    private void marshalToStream(Document document, OutputStream out) throws JAXBException {
        Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, properties.getOutput().getCharset());
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA_LOCATION);
        marshaller.marshal(document, out);
    }

    // -------------------------------------------------------------------------
    // Calcul
    // -------------------------------------------------------------------------

    private BigDecimal computeControlSum(List<SepaTransferRequest> requests) {
        return requests.stream()
                .map(SepaTransferRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
