package daa;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class AlgorithmsTest {
    final Algorithms.Distance[] distances={Algorithms.RECURSIVE, Algorithms.TABLE, Algorithms.TWO_ROWS};
    int d(Algorithms.Distance f,String a,String b) { return f.distance(a,b); }
    String random(Random r,int n) { StringBuilder b=new StringBuilder(); for(int i=0;i<n;i++) b.append("ABC".charAt(r.nextInt(3))); return b.toString(); }
    @Test void referencesAndEmpty() {
        for(var f:distances) { assertEquals(3,d(f,"kitten","sitting")); assertEquals(2,d(f,"flaw","lawn")); assertEquals(5,d(f,"intention","execution")); assertEquals(3,d(f,"","abc")); assertEquals(3,d(f,"abc","")); assertEquals(0,d(f,"abc","abc")); assertEquals(0,d(f,"","")); }
    }
    @Test void randomDistanceAgreement() { Random r=new Random(42); for(int i=0;i<100;i++) { String a=random(r,r.nextInt(7)),b=random(r,r.nextInt(7)); int expected=d(distances[0],a,b); for(var f:distances) assertEquals(expected,d(f,a,b)); } }
    @Test void prefixes() { assertArrayEquals(new int[]{0,1,2,0,1,2,3,3},Algorithms.lps("AAACAAAA")); assertArrayEquals(new int[]{0,0,1,2,0,1,2,3,4},Algorithms.lps("ABABCABAB")); }
    void check(String t,String p,List<Integer> result) { assertEquals(result,Algorithms.naive(t,p,new Algorithms.Counter())); assertEquals(result,Algorithms.kmp(t,p,new Algorithms.Counter())); }
    @Test void searchReferences() { check("ABABDABACDABABCABAB","ABABCABAB",List.of(10)); check("AAAAA","AA",List.of(0,1,2,3)); check("ABABABA","ABA",List.of(0,2,4)); check("abc","abcd",List.of()); check("","a",List.of()); check("abc","abc",List.of(0)); check("abc","xyz",List.of()); check("AAA","A",List.of(0,1,2)); }
    @Test void emptyPatternThrows() { assertThrows(IllegalArgumentException.class,()->Algorithms.naive("abc","",new Algorithms.Counter())); assertThrows(IllegalArgumentException.class,()->Algorithms.kmp("abc","",new Algorithms.Counter())); }
    @Test void randomSearchAgreement() { Random r=new Random(99); for(int i=0;i<100;i++) { String t=random(r,r.nextInt(50)),p=random(r,1+r.nextInt(8)); assertEquals(Algorithms.naive(t,p,new Algorithms.Counter()),Algorithms.kmp(t,p,new Algorithms.Counter())); } }
    @Test void searchAgainstIndependentLibraryReference() {
        Random r=new Random(2026);
        for(int trial=0;trial<1000;trial++) {
            String text=random(r,r.nextInt(80)), pattern=random(r,1+r.nextInt(12));
            List<Integer> expected=new ArrayList<>();
            for(int at=text.indexOf(pattern);at>=0;at=text.indexOf(pattern,at+1)) expected.add(at);
            assertEquals(expected,Algorithms.NAIVE.search(text,pattern));
            assertEquals(expected,Algorithms.KMP.search(text,pattern));
        }
    }
    @Test void workCountersCountActualOperations() {
        Algorithms.Counter c=new Algorithms.Counter();
        assertEquals(2,Algorithms.recursive("AA","BB",c)); assertEquals(19,c.work);
        for(var distance:new Algorithms.Distance[]{Algorithms.TABLE,Algorithms.TWO_ROWS}) {
            c=new Algorithms.Counter(); assertEquals(2,distance.distance("AA","BB",c)); assertEquals(4,c.work);
        }
        c=new Algorithms.Counter(); Algorithms.naive("AAAAA","AA",c); assertEquals(8,c.work);
        c=new Algorithms.Counter(); Algorithms.kmp("AAAAA","AA",c); assertEquals(6,c.work); // 1 LPS + 5 search comparisons.
    }
}
