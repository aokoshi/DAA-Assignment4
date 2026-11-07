package daa;

import java.util.ArrayList;
import java.util.List;

public final class Algorithms {
    private Algorithms() {}
    public static final class Counter { public long work; }
    @FunctionalInterface public interface Distance {
        int distance(String a, String b, Counter counter);
        default int distance(String a, String b) { return distance(a, b, new Counter()); }
    }

    // State: distance between prefixes of lengths i and j. Base: an empty prefix costs the other length.
    // Transition: equal final characters use (i-1,j-1); otherwise one plus the minimum of deletion, insertion and replacement.
    public static int recursive(String a, String b, Counter c) { return rec(a, b, a.length(), b.length(), c); }
    private static int rec(String a, String b, int i, int j, Counter c) {
        c.work++;
        if (i == 0) return j;
        if (j == 0) return i;
        if (a.charAt(i-1) == b.charAt(j-1)) return rec(a,b,i-1,j-1,c);
        return 1 + Math.min(rec(a,b,i-1,j,c), Math.min(rec(a,b,i,j-1,c), rec(a,b,i-1,j-1,c)));
    }
    // State: d[i][j] is the distance between the first i and j characters. Base: d[i][0]=i and d[0][j]=j.
    // Transition: minimum of deletion, insertion and replacement/match from the adjacent prefix states.
    public static int table(String a, String b, Counter c) {
        int n=a.length(), m=b.length(); int[][] d=new int[n+1][m+1];
        for(int i=0;i<=n;i++) d[i][0]=i;
        for(int j=0;j<=m;j++) d[0][j]=j;
        for(int i=1;i<=n;i++) for(int j=1;j<=m;j++) {
            c.work++; d[i][j]=Math.min(d[i-1][j]+1, Math.min(d[i][j-1]+1,d[i-1][j-1]+(a.charAt(i-1)==b.charAt(j-1)?0:1)));
        }
        return d[n][m];
    }
    // State: previous and current rows hold prefix distances. Base: row zero is 0..m and column zero is i.
    // Transition: the same three neighboring states as the full table; discard the older row after each i.
    public static int twoRows(String a, String b, Counter c) {
        int m=b.length(); int[] prev=new int[m+1], cur=new int[m+1];
        for(int j=0;j<=m;j++) prev[j]=j;
        for(int i=1;i<=a.length();i++) { cur[0]=i;
            for(int j=1;j<=m;j++) { c.work++; cur[j]=Math.min(prev[j]+1,Math.min(cur[j-1]+1,prev[j-1]+(a.charAt(i-1)==b.charAt(j-1)?0:1))); }
            int[] tmp=prev; prev=cur; cur=tmp;
        }
        return prev[m];
    }
    public static int[] lps(String p) { return lps(p,new Counter()); }
    private static int[] lps(String p, Counter c) {
        int[] l=new int[p.length()]; int i=1, len=0;
        while(i<p.length()) { c.work++; if(p.charAt(i)==p.charAt(len)) l[i++]=++len; else if(len>0) len=l[len-1]; else l[i++]=0; }
        return l;
    }
    public static List<Integer> naive(String t, String p, Counter c) {
        if(p.isEmpty()) throw new IllegalArgumentException("empty pattern");
        List<Integer> out=new ArrayList<>();
        for(int i=0;i<=t.length()-p.length();i++) { int j=0; while(j<p.length()) { c.work++; if(t.charAt(i+j)!=p.charAt(j)) break; j++; } if(j==p.length()) out.add(i); }
        return out;
    }
    public static List<Integer> kmp(String t, String p, Counter c) {
        if(p.isEmpty()) throw new IllegalArgumentException("empty pattern");
        List<Integer> out=new ArrayList<>(); if(p.length()>t.length()) return out;
        int[] l=lps(p,c); int i=0,j=0;
        while(i<t.length()) { c.work++; if(t.charAt(i)==p.charAt(j)) { i++; j++; if(j==p.length()) { out.add(i-j); j=l[j-1]; } } else if(j>0) j=l[j-1]; else i++; }
        return out;
    }
}
