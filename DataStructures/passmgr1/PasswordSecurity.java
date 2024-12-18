package passmgr1;

public class PasswordSecurity {

    // Initialize protected instance variables
    protected char[] E = new char[26];
    protected char[] D = new char[26];
    protected char[] e = new char[26];
    protected char[] d = new char[26];
    protected char[] eNum = new char[10];
    protected char[] dNum = new char[10];
    protected int shift;

    public PasswordSecurity(int shift) {
        this.shift = shift;
        for (int i = 0; i < 26; i++) {
            E[i] = (char) ('A' + (i + (shift % 26) + 26) % 26);
            D[i] = (char) ('A' + (i - (shift % 26) + 26) % 26);
            e[i] = (char) ('a' + (i + (shift % 26) + 26) % 26);
            d[i] = (char) ('a' + (i - (shift % 26) + 26) % 26);
        }
        int wrap;
        for(int i = 0; i < 10; i++) {
            wrap = (shift % 10 + 10) % 10;
            eNum[i] = (char) ('0' + (i + wrap) % 10);
            wrap = (-shift % 10 + 10) % 10;
            dNum[i] = (char) ('0' + (i + wrap) % 10);
        }
    }

    public String encrypt(String password) {
        return decode(password, E, e, eNum);
    }

    public String decrypt(String password) {
        return decode(password, D, d, dNum);
    }

    public String decode(String password, char[] CODE, char[] code, char[] num) {
        char[] pw = password.toCharArray();
        for (int i = 0; i < pw.length; i++) {
            if (Character.isUpperCase(pw[i])) {
                int j = pw[i] - 'A';
                pw[i] = CODE[j];
            } else if (Character.isLowerCase(pw[i])) {
                int j = pw[i] - 'a';
                pw[i] = code[j];
            } else if (Character.isDigit(pw[i])) {
                int j = pw[i] - '0';
                pw[i] = num[j];
            }
        }
        return new String(pw);
    }
}
