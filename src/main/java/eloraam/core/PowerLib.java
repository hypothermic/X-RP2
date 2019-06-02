/* X-RP - decompiled with CFR */
package eloraam.core;

public class PowerLib {

    public static int cutBits(int n, int n2) {
        int n3 = 1;
        while (n3 <= n2) {
            if ((n2 & n3) == 0) {
                n3 <<= 1;
                continue;
            }
            n = n & n3 - 1 | n >> 1 & ~(n3 - 1);
            n2 >>= 1;
        }
        return n;
    }
}
