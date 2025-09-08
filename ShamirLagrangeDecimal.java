import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import java.util.*;

public class ShamirLagrangeDecimal {

    public static BigDecimal reconstructSecret(List<Integer> x, List<BigDecimal> y) {
        BigDecimal secret = BigDecimal.ZERO;
        int k = x.size();
        MathContext mc = new MathContext(100);

        for (int i = 0; i < k; i++) {
            BigDecimal term = y.get(i);
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigDecimal numerator = new BigDecimal(x.get(j));
                    BigDecimal denominator = new BigDecimal(x.get(j) - x.get(i));
                    term = term.multiply(numerator.divide(denominator, mc), mc);
                }
            }
            secret = secret.add(term, mc);
        }
        return secret;
    }

    public static void main(String[] args) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get("input.json")), "UTF-8");
        JSONObject json = new JSONObject(content);
        JSONObject keys = json.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        TreeMap<Integer, BigDecimal> shares = new TreeMap<>();
        for (String key : json.keySet()) {
            if (key.equals("keys")) continue;
            int xi = Integer.parseInt(key);
            JSONObject entry = json.getJSONObject(key);
            int base = Integer.parseInt(entry.getString("base"));
            String valStr = entry.getString("value");
            BigDecimal yi = new BigDecimal(new java.math.BigInteger(valStr, base));
            shares.put(xi, yi);
        }

        if (shares.size() < k) {
            System.out.println("Not enough shares to reconstruct secret.");
            return;
        }

        List<Integer> xvals = new ArrayList<>();
        List<BigDecimal> yvals = new ArrayList<>();
        int count = 0;
        for (Map.Entry<Integer, BigDecimal> entry : shares.entrySet()) {
            xvals.add(entry.getKey());
            yvals.add(entry.getValue());
            count++;
            if (count == k) break;
        }

        BigDecimal secret = reconstructSecret(xvals, yvals);
        System.out.println("Secret: " + secret.toBigInteger().toString());
    }
}
