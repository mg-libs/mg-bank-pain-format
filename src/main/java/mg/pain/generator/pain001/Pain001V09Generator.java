package mg.pain.generator.pain001;

import mg.pain.autoconfigure.PainProperties;
import mg.pain.domain.AddressFormat;
import mg.pain.domain.PainGenerationResult;
import mg.pain.domain.SepaTransferRequest;

import mg.pain.exception.PainGenerationException;
import mg.pain.generator.PainGenerator;
import mg.pain.jaxb.pain001v09.*;
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
 * Générateur de fichiers pain.001.001.09 (SEPA Credit Transfer — version montante).
 * Requis pour SCT Inst (virement instantané SEPA).
 *
 * <p>Différences principales avec V03 :
 * <ul>
 *   <li>{@code ReqdExctnDt} est un {@link DateAndDateTime2Choice} (date ou dateTime)</li>
 *   <li>{@code BICFI} remplace {@code BIC} dans FinancialInstitutionIdentification</li>
 *   <li>Classes JAXB versionnées (GroupHeader85, PaymentInstruction30, etc.)</li>
 * </ul>
 */
public class Pain001V09Generator implements PainGenerator<SepaTransferRequest> {

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Document.class);
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final String SCHEMA_LOCATION =
            "urn:iso:std:iso:20022:tech:xsd:pain.001.001.09 pain.001.001.09.xsd";

    private final PainProperties properties;

    public Pain001V09Generator(PainProperties properties) {
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
            throw new PainGenerationException("Erreur lors de la génération pain.001.001.09", e);
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
            throw new PainGenerationException("Erreur lors de la génération pain.001.001.09", e);
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
        CustomerCreditTransferInitiationV09 initn = new CustomerCreditTransferInitiationV09();
        document.setCstmrCdtTrfInitn(initn);

        BigDecimal controlSum = computeControlSum(requests);
        String nbTransactions = String.valueOf(requests.size());

        initn.setGrpHdr(buildHeader(messageId, nbTransactions, controlSum));

        PaymentInstruction30 payment = buildPaymentInstruction(messageId, nbTransactions, controlSum);
        for (int i = 0; i < requests.size(); i++) {
            payment.getCdtTrfTxInf().add(buildCreditTransfer(messageId, i, requests.get(i)));
        }
        initn.getPmtInf().add(payment);

        return document;
    }

    private GroupHeader85 buildHeader(String messageId, String nbTransactions,
                                      BigDecimal controlSum) throws Exception {
        GroupHeader85 header = new GroupHeader85();
        header.setMsgId(SepaStringUtils.normalizeId(messageId));
        header.setCreDtTm(XmlCalendarUtils.nowDateTime());
        header.setNbOfTxs(nbTransactions);
        header.setCtrlSum(controlSum);
        header.setInitgPty(buildParty(properties.getDebtor().getName()));
        return header;
    }

    private PaymentInstruction30 buildPaymentInstruction(String messageId,
                                                          String nbTransactions,
                                                          BigDecimal controlSum) throws Exception {
        PaymentInstruction30 payment = new PaymentInstruction30();
        payment.setPmtInfId(messageId + "-1");
        payment.setPmtMtd(PaymentMethod3Code.TRF);
        payment.setNbOfTxs(nbTransactions);
        payment.setCtrlSum(controlSum);

        PaymentTypeInformation26 paymentType = new PaymentTypeInformation26();
        ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
        serviceLevel.setCd("SEPA");
        paymentType.setSvcLvl(serviceLevel);
        payment.setPmtTpInf(paymentType);

        // En v09 : ReqdExctnDt est un DateAndDateTime2Choice — on utilise Dt (date seule)
        DateAndDateTime2Choice reqdExctnDt = new DateAndDateTime2Choice();
        reqdExctnDt.setDt(XmlCalendarUtils.nowDate());
        payment.setReqdExctnDt(reqdExctnDt);

        payment.setDbtr(buildParty(properties.getDebtor().getName()));
        payment.setDbtrAcct(buildAccount(properties.getDebtor().getIban()));
        payment.setDbtrAgt(buildBic(properties.getDebtor().getBic()));
        payment.setChrgBr(ChargeBearerType1Code.SLEV);

        return payment;
    }

    private CreditTransferTransaction34 buildCreditTransfer(String messageId, int index,
                                                             SepaTransferRequest request) {
        CreditTransferTransaction34 tx = new CreditTransferTransaction34();

        PaymentIdentification6 paymentId = new PaymentIdentification6();
        paymentId.setInstrId(SepaStringUtils.normalizeId(messageId + "/" + (index + 1)));
        paymentId.setEndToEndId(SepaStringUtils.normalizeId(request.getEndToEndId()));
        tx.setPmtId(paymentId);

        AmountType3Choice amountType = new AmountType3Choice();
        ActiveOrHistoricCurrencyAndAmount amount = new ActiveOrHistoricCurrencyAndAmount();
        amount.setCcy(request.getCurrency() != null ? request.getCurrency() : "EUR");
        amount.setValue(request.getAmount());
        amountType.setInstdAmt(amount);
        tx.setAmt(amountType);

        tx.setCdtrAgt(buildBic(request.getCreditorBic()));
        tx.setCdtr(buildCreditorParty(request));
        tx.setCdtrAcct(buildAccount(request.getCreditorIban()));

        if (request.getRemittanceInfo() != null) {
            RemittanceInformation16 rmtInfo = new RemittanceInformation16();
            rmtInfo.setUstrd(SepaStringUtils.normalizeRemittanceInfo(request.getRemittanceInfo()));
            tx.setRmtInf(rmtInfo);
        }

        return tx;
    }

    // -------------------------------------------------------------------------
    // Helpers JAXB
    // -------------------------------------------------------------------------

    private PartyIdentification135 buildParty(String name) {
        PartyIdentification135 party = new PartyIdentification135();
        party.setNm(name);
        return party;
    }

    private PartyIdentification135 buildCreditorParty(SepaTransferRequest req) {
        PartyIdentification135 party = new PartyIdentification135();
        party.setNm(SepaStringUtils.normalizeName(req.getCreditorName()));
        PostalAddress24 addr = buildAddress(
                req.getCreditorCountry(), req.getCreditorTownName(), req.getCreditorPostalCode(),
                req.getCreditorAddressLine1(), req.getCreditorAddressLine2(),
                req.getCreditorStreetName(), req.getCreditorBuildingNumber());
        if (addr != null) party.setPstlAdr(addr);
        return party;
    }

    /**
     * Construit un PostalAddress24 (pain.001.001.09) à partir du format global.
     * Retourne null si le format n'est pas configuré ou si country/townName sont absents.
     * En V09, AdrLine est limité à 2 occurrences maximum.
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

    private CashAccount24 buildAccount(String iban) {
        CashAccount24 account = new CashAccount24();
        AccountIdentification4Choice accountId = new AccountIdentification4Choice();
        accountId.setIBAN(iban);
        account.setId(accountId);
        return account;
    }

    private BranchAndFinancialInstitutionIdentification6 buildBic(String bic) {
        BranchAndFinancialInstitutionIdentification6 institution =
                new BranchAndFinancialInstitutionIdentification6();
        FinancialInstitutionIdentification18 finInstnId = new FinancialInstitutionIdentification18();
        // En v09 : BICFI remplace BIC
        finInstnId.setBICFI(bic);
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
