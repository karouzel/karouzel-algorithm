package com.karouzel.algorithm;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Computes the selecting winner wallet address as specified by the karouzel blockchain game.
 *
 * @author karouzel.com
 */
public class WinnerSelector {

    /**
     * @return 1 to numAddresses inclusive (not zero based).
     */
    public static int selectWinner(int numAddresses, String entropy) {
        if (numAddresses < 1) throw new IllegalArgumentException("Can't pick one from zero!");

        if (numAddresses==1) return 1; //well it can only be this one.

        String hexSha512 = createSha512(entropy);
        BigInteger decimal = hexToDec(hexSha512);
        BigInteger[] bigIntegers = decimal.divideAndRemainder(BigInteger.valueOf(numAddresses));
        BigInteger remainder = bigIntegers[1];

        int result = remainder.intValue() +1; //yes plus one

        //sanity checks:
        if (result < 1) {
            throw new AssertionError("Result below range: "+result+" / "+numAddresses);
        }
        if (result > numAddresses) {
            throw new AssertionError("Result above range: "+result+" / "+numAddresses);
        }

        return result;
    }

    private static String createSha512(String entropy) {
        //charset is irrelevant as we use ASCII characters only in the entropy.
        //they all give the same result.
        Charset charset = StandardCharsets.UTF_8; //ISO_8859_1 //US_ASCII

        try {
            //sha-512 is actually sha2-512, as sha1 does not have a 512 version.
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            //there is absolutely no need for a salt, this is not password hashing.
            //md.update("salt".getBytes(charset));

            byte[] bytes = md.digest(entropy.getBytes(charset));
            StringBuilder sb = new StringBuilder();

            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    private static BigInteger hexToDec(String hexSha512) {
        return new BigInteger(hexSha512, 16);
    }

}
