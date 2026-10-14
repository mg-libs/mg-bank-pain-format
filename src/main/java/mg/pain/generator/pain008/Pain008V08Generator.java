package mg.pain.generator.pain008;

import mg.pain.autoconfigure.PainProperties;
import mg.pain.domain.AddressFormat;
import mg.pain.domain.PainGenerationResult;
import mg.pain.domain.SepaDirectDebitRequest;
import mg.pain.exception.PainGenerationException;
import mg.pain.generator.PainGenerator;
import mg.pain.jaxb.pain008v08.*;
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
 * Générateur de fichiers pain.008.001.08 (SEPA Direct Debit — version courante).
 *
 * <p>Différences principales avec pain.008.001.02 :
 * <ul>
 *   <li>BICFI remplace BIC pour l'identification des établissements financiers.</li>
 *   <li>PostalAddress24 (adresses structurées ou hybrides).</li>
 *   <li>SvcLvl est une liste dans PaymentTypeInformation29.</li>
 *   <li>RemittanceInformation16 (Ustrd en liste).</li>
 * </ul>
 *
 * <p>Toutes les requêtes d'un même appel doivent partager le même
 * {@code collectionDate} et {@code sequenceType} (contrainte SEPA au niveau PmtInf).
 */
public class Pain008V08Generator implements PainGenerator<SepaDirectDebitRequest> {

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Document.class);
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final String SCHEMA_LOCATION =
            "urn:iso:std:iso:20022:tech:xsd:pain.008.001.08 pain.008.001.08.xsd";

    private final PainProperties properties;
    private final String localInstrument;

    public Pain008V08Generator(PainProperties properties, String localInstrument) {
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
            throw new PainGenerationException("Erreur lors de la génération pain.008.001.08", e);
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
            throw new PainGenerationException("Erreur lors de la génération pain.008.001.08", e);
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
        CustomerDirectDebitInitiationV08 initn = new CustomerDirectDebitInitiationV08();
        document.setCstmrDrctDbtInitn(initn);

        BigDecimal controlSum = computeControlSum(requests);
        String nbTransactions = String.valueOf(requests.size());

        initn.setGrpHdr(buildHeader(messageId, nbTransactions, controlSum));
        initn.getPmtInf().add(buildPaymentInstruction(messageId, nbTransactions, controlSum, requests));

        return document;
    }

    private GroupHeader83 buildHeader(String messageId, String nbTransactions,
                                      BigDecimal controlSum) throws Exception {
        GroupHeader83 header = new GroupHeader83();
        header.setMsgId(SepaStringUtils.normalizeId(messageId));
        header.setCreDtTm(XmlCalendarUtils.nowDateTime());
        header.setNbOfTxs(nbTransactions);
        header.setCtrlSum(controlSum);
        header.setInitgPty(buildParty(properties.getCreditor().getName(),
                properties.getCreditor().getCountry(), properties.getCreditor().getTownName(),
                properties.getCreditor().getPostalCode(),
                properties.getCreditor().getAddressLine1(), properties.getCreditor().getAddressLine2(),
                properties.getCreditor().getStreetName(), properties.getCreditor().getBuildingNumber()));
        return header;
    }

    private PaymentInstruction43 buildPaymentInstruction(
            String messageId, String nbTransactions, BigDecimal controlSum,
            List<SepaDirectDebitRequest> requests) throws Exception {

        PaymentInstruction43 payment = new PaymentInstruction43();
        payment.setPmtInfId(messageId + "-1");
        payment.setPmtMtd(PaymentMethod2Code.DD);
        payment.setBtchBookg(properties.getCreditor().isBatchBooking());
        payment.setNbOfTxs(nbTransactions);
        payment.setCtrlSum(controlSum);
        payment.setPmtTpInf(buildPaymentType(requests.get(0).getSequenceType()));
        payment.setReqdColltnDt(XmlCalendarUtils.fromLocalDate(requests.get(0).getCollectionDate()));

        payment.setCdtr(buildParty(properties.getCreditor().getName(),
                properties.getCreditor().getCountry(), properties.getCreditor().getTownName(),
                properties.getCreditor().getPostalCode(),
                properties.getCreditor().getAddressLine1(), properties.getCreditor().getAddressLine2(),
                properties.getCreditor().getStreetName(), properties.getCreditor().getBuildingNumber()));
        payment.setCdtrAcct(buildAccount(properties.getCreditor().getIban()));
        payment.setCdtrAgt(buildBicfi(properties.getCreditor().getBic()));
        payment.setChrgBr(ChargeBearerType1Code.SLEV);

        for (int i = 0; i < requests.size(); i++) {
            payment.getDrctDbtTxInf().add(buildTransaction(messageId, i, requests.get(i)));
        }

        return payment;
    }

    private PaymentTypeInformation29 buildPaymentType(String sequenceType) {
        PaymentTypeInformation29 paymentType = new PaymentTypeInformation29();

        ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
        serviceLevel.setCd("SEPA");
        paymentType.getSvcLvl().add(serviceLevel);

        LocalInstrument2Choice lclInstrm = new LocalInstrument2Choice();
        lclInstrm.setCd(localInstrument);
        paymentType.setLclInstrm(lclInstrm);

        paymentType.setSeqTp(SequenceType3Code.fromValue(sequenceType));

        return paymentType;
    }

    private DirectDebitTransactionInformation24 buildTransaction(
            String messageId, int index, SepaDirectDebitRequest request) throws Exception {

        DirectDebitTransactionInformation24 tx = new DirectDebitTransactionInformation24();

        PaymentIdentification13 pmtId = new PaymentIdentification13();
        pmtId.setInstrId(SepaStringUtils.normalizeId(messageId + "/" + (index + 1)));
        pmtId.setEndToEndId(SepaStringUtils.normalizeId(request.getEndToEndId()));
        tx.setPmtId(pmtId);

        ActiveOrHistoricCurrencyAndAmount amount = new ActiveOrHistoricCurrencyAndAmount();
        amount.setCcy(request.getCurrency() != null ? request.getCurrency() : "EUR");
        amount.setValue(request.getAmount());
        tx.setInstdAmt(amount);

        MandateRelatedInformation14 mandate = new MandateRelatedInformation14();
        mandate.setMndtId(SepaStringUtils.normalizeId(request.getMandateId()));
        if (request.getMandateDate() != null) {
            mandate.setDtOfSgntr(XmlCalendarUtils.fromLocalDate(request.getMandateDate()));
        }
        mandate.setAmdmntInd(false);
        DirectDebitTransaction10 drctDbtTx = new DirectDebitTransaction10();
        drctDbtTx.setMndtRltdInf(mandate);
        tx.setDrctDbtTx(drctDbtTx);

        if (request.getUltimateCreditorName() != null) {
            tx.setUltmtCdtr(buildParty(request.getUltimateCreditorName(), null, null, null, null, null, null, null));
        }

        tx.setDbtrAgt(buildBicfi(request.getDebtorBic()));
        tx.setDbtr(buildParty(request.getDebtorName(),
                request.getDebtorCountry(), request.getDebtorTownName(), request.getDebtorPostalCode(),
                request.getDebtorAddressLine1(), request.getDebtorAddressLine2(),
                request.getDebtorStreetName(), request.getDebtorBuildingNumber()));
        tx.setDbtrAcct(buildAccount(request.getDebtorIban()));

        if (request.getRemittanceInfo() != null) {
            RemittanceInformation16 rmtInf = new RemittanceInformation16();
            rmtInf.getUstrd().add(SepaStringUtils.normalizeRemittanceInfo(request.getRemittanceInfo()));
            tx.setRmtInf(rmtInf);
        }

        return tx;
    }

    // -------------------------------------------------------------------------
    // Helpers JAXB
    // -------------------------------------------------------------------------

    private PartyIdentification135 buildParty(String name,
                                               String country, String townName, String postalCode,
                                               String addressLine1, String addressLine2,
                                               String streetName, String buildingNumber) {
        PartyIdentification135 party = new PartyIdentification135();
        party.setNm(SepaStringUtils.normalizeName(name));
        PostalAddress24 addr = buildAddress(country, townName, postalCode,
                addressLine1, addressLine2, streetName, buildingNumber);
        if (addr != null) party.setPstlAdr(addr);
        return party;
    }

    private CashAccount38 buildAccount(String iban) {
        CashAccount38 account = new CashAccount38();
        AccountIdentification4Choice accountId = new AccountIdentification4Choice();
        accountId.setIBAN(iban);
        account.setId(accountId);
        return account;
    }

    private BranchAndFinancialInstitutionIdentification6 buildBicfi(String bicfi) {
        BranchAndFinancialInstitutionIdentification6 institution =
                new BranchAndFinancialInstitutionIdentification6();
        FinancialInstitutionIdentification18 finInstnId = new FinancialInstitutionIdentification18();
        finInstnId.setBICFI(bicfi);
        institution.setFinInstnId(finInstnId);
        return institution;
    }

    /**
     * Construit un PostalAddress24 à partir du format global défini dans PainProperties.
     * Retourne null si le format n'est pas configuré ou si country/townName sont absents.
     */
    private PostalAddress24 buildAddress(String country, String townName, String postalCode,
                                         String addressLine1, String addressLine2,
                                         String streetName, String buildingNumber) {
        AddressFormat format = properties.getAddressFormat();
        if (format == null || country == null || townName == null) return null;

        PostalAddress24 addr = new PostalAddress24();
        addr.setCtry(country);
        addr.setTwnNm(SepaStringUtils.truncate(townName, 35));
        if (postalCode != null) addr.setPstCd(SepaStringUtils.truncate(postalCode, 16));

        if (format == AddressFormat.HYBRID) {
            if (addressLine1 != null) addr.getAdrLine().add(SepaStringUtils.truncate(addressLine1, 70));
            if (addressLine2 != null) addr.getAdrLine().add(SepaStringUtils.truncate(addressLine2, 70));
        } else {
            if (streetName != null) addr.setStrtNm(SepaStringUtils.truncate(streetName, 70));
            if (buildingNumber != null) addr.setBldgNb(SepaStringUtils.truncate(buildingNumber, 16));
        }
        return addr;
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
