package mg.pain.generator.pain008;

import mg.pain.autoconfigure.PainProperties;
import mg.pain.domain.PainGenerationResult;
import mg.pain.domain.SepaDirectDebitRequest;
import mg.pain.exception.PainGenerationException;
import mg.pain.generator.PainGenerator;
import mg.pain.jaxb.pain008v02.*;
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
 * Générateur de fichiers pain.008.001.02 (SEPA Direct Debit).
 *
 * <p>Supporte les deux instruments locaux SEPA :
 * <ul>
 *   <li>CORE — prélèvement standard (particuliers et entreprises)</li>
 *   <li>B2B  — prélèvement interentreprises uniquement</li>
 * </ul>
 *
 * <p>Toutes les requêtes d'un même appel doivent partager le même
 * {@code collectionDate} et {@code sequenceType} (contrainte SEPA au niveau PmtInf).
 */
public class Pain008V02Generator implements PainGenerator<SepaDirectDebitRequest> {

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Document.class);
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final String SCHEMA_LOCATION =
            "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02 pain.008.001.02.xsd";

    private final PainProperties properties;

    /** Instrument local SEPA : "CORE" ou "B2B". */
    private final String localInstrument;

    public Pain008V02Generator(PainProperties properties, String localInstrument) {
        this.properties = properties;
        this.localInstrument = localInstrument;
    }

    // -------------------------------------------------------------------------
    // Interface PainGenerator
    // -------------------------------------------------------------------------

    @Override
    public PainGenerationResult generate(List<SepaDirectDebitRequest> requests, String messageId) {
        try {
            Document document = buildDocument(requests, messageId);
            byte[] content = marshal(document);
            BigDecimal controlSum = computeControlSum(requests);
            return new PainGenerationResult(
                    content,
                    "prelevements_" + messageId + ".xml",
                    messageId,
                    requests.size(),
                    controlSum
            );
        } catch (PainGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new PainGenerationException("Erreur lors de la génération pain.008.001.02", e);
        }
    }

    @Override
    public void generateToStream(List<SepaDirectDebitRequest> requests, String messageId, OutputStream out) {
        try {
            Document document = buildDocument(requests, messageId);
            marshalToStream(document, out);
        } catch (PainGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new PainGenerationException("Erreur lors de la génération pain.008.001.02", e);
        }
    }

    @Override
    public void generateToFile(List<SepaDirectDebitRequest> requests, String messageId, Path outputPath) {
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

    private Document buildDocument(List<SepaDirectDebitRequest> requests, String messageId)
            throws Exception {
        Document document = new Document();
        CustomerDirectDebitInitiationV02 initn = new CustomerDirectDebitInitiationV02();
        document.setCstmrDrctDbtInitn(initn);

        BigDecimal controlSum = computeControlSum(requests);
        String nbTransactions = String.valueOf(requests.size());

        initn.setGrpHdr(buildHeader(messageId, nbTransactions, controlSum));
        initn.getPmtInf().add(buildPaymentInstruction(messageId, nbTransactions, controlSum, requests));

        return document;
    }

    private GroupHeader39 buildHeader(String messageId, String nbTransactions,
                                      BigDecimal controlSum) throws Exception {
        GroupHeader39 header = new GroupHeader39();
        header.setMsgId(SepaStringUtils.normalizeId(messageId));
        header.setCreDtTm(XmlCalendarUtils.nowDateTime());
        header.setNbOfTxs(nbTransactions);
        header.setCtrlSum(controlSum);
        header.setInitgPty(buildParty(properties.getCreditor().getName()));
        return header;
    }

    private PaymentInstructionInformation4 buildPaymentInstruction(
            String messageId, String nbTransactions, BigDecimal controlSum,
            List<SepaDirectDebitRequest> requests) throws Exception {

        PaymentInstructionInformation4 payment = new PaymentInstructionInformation4();
        payment.setPmtInfId(messageId + "-1");
        payment.setPmtMtd(PaymentMethod2Code.DD);
        payment.setNbOfTxs(nbTransactions);
        payment.setCtrlSum(controlSum);
        payment.setPmtTpInf(buildPaymentType(requests.get(0).getSequenceType()));
        payment.setReqdColltnDt(XmlCalendarUtils.fromLocalDate(requests.get(0).getCollectionDate()));
        payment.setCdtr(buildParty(properties.getCreditor().getName()));
        payment.setCdtrAcct(buildAccount(properties.getCreditor().getIban()));
        payment.setCdtrAgt(buildBic(properties.getCreditor().getBic()));
        payment.setChrgBr(ChargeBearerType1Code.SLEV);

        for (int i = 0; i < requests.size(); i++) {
            payment.getDrctDbtTxInf().add(buildDirectDebitTransaction(messageId, i, requests.get(i)));
        }

        return payment;
    }

    private PaymentTypeInformation20 buildPaymentType(String sequenceType) {
        PaymentTypeInformation20 paymentType = new PaymentTypeInformation20();

        ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
        serviceLevel.setCd("SEPA");
        paymentType.setSvcLvl(serviceLevel);

        LocalInstrument2Choice lclInstrm = new LocalInstrument2Choice();
        lclInstrm.setCd(localInstrument);
        paymentType.setLclInstrm(lclInstrm);

        paymentType.setSeqTp(SequenceType3Code.fromValue(sequenceType));

        return paymentType;
    }

    private DirectDebitTransactionInformation9 buildDirectDebitTransaction(
            String messageId, int index, SepaDirectDebitRequest request) throws Exception {

        DirectDebitTransactionInformation9 tx = new DirectDebitTransactionInformation9();

        PaymentIdentification1 pmtId = new PaymentIdentification1();
        pmtId.setInstrId(SepaStringUtils.normalizeId(messageId + "/" + (index + 1)));
        pmtId.setEndToEndId(SepaStringUtils.normalizeId(request.getEndToEndId()));
        tx.setPmtId(pmtId);

        ActiveOrHistoricCurrencyAndAmount amount = new ActiveOrHistoricCurrencyAndAmount();
        amount.setCcy(request.getCurrency() != null ? request.getCurrency() : "EUR");
        amount.setValue(request.getAmount());
        tx.setInstdAmt(amount);

        MandateRelatedInformation6 mandate = new MandateRelatedInformation6();
        mandate.setMndtId(SepaStringUtils.normalizeId(request.getMandateId()));
        if (request.getMandateDate() != null) {
            mandate.setDtOfSgntr(XmlCalendarUtils.fromLocalDate(request.getMandateDate()));
        }
        DirectDebitTransaction6 drctDbtTx = new DirectDebitTransaction6();
        drctDbtTx.setMndtRltdInf(mandate);
        tx.setDrctDbtTx(drctDbtTx);

        tx.setDbtrAgt(buildBic(request.getDebtorBic()));
        tx.setDbtr(buildParty(SepaStringUtils.normalizeName(request.getDebtorName())));
        tx.setDbtrAcct(buildAccount(request.getDebtorIban()));

        if (request.getRemittanceInfo() != null) {
            RemittanceInformation5 rmtInf = new RemittanceInformation5();
            rmtInf.setUstrd(SepaStringUtils.normalizeRemittanceInfo(request.getRemittanceInfo()));
            tx.setRmtInf(rmtInf);
        }

        return tx;
    }

    // -------------------------------------------------------------------------
    // Helpers JAXB
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

    private BigDecimal computeControlSum(List<SepaDirectDebitRequest> requests) {
        return requests.stream()
                .map(SepaDirectDebitRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
