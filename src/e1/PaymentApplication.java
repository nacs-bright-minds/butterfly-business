package e1;

import e1.credit.CreditCards;
import e1.credit.LuhnValidator;
import e1.csv.CSVFileWriter;
import e1.files.FileReader;
import e1.intelligence.BusinessFileWriter;
import e1.operation.PaymentOperation;
import e1.operation.SevenSegmentMapping;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaymentApplication {

    public static void main(String[] args) {

        FileReader fileReader = new FileReader();

        List<String> lines = fileReader.asList("resources/buterfly-business.txt");

        List<String> parsedLines = IntStream.iterate(0, i -> i + 3).limit(lines.size() / 3)
                .mapToObj(i -> {


                    List<String> partition0 = partition(lines.get(i));
                    List<String> partition1 = partition(lines.get(i + 1));
                    List<String> partition2 = partition(lines.get(i + 2));


                    List<String> strangeList = IntStream.range(0, partition0.size())
                            .mapToObj(index -> partition0.get(index) + partition1.get(index) + partition2.get(index))
                            .collect(Collectors.toList());


                    String digits = strangeList.stream()
                            .map(digit -> SevenSegmentMapping.strangeDigits.get(digit))
                            .collect(Collectors.joining());


                    return digits;
                }).collect(Collectors.toList());


        List<PaymentOperation> paymentOperations = getPaymentOperations(parsedLines);

        paymentOperations.forEach(PaymentOperation::updateIssuerFromCreditNumber);

        paymentOperations.sort(Comparator.comparing(PaymentOperation::getDate));

        CSVFileWriter.writeCSV(paymentOperations, "output/Payments.csv");

        BusinessFileWriter.writeBusinessIntelligenceReport(paymentOperations, "output/Report.txt");
    }

    public static List<PaymentOperation> getPaymentOperations(List<String> parts) {
        return parts.stream()
                .map(line -> line.split(" "))
                .map(columns -> new PaymentOperation(
                        LocalDate.parse(columns[0]),
                        columns[1],
                        Double.valueOf(columns[2])))
                .collect(Collectors.toList());
    }

    static List<String> partition(String line) {
        return IntStream.iterate(0, i -> i + 3).limit(line.length() / 3)
                .mapToObj(index -> line.substring(index, index + 3))
                .collect(Collectors.toList());
    }


}