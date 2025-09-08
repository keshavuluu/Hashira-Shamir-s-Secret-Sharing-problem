import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import java.util.*;

public class Shamir {
    public static void main(String[] args) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get("input.json")), "UTF-8");
        JSONObject json = new JSONObject(content);
        JSONObject keys = json.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        TreeMap<Integer, BigInteger> shares = new TreeMap<>();
        for (String key : json.keySet()) {
            if (key.equals("keys")) continue;
            int x = Integer.parseInt(key);
            JSONObject entry = json.getJSONObject(key);
            int base = Integer.parseInt(entry.getString("base"));
            String valStr = entry.getString("value");
            BigInteger y = new BigInteger(valStr, base);
            shares.put(x, y);
        }

        List<Map.Entry<Integer,BigInteger>> pts = new ArrayList<>(shares.entrySet());
        if (pts.size() < k) {
            System.out.println("Not enough shares to reconstruct secret.");
            return;
        }

        BigInteger secret = BigInteger.ZERO;
        List<BigInteger> yvals = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            yvals.add(pts.get(i).getValue());
        }

        BigInteger term = yvals.get(0);
        secret = secret.add(term);
        BigInteger sign = BigInteger.ONE.negate();
        List<BigInteger> cur = new ArrayList<>(yvals);
        for (int level = 1; level < k; level++) {
            List<BigInteger> nxt = new ArrayList<>();
            for (int i = 0; i < cur.size() - 1; i++) {
                nxt.add(cur.get(i + 1).subtract(cur.get(i)));
            }
            secret = secret.add(sign.multiply(nxt.get(0)));
            sign = sign.negate();
            cur = nxt;
        }

        System.out.println("Secret: " + secret.toString());
    }
}
