import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SecretSharing {

    public static void main(String[] args) {
        try {
            // Load JSON input
            JSONObject data = new JSONObject(new JSONTokener(new FileReader("input.json")));

            int n = data.getJSONObject("keys").getInt("n");
            int k = data.getJSONObject("keys").getInt("k");

            List<Point> points = new ArrayList<>();

            // Decode the roots
            for (String key : data.keySet()) {
                if (!key.equals("keys")) {
                    JSONObject root = data.getJSONObject(key);
                    String base = root.getString("base");
                    String value = root.getString("value");
                    long x = Long.parseLong(key);
                    BigInteger y = decodeValue(base, value);
                    points.add(new Point(x, y));
                }
            }

            // Ensure enough points
            if (points.size() < k) {
                System.out.println("Not enough points to determine the polynomial.");
                return;
            }

            // Calculate the constant term c
            BigInteger c = lagrangeInterpolation(points, BigInteger.ZERO);
            System.out.println("Constant term c: " + c);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BigInteger decodeValue(String baseStr, String valueStr) {
        int base = Integer.parseInt(baseStr);
        try {
            return new BigInteger(valueStr, base);
        } catch (NumberFormatException e) {
            System.err.println("Error decoding value: " + valueStr + " with base: " + base);
            throw e; // rethrow the exception to halt execution
        }
    }

    public static BigInteger lagrangeInterpolation(List<Point> points, BigInteger x) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).x);
            BigInteger yi = points.get(i).y;
            BigInteger term = yi;

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger xj = BigInteger.valueOf(points.get(j).x);
                    term = term.multiply(x.subtract(xj)).divide(xi.subtract(xj));
                }
            }

            result = result.add(term);
        }

        return result;
    }

    static class Point {
        long x;
        BigInteger y;

        Point(long x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
