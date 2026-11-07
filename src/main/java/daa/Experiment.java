package daa;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Experiment {
    static final long SEED=20261108L;
    static volatile int sink;
    static String random(Random r,int n) { StringBuilder b=new StringBuilder(n); for(int i=0;i<n;i++) b.append("ACGT".charAt(r.nextInt(4))); return b.toString(); }
    public static void main(String[] args) throws Exception {
        Files.createDirectories(Path.of("results")); Random rng=new Random(SEED);
        try(PrintWriter out=new PrintWriter(Files.newBufferedWriter(Path.of("results/results.csv")))) {
            out.println("family,algorithm,input,n,run,time_ms,work,result_count");
            Algorithms.Distance[] ds={Algorithms::recursive,Algorithms::table,Algorithms::twoRows}; String[] names={"recursive","table","two_rows"};
            for(int n:new int[]{2,4,8,10}) for(String type:new String[]{"random","identical","disjoint"}) {
                String a=type.equals("disjoint")?"A".repeat(n):random(rng,n), b=type.equals("identical")?a:type.equals("disjoint")?"B".repeat(n):random(rng,n);
                for(int k=0;k<ds.length;k++) { final int ix=k;
                    for(int warm=0;warm<3;warm++) sink=ds[ix].distance(a,b,new Algorithms.Counter());
                    for(int run=1;run<=5;run++) { Algorithms.Counter c=new Algorithms.Counter(); long start=System.nanoTime(); int value=ds[ix].distance(a,b,c); long elapsed=System.nanoTime()-start; sink=value; out.printf(Locale.ROOT,"edit,%s,%s,%d,%d,%.6f,%d,%d%n",names[ix],type,n,run,elapsed/1e6,c.work,value); }
                }
            }
            for(int n:new int[]{2048,4096,8192,16384}) for(String type:new String[]{"random","adversarial"}) {
                String t=type.equals("random")?random(rng,n):"A".repeat(n); String p=type.equals("random")?random(rng,32):"A".repeat(31)+"B";
                for(int k=0;k<2;k++) { final int ix=k;
                    for(int warm=0;warm<3;warm++) sink=(ix==0?Algorithms.naive(t,p,new Algorithms.Counter()):Algorithms.kmp(t,p,new Algorithms.Counter())).size();
                    for(int run=1;run<=5;run++) { Algorithms.Counter c=new Algorithms.Counter(); long start=System.nanoTime(); int value=(ix==0?Algorithms.naive(t,p,c):Algorithms.kmp(t,p,c)).size(); long elapsed=System.nanoTime()-start; sink=value; out.printf(Locale.ROOT,"search,%s,%s,%d,%d,%.6f,%d,%d%n",ix==0?"naive":"kmp",type,n,run,elapsed/1e6,c.work,value); }
                }
            }
        }
    }
}
