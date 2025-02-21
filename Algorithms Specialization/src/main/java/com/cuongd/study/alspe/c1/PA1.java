package com.cuongd.study.alspe.c1;

import java.math.BigInteger;

public class PA1 {
    private static String multiply(String x, String y) {
        if (x.length() == 1 || y.length() == 1) {
            return String.valueOf(Long.parseLong(x) * Long.parseLong(y));
        }
        int n = Math.max(x.length(), y.length());
        x = pad0s(x, n - x.length());
        y = pad0s(y, n - y.length());
        String a = x.substring(0, n/2);
        String b = x.substring(n/2);
        String c = y.substring(0, n/2);
        String d = y.substring(n/2);
        String ac = multiply(a, c);
        String bd = multiply(b, d);
        String abcd = multiply(
                new BigInteger(a).add(new BigInteger(b)).toString(),
                new BigInteger(c).add(new BigInteger(d)).toString()
        );
        String adbc = new BigInteger(abcd)
                .add(new BigInteger(ac).negate())
                .add(new BigInteger(bd).negate())
                .toString();
        int midPow = Math.max(n/2, b.length());
        String firstTerm = pow10(ac, 2*midPow);
        String secondTerm = pow10(adbc, midPow);
        String thirdTerm = bd;
        return new BigInteger(firstTerm)
                .add(new BigInteger(secondTerm))
                .add(new BigInteger(thirdTerm))
                .toString();
    }

    private static String pad0s(String s, int n) {
        if (n == 0) return s;
        StringBuilder sb = new StringBuilder(s);
        while (n-- > 0) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    private static String pow10(String s, int n) {
        StringBuilder sb = new StringBuilder(s);
        while (n-- > 0) {
            sb.append('0');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        assert multiply("3", "7").equals("21");
        assert multiply("3", "13").equals("39");
        assert multiply("17", "23").equals("391");
        assert multiply("123", "456").equals("56088");
        assert multiply("1", "2345").equals("2345");
        assert multiply("12", "3456").equals("41472");
        assert multiply("123", "4567").equals("561741");
        assert multiply("1234", "5678").equals("7006652");
        System.out.println(multiply("3141592653589793238462643383279502884197169399375105820974944592", "2718281828459045235360287471352662497757247093699959574966967627"));
    }
}
